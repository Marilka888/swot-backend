package ru.marilka.swotbackend.model;

import java.util.List;
import java.util.Map;

public class SwotSummaryDto {
    private String sessionName;
    private Map<String, List<String>> factors;
    private Map<String, List<String>> factorNumbers;
    private List<AlternativeDto> alternatives;

    public String getSessionName() {
        return sessionName;
    }

    public void setSessionName(String sessionName) {
        this.sessionName = sessionName;
    }

    public Map<String, List<String>> getFactors() {
        return factors;
    }

    public void setFactors(Map<String, List<String>> factors) {
        this.factors = factors;
    }

    public Map<String, List<String>> getFactorNumbers() {
        return factorNumbers;
    }

    public void setFactorNumbers(Map<String, List<String>> factorNumbers) {
        this.factorNumbers = factorNumbers;
    }

    public List<AlternativeDto> getAlternatives() {
        return alternatives;
    }

    public void setAlternatives(List<AlternativeDto> alternatives) {
        this.alternatives = alternatives;
    }

    public static class AlternativeDto {
        public String factor1;
        public String factor2;
        public double d_plus;
        public double d_minus;
        public double d_star;

        public String getFactor1() { return factor1; }
        public void setFactor1(String factor1) { this.factor1 = factor1; }

        public String getFactor2() { return factor2; }
        public void setFactor2(String factor2) { this.factor2 = factor2; }

        public double getD_plus() { return d_plus; }
        public void setD_plus(double d_plus) { this.d_plus = d_plus; }

        public double getD_minus() { return d_minus; }
        public void setD_minus(double d_minus) { this.d_minus = d_minus; }

        public double getD_star() { return d_star; }
        public void setD_star(double d_star) { this.d_star = d_star; }
    }
}

