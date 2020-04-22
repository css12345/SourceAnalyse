package io.github.css12345.sourceanalyse.display.dao.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import io.github.css12345.sourceanalyse.display.dao.DifferenceDao;
import io.github.css12345.sourceanalyse.jdtparse.entity.Project;
import io.github.css12345.sourceanalyse.jdtparse.utils.ProjectUtils;
import io.github.css12345.sourceanalyse.persistence.entity.Method;
import io.github.css12345.sourceanalyse.persistence.repository.MethodRepository;
import io.github.css12345.sourceanalyse.persistence.support.ProjectSaver;
import io.github.css12345.sourceanalyse.similarityanalyse.entity.FileCompare;
import io.github.css12345.sourceanalyse.similarityanalyse.entity.MethodCompare;
import io.github.css12345.sourceanalyse.similarityanalyse.entity.ProjectCompare;
import io.github.css12345.sourceanalyse.similarityanalyse.entity.State;
import io.github.css12345.sourceanalyse.similarityanalyse.support.FileComparator;
import io.github.css12345.sourceanalyse.similarityanalyse.support.MethodComparator;
import io.github.css12345.sourceanalyse.similarityanalyse.support.ProjectComparator;

@Repository
public class DifferenceDaoImpl implements DifferenceDao {

	@Autowired
	private ProjectComparator projectComparator;

	@Autowired
	private ProjectSaver projectSaver;

	@Autowired
	private MethodRepository methodRepository;

	@Autowired
	private FileComparator fileComparator;

	@Autowired
	private MethodComparator methodComparator;

	@Override
	public List<FileCompare> compareFiles(Project project1, Project project2, List<String> filePaths) {
		ProjectCompare projectCompare = new ProjectCompare(project1, project2);
		projectComparator.compare(projectCompare, filePaths);
		List<FileCompare> modifiedFileCompares = new ArrayList<>();
		for (FileCompare fileCompare : projectCompare.getFileCompares()) {
			if (fileCompare.getState() == State.UNMODIFIED)
				continue;
			if (fileCompare.getState() == State.MODIFIED) {
				Set<MethodCompare> allMethodCompares = fileCompare.getMethodCompares();
				fileCompare.setMethodCompares(
						allMethodCompares.stream().filter(methodCompare -> methodCompare.getState() != State.UNMODIFIED)
								.collect(Collectors.toSet()));
			}
			modifiedFileCompares.add(fileCompare);
		}
		return modifiedFileCompares;
	}

	@Override
	public List<MethodCompare> getRelatedMethodCompares(String briefMethodInformation1, String version1,
			String briefMethodInformation2, String version2, int type) {
		if (type != 1 && type != 2)
			throw new IllegalArgumentException("type " + type + " error");
		
		if (briefMethodInformation1 == null && briefMethodInformation2 == null)
			throw new IllegalArgumentException("both briefMethodInformation1 and briefMethodInformation2 are null");

		if (briefMethodInformation1 != null && briefMethodInformation2 != null
				&& !briefMethodInformation1.equals(briefMethodInformation2))
			throw new IllegalArgumentException(
					String.format("briefMethodInformation1(%s) is not equals briefMethodInformation2(%s)",
							briefMethodInformation1, briefMethodInformation2));

		List<Method> relatedMethods1;
		List<Method> relatedMethods2;
		
		if (type == 1) {
			relatedMethods1 = methodRepository.getRelatedMethods(briefMethodInformation1, version1);
			relatedMethods2 = methodRepository.getRelatedMethods(briefMethodInformation2, version2);
		} else {
			Method method1 = methodRepository.findByBriefMethodInformationAndVersion(briefMethodInformation1, version1);
			relatedMethods1 = method1 == null ? Collections.emptyList() : method1.getMethodInvocations();
			Method method2 = methodRepository.findByBriefMethodInformationAndVersion(briefMethodInformation2, version2);
			relatedMethods2 = method2 == null ? Collections.emptyList() : method2.getMethodInvocations();
		}

		Set<String> briefMethodInformationOfRelatedMetods1 = relatedMethods1.stream()
				.map(method -> method.getBriefMethodInformation()).collect(Collectors.toSet());
		Set<String> briefMethodInformationOfRelatedMetods2 = relatedMethods2.stream()
				.map(method -> method.getBriefMethodInformation()).collect(Collectors.toSet());

		Set<MethodCompare> relatedMethodCompares = new HashSet<>();
		for (Method method : relatedMethods1) {
			MethodCompare methodCompare = fileComparator.buildMethodCompare(method.getBriefMethodInformation(),
					version1, version2);
			// may be function A in version1 invoked by function B and C, in version2
			// invoked by function C and D,
			// this case when function B also in version2 project, last step will make
			// methodCompare to compare version1's B and version2's B,
			// but in version2 B didn't invoke A, so we should compare version1'B and null.
			if (!briefMethodInformationOfRelatedMetods2.contains(method.getBriefMethodInformation()))
				methodCompare.setBriefMethodInformation2(null);

			if (!relatedMethodCompares.contains(methodCompare)) {
				methodComparator.compare(methodCompare);
				relatedMethodCompares.add(methodCompare);
			}
		}

		for (Method method : relatedMethods2) {
			MethodCompare methodCompare = fileComparator.buildMethodCompare(method.getBriefMethodInformation(),
					version1, version2);

			if (!briefMethodInformationOfRelatedMetods1.contains(method.getBriefMethodInformation()))
				methodCompare.setBriefMethodInformation1(null);

			if (!relatedMethodCompares.contains(methodCompare)) {
				methodComparator.compare(methodCompare);
				relatedMethodCompares.add(methodCompare);
			}
		}
		return new ArrayList<>(relatedMethodCompares);
	}

	@Override
	public Map<String, List<String>> getModifiedFilesOfProjects(Project project1, Project project2) {
		projectSaver.saveProject(project1);
		projectSaver.saveProject(project2);

		List<File> filesOfProject1 = ProjectUtils.findSuffixLikeJavaFiles(project1);
		List<File> filesOfProject2 = ProjectUtils.findSuffixLikeJavaFiles(project2);
		List<String> allFilePaths = new ArrayList<>();
		for (File file : filesOfProject1) {
			allFilePaths.add(file.getAbsolutePath());
		}
		for (File file : filesOfProject2) {
			allFilePaths.add(file.getAbsolutePath());
		}
		ProjectCompare projectCompare = new ProjectCompare(project1, project2);
		projectComparator.compare(projectCompare, allFilePaths);

		Set<FileCompare> fileCompares = projectCompare.getFileCompares();
		List<String> modifiedFilesOfProject1 = new ArrayList<>();
		List<String> modifiedFilesOfProject2 = new ArrayList<>();
		for (FileCompare fileCompare : fileCompares) {
			if (fileCompare.getState() == State.UNMODIFIED)
				continue;

			if (fileCompare.getFilePath1() != null)
				modifiedFilesOfProject1.add(fileCompare.getFilePath1());
			if (fileCompare.getFilePath2() != null)
				modifiedFilesOfProject2.add(fileCompare.getFilePath2());
		}

		Map<String, List<String>> modifiedFilesMap = new HashMap<>();
		modifiedFilesMap.put("project1", modifiedFilesOfProject1);
		modifiedFilesMap.put("project2", modifiedFilesOfProject2);
		return modifiedFilesMap;
	}

}
