package com.bezkoder.spring.jpa.h2.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.bezkoder.spring.jpa.h2.model.Round;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.repository.Lock;
import jakarta.persistence.LockModeType;
import java.util.Optional;

public interface RoundRepository extends JpaRepository<Round, Long> {
    @Transactional
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Round> findById(Long id);
}
