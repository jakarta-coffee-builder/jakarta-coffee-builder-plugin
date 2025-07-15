/*
 * Copyright 2024 Diego Silva diego.silva at apuntesdejava.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.apuntesdejava.jakartacoffeebuilder.mojo.persistence;

import com.apuntesdejava.jakartacoffeebuilder.helper.JakartaEeHelper;
import com.apuntesdejava.jakartacoffeebuilder.util.CoffeeBuilderUtil;
import com.apuntesdejava.jakartacoffeebuilder.util.MavenProjectUtil;
import com.apuntesdejava.jakartacoffeebuilder.util.PomUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.project.ProjectBuildingException;

import java.io.IOException;

import static com.apuntesdejava.jakartacoffeebuilder.util.Constants.JAKARTAEE_VERSION_10;

/**
 * Mojo for adding persistence configuration to a Jakarta EE project.
 * <p>
 * This Mojo ensures that the required Jakarta EE dependencies are present in the project
 * and creates a `persistence.xml` file with the specified persistence unit name.
 * </p>
 * <p>
 * Usage:
 * <ul>
 *   <li>Configure the Mojo in the Maven POM file.</li>
 *   <li>Specify the Jakarta EE version and persistence unit name as parameters.</li>
 * </ul>
 * <p>
 * <p>
 * This Mojo is part of the Jakarta Coffee Builder Plugin and simplifies the configuration
 * of Jakarta EE persistence in Maven projects.
 * </p>
 *
 * @author Diego Silva diego.silva at apuntesdejava.com
 */
@Mojo(
    name = "add-persistence"
)
public class AddPersistenceMojo extends AddAbstractPersistenceMojo {

    /**
     * Default constructor.<br/>
     * This constructor is used by Maven to create an instance of this Mojo.
     */
    public AddPersistenceMojo() {
    }

    /**
     * Executes the Mojo to add persistence configuration to the project.
     * <p>
     * This method checks for required Jakarta EE dependencies and creates a `persistence.xml` file
     * with the specified persistence unit name.
     * </p>
     *
     * @throws MojoExecutionException if an error occurs during execution.
     * @throws MojoFailureException   if a required dependency is missing or cannot be resolved.
     */

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        var log = getLog();
        log.debug("Project name:%s".formatted(mavenProject.getName()));
        checkDependency(log);
        createPersistenceXml(log);
        var json = getDataSourceParameters();
        createPersistenceUnit(json);
        try {
            addDataSourceConfiguration(log, json);
        } catch (ProjectBuildingException | IOException e) {
            throw new MojoExecutionException(e);
        }

    }

    /**
     * Checks and adds required Jakarta EE dependencies to the project.
     * <p>
     * This method ensures that the project has the necessary Jakarta EE dependencies for
     * CDI, Persistence, and Data. If any dependency is missing, it is added to the project.
     * </p>
     *
     * @param log the Maven logger instance.
     * @throws MojoExecutionException if an error occurs while resolving dependencies.
     */
    private void checkDependency(Log log) throws MojoExecutionException {
        log.debug("checking Jakarta Persistence dependency");
        try {
            var fullProject = MavenProjectUtil.getFullProject(mavenSession, projectBuilder, mavenProject);
            var jakartaEeVersion = PomUtil.getJakartaEeCurrentVersion(fullProject, log).orElseThrow();

            var jakartaEeHelper = JakartaEeHelper.getInstance();
            if (jakartaEeHelper.hasNotJakartaCdiDependency(fullProject, log))
                jakartaEeHelper.addJakartaCdiDependency(mavenProject, log, jakartaEeVersion);
            if (jakartaEeHelper.hasNotJakartaPersistenceDependency(fullProject, log))
                jakartaEeHelper.addJakartaPersistenceDependency(mavenProject, log, jakartaEeVersion);
            if (!StringUtils.equals(jakartaEeVersion, JAKARTAEE_VERSION_10)
                && jakartaEeHelper.hasNotJakartaDataDependency(fullProject, log)
                && jakartaEeHelper.isValidAddJakartaDataDependency(fullProject, log))
                jakartaEeHelper.addJakartaDataDependency(mavenProject, log, jakartaEeVersion);

            jakartaEeHelper.addPersistenceClassProvider(mavenProject, log);
            CoffeeBuilderUtil.getJdbcConfiguration(url)
                             .ifPresent(definition ->
                                 jakartaEeHelper.checkDataDependencies(fullProject, log, definition));

        } catch (ProjectBuildingException | IOException ex) {
            log.error(ex);
            throw new MojoExecutionException("Error resolving dependencies", ex);

        }
    }

    private void createPersistenceXml(Log log) {
        var currentPath = mavenProject.getFile().toPath().getParent();
        JakartaEeHelper.getInstance().createPersistenceXml(currentPath, log, persistenceUnitName);

    }

}
