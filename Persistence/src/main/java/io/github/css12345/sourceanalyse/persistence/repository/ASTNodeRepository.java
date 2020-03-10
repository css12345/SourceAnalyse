package io.github.css12345.sourceanalyse.persistence.repository;

import org.springframework.data.neo4j.repository.Neo4jRepository;

import io.github.css12345.sourceanalyse.persistence.entity.ASTNode;

public interface ASTNodeRepository extends Neo4jRepository<ASTNode, Long> {

}
