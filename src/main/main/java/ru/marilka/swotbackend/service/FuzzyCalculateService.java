package ru.marilka.swotbackend.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.marilka.swotbackend.model.Result;
import ru.marilka.swotbackend.model.Strategy;
import ru.marilka.swotbackend.model.TrapezoidalFuzzyNumber;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class FuzzyCalculateService {

    public static double closenessCoefficient(double x, double y) {
        double dPlus = Math.sqrt(Math.pow(10 - x, 2) + Math.pow(10 - y, 2));
        double dMinus = Math.sqrt(Math.pow(1 - x, 2) + Math.pow(1 - y, 2));
        return dMinus / (dMinus + dPlus);
    }

    public static void main(String[] args) {
        // Примерные данные
        List<TrapezoidalFuzzyNumber> strengths = List.of(
                new TrapezoidalFuzzyNumber(7, 8, 9, 9.5),
                new TrapezoidalFuzzyNumber(6, 7, 8, 8.5)
        );

        List<TrapezoidalFuzzyNumber> weaknesses = List.of(
                new TrapezoidalFuzzyNumber(2, 3, 4, 4.5),
                new TrapezoidalFuzzyNumber(1, 2, 3, 3.5)
        );

        List<TrapezoidalFuzzyNumber> opportunities = List.of(
                new TrapezoidalFuzzyNumber(6, 7, 8, 9),
                new TrapezoidalFuzzyNumber(5, 6, 7, 8)
        );

        List<TrapezoidalFuzzyNumber> threats = List.of(
                new TrapezoidalFuzzyNumber(3, 4, 5, 6),
                new TrapezoidalFuzzyNumber(2, 3, 4, 5)
        );

        List<Double> alphas = List.of(0.1, 0.5, 0.9);
        List<Double> weights = List.of(0.2, 0.3, 0.5);

        List<Result> allStrategies = new ArrayList<>();

        // ST (Strength + Threat)
        for (int i = 0; i < strengths.size(); i++) {
            for (int j = 0; j < threats.size(); j++) {
                String name = "Использовать сильную сторону S" + (i + 1) + " для снижения угрозы T" + (j + 1);
                Strategy strategy = new Strategy(name, strengths.get(i), threats.get(j));
                double score = calculatePriority(strategy, alphas, weights);
                allStrategies.add(new Result(strategy.name(), score));
            }
        }

        // WO (Weakness + Opportunity)
        for (int i = 0; i < weaknesses.size(); i++) {
            for (int j = 0; j < opportunities.size(); j++) {
                String name = "Компенсировать слабость W" + (i + 1) + " за счёт возможности O" + (j + 1);
                Strategy strategy = new Strategy(name, weaknesses.get(i), opportunities.get(j));
                double score = calculatePriority(strategy, alphas, weights);
                allStrategies.add(new Result(strategy.name(), score));
            }
        }

        // Сортировка по приоритету
        allStrategies.sort((a, b) -> Double.compare(b.score(), a.score()));

        // Вывод
        System.out.println("📋 Приоритезация стратегий (по убыванию):");
        int rank = 1;
        for (Result r : allStrategies) {
            System.out.printf("%2d. %-60s | Score = %.4f%n", rank++, r.description(), r.score());
        }
    }

    static double calculatePriority(Strategy s, List<Double> alphas, List<Double> weights) {
        double total = 0.0;
        for (int i = 0; i < alphas.size(); i++) {
            double alpha = alphas.get(i);
            double xi = s.internal().alphaCutCenter(alpha);
            double yi = s.external().alphaCutCenter(alpha);
            double cc = closenessCoefficient(xi, yi);
            total += cc * weights.get(i);
        }
        return total;
    }
}
