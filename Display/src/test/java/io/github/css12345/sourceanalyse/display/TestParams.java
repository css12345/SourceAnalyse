package io.github.css12345.sourceanalyse.display;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;

import io.github.css12345.sourceanalyse.jdtparse.Params;
import io.github.css12345.sourceanalyse.jdtparse.entity.Project;

@SpringBootTest
public class TestParams {
	@Configuration
	@ComponentScan(excludeFilters = @Filter(classes = TestConfiguration.class), basePackageClasses = Params.class)
	static class Config {
		@Bean
		public Project gradleProject() {
			Project project = new Project();
			project.setPath("file:D:\\Users\\lcs\\workspace\\SpringBootTest");
			return project;
		}
	}
	
	@Autowired
	private Params params;
	
	@Test
	public void testLoad() {
		System.out.println(params.getGradleTasksFile());
		System.out.println(params.getMethodDeclarationIncludeTypesFile());
		System.out.println(params.getGRADLE_USER_HOME());
		assertEquals(2, params.getProjects().size());
	}
}
