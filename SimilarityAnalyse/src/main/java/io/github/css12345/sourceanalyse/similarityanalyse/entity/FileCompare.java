package io.github.css12345.sourceanalyse.similarityanalyse.entity;

import java.util.ArrayList;
import java.util.List;

import io.github.css12345.sourceanalyse.persistence.entity.FileInformation;

public class FileCompare {

	private FileInformation fileInformation1;

	private FileInformation fileInformation2;

	private List<MethodCompare> methodCompares = new ArrayList<>();

	private State state;

	public FileCompare(FileInformation fileInformation1, FileInformation fileInformation2) {
		this.fileInformation1 = fileInformation1;
		this.fileInformation2 = fileInformation2;
	}

	public FileCompare(FileInformation fileInformation1, FileInformation fileInformation2, State state) {
		this.fileInformation1 = fileInformation1;
		this.fileInformation2 = fileInformation2;
		this.state = state;
	}

	public FileCompare() {
		
	}

	public FileInformation getFileInformation1() {
		return fileInformation1;
	}

	public void setFileInformation1(FileInformation fileInformation1) {
		this.fileInformation1 = fileInformation1;
	}

	public FileInformation getFileInformation2() {
		return fileInformation2;
	}

	public void setFileInformation2(FileInformation fileInformation2) {
		this.fileInformation2 = fileInformation2;
	}

	public List<MethodCompare> getMethodCompares() {
		return methodCompares;
	}

	public void setMethodCompares(List<MethodCompare> methodCompares) {
		this.methodCompares = methodCompares;
	}

	public State getState() {
		return state;
	}

	public void setState() {
		if (fileInformation1 != null && fileInformation2 == null)
			state = State.DELETE;
		else if (fileInformation1 == null && fileInformation2 != null)
			state = State.ADD;
		else {
			boolean unmodified = true;
			for (MethodCompare methodCompare : methodCompares) {
				if (methodCompare.getState() != State.UNMODIFIED) {
					unmodified = false;
					break;
				}
			}

			if (unmodified)
				state = State.UNMODIFIED;
			else
				state = State.MODIFIED;
		}
	}

}
