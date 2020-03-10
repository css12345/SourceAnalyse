package io.github.css12345.sourceanalyse.persistence.entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

@NodeEntity
public class FileInformation {
	@Id
	@GeneratedValue
	private Long id;

	private String filePath;

	private String rootProjectPath;

	private String version;

	@Relationship("HASMETHOD")
	private List<Method> methods = new ArrayList<>();

	public FileInformation() {

	}

	public FileInformation(String filePath, String rootProjectPath, String version) {
		this.filePath = filePath;
		this.rootProjectPath = rootProjectPath;
		this.version = version;
	}
	
	public void addMethod(Method method) {
		methods.add(method);
	}
	
	public void addMethods(Collection<Method> methods) {
		for (Method method : methods) {
			this.addMethod(method);
		}
	}
	
	public void addMethods(Method... methods) {
		for (Method method : methods) {
			this.addMethod(method);
		}
	}

	public String getFilePath() {
		return filePath;
	}

	public String getRootProjectPath() {
		return rootProjectPath;
	}

	public String getVersion() {
		return version;
	}

	public List<Method> getMethods() {
		return methods;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public void setRootProjectPath(String rootProjectPath) {
		this.rootProjectPath = rootProjectPath;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	@Override
	public String toString() {
		return "FileInformation [filePath=" + filePath + ", rootProjectPath=" + rootProjectPath + ", version=" + version
				+ ", methods=" + methods + "]";
	}

}
