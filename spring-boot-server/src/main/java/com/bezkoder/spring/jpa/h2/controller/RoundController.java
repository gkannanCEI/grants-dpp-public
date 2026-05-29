package com.bezkoder.spring.jpa.h2.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.bezkoder.spring.jpa.h2.model.Round;
import com.bezkoder.spring.jpa.h2.repository.RoundRepository;
import com.bezkoder.spring.jpa.h2.exception.ResourceNotFoundException;

/**
 * REST controller for Rounds.
 *
 * <p>Note: CORS is handled globally in {@code SecurityConfig}.
 * Do NOT add {@code @CrossOrigin} here.</p>
 */
@RestController
@RequestMapping("/api")
public class RoundController {

    @Autowired
    RoundRepository roundRepository;

    @GetMapping("/rounds")
    public ResponseEntity<List<Round>> getAllRounds() {
        List<Round> rounds = roundRepository.findAll();
        if (rounds.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(rounds, HttpStatus.OK);
    }

    @GetMapping("/rounds/{id}")
    public ResponseEntity<Round> getRoundById(@PathVariable("id") long id) {
        return roundRepository.findById(id)
                .map(round -> new ResponseEntity<>(round, HttpStatus.OK))
                .orElseThrow(() -> new ResourceNotFoundException("Not found Round with id = " + id));
    }

    @PostMapping("/rounds")
    public ResponseEntity<Round> createRound(@RequestBody Round round) {
        Round _round = roundRepository.save(round);
        return new ResponseEntity<>(_round, HttpStatus.CREATED);
    }

    @PutMapping("/rounds/{id}")
    public ResponseEntity<Round> updateRound(@PathVariable("id") long id, @RequestBody Round round) {
        return roundRepository.findById(id)
                .map(roundData -> {
                    roundData.setRoundName(round.getRoundName());
                    roundData.setProgramType(round.getProgramType());
                    roundData.setStatus(round.getStatus());
                    roundData.setRoundOpenDate(round.getRoundOpenDate());
                    roundData.setRoundCloseDate(round.getRoundCloseDate());
                    roundData.setTotalFunds(round.getTotalFunds());
                    roundData.setDescription(round.getDescription());
                    return new ResponseEntity<>(roundRepository.save(roundData), HttpStatus.OK);
                }).orElseThrow(() -> new ResourceNotFoundException("Not found Round with id = " + id));
    }

    @DeleteMapping("/rounds/{id}")
    public ResponseEntity<HttpStatus> deleteRound(@PathVariable("id") long id) {
        if (!roundRepository.existsById(id)) {
            throw new ResourceNotFoundException("Not found Round with id = " + id);
        }
        roundRepository.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
