package io.github.css12345.sourceanalyse.persistence.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;

import io.github.css12345.sourceanalyse.persistence.entity.FileInformation;
import io.github.css12345.sourceanalyse.persistence.entity.Method;
import io.github.css12345.sourceanalyse.persistence.utils.ConverterUtils;

@SpringBootTest
public class TestFileInformationRepository {

	@SpringBootApplication(scanBasePackages = "io.github.css12345.sourceanalyse")
	@EnableNeo4jRepositories(basePackageClasses = MethodRepository.class)
	@EntityScan(basePackageClasses = Method.class)
	@PropertySource("settings.properties")
	static class Config {

	}

	@Autowired
	private FileInformationRepository fileInformationRepository;

	@Autowired
	private MethodRepository methodRepository;

	@Autowired
	private ASTNodeRepository astNodeRepository;

	@Autowired
	private ConverterUtils converterUtils;

	private FileInformation fileInformation;

	@BeforeEach
	public void setUpMethod() {
		fileInformationRepository.deleteAll();
		methodRepository.deleteAll();
		astNodeRepository.deleteAll();

		fileInformation = converterUtils
				.convert("D:\\tmp\\0.0.1\\download\\src\\main\\java\\com\\example\\download\\Main.java");
	}

	@Test
	public void testSave() {
		fileInformationRepository.save(fileInformation);
	}
}
