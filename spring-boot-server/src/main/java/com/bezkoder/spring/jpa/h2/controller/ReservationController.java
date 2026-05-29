package com.bezkoder.spring.jpa.h2.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.bezkoder.spring.jpa.h2.model.Reservation;
import com.bezkoder.spring.jpa.h2.repository.ReservationRepository;
import com.bezkoder.spring.jpa.h2.exception.ResourceNotFoundException;

/**
 * REST controller for Reservations.
 *
 * <p>Note: CORS is handled globally in {@code SecurityConfig}.
 * Do NOT add {@code @CrossOrigin} here.</p>
 */
@RestController
@RequestMapping("/api")
public class ReservationController {

    @Autowired
    ReservationRepository reservationRepository;

    @Autowired
    com.bezkoder.spring.jpa.h2.repository.RoundRepository roundRepository;

    @GetMapping("/reservations")
    public ResponseEntity<List<Reservation>> getAllReservations() {
        List<Reservation> reservations = reservationRepository.findAll();
        if (reservations.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(reservations, HttpStatus.OK);
    }

    @GetMapping("/reservations/{id}")
    public ResponseEntity<Reservation> getReservationById(@PathVariable("id") long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Not found Reservation with id = " + id));
        return new ResponseEntity<>(reservation, HttpStatus.OK);
    }

    @GetMapping("/rounds/{roundId}/reservations")
    public ResponseEntity<List<Reservation>> getAllReservationsByRoundId(
            @PathVariable(value = "roundId") Long roundId) {
        List<Reservation> reservations = reservationRepository.findByRoundId(roundId);
        return new ResponseEntity<>(reservations, HttpStatus.OK);
    }

    @PostMapping("/rounds/{roundId}/reservations")
    public ResponseEntity<Reservation> createReservation(
            @PathVariable(value = "roundId") Long roundId,
            @RequestBody Reservation reservationRequest) {
        return roundRepository.findById(roundId).map(round -> {
            reservationRequest.setRound(round);
            Reservation _reservation = reservationRepository.save(reservationRequest);
            return new ResponseEntity<>(_reservation, HttpStatus.CREATED);
        }).orElseThrow(() -> new ResourceNotFoundException("Not found Round with id = " + roundId));
    }

    @PutMapping("/reservations/{id}")
    public ResponseEntity<Reservation> updateReservation(
            @PathVariable("id") long id,
            @RequestBody Reservation reservationRequest) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Not found Reservation with id = " + id));
        reservation.setReservationIdCode(reservationRequest.getReservationIdCode());
        reservation.setMemberName(reservationRequest.getMemberName());
        reservation.setAmount(reservationRequest.getAmount());
        reservation.setStatus(reservationRequest.getStatus());
        return new ResponseEntity<>(reservationRepository.save(reservation), HttpStatus.OK);
    }

    @DeleteMapping("/reservations/{id}")
    public ResponseEntity<HttpStatus> deleteReservation(@PathVariable("id") long id) {
        if (!reservationRepository.existsById(id)) {
            throw new ResourceNotFoundException("Not found Reservation with id = " + id);
        }
        reservationRepository.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/reservations")
    public ResponseEntity<HttpStatus> deleteAllReservations() {
        reservationRepository.deleteAll();
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
