package io.github.css12345.sourceanalyse.jdtparse.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.github.css12345.sourceanalyse.jdtparse.entity.Project;

public class ProjectUtils {
	
	/**
	 * allProjects is {@link #getAllProjectsMap(Project)}
	 * @see ProjectUtils#findClosestProject(String, Map)
	 */
	public static Project findClosestProject(String filePath, Project project) {
		return findClosestProject(filePath, getAllProjectsMap(project));
	}
	
	/**
	 * find the project which the filePath is in it's sub directory
	 * @param filePath to be find path
	 * @param allProjects all projects(include subproject), key is {@link Project#path}, value is {@link Project}
	 * @return the project which the filePath is in it's sub directory, if not find, return null.
	 */
	public static Project findClosestProject(String filePath, Map<String, Project> allProjects) {
		int lastIndexOfFileSeparator;
		while ((lastIndexOfFileSeparator = filePath.lastIndexOf(File.separatorChar)) != -1) {
			filePath = filePath.substring(0, lastIndexOfFileSeparator);
			if (allProjects.containsKey(filePath))
				return allProjects.get(filePath);
		}
		return null;
	}
	
	/**
	 * suffix is ".java"
	 * @see ProjectUtils#findSuffixFiles(Project, String)
	 */
	public static List<File> findSuffixLikeJavaFiles(Project project) {
		return findSuffixFiles(project, ".java");
	}

	/**
	 * recursively get all files in project end with suffix
	 * @param project to be find's project
	 * @param suffix file name end with
	 * @return a list contains all fit files
	 */
	public static List<File> findSuffixFiles(Project project, String suffix) {
		Map<String, Project> allProjects = getAllProjectsMap(project);

		File rootFile = new File(project.getPath());
		List<File> suffixLikeFiles = getAllSuffixFiles(rootFile, suffix);
		// may be some module did not in root project directory
		for (String path : allProjects.keySet()) {
			if (!path.startsWith(project.getPath()))
				suffixLikeFiles.addAll(getAllSuffixFiles(new File(path), suffix));
		}
		
		return suffixLikeFiles;
	}

	/**
	 * recursively get all files in rootFile end with suffix
	 * @param rootFile to be find's file
	 * @param suffix file name end with
	 * @return a list contains all fit files
	 */
	public static List<File> getAllSuffixFiles(File rootFile, String suffix) {
		List<File> suffixFiles = new ArrayList<>();
		addSuffixFiles(rootFile, suffixFiles, suffix);
		return suffixFiles;
	}
	
	private static void addSuffixFiles(File rootFile, List<File> suffixFiles, String suffix) {
		File[] subFiles = rootFile.listFiles();
		for (File subFile : subFiles) {
			if (subFile.isDirectory())
				addSuffixFiles(subFile, suffixFiles, suffix);
			else {
				if (subFile.getName().endsWith(suffix) && subFile.getAbsolutePath().contains("src"))
					suffixFiles.add(subFile);
			}
		}
	}
	
	/**
	 * recursively get all projects
	 * @param project to be resolved project
	 * @return a map, key is {@link Project#path}, value is {@link Project}
	 */
	public static Map<String, Project> getAllProjectsMap (Project project) {
		Map<String, Project> allProjectsMap = new HashMap<>();
		addProjects(project, allProjectsMap);
		return allProjectsMap;
	}

	private static void addProjects(Project project, Map<String, Project> allProjects) {
		allProjects.put(project.getPath(), project);
		for (Project subproject : project.getModules())
			addProjects(subproject, allProjects);
	}
}
