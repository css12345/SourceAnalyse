package io.github.css12345.sourceanalyse.similarityanalyse.support;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import io.github.css12345.sourceanalyse.persistence.entity.Method;
import io.github.css12345.sourceanalyse.persistence.repository.MethodRepository;
import io.github.css12345.sourceanalyse.similarityanalyse.entity.MethodCompare;
import io.github.css12345.sourceanalyse.similarityanalyse.support.TestProjectComparator.Config;

@SpringJUnitConfig(Config.class)
public class TestMethodComparator {
	
	@Autowired
	private MethodRepository methodRepository;
	
	@Autowired
	private MethodComparator methodComparator;
	
	@Test
	public void testMethodCompare() {
		MethodCompare methodCompare = new MethodCompare();
		List<Method> methods = methodRepository.findByBriefMethodInformation("writeJSONString-com.alibaba.fastjson.JSON-java.lang.Appendable-");
		methodCompare.setMethod1(methods.get(0));
		methodCompare.setMethod2(methods.get(1));
		methodComparator.compare(methodCompare);
		System.out.println(methodCompare.getSimilarity());
	}
}
