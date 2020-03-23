package io.github.css12345.sourceanalyse.persistence.repository;

import java.util.List;

import org.springframework.data.neo4j.annotation.Depth;
import org.springframework.data.neo4j.repository.Neo4jRepository;

import io.github.css12345.sourceanalyse.persistence.entity.FileInformation;

public interface FileInformationRepository extends Neo4jRepository<FileInformation, Long> {
	List<FileInformation> findByVersion(String version, @Depth int depth);

	FileInformation findByFilePath(String filePath);

}
