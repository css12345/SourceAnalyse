package io.github.css12345.sourceanalyse.persistence.repository;

import java.util.List;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;

import io.github.css12345.sourceanalyse.persistence.entity.ASTNode;

public interface ASTNodeRepository extends Neo4jRepository<ASTNode, String> {
	@Query("match (parentNode:ASTNode)-[:CHILD]->(childNode:ASTNode) where parentNode.id = {parentNodeId} return childNode")
	List<ASTNode> findChildNodesByParentNodeId(String parentNodeId);
}
