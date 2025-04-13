package ru.marilka.swotbackend.model;

import java.util.List;

/**
 * Трапецевидное нечеткое число
 *
 * @param leftLowerBoundary   - левая нижняя граница
 * @param leftUpperBoundary   - левая верхняя граница
 * @param rightUpperBoundary  - правая верхняя граница
 * @param rightBottomBoundary -правая нижняя граница
 */
public record TrapezoidalFuzzyNumber(
        double leftLowerBoundary,
        double leftUpperBoundary,
        double rightUpperBoundary,
        double rightBottomBoundary
) {
    public static TrapezoidalFuzzyNumber weightedAverage(
            List<TrapezoidalFuzzyNumber> values,
            List<Double> weights
    ) {
        if (values.size() != weights.size()) {
            throw new IllegalArgumentException("Размеры списков оценок и весов не совпадают.");
        }

        double sumWeights = weights.stream().mapToDouble(Double::doubleValue).sum();

        double a = 0, b = 0, c = 0, d = 0;
        for (int i = 0; i < values.size(); i++) {
            double w = weights.get(i) / sumWeights; // нормализуем веса
            TrapezoidalFuzzyNumber t = values.get(i);
            a += t.leftLowerBoundary() * w;
            b += t.leftUpperBoundary() * w;
            c += t.rightUpperBoundary() * w;
            d += t.rightBottomBoundary() * w;
        }

        return new TrapezoidalFuzzyNumber(a, b, c, d);
    }

    public double alphaCutCenter(double alpha) {
        double left = alpha * (leftUpperBoundary - leftLowerBoundary) + leftLowerBoundary;
        double right = rightBottomBoundary - alpha * (rightBottomBoundary - rightUpperBoundary);
        return (left + right) / 2.0;
    }
}
