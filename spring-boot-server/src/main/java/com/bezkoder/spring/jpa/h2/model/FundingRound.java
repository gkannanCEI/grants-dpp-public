package com.bezkoder.spring.jpa.h2.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a single funding round ("Set-Aside Round") in the DPP Program.
 * Maps to the PostgreSQL table: funding_rounds
 *
 * Performance notes:
 *  - programAllocations uses FetchType.LAZY to prevent automatic JOIN on every
 *    findAll() call. The service layer uses @EntityGraph or JOIN FETCH when it
 *    actually needs the allocations, eliminating the N+1 SELECT problem.
 *  - @Index on status column for fast filter-by-status queries.
 *  - @Index on round_name for fast uniqueness checks.
 */
@Entity
@Table(
    name = "funding_rounds",
    indexes = {
        @Index(name = "idx_funding_round_status",     columnList = "status"),
        @Index(name = "idx_funding_round_round_name", columnList = "round_name")
    }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class FundingRound extends AuditModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ── Section 1: General Information ─────────────────────────────────────
    @Column(name = "round_name", nullable = false, unique = true, length = 100)
    private String roundName;

    @Column(name = "description", length = 250)
    private String description;

    // ── Section 2: Uses of Funds ────────────────────────────────────────────
    @Column(name = "use_acquisition_costs", nullable = false)
    private boolean useAcquisitionCosts;

    @Column(name = "use_rehabilitation", nullable = false)
    private boolean useRehabilitation;

    @Column(name = "use_other", nullable = false)
    private boolean useOther;

    @Column(name = "other_use_description", length = 250)
    private String otherUseDescription;

    // ── Section 3: Key Dates ────────────────────────────────────────────────
    @Column(name = "enrollment_open_date")
    private LocalDate enrollmentOpenDate;

    @Column(name = "reservation_open_date")
    private LocalDate reservationOpenDate;

    /**
     * How reservations are closed: "Close Date" | "Exhaustion" | "Manual"
     */
    @Column(name = "reservation_close_method", length = 50)
    private String reservationCloseMethod;

    @Column(name = "close_date")
    private LocalDate closeDate;

    @Column(name = "board_approval_date")
    private LocalDate boardApprovalDate;

    // ── Section 4: Funding Information ─────────────────────────────────────
    @Column(name = "total_funds_available", precision = 15, scale = 2)
    private BigDecimal totalFundsAvailable;

    @Column(name = "individual_subsidy_min_enabled", nullable = false)
    private boolean individualSubsidyMinEnabled;

    @Column(name = "individual_subsidy_min", precision = 15, scale = 2)
    private BigDecimal individualSubsidyMin;

    @Column(name = "individual_subsidy_max_enabled", nullable = false)
    private boolean individualSubsidyMaxEnabled;

    @Column(name = "individual_subsidy_max", precision = 15, scale = 2)
    private BigDecimal individualSubsidyMax;

    @Column(name = "limit_funds_per_member", nullable = false)
    private boolean limitFundsPerMember;

    @Column(name = "member_fund_limit", precision = 15, scale = 2)
    private BigDecimal memberFundLimit;

    @Column(name = "default_limit_per_member", precision = 15, scale = 2)
    private BigDecimal defaultLimitPerMember;

    // ── Section 5: Program Allocations ─────────────────────────────────────
    /**
     * LAZY fetch — prevents automatic N+1 SELECTs on every findAll().
     * Use @EntityGraph("FundingRound.withAllocations") when allocations
     * are needed (e.g. single-record detail view).
     */
    @OneToMany(mappedBy = "fundingRound", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<ProgramAllocation> programAllocations = new ArrayList<>();

    // ── Section 6: Allocation & Reminder Settings ───────────────────────────
    /**
     * Drawdown event trigger: "Eligible Approval" | "Final Approval" | "Disbursement"
     */
    @Column(name = "allocation_drawdown_event", length = 50)
    private String allocationDrawdownEvent;

    @Column(name = "reservation_completion_expiration_days")
    private Integer reservationCompletionExpirationDays;

    @Column(name = "reservation_expiration_days")
    private Integer reservationExpirationDays;

    @Column(name = "disbursement_submission_expiration_days")
    private Integer disbursementSubmissionExpirationDays;

    @Column(name = "reminder_email_completion_days")
    private Integer reminderEmailCompletionDays;

    @Column(name = "reminder_email_expiration_days")
    private Integer reminderEmailExpirationDays;

    /**
     * Lifecycle status: "DRAFT" | "PENDING_APPROVAL" | "ACTIVE" | "CLOSED"
     */
    @Column(name = "status", length = 30, nullable = false)
    @Builder.Default
    private String status = "DRAFT";
}
