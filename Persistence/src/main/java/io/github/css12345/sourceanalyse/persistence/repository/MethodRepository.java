package io.github.css12345.sourceanalyse.persistence.repository;

import java.util.List;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;

import io.github.css12345.sourceanalyse.persistence.entity.Method;

public interface MethodRepository extends Neo4jRepository<Method, String> {
	List<Method> findByFilePath(String filePath);
	
	Method findByBriefMethodInformationAndVersion(String briefMethodInformation, String version);
	
	@Query("match (invocatedMethod:Method)<-[:INVOCATE]-(method:Method) where invocatedMethod.briefMethodInformation = {briefMethodInformation} and invocatedMethod.version = {version} return method")
	List<Method> getRelatedMethods(String briefMethodInformation, String version);
}
