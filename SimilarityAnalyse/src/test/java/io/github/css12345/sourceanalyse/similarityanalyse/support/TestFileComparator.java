package io.github.css12345.sourceanalyse.similarityanalyse.support;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import io.github.css12345.sourceanalyse.similarityanalyse.entity.FileCompare;
import io.github.css12345.sourceanalyse.similarityanalyse.entity.MethodCompare;
import io.github.css12345.sourceanalyse.similarityanalyse.entity.State;
import io.github.css12345.sourceanalyse.similarityanalyse.support.TestProjectComparator.Config;

@SpringJUnitConfig(Config.class)
public class TestFileComparator {
	@Autowired
	private FileComparator fileComparator;

//	@Autowired
//	private ProjectSaver projectSaver;
//	
//	@Autowired
//	private Params params;

	@Test
	public void testFileCompare() {
//		List<Project> projects = params.getProjects();
//		for (Project project : projects) {
//			projectSaver.saveProject(project, false);
//		}

		String path1 = "D:\\tmp\\fastjson\\1.1.44\\fastjson-1.1.44\\src\\main\\java\\com\\alibaba\\fastjson\\JSON.java";
		String path2 = "D:\\tmp\\fastjson\\1.1.157\\fastjson-1.1.157\\src\\main\\java\\com\\alibaba\\fastjson\\JSON.java";
		FileCompare fileCompare = new FileCompare(path1, path2);
		fileComparator.compare(fileCompare, "1.1.44", "1.1.157");

		System.out.println(fileCompare.getState());
		for (MethodCompare methodCompare : fileCompare.getMethodCompares()) {
			if (methodCompare.getState() == State.UNMODIFIED)
				continue;

			String briefMethodInformation1 = methodCompare.getBriefMethodInformation1();
			String briefMethodInformation2 = methodCompare.getBriefMethodInformation2();
			if (StringUtils.equals(briefMethodInformation1, briefMethodInformation2)) {
				System.out.print(briefMethodInformation1);
			} else {
				System.out.print(briefMethodInformation1 + " and " + briefMethodInformation2);
			}
			System.out.println(" : " + methodCompare.getState() + " : " + methodCompare.getSimilarity());
		}
	}
}
