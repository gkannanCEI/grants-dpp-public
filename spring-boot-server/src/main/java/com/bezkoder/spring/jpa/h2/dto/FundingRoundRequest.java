package com.bezkoder.spring.jpa.h2.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * DTO for creating or updating a FundingRound.
 * Decouples API payload from the JPA entity.
 */
@Data
public class FundingRoundRequest {

    // Section 1
    private String roundName;
    private String description;

    // Section 2
    private boolean useAcquisitionCosts;
    private boolean useRehabilitation;
    private boolean useOther;
    private String otherUseDescription;

    // Section 3
    private LocalDate enrollmentOpenDate;
    private LocalDate reservationOpenDate;
    private String reservationCloseMethod;
    private LocalDate closeDate;
    private LocalDate boardApprovalDate;

    // Section 4
    private BigDecimal totalFundsAvailable;
    private boolean individualSubsidyMinEnabled;
    private BigDecimal individualSubsidyMin;
    private boolean individualSubsidyMaxEnabled;
    private BigDecimal individualSubsidyMax;
    private boolean limitFundsPerMember;
    private BigDecimal memberFundLimit;
    private BigDecimal defaultLimitPerMember;

    // Section 5 – program allocations (name + percentage pairs)
    private List<ProgramAllocationRequest> programAllocations;

    // Section 6
    private String allocationDrawdownEvent;
    private Integer reservationCompletionExpirationDays;
    private Integer reservationExpirationDays;
    private Integer disbursementSubmissionExpirationDays;
    private Integer reminderEmailCompletionDays;
    private Integer reminderEmailExpirationDays;

    // Lifecycle
    private String status;

    @Data
    public static class ProgramAllocationRequest {
        private String programName;
        private BigDecimal allocationPercentage;
    }
}
