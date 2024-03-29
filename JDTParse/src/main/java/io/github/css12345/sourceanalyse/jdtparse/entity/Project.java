package io.github.css12345.sourceanalyse.jdtparse.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Project {
	private String path;

	/**
	 * use to record relative path for sub projects compare with root project.
	 */
	private String relativePath;

	private String version;

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	private List<String> pathOfDependencies = new ArrayList<>();

	private String pathOfDependenciesLocation;

	private Set<String> wantedPackageNames = new HashSet<>();

	private List<Project> modules = new ArrayList<>();

	private Map<String, String> classQualifiedNameLocationMap = new HashMap<>();

	public String getRelativePath() {
		return relativePath;
	}

	public void setRelativePath(String relativePath) {
		this.relativePath = relativePath;
	}

	public Set<String> getWantedPackageNames() {
		return wantedPackageNames;
	}

	public void setWantedPackageNames(Set<String> wantedPackageNames) {
		this.wantedPackageNames = wantedPackageNames;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public List<String> getPathOfDependencies() {
		return pathOfDependencies;
	}

	public void setPathOfDependencies(List<String> pathOfDependencies) {
		this.pathOfDependencies = pathOfDependencies;
	}

	public String getPathOfDependenciesLocation() {
		return pathOfDependenciesLocation;
	}

	public void setPathOfDependenciesLocation(String pathOfDependenciesLocation) {
		this.pathOfDependenciesLocation = pathOfDependenciesLocation;
	}

	public List<Project> getModules() {
		return modules;
	}

	public Project findModule(String modulePath) {
		for (Project module : modules)
			if (module.getPath().equals(modulePath))
				return module;
		return null;
	}

	public void addModules(List<Project> modules) {
		this.modules.addAll(modules);
	}

	public void addModule(Project module) {
		this.modules.add(module);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((path == null) ? 0 : path.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Project other = (Project) obj;
		if (path == null) {
			if (other.path != null)
				return false;
		} else if (!path.equals(other.path))
			return false;
		return true;
	}

	public Map<String, String> getClassQualifiedNameLocationMap() {
		return classQualifiedNameLocationMap;
	}

	public void setClassQualifiedNameLocationMap(Map<String, String> classQualifiedNameLocationMap) {
		this.classQualifiedNameLocationMap = classQualifiedNameLocationMap;
	}

	@Override
	public String toString() {
		return "Project [path=" + path + ", relativePath=" + relativePath + ", version=" + version
				+ ", pathOfDependencies=" + pathOfDependencies + ", pathOfDependenciesLocation="
				+ pathOfDependenciesLocation + ", wantedPackageNames=" + wantedPackageNames + ", modules=" + modules
				+ ", classQualifiedNameLocationMap=" + classQualifiedNameLocationMap + "]";
	}

}
