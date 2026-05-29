package com.bezkoder.spring.jpa.h2.service;

import com.bezkoder.spring.jpa.h2.dto.FundingRoundRequest;
import com.bezkoder.spring.jpa.h2.dto.FundingRoundResponse;
import com.bezkoder.spring.jpa.h2.model.FundingRound;
import com.bezkoder.spring.jpa.h2.model.ProgramAllocation;
import com.bezkoder.spring.jpa.h2.repository.FundingRoundRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

/**
 * Business logic layer for Funding Rounds.
 *
 * Caching strategy (Caffeine — in-process, no external server):
 *  - findAll()        → cached under "fundingRounds"  (TTL 300s, max 500 entries)
 *  - findById(id)     → cached under "fundingRound"   (keyed by id)
 *  - create/update/delete → evict both caches so stale data is never served
 *
 * Performance strategy:
 *  - findAll() / findByStatus() use LAZY-fetch queries (1 SQL SELECT, no JOIN)
 *  - findById() uses JOIN FETCH (parent + children in 1 round-trip)
 */
@Service
@Transactional
public class FundingRoundService {

    private final FundingRoundRepository fundingRoundRepository;

    public FundingRoundService(FundingRoundRepository fundingRoundRepository) {
        this.fundingRoundRepository = fundingRoundRepository;
    }

    // ── List ─────────────────────────────────────────────────────────────────

    /**
     * Returns all funding rounds for the list/table view.
     * Result is cached in Caffeine — subsequent calls within TTL cost 0ms DB time.
     */
    @Cacheable(value = "fundingRounds")
    @Transactional(readOnly = true)
    public List<FundingRoundResponse> findAll() {
        return fundingRoundRepository.findAllOrderedByCreatedDateDesc()
                .stream()
                .map(this::toListResponse)
                .collect(Collectors.toList());
    }

    @Cacheable(value = "fundingRounds", key = "#status")
    @Transactional(readOnly = true)
    public List<FundingRoundResponse> findByStatus(String status) {
        return fundingRoundRepository.findByStatus(status)
                .stream()
                .map(this::toListResponse)
                .collect(Collectors.toList());
    }

    // ── Read one (with allocations) ──────────────────────────────────────────

    /**
     * Loads a single round with its allocations using one JOIN FETCH query.
     * Result is cached by id — detail screen re-opens cost 0ms DB time.
     */
    @Cacheable(value = "fundingRound", key = "#id")
    @Transactional(readOnly = true)
    public FundingRoundResponse findById(Long id) {
        FundingRound round = getWithAllocationsOrThrow(id);
        return toDetailResponse(round);
    }

    // ── Create ───────────────────────────────────────────────────────────────

    /**
     * Evicts both caches on write so the list view always reflects the latest data.
     */
    @CacheEvict(value = {"fundingRounds", "fundingRound"}, allEntries = true)
    public FundingRoundResponse create(FundingRoundRequest request) {
        if (fundingRoundRepository.existsByRoundName(request.getRoundName())) {
            throw new IllegalArgumentException(
                    "A funding round with name '" + request.getRoundName() + "' already exists.");
        }
        FundingRound round = mapToEntity(new FundingRound(), request);
        round.setStatus(request.getStatus() != null ? request.getStatus() : "DRAFT");
        FundingRound saved = fundingRoundRepository.save(round);
        return toDetailResponse(saved);
    }

    // ── Update ───────────────────────────────────────────────────────────────

    @CacheEvict(value = {"fundingRounds", "fundingRound"}, allEntries = true)
    public FundingRoundResponse update(Long id, FundingRoundRequest request) {
        FundingRound existing = getWithAllocationsOrThrow(id);

        if (!existing.getRoundName().equals(request.getRoundName())
                && fundingRoundRepository.existsByRoundName(request.getRoundName())) {
            throw new IllegalArgumentException(
                    "A funding round with name '" + request.getRoundName() + "' already exists.");
        }

        existing.getProgramAllocations().clear();
        FundingRound updated = mapToEntity(existing, request);
        if (request.getStatus() != null) {
            updated.setStatus(request.getStatus());
        }
        return toDetailResponse(fundingRoundRepository.save(updated));
    }

    // ── Delete ───────────────────────────────────────────────────────────────

    @CacheEvict(value = {"fundingRounds", "fundingRound"}, allEntries = true)
    public void delete(Long id) {
        FundingRound round = getOrThrow(id);
        fundingRoundRepository.delete(round);
    }

    // ── Submit for Approval ──────────────────────────────────────────────────

    @CacheEvict(value = {"fundingRounds", "fundingRound"}, allEntries = true)
    public FundingRoundResponse submitForApproval(Long id) {
        FundingRound round = getOrThrow(id);
        round.setStatus("PENDING_APPROVAL");
        return toListResponse(fundingRoundRepository.save(round));
    }

    // ── Private: entity fetchers ─────────────────────────────────────────────

    private FundingRound getOrThrow(Long id) {
        return fundingRoundRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("FundingRound not found with id: " + id));
    }

    private FundingRound getWithAllocationsOrThrow(Long id) {
        return fundingRoundRepository.findWithAllocationsById(id)
                .orElseThrow(() -> new NoSuchElementException("FundingRound not found with id: " + id));
    }

    // ── Private: DTO mappers ─────────────────────────────────────────────────

    private FundingRoundResponse toListResponse(FundingRound round) {
        FundingRoundResponse resp = new FundingRoundResponse();
        resp.setId(round.getId());
        resp.setRoundName(round.getRoundName());
        resp.setDescription(round.getDescription());
        resp.setStatus(round.getStatus());
        resp.setEnrollmentOpenDate(round.getEnrollmentOpenDate());
        resp.setReservationOpenDate(round.getReservationOpenDate());
        resp.setCloseDate(round.getCloseDate());
        resp.setBoardApprovalDate(round.getBoardApprovalDate());
        resp.setTotalFundsAvailable(round.getTotalFundsAvailable());
        resp.setCreatedAt(round.getCreatedDate());
        resp.setUpdatedAt(round.getUpdatedDate());
        resp.setProgramAllocations(Collections.emptyList());
        resp.setEstimatedUtilizationPercent(BigDecimal.valueOf(68));
        resp.setEstimatedExhaustionDate(computeExhaustionDate(round));
        resp.setRiskLevel(computeRiskLevel(round));
        resp.setComplianceRisk("Low");
        return resp;
    }

    private FundingRoundResponse toDetailResponse(FundingRound round) {
        FundingRoundResponse resp = toListResponse(round);
        resp.setUseAcquisitionCosts(round.isUseAcquisitionCosts());
        resp.setUseRehabilitation(round.isUseRehabilitation());
        resp.setUseOther(round.isUseOther());
        resp.setOtherUseDescription(round.getOtherUseDescription());
        resp.setReservationCloseMethod(round.getReservationCloseMethod());
        resp.setIndividualSubsidyMinEnabled(round.isIndividualSubsidyMinEnabled());
        resp.setIndividualSubsidyMin(round.getIndividualSubsidyMin());
        resp.setIndividualSubsidyMaxEnabled(round.isIndividualSubsidyMaxEnabled());
        resp.setIndividualSubsidyMax(round.getIndividualSubsidyMax());
        resp.setLimitFundsPerMember(round.isLimitFundsPerMember());
        resp.setMemberFundLimit(round.getMemberFundLimit());
        resp.setDefaultLimitPerMember(round.getDefaultLimitPerMember());
        resp.setAllocationDrawdownEvent(round.getAllocationDrawdownEvent());
        resp.setReservationCompletionExpirationDays(round.getReservationCompletionExpirationDays());
        resp.setReservationExpirationDays(round.getReservationExpirationDays());
        resp.setDisbursementSubmissionExpirationDays(round.getDisbursementSubmissionExpirationDays());
        resp.setReminderEmailCompletionDays(round.getReminderEmailCompletionDays());
        resp.setReminderEmailExpirationDays(round.getReminderEmailExpirationDays());

        if (round.getProgramAllocations() != null) {
            List<FundingRoundResponse.ProgramAllocationResponse> paList =
                    round.getProgramAllocations().stream().map(pa -> {
                        FundingRoundResponse.ProgramAllocationResponse par =
                                new FundingRoundResponse.ProgramAllocationResponse();
                        par.setId(pa.getId());
                        par.setProgramName(pa.getProgramName());
                        par.setAllocationPercentage(pa.getAllocationPercentage());
                        par.setAllocationAmount(pa.getAllocationAmount());
                        return par;
                    }).collect(Collectors.toList());
            resp.setProgramAllocations(paList);
        }
        return resp;
    }

    private FundingRound mapToEntity(FundingRound round, FundingRoundRequest req) {
        round.setRoundName(req.getRoundName());
        round.setDescription(req.getDescription());
        round.setUseAcquisitionCosts(req.isUseAcquisitionCosts());
        round.setUseRehabilitation(req.isUseRehabilitation());
        round.setUseOther(req.isUseOther());
        round.setOtherUseDescription(req.getOtherUseDescription());
        round.setEnrollmentOpenDate(req.getEnrollmentOpenDate());
        round.setReservationOpenDate(req.getReservationOpenDate());
        round.setReservationCloseMethod(req.getReservationCloseMethod());
        round.setCloseDate(req.getCloseDate());
        round.setBoardApprovalDate(req.getBoardApprovalDate());
        round.setTotalFundsAvailable(req.getTotalFundsAvailable());
        round.setIndividualSubsidyMinEnabled(req.isIndividualSubsidyMinEnabled());
        round.setIndividualSubsidyMin(req.getIndividualSubsidyMin());
        round.setIndividualSubsidyMaxEnabled(req.isIndividualSubsidyMaxEnabled());
        round.setIndividualSubsidyMax(req.getIndividualSubsidyMax());
        round.setLimitFundsPerMember(req.isLimitFundsPerMember());
        round.setMemberFundLimit(req.getMemberFundLimit());
        round.setDefaultLimitPerMember(req.getDefaultLimitPerMember());
        round.setAllocationDrawdownEvent(req.getAllocationDrawdownEvent());
        round.setReservationCompletionExpirationDays(req.getReservationCompletionExpirationDays());
        round.setReservationExpirationDays(req.getReservationExpirationDays());
        round.setDisbursementSubmissionExpirationDays(req.getDisbursementSubmissionExpirationDays());
        round.setReminderEmailCompletionDays(req.getReminderEmailCompletionDays());
        round.setReminderEmailExpirationDays(req.getReminderEmailExpirationDays());

        if (req.getProgramAllocations() != null) {
            List<ProgramAllocation> allocations = new ArrayList<>();
            for (FundingRoundRequest.ProgramAllocationRequest pa : req.getProgramAllocations()) {
                BigDecimal amount = computeAllocationAmount(
                        req.getTotalFundsAvailable(), pa.getAllocationPercentage());
                ProgramAllocation alloc = ProgramAllocation.builder()
                        .fundingRound(round)
                        .programName(pa.getProgramName())
                        .allocationPercentage(pa.getAllocationPercentage())
                        .allocationAmount(amount)
                        .build();
                allocations.add(alloc);
            }
            round.getProgramAllocations().addAll(allocations);
        }
        return round;
    }

    private BigDecimal computeAllocationAmount(BigDecimal total, BigDecimal pct) {
        if (total == null || pct == null) return BigDecimal.ZERO;
        return total.multiply(pct)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
    }

    private LocalDate computeExhaustionDate(FundingRound round) {
        if (round.getEnrollmentOpenDate() != null) {
            return round.getEnrollmentOpenDate().plusDays(323);
        }
        return LocalDate.now().plusDays(323);
    }

    private String computeRiskLevel(FundingRound round) {
        return round.getBoardApprovalDate() == null ? "Moderate" : "Low";
    }
}
