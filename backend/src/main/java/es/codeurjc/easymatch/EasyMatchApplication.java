package es.codeurjc.easymatch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "es.codeurjc")
@EnableJpaRepositories(basePackages = "es.codeurjc.repository")
@EntityScan(basePackages = "es.codeurjc.model")

public class EasyMatchApplication {

	public static void main(String[] args) {
		SpringApplication.run(EasyMatchApplication.class, args);
	}

}
