package io.github.css12345.sourceanalyse.persistence.support;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;

import io.github.css12345.sourceanalyse.jdtparse.entity.Project;
import io.github.css12345.sourceanalyse.persistence.entity.Method;
import io.github.css12345.sourceanalyse.persistence.repository.MethodRepository;

@SpringBootTest
public class TestProjectSaver {
	@SpringBootApplication(scanBasePackages = "io.github.css12345.sourceanalyse")
	@EnableNeo4jRepositories(basePackageClasses = MethodRepository.class)
	@EntityScan(basePackageClasses = Method.class)
	@PropertySource("settings.properties")
	static class Config {
		@Bean
		public Project testProject1() {
			Project testProject1 = new Project();
			testProject1.setPath("file:D:\\tmp\\fastjson\\1.1.44\\fastjson-1.1.44");
			testProject1.setWantedPackageNames(new HashSet<>(new ArrayList<>(Arrays.asList("com.alibaba.fastjson"))));
			return testProject1;
		}
		
		@Bean
		public Project testProject2() {
			Project testProject2 = new Project();
			testProject2.setPath("file:D:\\tmp\\fastjson\\1.1.157\\fastjson-1.1.157");
			testProject2.setWantedPackageNames(new HashSet<>(new ArrayList<>(Arrays.asList("com.alibaba.fastjson"))));
			return testProject2;
		}
	}
	
	@Autowired
	private ProjectSaver projectSaver;
	
	@Autowired
	private Project testProject1;
	
	@Autowired
	private Project testProject2;
	
	@Test
	public void testSaveProject() {
		projectSaver.saveProject(testProject1);
		projectSaver.saveProject(testProject2);
	}
}
