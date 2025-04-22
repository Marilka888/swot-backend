package ru.marilka.swotbackend.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import ru.marilka.swotbackend.model.FuzzyWeights;

@Entity
@Getter
@Setter
public class SwotFactor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String type;
    private String sessionId;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "min", column = @Column(name = "weight_min")),
            @AttributeOverride(name = "max", column = @Column(name = "weight_max")),
            @AttributeOverride(name = "value", column = @Column(name = "weight_value"))
    })
    private FuzzyWeights weights;
}
