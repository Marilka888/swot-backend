package ru.marilka.swotbackend.alt;

import java.util.List;

public class FuzzyAnalysisRunner {

    record Trapezoid(double a, double b, double c, double d) {}

    public static void main(String[] args) {
        // Проверка 1 — один эксперт
        List<Trapezoid> s1 = List.of(new Trapezoid(0.5, 0.63, 0.63, 0.87));
        List<Trapezoid> o1 = List.of(new Trapezoid(0.65, 0.73, 0.73, 0.91));
        List<Double> weights1 = List.of(1.0);

        runCase(s1, o1, weights1, 1.0, 1.0, "S1-O1 full");
        runCase(s1, o1, weights1, 0.8, 0.9, "S1-O1 80%/90%");
        runCase(s1, o1, weights1, 0.6, 0.95, "S1-O1 60%/95%");

        // Проверка 2 — три эксперта
        List<Trapezoid> s1Group = List.of(
                new Trapezoid(0.6, 0.72, 0.72, 0.9),   // A
                new Trapezoid(0.5, 0.63, 0.63, 0.87), // B
                new Trapezoid(0.75, 0.82, 0.82, 0.93) // C
        );
        List<Trapezoid> o1Group = List.of(
                new Trapezoid(0.7, 0.85, 0.85, 0.95), // A
                new Trapezoid(0.65, 0.73, 0.73, 0.91),// B
                new Trapezoid(0.7, 0.8, 0.8, 0.9)     // C
        );
        List<Double> weights2 = List.of(0.2, 0.5, 0.3);

        runCase(s1Group, o1Group, weights2, 1.0, 1.0, "Group full");
        runCase(s1Group, o1Group, weights2, 0.8, 0.9, "Group 80%/90%");
        runCase(s1Group, o1Group, weights2, 0.9, 0.65, "Group 90%/65%");

        // Проверка 3 — W1–T1 варианты
        List<Trapezoid> w1_1 = List.of(new Trapezoid(0.55, 0.65, 0.65, 0.71));
        List<Trapezoid> t1_1 = List.of(new Trapezoid(0.62, 0.75, 0.75, 0.85));
        List<Trapezoid> w1_2 = List.of(new Trapezoid(0.15, 0.2, 0.2, 0.35));
        List<Trapezoid> t1_2 = List.of(new Trapezoid(0.62, 0.75, 0.75, 0.85));
        List<Trapezoid> w1_3 = List.of(new Trapezoid(0.15, 0.2, 0.2, 0.35));
        List<Trapezoid> t1_3 = List.of(new Trapezoid(0.25, 0.32, 0.32, 0.43));

        runCase(w1_1, t1_1, weights1, 1.0, 1.0, "W1-T1 opt1 full");
        runCase(w1_2, t1_2, weights1, 1.0, 1.0, "W1-T1 opt2 full");
        runCase(w1_3, t1_3, weights1, 1.0, 1.0, "W1-T1 opt3 full");
        runCase(w1_1, t1_1, weights1, 0.8, 0.9, "W1-T1 opt1 80%/90%");
        runCase(w1_2, t1_2, weights1, 0.8, 0.9, "W1-T1 opt2 80%/90%");
        runCase(w1_3, t1_3, weights1, 0.8, 0.9, "W1-T1 opt3 80%/90%");
        runCase(w1_1, t1_1, weights1, 0.6, 0.5, "W1-T1 opt1 60%/50%");
        runCase(w1_2, t1_2, weights1, 0.6, 0.5, "W1-T1 opt2 60%/50%");
        runCase(w1_3, t1_3, weights1, 0.6, 0.5, "W1-T1 opt3 60%/50%");
    }

    /**
     *
     * @param f1 - список весов фактора 1
     * @param f2 - список весов фактора 2
     * @param weights - веса пользователей, которые поставили оценки
     * @param reveal1 - степень раскрытия фактора 1
     * @param reveal2 - степень раскрытия фактора 2
     */
    private static void runCase(
            List<Trapezoid> f1, List<Trapezoid> f2,
            List<Double> weights,
            double reveal1, double reveal2,
            String label
    ) {
        System.out.println("=== " + label + " ===");
        List<Double> alphas = List.of(0.1, 0.5, 0.9);
        for (double alpha : alphas) {
            double x01 = 0, y01 = 0;
            for (int i = 0; i < weights.size(); i++) {
                x01 += weights.get(i) * alphaCut(f1.get(i), alpha);
                y01 += weights.get(i) * alphaCut(f2.get(i), alpha);
            }

            x01 *= reveal1;
            y01 *= reveal2;

            // оставляем координаты в [0, 1]
            double x = x01;
            double y = y01;

            // PIS = (1,1), NIS = (0,0)
            double dPlus = Math.sqrt(Math.pow(1 - x, 2) + Math.pow(1 - y, 2));
            double dMinus = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
            double closeness = dMinus / (dPlus + dMinus);

            System.out.printf("α = %.1f  d+ = %.6f  d- = %.6f  d* = %.6f%n",
                    alpha, dPlus, dMinus, closeness);
        }
        System.out.println();
    }

    /**
     * Находит центр α-среза для трапециевидного нечеткого числа
     * @param t - нечеткое число
     * @param alpha - срез
     * @return центр среза
     */
    private static double alphaCut(Trapezoid t, double alpha) {
        double left = t.a + (t.b - t.a) * alpha;
        double right = t.d - (t.d - t.c) * alpha;
        return (left + right) / 2.0;
    }
}
