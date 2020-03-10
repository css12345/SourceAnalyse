package io.github.css12345.sourceanalyse.persistence.entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

@NodeEntity
public class Method {
	@Id
	@GeneratedValue
	private Long id;

	private String filePath;

	private String version;

	private String briefMethodInformation;

	@Relationship("INVOCATE")
	private List<Method> methodInvocations = new ArrayList<>();

	@Relationship("CONTAIN")
	private ASTNode rootNode;

	public Method() {

	}

	public Method(String briefMethodInformation) {
		this.briefMethodInformation = briefMethodInformation;
	}

	public Method(String filePath, String version, String briefMethodInformation) {
		this.filePath = filePath;
		this.version = version;
		this.briefMethodInformation = briefMethodInformation;
	}

	public void invocate(Method method) {
		methodInvocations.add(method);
	}

	public void invocate(Collection<Method> methods) {
		for (Method method : methods) {
			this.invocate(method);
		}
	}

	public void invocate(Method... methods) {
		for (Method method : methods) {
			this.invocate(method);
		}
	}

	public ASTNode getRootNode() {
		return rootNode;
	}

	public void setRootNode(ASTNode rootNode) {
		this.rootNode = rootNode;
	}

	public List<Method> getMethodInvocations() {
		return this.methodInvocations;
	}

	public String getFilePath() {
		return filePath;
	}

	public String getVersion() {
		return version;
	}

	public String getBriefMethodInformation() {
		return briefMethodInformation;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public void setBriefMethodInformation(String briefMethodInformation) {
		this.briefMethodInformation = briefMethodInformation;
	}

	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("Method [filePath=");
		stringBuilder.append(filePath);
		stringBuilder.append(", version=");
		stringBuilder.append(version);
		stringBuilder.append(", briefMethodInformation=");
		stringBuilder.append(briefMethodInformation);
		stringBuilder.append(", methodInvocations=");
		stringBuilder.append("[");
		for (int i = 0;i < methodInvocations.size();i++) {
			Method methodInvocation = methodInvocations.get(i);
			stringBuilder.append(methodInvocation.getFilePath());
			stringBuilder.append("-");
			stringBuilder.append(methodInvocation.getBriefMethodInformation());
			if (i < methodInvocations.size() - 1)
				stringBuilder.append(",");
		}
		stringBuilder.append("]");
		stringBuilder.append(", rootNode=");
		stringBuilder.append(rootNode.getType());
		return stringBuilder.toString();
	}

}
