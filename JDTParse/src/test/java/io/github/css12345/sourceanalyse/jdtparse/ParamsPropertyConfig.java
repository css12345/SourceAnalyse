package io.github.css12345.sourceanalyse.jdtparse;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.PropertySource;

@TestConfiguration
@PropertySource("classpath:params.properties")
@ComponentScan(excludeFilters = @Filter(classes = TestConfiguration.class))
public class ParamsPropertyConfig {

}
