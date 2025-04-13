package ru.marilka.swotbackend.model.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Alternative {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String factor1;
    private String factor2;

    private double dMinus;
    private double dPlus;
    private double dStar;

    private Integer factor1Percentage;
    private Integer factor2Percentage;

    private String sessionId; // <== Add this
}
