package io.github.css12345.sourceanalyse.display.dao.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import io.github.css12345.sourceanalyse.display.dao.DifferenceDao;
import io.github.css12345.sourceanalyse.jdtparse.entity.Project;
import io.github.css12345.sourceanalyse.jdtparse.utils.ProjectUtils;
import io.github.css12345.sourceanalyse.persistence.support.ProjectSaver;
import io.github.css12345.sourceanalyse.similarityanalyse.entity.FileCompare;
import io.github.css12345.sourceanalyse.similarityanalyse.entity.ProjectCompare;
import io.github.css12345.sourceanalyse.similarityanalyse.support.ProjectComparator;

@Repository
public class DifferenceDaoImpl implements DifferenceDao {

	@Autowired
	private ProjectComparator projectComparator;

	@Autowired
	private ProjectSaver projectSaver;

	@Override
	public List<FileCompare> compareFiles(Project project1, Project project2, List<String> filePaths) {
		Set<File> allFilesOfProject1 = ProjectUtils.findSuffixLikeJavaFiles(project1).stream()
				.collect(Collectors.toSet());
		Set<File> allFilesOfProject2 = ProjectUtils.findSuffixLikeJavaFiles(project2).stream()
				.collect(Collectors.toSet());
		List<File> files = filePaths.stream().map(filePath -> new File(filePath)).collect(Collectors.toList());
		List<File> filesOfProject1 = new ArrayList<>();
		List<File> filesOfProject2 = new ArrayList<>();
		for (File file : files) {
			if (allFilesOfProject1.contains(file))
				filesOfProject1.add(file);
			else if (allFilesOfProject2.contains(file))
				filesOfProject2.add(file);
			else {
				throw new IllegalArgumentException("file " + file.getAbsolutePath() + " neither in project1 "
						+ project1.getPath() + " nor in project2" + project2.getPath());
			}
		}
		projectSaver.saveProject(project1, filesOfProject1);
		projectSaver.saveProject(project2, filesOfProject2);

		ProjectCompare projectCompare = new ProjectCompare(project1, project2);
		projectComparator.compare(projectCompare, filePaths);
		return projectCompare.getFileCompares();
	}

}
