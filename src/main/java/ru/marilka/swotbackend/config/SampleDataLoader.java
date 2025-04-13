package ru.marilka.swotbackend.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import ru.marilka.swotbackend.model.FuzzyWeights;
import ru.marilka.swotbackend.model.entity.SwotFactor;
import ru.marilka.swotbackend.repository.SwotFactorRepository;

import java.util.Arrays;
import java.util.List;

@Component
public class SampleDataLoader implements CommandLineRunner {

    private final SwotFactorRepository repo;

    public SampleDataLoader(SwotFactorRepository repo) {
        this.repo = repo;
    }

    @Override
    public void run(String... args) {
        if (repo.count() > 0) return; // Prevent duplicate inserts

        repo.saveAll(sampleFactors("strong", Arrays.asList("Сильная команда", "Хороший UX", "Гибкость архитектуры")));
        repo.saveAll(sampleFactors("weak", Arrays.asList("Мало пользователей", "Недостаток финансирования")));
        repo.saveAll(sampleFactors("opportunity", Arrays.asList("Рост рынка", "Гос. гранты")));
        repo.saveAll(sampleFactors("threat", Arrays.asList("Конкуренты", "Снижение интереса к теме")));
    }

    private List<SwotFactor> sampleFactors(String type, List<String> names) {
        return names.stream().map(name -> {
            SwotFactor f = new SwotFactor();
            f.setName(name);
            f.setType(type);



            return f;
        }).toList();
    }
}

