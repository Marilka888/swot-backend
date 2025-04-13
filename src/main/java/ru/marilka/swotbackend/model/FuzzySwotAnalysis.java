package ru.marilka.swotbackend.model;

import java.util.List;

public record FuzzySwotAnalysis(
        List<SwotFactor> strengths,
        List<SwotFactor> weaknesses,
        List<SwotFactor> opportunities,
        List<SwotFactor> threats
) {
}


public class FuzzySWOTAnalysis {

    private List<SwotFactor> getAllInternalFactors() {
        List<SwotFactor> internals = new ArrayList<>(strengths);
        internals.addAll(weaknesses);
        return internals;
    }

    private List<SwotFactor> getAllExternalFactors() {
        List<SwotFactor> externals = new ArrayList<>(opportunities);
        externals.addAll(threats);
        return externals;
    }

    public Map<String, Double> analyze(double alpha) {
        Map<String, Double> strategyScores = new HashMap<>();

        List<SwotFactor> internals = getAllInternalFactors();
        List<SwotFactor> externals = getAllExternalFactors();

        for (SwotFactor internal : internals) {
            for (SwotFactor external : externals) {
                // Calculate centroid (simplified)
                double x = (internal.getFuzzyNumber().getA() + internal.getFuzzyNumber().getD()) / 2;
                double y = (external.getFuzzyNumber().getA() + external.getFuzzyNumber().getD()) / 2;

                // Calculate closeness coefficient
                double dPlus = Math.sqrt(Math.pow(10 - x, 2) + Math.pow(10 - y, 2));
                double dMinus = Math.sqrt(Math.pow(-10 - x, 2) + Math.pow(-10 - y, 2));
                double c = dMinus / (dMinus + dPlus);

                String key = internal.getId() + "+" + external.getId();
                strategyScores.put(key, c);
            }
        }

        return strategyScores.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new));
    }

    public static void main(String[] args) {
        FuzzySWOTAnalysis analysis = new FuzzySWOTAnalysis();

        // Добавляем факторы (пример из статьи)
        analysis.addStrength(new SwotFactor("S1", "Great relationship",
                new TrapezoidalFuzzyNumber(7, 8, 8, 9)));
        analysis.addStrength(new SwotFactor("S2", "Team work culture",
                new TrapezoidalFuzzyNumber(6, 7, 7, 9)));

        analysis.addWeakness(new SwotFactor("W1", "Design delays",
                new TrapezoidalFuzzyNumber(-9, -7, -7, -5)));
        analysis.addWeakness(new SwotFactor("W2", "Low motivation",
                new TrapezoidalFuzzyNumber(-9, -7, -7, -4)));

        analysis.addOpportunity(new SwotFactor("O1", "Profitable market",
                new TrapezoidalFuzzyNumber(6, 8, 8, 9)));
        analysis.addOpportunity(new SwotFactor("O2", "University support",
                new TrapezoidalFuzzyNumber(2, 4, 4, 6)));

        analysis.addThreat(new SwotFactor("T1", "Price pressure",
                new TrapezoidalFuzzyNumber(-7, -6, -6, -3)));
        analysis.addThreat(new SwotFactor("T2", "IT threats",
                new TrapezoidalFuzzyNumber(2, 4, 4, 6)));

        // Анализ с разными уровнями α
        double[] alphas = {0.1, 0.5, 0.9};
        for (double alpha : alphas) {
            System.out.println("\nResults for alpha = " + alpha);
            Map<String, Double> results = analysis.analyze(alpha);

            results.forEach((key, value) ->
                    System.out.printf("%s: %.4f%n", key, value));
        }

        // Пример конкретной стратегии
        System.out.println("\nExample strategy evaluation:");
        TrapezoidalFuzzyNumber internal = new TrapezoidalFuzzyNumber(7, 8, 8, 9);
        TrapezoidalFuzzyNumber external = new TrapezoidalFuzzyNumber(6, 8, 8, 9);

        double x = (internal.getA() + internal.getD()) / 2;
        double y = (external.getA() + external.getD()) / 2;
        double c = Math.sqrt(Math.pow(-10 - x, 2) + Math.pow(-10 - y, 2)) /
                (Math.sqrt(Math.pow(10 - x, 2) + Math.pow(10 - y, 2)) +
                        Math.sqrt(Math.pow(-10 - x, 2) + Math.pow(-10 - y, 2)));

        System.out.printf("Strategy score: %.4f%n", c);
    }
}