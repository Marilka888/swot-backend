package ru.marilka.swotbackend.model;

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
    public double membership(double x) {
        if (x < leftLowerBoundary) return 0;
        if (x < leftUpperBoundary) {
            return (x - leftLowerBoundary) / (leftUpperBoundary - leftLowerBoundary);
        }
        if (x <= rightUpperBoundary) return 1;
        if (x <= rightBottomBoundary) {
            return (rightBottomBoundary - x) / (rightBottomBoundary - rightUpperBoundary);
        }
        return 0;
    }
}
