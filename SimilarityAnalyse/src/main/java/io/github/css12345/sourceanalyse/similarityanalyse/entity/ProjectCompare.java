package io.github.css12345.sourceanalyse.similarityanalyse.entity;

import java.util.ArrayList;
import java.util.List;

import io.github.css12345.sourceanalyse.jdtparse.entity.Project;

public class ProjectCompare {
	private Project project1;

	private Project project2;

	private List<FileCompare> fileCompares = new ArrayList<>();

	public ProjectCompare(Project project1, Project project2) {
		this.project1 = project1;
		this.project2 = project2;
	}

	public ProjectCompare() {
		
	}

	public Project getProject1() {
		return project1;
	}

	public void setProject1(Project project1) {
		this.project1 = project1;
	}

	public Project getProject2() {
		return project2;
	}

	public void setProject2(Project project2) {
		this.project2 = project2;
	}

	public List<FileCompare> getFileCompares() {
		return fileCompares;
	}

	public void setFileCompares(List<FileCompare> fileCompares) {
		this.fileCompares = fileCompares;
	}

}
