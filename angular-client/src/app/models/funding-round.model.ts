/**
 * Client-side model for a Funding Round — mirrors FundingRoundResponse.java.
 */
export interface ProgramAllocationModel {
  id?: number;
  programName: string;
  allocationPercentage: number;
  allocationAmount?: number;
}

export interface FundingRoundModel {
  id?: number;

  // Section 1
  roundName: string;
  description?: string;

  // Section 2
  useAcquisitionCosts: boolean;
  useRehabilitation: boolean;
  useOther: boolean;
  otherUseDescription?: string;

  // Section 3
  enrollmentOpenDate?: string;   // ISO date: yyyy-MM-dd
  reservationOpenDate?: string;
  reservationCloseMethod?: string;
  closeDate?: string;
  boardApprovalDate?: string;

  // Section 4
  totalFundsAvailable?: number;
  individualSubsidyMinEnabled: boolean;
  individualSubsidyMin?: number;
  individualSubsidyMaxEnabled: boolean;
  individualSubsidyMax?: number;
  limitFundsPerMember: boolean;
  memberFundLimit?: number;
  defaultLimitPerMember?: number;

  // Section 5
  programAllocations: ProgramAllocationModel[];

  // Section 6
  allocationDrawdownEvent?: string;
  reservationCompletionExpirationDays?: number;
  reservationExpirationDays?: number;
  disbursementSubmissionExpirationDays?: number;
  reminderEmailCompletionDays?: number;
  reminderEmailExpirationDays?: number;

  // Lifecycle
  status?: string;
  createdAt?: string;
  updatedAt?: string;

  // AI-computed summary
  estimatedUtilizationPercent?: number;
  estimatedExhaustionDate?: string;
  riskLevel?: string;
  complianceRisk?: string;
}

/** Default factory for a blank funding round form */
export function createEmptyFundingRound(): FundingRoundModel {
  return {
    roundName: '',
    description: '',
    useAcquisitionCosts: true,
    useRehabilitation: true,
    useOther: false,
    otherUseDescription: '',
    enrollmentOpenDate: undefined,
    reservationOpenDate: undefined,
    reservationCloseMethod: 'Close Date',
    closeDate: undefined,
    boardApprovalDate: undefined,
    totalFundsAvailable: 400000000,
    individualSubsidyMinEnabled: true,
    individualSubsidyMin: 1000,
    individualSubsidyMaxEnabled: true,
    individualSubsidyMax: 15000,
    limitFundsPerMember: true,
    memberFundLimit: 500000,
    defaultLimitPerMember: 500000,
    programAllocations: [
      { programName: 'HomeStart', allocationPercentage: 70 },
      { programName: 'NAHI', allocationPercentage: 30 }
    ],
    allocationDrawdownEvent: 'Eligible Approval',
    reservationCompletionExpirationDays: 30,
    reservationExpirationDays: 60,
    disbursementSubmissionExpirationDays: 90,
    reminderEmailCompletionDays: 5,
    reminderEmailExpirationDays: 5,
    status: 'DRAFT'
  };
}
