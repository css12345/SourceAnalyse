package io.github.css12345.sourceanalyse.similarityanalyse.support;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import io.github.css12345.sourceanalyse.jdtparse.entity.Project;
import io.github.css12345.sourceanalyse.persistence.entity.Method;
import io.github.css12345.sourceanalyse.persistence.repository.MethodRepository;
import io.github.css12345.sourceanalyse.similarityanalyse.entity.FileCompare;
import io.github.css12345.sourceanalyse.similarityanalyse.entity.MethodCompare;
import io.github.css12345.sourceanalyse.similarityanalyse.entity.ProjectCompare;
import io.github.css12345.sourceanalyse.similarityanalyse.entity.State;
import io.github.css12345.sourceanalyse.similarityanalyse.support.TestProjectComparator.Config;

@SpringJUnitConfig(Config.class)
public class TestProjectComparator {
	@TestConfiguration
	@EnableNeo4jRepositories(basePackageClasses = MethodRepository.class)
	@EntityScan(basePackageClasses = Method.class)
	@EnableAutoConfiguration
	@PropertySource("classpath:settings.properties")
	@ComponentScan(basePackages = "io.github.css12345.sourceanalyse")
	public static class Config {
		@Bean
		public Project testProject1() {
			Project testProject1 = new Project();
			testProject1.setPath("file:D:\\tmp\\fastjson\\1.1.44\\fastjson-1.1.44");
			testProject1.setWantedPackageNames(new HashSet<>(new ArrayList<>(Arrays.asList("com.alibaba.fastjson"))));
			testProject1.setVersion("1.1.44");
			return testProject1;
		}

		@Bean
		public Project testProject2() {
			Project testProject2 = new Project();
			testProject2.setPath("file:D:\\tmp\\fastjson\\1.1.157\\fastjson-1.1.157");
			testProject2.setWantedPackageNames(new HashSet<>(new ArrayList<>(Arrays.asList("com.alibaba.fastjson"))));
			testProject2.setVersion("1.1.157");
			return testProject2;
		}
	}

	@Autowired
	private Project testProject1;

	@Autowired
	private Project testProject2;

	@Autowired
	private ProjectComparator projectComparator;

	@Test
	public void testCompare() {
		ProjectCompare projectCompare = new ProjectCompare();
		projectCompare.setProject1(testProject1);
		projectCompare.setProject2(testProject2);
		projectComparator.compare(projectCompare, "a-b.json");

		Set<FileCompare> fileCompares = projectCompare.getFileCompares();
		for (FileCompare fileCompare : fileCompares) {
			if (fileCompare.getState() == State.UNMODIFIED)
				continue;
			String filePath1 = fileCompare.getFilePath1();
			String filePath2 = fileCompare.getFilePath2();
			System.out.print("filePath1: " + filePath1 + " filePath2: " + filePath2);
			int count = 0;
			for (MethodCompare methodCompare : fileCompare.getMethodCompares()) {
				if (methodCompare.getState() == State.UNMODIFIED)
					continue;
				count++;
			}
			System.out.println(" : " + count);
		}
	}

	@Test
	public void testCompareSomeFiles() {
		List<String> filePaths = new ArrayList<>(
				Arrays.asList("\\fastjson-1.1.44\\src\\main\\java\\com\\alibaba\\fastjson\\JSON.java",
						"\\fastjson-1.1.44\\src\\main\\java\\com\\alibaba\\fastjson\\JSONArray.java",
						"\\fastjson-1.1.44\\src\\main\\java\\com\\alibaba\\fastjson\\JSONAware.java",
						"\\fastjson-1.1.44\\src\\main\\java\\com\\alibaba\\fastjson\\JSONException.java",
						"\\fastjson-1.1.44\\src\\main\\java\\com\\alibaba\\fastjson\\JSONObject.java",
						"\\fastjson-1.1.44\\src\\main\\java\\com\\alibaba\\fastjson\\JSONPObject.java",
						"\\fastjson-1.1.44\\src\\main\\java\\com\\alibaba\\fastjson\\JSONReader.java",
						"\\fastjson-1.1.157\\src\\main\\java\\com\\alibaba\\fastjson\\JSONStreamAware.java",
						"\\fastjson-1.1.157\\src\\main\\java\\com\\alibaba\\fastjson\\JSONStreamContext.java",
						"\\fastjson-1.1.157\\src\\main\\java\\com\\alibaba\\fastjson\\JSONWriter.java",
						"\\fastjson-1.1.157\\src\\main\\java\\com\\alibaba\\fastjson\\PropertyNamingStrategy.java",
						"\\fastjson-1.1.157\\src\\main\\java\\com\\alibaba\\fastjson\\TypeReference.java"));
		String prefix1 = "D:\\tmp\\fastjson\\1.1.44";
		String prefix2 = "D:\\tmp\\fastjson\\1.1.157";
		for (int i = 0;i < filePaths.size();i++) {
			String filePath = filePaths.get(i);
			if (filePath.contains("1.1.44"))
				filePath = prefix1 + filePath;
			else 
				filePath = prefix2 + filePath;
			
			filePaths.set(i, filePath);
		}
		
		ProjectCompare projectCompare = new ProjectCompare(testProject1, testProject2);
		projectComparator.compare(projectCompare, filePaths, "a-b.json");
	}
}
