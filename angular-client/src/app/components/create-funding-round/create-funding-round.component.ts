import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { FundingRoundService } from '../../services/funding-round.service';
import { FundingRoundModel, ProgramAllocationModel, createEmptyFundingRound } from '../../models/funding-round.model';

@Component({
  selector: 'app-create-funding-round',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './create-funding-round.component.html',
  styleUrls: ['./create-funding-round.component.css']
})
export class CreateFundingRoundComponent implements OnInit {

  round: FundingRoundModel = createEmptyFundingRound();
  isEditMode = false;
  isSubmitting = false;
  isSavingDraft = false;
  errorMessage = '';
  successMessage = '';

  // Validation checklist flags
  get allocationTotal(): number {
    return this.round.programAllocations
      .reduce((sum, pa) => sum + (pa.allocationPercentage || 0), 0);
  }

  get isAllocationValid(): boolean {
    return Math.abs(this.allocationTotal - 100) < 0.01;
  }

  get isCloseDateAfterOpenDate(): boolean {
    if (!this.round.closeDate || !this.round.reservationOpenDate) return true;
    return new Date(this.round.closeDate) > new Date(this.round.reservationOpenDate);
  }

  get isBoardApprovalSet(): boolean {
    return !!this.round.boardApprovalDate;
  }

  // Round summary computed values
  get homestartAllocation(): ProgramAllocationModel | undefined {
    return this.round.programAllocations.find(p => p.programName === 'HomeStart');
  }

  get nahiAllocation(): ProgramAllocationModel | undefined {
    return this.round.programAllocations.find(p => p.programName === 'NAHI');
  }

  get homestartAmount(): number {
    const total = this.round.totalFundsAvailable || 0;
    const pct = this.homestartAllocation?.allocationPercentage || 0;
    return total * pct / 100;
  }

  get nahiAmount(): number {
    const total = this.round.totalFundsAvailable || 0;
    const pct = this.nahiAllocation?.allocationPercentage || 0;
    return total * pct / 100;
  }

  get estimatedExhaustionDate(): string {
    const base = this.round.enrollmentOpenDate
      ? new Date(this.round.enrollmentOpenDate)
      : new Date();
    base.setDate(base.getDate() + 323);
    return base.toLocaleDateString('en-US', { month: 'short', day: 'numeric', year: 'numeric' });
  }

  get riskLevel(): string {
    return this.round.boardApprovalDate ? 'Low' : 'Moderate';
  }

  reservationCloseMethodOptions = ['Close Date', 'Exhaustion', 'Manual'];
  allocationDrawdownEventOptions = ['Eligible Approval', 'Final Approval', 'Disbursement'];

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private fundingRoundService: FundingRoundService
  ) {}

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.isEditMode = true;
      this.fundingRoundService.getById(Number(id)).subscribe({
        next: (data) => {
          this.round = data;
          // Ensure allocations array is not null
          if (!this.round.programAllocations) {
            this.round.programAllocations = [];
          }
        },
        error: (err) => {
          console.error('Failed to load round', err);
          this.errorMessage = 'Failed to load the funding round.';
        }
      });
    }
  }

  onHomestartSliderChange(event: Event): void {
    const val = Number((event.target as HTMLInputElement).value);
    const homestartAlloc = this.round.programAllocations.find(p => p.programName === 'HomeStart');
    const nahiAlloc = this.round.programAllocations.find(p => p.programName === 'NAHI');
    if (homestartAlloc) homestartAlloc.allocationPercentage = val;
    if (nahiAlloc) nahiAlloc.allocationPercentage = 100 - val;
  }

  onNahiSliderChange(event: Event): void {
    const val = Number((event.target as HTMLInputElement).value);
    const nahiAlloc = this.round.programAllocations.find(p => p.programName === 'NAHI');
    const homestartAlloc = this.round.programAllocations.find(p => p.programName === 'HomeStart');
    if (nahiAlloc) nahiAlloc.allocationPercentage = val;
    if (homestartAlloc) homestartAlloc.allocationPercentage = 100 - val;
  }

  saveDraft(): void {
    if (!this.round.roundName?.trim()) {
      this.errorMessage = 'Round name is required.';
      return;
    }
    this.isSavingDraft = true;
    this.errorMessage = '';
    const payload = { ...this.round, status: 'DRAFT' };
    const request$ = this.round.id
      ? this.fundingRoundService.update(this.round.id, payload)
      : this.fundingRoundService.create(payload);

    request$.subscribe({
      next: (saved) => {
        this.round = saved;
        this.isEditMode = true;
        this.successMessage = 'Draft saved successfully.';
        this.isSavingDraft = false;
        setTimeout(() => (this.successMessage = ''), 3000);
      },
      error: (err) => {
        this.errorMessage = err?.error?.message || 'Failed to save draft.';
        this.isSavingDraft = false;
      }
    });
  }

  validateAndSubmit(): void {
    this.errorMessage = '';
    if (!this.round.roundName?.trim()) {
      this.errorMessage = 'Round name is required.';
      return;
    }
    if (!this.isAllocationValid) {
      this.errorMessage = 'Program allocations must sum to 100%.';
      return;
    }
    if (!this.isCloseDateAfterOpenDate) {
      this.errorMessage = 'Close date must be after reservation open date.';
      return;
    }
    this.isSubmitting = true;

    // If not yet persisted, create first then submit
    if (!this.round.id) {
      this.fundingRoundService.create({ ...this.round, status: 'DRAFT' }).subscribe({
        next: (saved) => {
          this.round = saved;
          this.submitForApproval();
        },
        error: (err) => {
          this.errorMessage = err?.error?.message || 'Failed to save before submitting.';
          this.isSubmitting = false;
        }
      });
    } else {
      this.submitForApproval();
    }
  }

  private submitForApproval(): void {
    this.fundingRoundService.submitForApproval(this.round.id!).subscribe({
      next: () => {
        this.isSubmitting = false;
        this.router.navigate(['/funding-intelligence']);
      },
      error: (err) => {
        this.errorMessage = err?.error?.message || 'Failed to submit for approval.';
        this.isSubmitting = false;
      }
    });
  }

  cancel(): void {
    this.router.navigate(['/funding-intelligence']);
  }

  saveAndContinue(): void {
    this.saveDraft();
  }

  formatCurrency(value: number | undefined): string {
    if (value === undefined || value === null) return '$0';
    return new Intl.NumberFormat('en-US', {
      style: 'currency', currency: 'USD', maximumFractionDigits: 0
    }).format(value);
  }
}
