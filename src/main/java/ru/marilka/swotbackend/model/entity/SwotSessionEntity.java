package ru.marilka.swotbackend.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import ru.marilka.swotbackend.model.SwotSummaryDto;

import java.util.List;
import java.util.Map;

import jakarta.persistence.*;

import java.util.List;
import java.util.Map;

@Entity
@Getter
@Setter
public class SwotSessionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(name = "user_id")
    private String userId;

    @ElementCollection
    @CollectionTable(name = "swot_factors", joinColumns = @JoinColumn(name = "session_id"))
    private List<FactorEntry> factors;

    @ElementCollection
    @CollectionTable(name = "swot_factor_numbers", joinColumns = @JoinColumn(name = "session_id"))
    private List<FactorEntry> factorNumbers;


    @Lob
    @Column(name = "summary_json", columnDefinition = "TEXT")
    private String summaryJson; // хранение сериализованного SwotSummaryDto


}
