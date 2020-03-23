package io.github.css12345.sourceanalyse.similarityanalyse.support;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import io.github.css12345.sourceanalyse.persistence.entity.FileInformation;
import io.github.css12345.sourceanalyse.persistence.repository.FileInformationRepository;
import io.github.css12345.sourceanalyse.similarityanalyse.entity.FileCompare;
import io.github.css12345.sourceanalyse.similarityanalyse.entity.MethodCompare;
import io.github.css12345.sourceanalyse.similarityanalyse.entity.State;
import io.github.css12345.sourceanalyse.similarityanalyse.support.TestProjectComparator.Config;

@SpringJUnitConfig(Config.class)
public class TestFileComparator {
	@Autowired
	private FileComparator fileComparator;

	@Autowired
	private FileInformationRepository fileInformationRepository;
	
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
		FileInformation fileInformation1 = fileInformationRepository.findByFilePath(path1);
		FileInformation fileInformation2 = fileInformationRepository.findByFilePath(path2);
		FileCompare fileCompare = new FileCompare(fileInformation1, fileInformation2);
		fileComparator.compare(fileCompare);

		System.out.println(fileCompare.getState());
		for (MethodCompare methodCompare : fileCompare.getMethodCompares()) {
			if (methodCompare.getState() == State.UNMODIFIED)
				continue;

			String briefMethodInformation1 = methodCompare.getMethod1() == null ? null
					: methodCompare.getMethod1().getBriefMethodInformation();
			String briefMethodInformation2 = methodCompare.getMethod2() == null ? null
					: methodCompare.getMethod2().getBriefMethodInformation();
			if (StringUtils.equals(briefMethodInformation1, briefMethodInformation2)) {
				System.out.print(briefMethodInformation1);
			} else {
				System.out.print(briefMethodInformation1 + " and " + briefMethodInformation2);
			}
			System.out.println(" : " + methodCompare.getState() + " : " + methodCompare.getSimilarity());
		}
	}
}
