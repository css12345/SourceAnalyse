package io.github.css12345.sourceanalyse.jdtparse.entity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileInformation {
	private File file;

	private String rootProjectPath;

	private String version;

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	private List<MethodInformation> methodInformations = new ArrayList<>();

	public File getFile() {
		return file;
	}

	public String getRootProjectPath() {
		return rootProjectPath;
	}

	public void setRootProjectPath(String rootProjectPath) {
		this.rootProjectPath = rootProjectPath;
	}

	public List<MethodInformation> getMethodInformations() {
		return methodInformations;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public void setMethodInformations(List<MethodInformation> methodInformations) {
		this.methodInformations = methodInformations;
	}

	@Override
	public String toString() {
		return "FileInformation [file=" + file + ", rootProjectPath=" + rootProjectPath + ", version=" + version
				+ ", methodInformations=" + methodInformations + "]";
	}

}
