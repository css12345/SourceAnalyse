package io.github.css12345.sourceanalyse.display;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;

import io.github.css12345.sourceanalyse.persistence.entity.FileInformation;
import io.github.css12345.sourceanalyse.persistence.repository.FileInformationRepository;

@SpringBootApplication(scanBasePackages = "io.github.css12345.sourceanalyse")
@EnableNeo4jRepositories(basePackageClasses = FileInformationRepository.class)
@EntityScan(basePackageClasses = FileInformation.class)
public class SourceAnalyseApplication {

	public static void main(String[] args) {
		SpringApplication.run(SourceAnalyseApplication.class, args);
	}

}
