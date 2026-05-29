package com.bezkoder.spring.jpa.h2.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Table(name = "reservations")
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Reservation extends AuditModel {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(name = "reservation_id_code")
    private String reservationIdCode;

    @Column(name = "member_name")
    private String memberName;

    @Column(name = "amount")
    private Double amount;

    @Column(name = "status")
    private String status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "round_id")
    @JsonBackReference
    private Round round;
}
