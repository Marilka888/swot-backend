package ru.marilka.swotbackend.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import ru.marilka.swotbackend.model.SwotFactor;
import ru.marilka.swotbackend.model.TrapezoidalFuzzyNumber;
import ru.marilka.swotbackend.service.FuzzyCalculateService;
import ru.marilka.swotbackend.service.FuzzySWOTAnalysis;
import ru.marilka.swotbackend.service.SwotService;

import java.util.Arrays;
import java.util.List;

@RestController
@AllArgsConstructor
public class SwotController {
    private final SwotService swotService;
    private final FuzzyCalculateService fuzzyCalculateService;


    public static void calculate() {
        FuzzySWOTAnalysis fuzzySWOT = new FuzzySWOTAnalysis();


        // Добавление внутренних факторов с трапециевидными числами
        fuzzySWOT.addInternalFactor(new SwotFactor("I1", "Great and effective relationship",
                new TrapezoidalFuzzyNumber(6, 7, 8, 9))); // a=6, b=7, c=8, d=9
        fuzzySWOT.addInternalFactor(new SwotFactor("I2", "Great team work culture",
                new TrapezoidalFuzzyNumber(5, 6, 7, 9)));

        // Добавление внешних факторов с трапециевидными числами
        fuzzySWOT.addExternalFactor(new SwotFactor("E3", "Profitable market",
                new TrapezoidalFuzzyNumber(5, 6, 8, 9)));
        fuzzySWOT.addExternalFactor(new SwotFactor("E6", "Universities' capabilities",
                new TrapezoidalFuzzyNumber(1, 2, 4, 6)));

        // Дефаззификация с разными α
        List<Double> alphaValues = Arrays.asList(0.1, 0.5, 0.9);
        for (double alpha : alphaValues) {
            List<Double> closenessCoefficients = fuzzySWOT.defuzzify(alpha);
            System.out.printf("Alpha = %.1f, Closeness Coefficients: %s%n",
                    alpha, closenessCoefficients);
        }

        // Пример приоритизации стратегий
        List<String> strategies = Arrays.asList(
                "Monopolizing the designing and supplying products",
                "Mobile based products and services development",
                "Out sourcing design of products to universities");

        // Пример коэффициентов близости
        List<Double> sampleCoefficients = Arrays.asList(0.85, 0.78, 0.92);

        System.out.println("\nPrioritized Strategies:");
        fuzzySWOT.prioritizeStrategies(strategies, sampleCoefficients)
                .forEach((strategy, coeff) ->
                        System.out.printf("%s : %.4f%n", strategy, coeff));

        // Визуализация функции принадлежности для примера
        System.out.println("\nMembership function values for I1 (6,7,8,9):");
        TrapezoidalFuzzyNumber example = new TrapezoidalFuzzyNumber(6, 7, 8, 9);
        for (double x = 5; x <= 10; x += 0.5) {
            System.out.printf("μ(%.1f) = %.2f%n", x, example.membership(x));
        }
    }
}

