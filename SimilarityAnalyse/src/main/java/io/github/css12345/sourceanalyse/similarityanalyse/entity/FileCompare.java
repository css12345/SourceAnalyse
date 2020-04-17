package io.github.css12345.sourceanalyse.similarityanalyse.entity;

import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang3.builder.CompareToBuilder;

public class FileCompare implements Comparable<FileCompare> {

	private String filePath1;

	private String filePath2;

	private Set<MethodCompare> methodCompares = new TreeSet<>();

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

	public Set<MethodCompare> getMethodCompares() {
		return methodCompares;
	}

	public void setMethodCompares(Set<MethodCompare> methodCompares) {
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
		if (filePath1 != null && filePath2 == null)
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

	@Override
	public int compareTo(FileCompare o) {
		return new CompareToBuilder().append(filePath1, o.filePath1)
				.append(filePath2, o.filePath2).toComparison();
	}

	@Override
	public int hashCode() {
		return Objects.hash(filePath1, filePath2);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FileCompare other = (FileCompare) obj;
		return Objects.equals(filePath1, other.filePath1) && Objects.equals(filePath2, other.filePath2);
	}

}
