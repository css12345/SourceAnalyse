package io.github.css12345.sourceanalyse.similarityanalyse.entity;

import io.github.css12345.sourceanalyse.persistence.entity.Method;

public class MethodCompare {
	private Method method1;

	private Method method2;

	private State state;

	private double similarity = 0;

	public MethodCompare(Method method1, Method method2) {
		this.method1 = method1;
		this.method2 = method2;
	}

	public MethodCompare() {

	}

	public Method getMethod1() {
		return method1;
	}

	public void setMethod1(Method method1) {
		this.method1 = method1;
	}

	public Method getMethod2() {
		return method2;
	}

	public void setMethod2(Method method2) {
		this.method2 = method2;
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
