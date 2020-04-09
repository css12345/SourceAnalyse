package io.github.css12345.sourceanalyse.persistence.repository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;

import io.github.css12345.sourceanalyse.jdtparse.entity.FileInformationDTO;
import io.github.css12345.sourceanalyse.jdtparse.entity.Project;
import io.github.css12345.sourceanalyse.jdtparse.support.FileInformationResolver;
import io.github.css12345.sourceanalyse.persistence.entity.Method;
import io.github.css12345.sourceanalyse.persistence.support.FileInformationDTOMapper;
import io.github.css12345.sourceanalyse.persistence.utils.ConverterUtils;

@SpringBootTest
public class TestFileInformationRepository {

	@SpringBootApplication(scanBasePackages = "io.github.css12345.sourceanalyse")
	@EnableNeo4jRepositories(basePackageClasses = MethodRepository.class)
	@EntityScan(basePackageClasses = Method.class)
	@PropertySource("settings.properties")
	static class Config {
		@Bean
		public Project testProject1() {
			Project testProject1 = new Project();
			testProject1.setPath("file:D:\\tmp\\4.1.3\\easypoi");
			testProject1.setWantedPackageNames(new HashSet<>(new ArrayList<>(Arrays.asList("cn.afterturn.easypoi"))));
//			testProject1.setPath("file:D:\\tmp\\1.0\\oim-client-parent");
//			testProject1.setWantedPackageNames(new HashSet<>(new ArrayList<>(Arrays.asList("com.only", "com.oim", "com.onlyxiahui"))));
			return testProject1;
		}
	}

	@Autowired
	private Project testProject1;

	@Autowired
	private FileInformationResolver fileInformationResolver;

	@Autowired
	private FileInformationDTOMapper fileInformationDTOMapper;

	@Autowired
	private FileInformationRepository fileInformationRepository;

	@Autowired
	private MethodRepository methodRepository;

	@Autowired
	private ASTNodeRepository astNodeRepository;

	@Autowired
	private ConverterUtils converterUtils;

	@BeforeEach
	public void setUpMethod() {
		fileInformationRepository.deleteAll();
		methodRepository.deleteAll();
		astNodeRepository.deleteAll();
	}

	@Test
	public void testSave() {
		List<io.github.css12345.sourceanalyse.jdtparse.entity.FileInformation> fileInformations = fileInformationResolver
				.getFileInformations(testProject1);
		for (io.github.css12345.sourceanalyse.jdtparse.entity.FileInformation fileInformation : fileInformations) {
			FileInformationDTO fileInformationDTO = new FileInformationDTO(fileInformation, testProject1.getClassQualifiedNameLocationMap());
			fileInformationDTOMapper.writeToJSONFile(fileInformationDTO);
		}

		for (io.github.css12345.sourceanalyse.jdtparse.entity.FileInformation fileInformation : fileInformations) {
			fileInformationRepository.save(converterUtils.convert(fileInformation.getFile().getAbsolutePath()));
		}

	}
}
