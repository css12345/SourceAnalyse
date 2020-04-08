package io.github.css12345.sourceanalyse.display;

import static org.junit.Assert.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import io.github.css12345.sourceanalyse.jdtparse.Params;
import io.github.css12345.sourceanalyse.persistence.support.ProjectSaver;
import io.github.css12345.sourceanalyse.similarityanalyse.support.ProjectComparator;

@SpringBootTest
public class TestLoads {
	@Autowired
	private Params params;
	
	@Autowired
	private ProjectSaver projectSaver;
	
	@Autowired
	private ProjectComparator projectComparator;
	
	@Test
	public void testLoads() {
		assertNotNull(params);
		assertNotNull(projectSaver);
		assertNotNull(projectComparator);
	}
}
