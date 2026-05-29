package com.bezkoder.spring.jpa.h2.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@Table(name = "rounds")
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Round extends AuditModel {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(name = "round_name")
    private String roundName;

    @Column(name = "program_type")
    private String programType;

    @Column(name = "status")
    private String status;

    @Column(name = "round_open_date")
    private LocalDate roundOpenDate;

    @Column(name = "round_close_date")
    private LocalDate roundCloseDate;

    @Column(name = "days_to_exhaustion")
    private Integer daysToExhaustion;

    @Column(name = "total_funds")
    private Double totalFunds;

    @Column(name = "description")
    private String description;

    @Column(name = "reservation_velocity_target")
    private String reservationVelocityTarget;

    @Column(name = "compliance_threshold")
    private Double complianceThreshold;

    @OneToMany(mappedBy = "round", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<Reservation> reservations;
}
