package ru.hits.fitnesssystem.core.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Builder
@RequiredArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "approaches")
public class Approach {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "approaches_count", nullable = false)
    private BigDecimal approachesCount;

    @Column(name = "repetition_per_aproach_count", nullable = false)
    private BigDecimal repetitionPerApproachCount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trainer_id", nullable = false)
    private User trainer;
}