package io.github.css12345.sourceanalyse.jdtparse.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import io.github.css12345.sourceanalyse.jdtparse.ParamsPropertyConfig;
import io.github.css12345.sourceanalyse.jdtparse.entity.Project;
import io.github.css12345.sourceanalyse.jdtparse.utils.TestProjectResolver.Config;

@SpringJUnitConfig(Config.class)
public class TestProjectResolver {
	@TestConfiguration
	@PropertySource("classpath:params.properties")
	@ComponentScan(basePackageClasses = ParamsPropertyConfig.class, excludeFilters = @Filter(classes = TestConfiguration.class))
	static class Config {
		@Bean
		public Project mavenProject() {
			Project project = new Project();
			project.setPath("file:D:\\Users\\lenovo\\workspace\\Fruit-Sales-Platform");
			return project;
		}

		@Bean
		public Project gradleProject() {
			Project project = new Project();
			project.setPath("file:D:\\Users\\lcs\\workspace\\SpringBootTest");
			return project;
		}

		@Bean
		public Project specificDirectoryProject() {
			Project project = new Project();
			project.setPath("file:D:\\Users\\lenovo\\workspace\\MyBatisFirstDemo");
			return project;
		}

		@Bean
		public Project configuredDependencyLocationProject() {
			Project project = new Project();
			project.setPath("file:D:\\Users\\cs\\Documents\\GitHub\\SourceAnalyse\\JDTParse");
			project.setPathOfDependenciesLocation("classpath:project2Dependencies.txt");
			return project;
		}
	}

	@Autowired
	private Project mavenProject;
	
	@Autowired
	private Project gradleProject;
	
	@Autowired
	private Project specificDirectoryProject;
	
	@Autowired
	private Project configuredDependencyLocationProject;

	@Test
	public void testMavenProject() {
		Project resolvedProject = mavenProject;
		assertEquals("D:\\Users\\lenovo\\workspace\\Fruit-Sales-Platform", resolvedProject.getPath());
		assertEquals(52, resolvedProject.getPathOfDependencies().size());
	}

	@Test
	public void testGradleProject() {
		Project resolvedProject = gradleProject;
		assertEquals("D:\\Users\\lcs\\workspace\\SpringBootTest", resolvedProject.getPath());
		assertEquals(96, resolvedProject.getPathOfDependencies().size());
	}

	@Test
	public void testSpecificDirectoryProject() {
		Project resolvedProject = specificDirectoryProject;
		assertEquals("D:\\Users\\lenovo\\workspace\\MyBatisFirstDemo", resolvedProject.getPath());
		assertEquals(12, resolvedProject.getPathOfDependencies().size());
	}

	@Test
	public void testConfiguredDependencyLocationProject() {
		Project resolvedProject = configuredDependencyLocationProject;
		assertEquals("D:\\Users\\cs\\Documents\\GitHub\\SourceAnalyse\\JDTParse", resolvedProject.getPath());
		assertEquals(65, resolvedProject.getPathOfDependencies().size());
	}
}
