package io.github.css12345.sourceanalyse.jdtparse.entity;

public class ASTNodeInformation {
	private String content;

	private String type;

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
		this.type = type;
	}

	@Override
	public String toString() {
		return "ASTNodeInformation [content=" + content + ", type=" + type + "]";
	}

}
