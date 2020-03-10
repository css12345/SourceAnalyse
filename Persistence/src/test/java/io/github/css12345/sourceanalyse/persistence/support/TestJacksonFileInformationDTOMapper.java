package io.github.css12345.sourceanalyse.persistence.support;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import io.github.css12345.sourceanalyse.jdtparse.entity.FileInformation;
import io.github.css12345.sourceanalyse.jdtparse.entity.FileInformationDTO;
import io.github.css12345.sourceanalyse.jdtparse.entity.Project;
import io.github.css12345.sourceanalyse.jdtparse.support.FileInformationResolver;
import io.github.css12345.sourceanalyse.persistence.support.FileInformationDTOMapper;
import io.github.css12345.sourceanalyse.persistence.support.TestJacksonFileInformationDTOMapper.Config;

@SpringJUnitConfig(classes = Config.class)
public class TestJacksonFileInformationDTOMapper {
	
	@TestConfiguration
	@ComponentScan(basePackages = "io.github.css12345.sourceanalyse")
	static class Config {
		@Bean
		public Project testProject() {
			Project testProject = new Project();
			testProject.setPath("file:D:\\tmp\\0.0.1\\download");
			testProject.setWantedPackageNames(
					new HashSet<>(new ArrayList<>(Arrays.asList("com.example.download", "download"))));
			return testProject;
		}
	}
	
	@Autowired
	private Project testProject;
	
	@Autowired
	private FileInformationResolver fileInformationResolver;
	
	@Autowired
	private FileInformationDTOMapper fileInformationDTOMapper;
	
	private List<FileInformationDTO> fileInformationDTOs = new ArrayList<>();
	
	@BeforeEach
	public void setUp() {
		List<FileInformation> fileInformations = fileInformationResolver.getFileInformations(testProject);
		fileInformationDTOs.clear();
		for (FileInformation fileInformation : fileInformations) {
			fileInformationDTOs.add(new FileInformationDTO(fileInformation));
		}
	}
	
	@Test
	public void test() {
		for (FileInformationDTO fileInformationDTO : fileInformationDTOs) {
			fileInformationDTOMapper.writeToJSONFile(fileInformationDTO);
		}
	}
}
