package com.bezkoder.spring.jpa.h2.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

/**
 * Stores the per-program percentage and computed dollar allocation
 * for a funding round (e.g. HomeStart 70% / NAHI 30%).
 * Maps to the PostgreSQL table: program_allocations
 */
@Entity
@Table(name = "program_allocations")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class ProgramAllocation extends AuditModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Back-reference to the owning FundingRound.
     * Excluded from JSON serialization to prevent circular references.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "funding_round_id", nullable = false)
    @JsonIgnore
    @ToString.Exclude
    private FundingRound fundingRound;

    /** Program name, e.g. "HomeStart" or "NAHI" */
    @Column(name = "program_name", nullable = false, length = 100)
    private String programName;

    /**
     * Allocation as a percentage (0–100).
     * Example: 70.00 means 70%.
     */
    @Column(name = "allocation_percentage", precision = 5, scale = 2, nullable = false)
    private BigDecimal allocationPercentage;

    /**
     * Computed dollar amount = totalFundsAvailable * allocationPercentage / 100.
     * Updated automatically by the service layer.
     */
    @Column(name = "allocation_amount", precision = 15, scale = 2)
    private BigDecimal allocationAmount;
}
