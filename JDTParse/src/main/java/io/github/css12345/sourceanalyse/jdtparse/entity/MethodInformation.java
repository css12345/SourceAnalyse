package io.github.css12345.sourceanalyse.jdtparse.entity;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;

public class MethodInformation {
	private MethodDeclaration methodDeclaration;

	private List<MethodInvocation> methodInvocations = new ArrayList<>();

	private List<ASTNode> nodes = new ArrayList<>();

	private List<List<Integer>> edges = new ArrayList<>();

	public MethodDeclaration getMethodDeclaration() {
		return methodDeclaration;
	}

	public void setMethodDeclaration(MethodDeclaration methodDeclaration) {
		this.methodDeclaration = methodDeclaration;
	}

	public List<ASTNode> getNodes() {
		return nodes;
	}

	public void addNode(ASTNode node) {
		this.nodes.add(node);
	}

	public List<List<Integer>> getEdges() {
		return edges;
	}

	public void setEdges(List<List<Integer>> edges) {
		this.edges = edges;
	}

	public List<MethodInvocation> getMethodInvocations() {
		return methodInvocations;
	}

	public void setMethodInvocations(List<MethodInvocation> methodInvocations) {
		this.methodInvocations = methodInvocations;
	}

	@Override
	public String toString() {
		return "MethodInformation [methodDeclaration=" + methodDeclaration.getName() + ", methodInvocations="
				+ methodInvocations + ", nodes=" + nodes + ", edges=" + edges + "]";
	}

}
