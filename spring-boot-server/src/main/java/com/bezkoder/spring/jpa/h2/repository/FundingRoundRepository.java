package com.bezkoder.spring.jpa.h2.repository;

import com.bezkoder.spring.jpa.h2.model.FundingRound;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for FundingRound entities.
 *
 * Performance strategy:
 *  - findAll() / findByStatus() do NOT load programAllocations (LAZY fetch).
 *    This means the list screen executes exactly 1 SQL query regardless of
 *    how many rounds exist — eliminating the N+1 problem.
 *
 *  - findWithAllocationsById() uses a single JOIN FETCH query so the detail
 *    screen loads the parent + all children in one round-trip.
 *
 *  - findAllWithAllocations() uses JOIN FETCH for cases where allocations
 *    are needed for all rows (e.g. export/report screens).
 */
@Repository
public interface FundingRoundRepository extends JpaRepository<FundingRound, Long> {

    // ── List queries (NO allocation join — fast for table view) ─────────────

    /** Returns all rounds ordered by creation date DESC — no allocation fetch. */
    @Query("SELECT f FROM FundingRound f ORDER BY f.createdDate DESC")
    List<FundingRound> findAllOrderedByCreatedDateDesc();

    /** Filter by lifecycle status — used for ACTIVE/DRAFT/CLOSED tabs. */
    @Query("SELECT f FROM FundingRound f WHERE f.status = :status ORDER BY f.createdDate DESC")
    List<FundingRound> findByStatus(String status);

    // ── Detail queries (WITH allocation join — single round-trip) ───────────

    /**
     * Loads a single FundingRound WITH its programAllocations in one JOIN FETCH.
     * Use this for detail/edit screens to avoid lazy-load proxy exceptions.
     */
    @Query("SELECT f FROM FundingRound f LEFT JOIN FETCH f.programAllocations WHERE f.id = :id")
    Optional<FundingRound> findWithAllocationsById(Long id);

    /**
     * Loads ALL FundingRounds WITH their allocations in a single query.
     * Use for export/report screens; avoid for normal list views.
     */
    @Query("SELECT DISTINCT f FROM FundingRound f LEFT JOIN FETCH f.programAllocations ORDER BY f.createdDate DESC")
    List<FundingRound> findAllWithAllocations();

    // ── Existence checks (index-backed — near-instant) ───────────────────────

    /** Ensure round names remain unique. */
    Optional<FundingRound> findByRoundName(String roundName);

    /** Check existence by name for duplicate validation. */
    boolean existsByRoundName(String roundName);
}
