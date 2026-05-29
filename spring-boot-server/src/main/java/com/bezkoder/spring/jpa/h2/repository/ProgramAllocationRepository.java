package com.bezkoder.spring.jpa.h2.repository;

import com.bezkoder.spring.jpa.h2.model.ProgramAllocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProgramAllocationRepository extends JpaRepository<ProgramAllocation, Long> {

    /** Retrieve all allocations belonging to a specific funding round. */
    List<ProgramAllocation> findByFundingRoundId(Long fundingRoundId);

    /** Remove all allocations for a funding round (used on full update). */
    void deleteByFundingRoundId(Long fundingRoundId);
}
