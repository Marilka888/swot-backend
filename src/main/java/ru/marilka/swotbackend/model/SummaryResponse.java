package ru.marilka.swotbackend.model;

import java.util.List;
import java.util.Map;

public class SummaryResponse {
    public String sessionName;
    public Map<String, List<String>> factors;
    public Map<String, List<String>> factorNumbers;
    public List<AlternativeDTO> alternatives;

    // вложенный DTO
    public static class AlternativeDTO {
        public String factor1;
        public String factor2;
        public double d_plus;
        public double d_minus;
        public double d_star;
    }
}
