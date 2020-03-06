package io.github.css12345.sourceanalyse.jdtparse.support;

import io.github.css12345.sourceanalyse.jdtparse.entity.Project;

/**
 * an interface try to resolve dependencies of project
 */
public interface DependencyManager {
	/**
	 * config project's dependencies and modules
	 * @param project a project(may has many module)
	 */
	void configureProjectDependencies(Project project);
}
