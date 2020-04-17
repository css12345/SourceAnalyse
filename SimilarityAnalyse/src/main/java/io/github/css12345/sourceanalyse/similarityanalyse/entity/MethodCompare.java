package io.github.css12345.sourceanalyse.similarityanalyse.entity;

import java.util.Objects;

import org.apache.commons.lang3.builder.CompareToBuilder;

public class MethodCompare implements Comparable<MethodCompare> {
	private String briefMethodInformation1;

	private String version1;

	private String briefMethodInformation2;

	private String version2;

	private State state;

	private double similarity = 0;

	public MethodCompare() {

	}

	public MethodCompare(String briefMethodInformation1, String version1, String briefMethodInformation2,
			String version2) {
		this.briefMethodInformation1 = briefMethodInformation1;
		this.version1 = version1;
		this.briefMethodInformation2 = briefMethodInformation2;
		this.version2 = version2;
	}

	public String getVersion1() {
		return version1;
	}

	public void setVersion1(String version1) {
		this.version1 = version1;
	}

	public String getVersion2() {
		return version2;
	}

	public void setVersion2(String version2) {
		this.version2 = version2;
	}

	public String getBriefMethodInformation1() {
		return briefMethodInformation1;
	}

	public void setBriefMethodInformation1(String briefMethodInformation1) {
		this.briefMethodInformation1 = briefMethodInformation1;
	}

	public String getBriefMethodInformation2() {
		return briefMethodInformation2;
	}

	public void setBriefMethodInformation2(String briefMethodInformation2) {
		this.briefMethodInformation2 = briefMethodInformation2;
	}

	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
	}

	public double getSimilarity() {
		return similarity;
	}

	public void setSimilarity(double similarity) {
		this.similarity = similarity;
	}

	@Override
	public int compareTo(MethodCompare o) {
		return new CompareToBuilder().append(briefMethodInformation1, o.briefMethodInformation1)
				.append(briefMethodInformation2, o.briefMethodInformation2)
				.append(version1, o.version1)
				.append(version2, o.version2)
				.toComparison();
	}

	@Override
	public int hashCode() {
		return Objects.hash(briefMethodInformation1, briefMethodInformation2, version1, version2);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MethodCompare other = (MethodCompare) obj;
		return Objects.equals(briefMethodInformation1, other.briefMethodInformation1)
				&& Objects.equals(briefMethodInformation2, other.briefMethodInformation2)
				&& Objects.equals(version1, other.version1) && Objects.equals(version2, other.version2);
	}

}
