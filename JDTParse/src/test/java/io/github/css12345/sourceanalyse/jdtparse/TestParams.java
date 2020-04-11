package io.github.css12345.sourceanalyse.jdtparse;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;

import org.apache.commons.io.IOUtils;
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
		assertEquals(IOUtils.readLines(new ClassPathResource("includedNodeTypes.txt").getInputStream(),"UTF-8"), params.getMethodDeclarationIncludeTypes());
		assertEquals(IOUtils.toString(new ClassPathResource("gradleTasks.txt").getInputStream(), "UTF-8"), params.getGradleTasks());
		assertEquals(IOUtils.readLines(new ClassPathResource("specialNodes.txt").getInputStream(), "UTF-8"), params.getSpecialNodes());
		assertEquals("D:\\Users\\cs\\gradle", params.getGRADLE_USER_HOME());
	}
}
