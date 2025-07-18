package ru.marilka.swotbackend;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@OpenAPIDefinition(
		info = @Info(title = "SWOT Analysis API", version = "1.0", description = "API for managing fuzzy SWOT sessions")
)
@SpringBootApplication
public class SwotBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(SwotBackendApplication.class, args);
	}

}
