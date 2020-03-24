package io.github.css12345.sourceanalyse.jdtparse.entity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class ASTNodeInformation {
	private String content;

	private String type;
	
	private static Set<String> nameOfSpecialNodes = new HashSet<>();
	
	public static void add(String name) {
		nameOfSpecialNodes.add(name);
	}
	
	public static void addAll(Collection<String> names) {
		nameOfSpecialNodes.addAll(names);
	}
	
	public static void addAll(String[] names) {
		nameOfSpecialNodes.addAll(new ArrayList<>(Arrays.asList(names)));
	}

	public String getContent() {
		return content;
	}

	public String getType() {
		return type;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public void setType(String type) {
		if (nameOfSpecialNodes.contains(type))
			this.type = type + ':' + this.content;
		else
			this.type = type;
	}

	@Override
	public String toString() {
		return "ASTNodeInformation [content=" + content + ", type=" + type + "]";
	}

}
