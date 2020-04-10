package io.github.css12345.sourceanalyse.similarityanalyse.entity;

public class MethodCompare {
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

}
