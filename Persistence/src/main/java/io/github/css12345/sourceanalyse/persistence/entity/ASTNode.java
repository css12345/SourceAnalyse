package io.github.css12345.sourceanalyse.persistence.entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

@NodeEntity
public class ASTNode {
	@Id
	@GeneratedValue
	private Long id;

	private String content;

	private String type;

	private transient ASTNode parentNode;

	@Relationship("CHILD")
	private List<ASTNode> childs = new ArrayList<>();

	public ASTNode() {

	}

	public ASTNode(String content, String type) {
		this.content = content;
		this.type = type;
	}

	public void addChild(ASTNode astNode) {
		this.childs.add(astNode);
	}

	public void addChilds(Collection<ASTNode> astNodes) {
		for (ASTNode astNode : astNodes) {
			this.addChild(astNode);
		}
	}

	public void addChilds(ASTNode... astNodes) {
		for (ASTNode astNode : astNodes) {
			this.addChild(astNode);
		}
	}

	public String getContent() {
		return content;
	}

	public String getType() {
		return type;
	}

	public List<ASTNode> getChilds() {
		return childs;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public void setType(String type) {
		this.type = type;
	}

	public ASTNode getParentNode() {
		return parentNode;
	}

	public void setParentNode(ASTNode parentNode) {
		this.parentNode = parentNode;
	}

	@Override
	public String toString() {
		return "ASTNode [content=" + content + ", type=" + type + "]";
	}

}
