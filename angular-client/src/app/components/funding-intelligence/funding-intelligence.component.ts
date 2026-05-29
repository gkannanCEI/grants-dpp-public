import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { Subject } from 'rxjs';
import { takeUntil, finalize } from 'rxjs/operators';
import { FundingRoundService } from '../../services/funding-round.service';
import { FundingRoundModel } from '../../models/funding-round.model';

@Component({
  selector: 'app-funding-intelligence',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './funding-intelligence.component.html',
  styleUrls: ['./funding-intelligence.component.css']
})
export class FundingIntelligenceComponent implements OnInit, OnDestroy {

  rounds: FundingRoundModel[]  = [];
  isLoading                    = false;
  errorMessage                 = '';

  /**
   * Skeleton rows array — drives the *ngFor in the skeleton table.
   * 5 rows gives the user a realistic preview of the table structure
   * while data is loading.
   */
  readonly skeletonRows = [1, 2, 3, 4, 5];

  private destroy$ = new Subject<void>();

  constructor(
    private fundingRoundService: FundingRoundService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadRounds();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  // ── Data Loading ──────────────────────────────────────────────────────────

  loadRounds(): void {
    this.isLoading    = true;
    this.errorMessage = '';

    this.fundingRoundService.getAll()
      .pipe(
        // finalize() guarantees isLoading resets regardless of success/error
        finalize(() => { this.isLoading = false; }),
        // Auto-unsubscribe when component is destroyed — prevents memory leaks
        takeUntil(this.destroy$)
      )
      .subscribe({
        next: (data: FundingRoundModel[]) => {
          this.rounds = data;
        },
        error: (err: Error) => {
          console.error('[FundingIntelligence] Failed to load funding rounds:', err);
          this.errorMessage =
            err?.message ??
            'Failed to load funding rounds. Please check your connection and try again.';
        }
      });
  }

  // ── Navigation ────────────────────────────────────────────────────────────

  createNewRound(): void {
    this.router.navigate(['/create-funding-round']);
  }

  editRound(id: number | undefined): void {
    if (id !== undefined) {
      this.router.navigate(['/create-funding-round', id]);
    }
  }

  // ── Delete ────────────────────────────────────────────────────────────────

  deleteRound(id: number | undefined): void {
    if (id === undefined) return;
    if (!confirm('Are you sure you want to delete this funding round?')) return;

    this.fundingRoundService.delete(id)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: () => this.loadRounds(),
        error: (err: Error) => {
          console.error('[FundingIntelligence] Delete failed:', err);
          this.errorMessage = err?.message ?? 'Failed to delete funding round.';
        }
      });
  }

  // ── trackBy ───────────────────────────────────────────────────────────────

  /**
   * trackBy function for *ngFor — tells Angular to identify rows by id.
   * Without this, Angular re-renders every row on every change detection cycle.
   * With this, only rows whose id changed are re-rendered — significant
   * performance gain for large tables.
   */
  trackById(_index: number, round: FundingRoundModel): number | undefined {
    return round.id;
  }

  // ── Utility ───────────────────────────────────────────────────────────────

  getStatusClass(status: string | undefined): string {
    switch (status?.toUpperCase()) {
      case 'ACTIVE':           return 'status-active';
      case 'PENDING_APPROVAL': return 'status-pending';
      case 'CLOSED':           return 'status-closed';
      default:                 return 'status-draft';
    }
  }

  formatCurrency(value: number | undefined): string {
    if (value === undefined || value === null) return '—';
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: 'USD',
      maximumFractionDigits: 0
    }).format(value);
  }
}
