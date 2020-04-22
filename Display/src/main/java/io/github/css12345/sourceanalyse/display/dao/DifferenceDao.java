package io.github.css12345.sourceanalyse.display.dao;

import java.util.List;
import java.util.Map;

import io.github.css12345.sourceanalyse.jdtparse.entity.Project;
import io.github.css12345.sourceanalyse.similarityanalyse.entity.FileCompare;
import io.github.css12345.sourceanalyse.similarityanalyse.entity.MethodCompare;

public interface DifferenceDao {

	List<FileCompare> compareFiles(Project project1, Project project2, List<String> filePaths);

	List<MethodCompare> getRelatedMethodCompares(String briefMethodInformation1,
			String version1, String briefMethodInformation2, String version2, int type);

	Map<String, List<String>> getModifiedFilesOfProjects(Project project1, Project project2);
}
