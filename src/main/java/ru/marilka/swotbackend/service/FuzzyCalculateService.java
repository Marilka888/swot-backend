package ru.marilka.swotbackend.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.marilka.swotbackend.model.SwotFactor;
import ru.marilka.swotbackend.model.TrapezoidalFuzzyNumber;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class FuzzyCalculateService {

    public Map<String, Double> analyze(double alpha) {
        Map<String, Double> strategyScores = new HashMap<>();

        List<SwotFactor> internals = getAllInternalFactors();
        List<SwotFactor> externals = getAllExternalFactors();

        for (SwotFactor internal : internals) {
            for (SwotFactor external : externals) {
                // Calculate centroid (simplified)
                double x = (internal.fuzzyNumber().leftLowerBoundary() + internal.fuzzyNumber().rightBottomBoundary()) / 2;
                double y = (external.fuzzyNumber().leftLowerBoundary() + external.fuzzyNumber().rightBottomBoundary()) / 2;

                // Calculate closeness coefficient
                double dPlus = Math.sqrt(Math.pow(10 - x, 2) + Math.pow(10 - y, 2));
                double dMinus = Math.sqrt(Math.pow(-10 - x, 2) + Math.pow(-10 - y, 2));
                double c = dMinus / (dMinus + dPlus);

                String key = internal.id() + "+" + external.id();
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









    // Агрегация внутренних и внешних факторов (минимальное значение функций принадлежности)
    public double aggregateMembership(SwotFactor internal, SwotFactor external, double x, double y) {
        var muInternal = internal.fuzzyNumber().membership(x);
        var muExternal = external.fuzzyNumber().membership(y);
        return Math.min(muInternal, muExternal);
    }

    // Дефаззификация с использованием α-среза
    public List<Double> defuzzify(double alpha) {
        List<Double> closenessCoefficients = new ArrayList<>();

        for (SwotFactor internal : internalFactors) {
            for (SwotFactor external : externalFactors) {
                // Рассчитываем центр тяжести для трапециевидного числа
                TrapezoidalFuzzyNumber intFuzzy = internal.getFuzzyNumber();
                TrapezoidalFuzzyNumber extFuzzy = external.getFuzzyNumber();

                // Центр тяжести по X (для внутреннего фактора)
                double x_cg = (intFuzzy.getA() + intFuzzy.getB() + intFuzzy.getC() + intFuzzy.getD()) / 4;

                // Центр тяжести по Y (для внешнего фактора)
                double y_cg = (extFuzzy.getA() + extFuzzy.getB() + extFuzzy.getC() + extFuzzy.getD()) / 4;

                // Рассчитываем коэффициент близости
                double d_plus = Math.sqrt(Math.pow(10 - x_cg, 2) + Math.pow(10 - y_cg, 2));
                double d_minus = Math.sqrt(Math.pow(-10 - x_cg, 2) + Math.pow(-10 - y_cg, 2));
                double c_fj = d_minus / (d_minus + d_plus);

                closenessCoefficients.add(c_fj);
            }
        }

        return closenessCoefficients;
    }

    // Приоритизация стратегий
    public Map<String, Double> prioritizeStrategies(List<String> strategies, List<Double> closenessCoefficients) {
        if (strategies.size() != closenessCoefficients.size()) {
            throw new IllegalArgumentException("Strategies and coefficients lists must have the same size");
        }

        Map<String, Double> strategyPriorities = new HashMap<>();
        for (int i = 0; i < strategies.size(); i++) {
            strategyPriorities.put(strategies.get(i), closenessCoefficients.get(i));
        }

        // Сортировка по убыванию коэффициента близости
        return strategyPriorities.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .collect(LinkedHashMap::new, (m, e) -> m.put(e.getKey(), e.getValue()), LinkedHashMap::putAll);
    }
}
