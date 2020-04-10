package io.github.css12345.sourceanalyse.similarityanalyse.support;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import io.github.css12345.sourceanalyse.similarityanalyse.entity.MethodCompare;
import io.github.css12345.sourceanalyse.similarityanalyse.support.TestProjectComparator.Config;

@SpringJUnitConfig(Config.class)
public class TestMethodComparator {

	@Autowired
	private MethodComparator methodComparator;

	@Test
	public void testMethodCompare() {
		final String briefMethodInformation = "writeJSONString-com.alibaba.fastjson.JSON-java.lang.Appendable-";
		MethodCompare methodCompare = new MethodCompare(briefMethodInformation, "1.1.44", briefMethodInformation,
				"1.1.157");
		methodComparator.compare(methodCompare);
		System.out.println(methodCompare.getSimilarity());
	}
}
