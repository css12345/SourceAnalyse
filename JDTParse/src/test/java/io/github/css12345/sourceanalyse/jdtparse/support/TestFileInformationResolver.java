package io.github.css12345.sourceanalyse.jdtparse.support;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import io.github.css12345.sourceanalyse.jdtparse.ParamsPropertyConfig;
import io.github.css12345.sourceanalyse.jdtparse.entity.FileInformation;
import io.github.css12345.sourceanalyse.jdtparse.entity.FileInformationDTO;
import io.github.css12345.sourceanalyse.jdtparse.entity.Project;
import io.github.css12345.sourceanalyse.jdtparse.support.TestFileInformationResolver.Config;

@SpringJUnitConfig(Config.class)
public class TestFileInformationResolver {

	@TestConfiguration
	@ComponentScan(basePackageClasses = ParamsPropertyConfig.class, excludeFilters = @Filter(classes = TestConfiguration.class))
	static class Config {
		@Bean
		public Project testProject1() {
			Project testProject1 = new Project();
			testProject1.setPath("file:D:\\tmp\\0.0.1\\download");
			testProject1.setWantedPackageNames(
					new HashSet<>(new ArrayList<>(Arrays.asList("com.example.download", "download"))));
			return testProject1;
		}

		@Bean
		public Project testProject2() {
			Project testProject2 = new Project();
			testProject2.setPath("file:D:\\Users\\cs\\Documents\\GitHub\\easypoi");
			testProject2.setWantedPackageNames(new HashSet<>(new ArrayList<>(Arrays.asList("cn.afterturn.easypoi"))));
			return testProject2;
		}

		@Bean
		public Project testProject3() {
			Project testProject3 = new Project();
			testProject3.setPath("file:D:\\Users\\cs\\Documents\\GitHub\\oim-fx\\oim-client-parent");
			testProject3.setWantedPackageNames(
					new HashSet<>(new ArrayList<>(Arrays.asList("com.only", "com.oim", "com.onlyxiahui"))));
			return testProject3;
		}

		@Bean
		public Project testProject4() {
			Project testProject4 = new Project();
			testProject4.setPath("file:D:\\tmp\\groovy");
			testProject4.setWantedPackageNames(new HashSet<>(new ArrayList<>(Arrays.asList("org.gradle"))));
			return testProject4;
		}
	}

	@Autowired
	private FileInformationResolver fileInformationResolver;

	@Autowired
	private Project testProject1;

	@Autowired
	private Project testProject2;

	@Autowired
	private Project testProject3;

	@Autowired
	private Project testProject4;

	@Test
	public void test1() {
		List<FileInformation> fileInformations = fileInformationResolver.getFileInformations(testProject1);
		assertEquals(5, fileInformations.size());
		System.out.println("\n\n\n");
		display(fileInformations);
	}

	@Test
	public void test2() {
		List<FileInformation> fileInformations = fileInformationResolver.getFileInformations(testProject2);
		display(fileInformations);
	}

	@Test
	public void test3() {
		List<FileInformation> fileInformations = fileInformationResolver.getFileInformations(testProject3);
		display(fileInformations);
	}

	@Test
	public void test4() {
		List<FileInformation> fileInformations = fileInformationResolver.getFileInformations(testProject4);
		display(fileInformations);
	}

	private void display(List<FileInformation> fileInformations) {
		ObjectMapper mapper = new ObjectMapper();
		// configure Object mapper for pretty print
		mapper.configure(SerializationFeature.INDENT_OUTPUT, true);

		for (FileInformation fileInformation : fileInformations) {
			FileInformationDTO fileInformationDTO = new FileInformationDTO(fileInformation);

			// writing to console, can write to any output stream such as file
			StringWriter stringEmp = new StringWriter();
			try {
				mapper.writeValue(stringEmp, fileInformationDTO);
			} catch (IOException e) {
				e.printStackTrace();
			}
			System.out.println(stringEmp);
		}
	}
}
