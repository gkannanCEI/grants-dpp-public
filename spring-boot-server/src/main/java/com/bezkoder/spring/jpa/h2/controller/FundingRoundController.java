package com.bezkoder.spring.jpa.h2.controller;

import com.bezkoder.spring.jpa.h2.dto.FundingRoundRequest;
import com.bezkoder.spring.jpa.h2.dto.FundingRoundResponse;
import com.bezkoder.spring.jpa.h2.service.FundingRoundService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * REST controller for Funding Intelligence — Funding Rounds.
 *
 * <p>Base URL: /api/funding-rounds</p>
 *
 * <p>Note: CORS is handled globally in {@code SecurityConfig#corsConfigurationSource()}.
 * Do NOT add {@code @CrossOrigin} here as it conflicts with
 * {@code allowCredentials=true} set in the global CORS policy.</p>
 */
@RestController
@RequestMapping("/api/funding-rounds")
public class FundingRoundController {

    private final FundingRoundService fundingRoundService;

    public FundingRoundController(FundingRoundService fundingRoundService) {
        this.fundingRoundService = fundingRoundService;
    }

    // ── GET /api/funding-rounds ─────────────────────────────────────────────
    @GetMapping
    public ResponseEntity<List<FundingRoundResponse>> getAll(
            @RequestParam(required = false) String status) {
        List<FundingRoundResponse> list = (status != null && !status.isBlank())
                ? fundingRoundService.findByStatus(status)
                : fundingRoundService.findAll();
        return ResponseEntity.ok(list);
    }

    // ── GET /api/funding-rounds/{id} ────────────────────────────────────────
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(fundingRoundService.findById(id));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", e.getMessage()));
        }
    }

    // ── POST /api/funding-rounds ────────────────────────────────────────────
    @PostMapping
    public ResponseEntity<?> create(@RequestBody FundingRoundRequest request) {
        try {
            FundingRoundResponse created = fundingRoundService.create(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", e.getMessage()));
        }
    }

    // ── PUT /api/funding-rounds/{id} ────────────────────────────────────────
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id,
                                    @RequestBody FundingRoundRequest request) {
        try {
            return ResponseEntity.ok(fundingRoundService.update(id, request));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", e.getMessage()));
        }
    }

    // ── POST /api/funding-rounds/{id}/submit ───────────────────────────────
    @PostMapping("/{id}/submit")
    public ResponseEntity<?> submitForApproval(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(fundingRoundService.submitForApproval(id));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", e.getMessage()));
        }
    }
}
