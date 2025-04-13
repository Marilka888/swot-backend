package ru.marilka.swotbackend.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class SwotService {
    private double a; // левая нижняя граница
    private double b; // левая верхняя граница
    private double c; // правая верхняя граница
    private double d; // правая нижняя граница

    public TrapezoidalFuzzyNumber(double a, double b, double c, double d) {
        if (a > b || b > c || c > d) {
            throw new IllegalArgumentException("Parameters must satisfy a <= b <= c <= d");
        }
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
    }

    // Функция принадлежности для трапециевидного числа
    public double membership(double x) {
        if (x < a) return 0;
        if (a <= x && x < b) return (x - a) / (b - a);
        if (b <= x && x <= c) return 1;
        if (c < x && x <= d) return (d - x) / (d - c);
        return 0;
    }

    // Геттеры
    public double getA() { return a; }
    public double getB() { return b; }
    public double getC() { return c; }
    public double getD() { return d; }
}



// Основной класс для Fuzzy SWOT анализа
public class FuzzySWOTAnalysis {
    private List<SwotFactor> internalFactors;
    private List<SwotFactor> externalFactors;

    public FuzzySWOTAnalysis() {
        this.internalFactors = new ArrayList<>();
        this.externalFactors = new ArrayList<>();
    }

    // Добавление факторов
    public void addInternalFactor(SwotFactor factor) {
        internalFactors.add(factor);
    }

    public void addExternalFactor(SwotFactor factor) {
        externalFactors.add(factor);
    }

    // Агрегация внутренних и внешних факторов (минимальное значение функций принадлежности)
    public double aggregateMembership(SwotFactor internal, SwotFactor external, double x, double y) {
        double muInternal = internal.getFuzzyNumber().membership(x);
        double muExternal = external.getFuzzyNumber().membership(y);
        return Math.min(muInternal, muExternal);
    }



    public static void main(String[] args) {
        // Пример использования

    }
}
