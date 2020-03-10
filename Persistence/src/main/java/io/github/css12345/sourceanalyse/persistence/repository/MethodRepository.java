package io.github.css12345.sourceanalyse.persistence.repository;

import org.springframework.data.neo4j.repository.Neo4jRepository;

import io.github.css12345.sourceanalyse.persistence.entity.Method;

public interface MethodRepository extends Neo4jRepository<Method, Long> {
	Method findByFilePathAndBriefMethodInformation(String filePath, String briefMethodInformation);
}
