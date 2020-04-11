package io.github.css12345.sourceanalyse.jdtparse;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import io.github.css12345.sourceanalyse.jdtparse.entity.ASTNodeInformation;
import io.github.css12345.sourceanalyse.jdtparse.entity.Project;
import io.github.css12345.sourceanalyse.jdtparse.utils.ProjectResolver;
import net.harawata.appdirs.AppDirs;
import net.harawata.appdirs.AppDirsFactory;

@Component
public class Params implements InitializingBean {
	private Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private Environment environment;

	@Autowired
	private ApplicationContext applicationContext;

	@Autowired
	private ProjectResolver projectResolver;

	private List<String> methodDeclarationIncludeTypes;
	private List<String> specialNodes;
	private String gradleTasks;
	private String GRADLE_USER_HOME;
	
	private String SITE_DATA_DIR;

	public void resolveProjects() {
		Map<String, Project> beansOfProjectMap = applicationContext.getBeansOfType(Project.class);
		if (logger.isInfoEnabled()) {
			logger.info("find beans in application context of project, specific value is {}", beansOfProjectMap);
		}

		for (Entry<String, Project> entry : beansOfProjectMap.entrySet()) {
			String name = entry.getKey();
			Project project = entry.getValue();
			if (project.getPath() == null) {
				logger.warn("the name {} of project's path can not be null, this project will be ignore", name);
			} else {
				if (logger.isInfoEnabled()) {
					logger.info("start to resolve project {}, name is {}", project, name);
				}

				Project resolvedProject = projectResolver.resolveProject(project);
				if (resolvedProject != null) {

					if (logger.isInfoEnabled()) {
						logger.info("add resolved project {} to projects", resolvedProject);
					}
				}
			}
		}
	}

	private void setProperties() throws IOException {
		methodDeclarationIncludeTypes = IOUtils.readLines(
				applicationContext.getResource(environment.getProperty("methodDeclaration.includedNodeTypes.location",
						"classpath:includedNodeTypes.txt")).getInputStream(),
				"UTF-8");
		if (logger.isInfoEnabled()) {
			logger.info("set methodDeclarationIncludeTypes {}", methodDeclarationIncludeTypes);
		}
		gradleTasks = IOUtils.toString(applicationContext
				.getResource(environment.getProperty("gradleTasks.location", "classpath:gradleTasks.txt"))
				.getInputStream(), "UTF-8");
		if (logger.isInfoEnabled()) {
			logger.info("set gradleTasks {}", gradleTasks);
		}
		specialNodes = IOUtils.readLines(applicationContext
				.getResource(environment.getProperty("ASTNode.specialNodes.location", "classpath:specialNodes.txt"))
				.getInputStream(), "UTF-8");
		if (logger.isInfoEnabled()) {
			logger.info("set specialNodes {}", specialNodes);
		}
		ASTNodeInformation.addAll(specialNodes);
		GRADLE_USER_HOME = environment.getProperty("GRADLE_USER_HOME");
		if (logger.isInfoEnabled()) {
			logger.info("set GRADLE_USER_HOME {}", GRADLE_USER_HOME);
		}
		
		SITE_DATA_DIR = environment.getProperty("site.data.dir", getSiteDataDir());
		if (logger.isInfoEnabled()) {
			logger.info("set SITE_DATA_DIR {}", SITE_DATA_DIR);
		}
	}

	private String getSiteDataDir() {
		AppDirs appDirs = AppDirsFactory.getInstance();
		return appDirs.getSiteDataDir("SourceAnalyse", null, null);
	}

	public List<String> getMethodDeclarationIncludeTypes() {
		return methodDeclarationIncludeTypes;
	}

	public List<String> getSpecialNodes() {
		return specialNodes;
	}

	public String getGradleTasks() {
		return gradleTasks;
	}

	public String getGRADLE_USER_HOME() {
		return GRADLE_USER_HOME;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		setProperties();
		resolveProjects();
	}

	public String getSITE_DATA_DIR() {
		return SITE_DATA_DIR;
	}
}
