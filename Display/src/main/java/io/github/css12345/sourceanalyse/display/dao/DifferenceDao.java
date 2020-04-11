package io.github.css12345.sourceanalyse.display.dao;

import java.util.List;

import io.github.css12345.sourceanalyse.jdtparse.entity.Project;
import io.github.css12345.sourceanalyse.similarityanalyse.entity.FileCompare;

public interface DifferenceDao {

	List<FileCompare> compareFiles(Project project1, Project project2, List<String> filePaths);

}
