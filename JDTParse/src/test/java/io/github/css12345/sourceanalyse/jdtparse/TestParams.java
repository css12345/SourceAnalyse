package io.github.css12345.sourceanalyse.jdtparse;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import io.github.css12345.sourceanalyse.jdtparse.Params;

@SpringJUnitConfig(ParamsPropertyConfig.class)
public class TestParams {

	@Autowired
	private Params params;
	
	@Test
	public void test() throws IOException {
		assertEquals(new ClassPathResource("includedNodeTypes.txt").getFile(), params.getMethodDeclarationIncludeTypesFile());
		assertEquals(new ClassPathResource("gradleTasks.txt").getFile(), params.getGradleTasksFile());
		assertEquals("D:\\Users\\cs\\gradle", params.getGRADLE_USER_HOME());
	}
}
