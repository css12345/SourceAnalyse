package io.github.css12345.sourceanalyse.display.entity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ProjectVOWrapper {
	
	private String id;
	
	private String pid;
	
	/**
	 * @see ProjectVO#path
	 */
	private String projectPath;

	/**
	 * @see ProjectVO#projectName
	 */
	private String projectName;

	/**
	 * @see ProjectVO#relativePath
	 */
	private String relativePath;

	/**
	 * @see ProjectVO#version
	 */
	private String version;

	private String parentProjectPath;

	/**
	 * @see ProjectVO#pathOfDependencies
	 */
	private List<String> pathOfDependencies = new ArrayList<>();

	/**
	 * @see ProjectVO#wantedPackageNames
	 */
	private Set<String> wantedPackageNames = new HashSet<>();

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public ProjectVOWrapper() {

	}

	public String getProjectPath() {
		return projectPath;
	}

	public void setProjectPath(String projectPath) {
		this.projectPath = projectPath;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public List<String> getPathOfDependencies() {
		return pathOfDependencies;
	}

	public void setPathOfDependencies(List<String> pathOfDependencies) {
		this.pathOfDependencies = pathOfDependencies;
	}

	public Set<String> getWantedPackageNames() {
		return wantedPackageNames;
	}

	public void setWantedPackageNames(Set<String> wantedPackageNames) {
		this.wantedPackageNames = wantedPackageNames;
	}

	public String getRelativePath() {
		return relativePath;
	}

	public void setRelativePath(String relativePath) {
		this.relativePath = relativePath;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public String getParentProjectPath() {
		return parentProjectPath;
	}

	public void setParentProjectPath(String parentProjectPath) {
		this.parentProjectPath = parentProjectPath;
	}
}
