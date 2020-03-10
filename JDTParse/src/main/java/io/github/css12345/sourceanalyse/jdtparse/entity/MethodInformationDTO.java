package io.github.css12345.sourceanalyse.jdtparse.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodInvocation;

public class MethodInformationDTO {

	/**
	 * @see FileInformationDTO#filePath
	 */
	private String filePath;

	/**
	 * @see FileInformationDTO#version
	 */
	private String version;

	/**
	 * format:method name-class name-arg1-arg2-...<br>
	 * eg:setBriefMethodInformation-io.github.css12345.sourceanalyse.jdtparse.entity.MethodInformationDTO-String
	 */
	private String briefMethodInformation;

	/**
	 * key is the ith node in {@link #nodes}, value is the called method's
	 * {@link filePath}-{@link briefMethodInformation}.
	 */
	private Map<Integer, String> methodInvocationsMap = new HashMap<>();
	/**
	 * {@link MethodInformation#nodes} 's type and content
	 */
	private List<ASTNodeInformation> nodes;

	/**
	 * @see MethodInformation#edges
	 */
	private List<List<Integer>> edges;

	public MethodInformationDTO() {

	}

	public MethodInformationDTO(MethodInformation methodInformation, FileInformationDTO fileInformationDTO) {
		this.filePath = fileInformationDTO.getFilePath();
		this.version = fileInformationDTO.getVersion();
		this.edges = methodInformation.getEdges();

		this.briefMethodInformation = generateBriefMethodInformation(
				methodInformation.getMethodDeclaration().resolveBinding());
		this.nodes = generateNodes(methodInformation.getNodes());

		for (MethodInvocation methodInvocation : methodInformation.getMethodInvocations()) {
			int index = methodInformation.getNodes().indexOf(methodInvocation);
			IMethodBinding methodBinding = methodInvocation.resolveMethodBinding();
			String qualifiedName = methodBinding.getDeclaringClass().getQualifiedName();
			String location = FileInformation.getClassQualifiedNameLocationMap().get(qualifiedName);
			String briefMethodInformationOfCalledMethod = generateBriefMethodInformation(methodBinding);
			methodInvocationsMap.put(index, String.format("%s-%s", location, briefMethodInformationOfCalledMethod));
		}
	}

	private List<ASTNodeInformation> generateNodes(List<ASTNode> nodes) {
		List<ASTNodeInformation> astNodeInformations = new ArrayList<>();
		for (ASTNode node : nodes) {
			ASTNodeInformation astNodeInformation = new ASTNodeInformation();
			astNodeInformation.setContent(node.toString());
			astNodeInformation.setType(node.getClass().getSimpleName());
			astNodeInformations.add(astNodeInformation);
		}
		return astNodeInformations;
	}

	private String generateBriefMethodInformation(IMethodBinding methodBinding) {
		String methodName = methodBinding.getName();
		String className = methodBinding.getDeclaringClass().getBinaryName();
		StringBuilder informationBuilder = new StringBuilder();
		informationBuilder.append(methodName);
		informationBuilder.append('-');
		informationBuilder.append(className);
		informationBuilder.append('-');
		ITypeBinding[] parameters = methodBinding.getParameterTypes();
		for (ITypeBinding parameter : parameters) {
			informationBuilder.append(parameter.getQualifiedName());
			informationBuilder.append('-');
		}
		return informationBuilder.toString();
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getVersion() {
		return version;
	}

	public String getBriefMethodInformation() {
		return briefMethodInformation;
	}

	public Map<Integer, String> getMethodInvocationsMap() {
		return methodInvocationsMap;
	}

	public List<ASTNodeInformation> getNodes() {
		return nodes;
	}

	public List<List<Integer>> getEdges() {
		return edges;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public void setBriefMethodInformation(String briefMethodInformation) {
		this.briefMethodInformation = briefMethodInformation;
	}

	public void setMethodInvocationsMap(Map<Integer, String> methodInvocationsMap) {
		this.methodInvocationsMap = methodInvocationsMap;
	}

	public void setOfNodes(List<ASTNodeInformation> nodes) {
		this.nodes = nodes;
	}

	public void setEdges(List<List<Integer>> edges) {
		this.edges = edges;
	}

	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("filePath:" + filePath + "\n");
		stringBuilder.append("version:" + version + "\n");
		stringBuilder.append("briefMethodInformation:" + briefMethodInformation + "\n");
		stringBuilder.append("methodInvocationsMap:" + methodInvocationsMap + "\n");
		stringBuilder.append("nodes:");
		for (int i = 0; i < nodes.size(); i++) {
			ASTNodeInformation astNodeInformation = nodes.get(i);
			stringBuilder.append(astNodeInformation.getType());
			if (i < nodes.size() - 1)
				stringBuilder.append(",");
			else 
				stringBuilder.append("\n");
		}

		stringBuilder.append("edges:" + edges + "\n");

		return stringBuilder.toString();
	}

}
