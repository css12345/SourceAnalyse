package io.github.css12345.sourceanalyse.similarityanalyse.entity;

import java.util.ArrayList;
import java.util.List;

public class FileCompare {

	private String filePath1;

	private String filePath2;

	private List<MethodCompare> methodCompares = new ArrayList<>();

	private State state;

	public FileCompare(String filePath1, String filePath2) {
		this.filePath1 = filePath1;
		this.filePath2 = filePath2;
	}

	public FileCompare(String filePath1, String filePath2, State state) {
		this.filePath1 = filePath1;
		this.filePath2 = filePath2;
		this.state = state;
	}

	public FileCompare() {

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

	public String getFilePath1() {
		return filePath1;
	}

	public void setFilePath1(String filePath1) {
		this.filePath1 = filePath1;
	}

	public String getFilePath2() {
		return filePath2;
	}

	public void setFilePath2(String filePath2) {
		this.filePath2 = filePath2;
	}

	public void setState(State state) {
		this.state = state;
	}

	public void judgeAndSetState() {
		if (filePath1 != null && filePath1 == null)
			state = State.DELETE;
		else if (filePath1 == null && filePath2 != null)
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
