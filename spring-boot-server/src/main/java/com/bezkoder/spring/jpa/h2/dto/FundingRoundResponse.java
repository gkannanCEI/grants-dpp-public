package com.bezkoder.spring.jpa.h2.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO returned to the Angular client — avoids exposing internal entity details.
 */
@Data
public class FundingRoundResponse {

    private Long id;
    private String roundName;
    private String description;

    private boolean useAcquisitionCosts;
    private boolean useRehabilitation;
    private boolean useOther;
    private String otherUseDescription;

    private LocalDate enrollmentOpenDate;
    private LocalDate reservationOpenDate;
    private String reservationCloseMethod;
    private LocalDate closeDate;
    private LocalDate boardApprovalDate;

    private BigDecimal totalFundsAvailable;
    private boolean individualSubsidyMinEnabled;
    private BigDecimal individualSubsidyMin;
    private boolean individualSubsidyMaxEnabled;
    private BigDecimal individualSubsidyMax;
    private boolean limitFundsPerMember;
    private BigDecimal memberFundLimit;
    private BigDecimal defaultLimitPerMember;

    private List<ProgramAllocationResponse> programAllocations;

    private String allocationDrawdownEvent;
    private Integer reservationCompletionExpirationDays;
    private Integer reservationExpirationDays;
    private Integer disbursementSubmissionExpirationDays;
    private Integer reminderEmailCompletionDays;
    private Integer reminderEmailExpirationDays;

    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // AI-computed summary fields (calculated server-side)
    private BigDecimal estimatedUtilizationPercent;
    private LocalDate estimatedExhaustionDate;
    private String riskLevel;
    private String complianceRisk;

    @Data
    public static class ProgramAllocationResponse {
        private Long id;
        private String programName;
        private BigDecimal allocationPercentage;
        private BigDecimal allocationAmount;
    }
}
