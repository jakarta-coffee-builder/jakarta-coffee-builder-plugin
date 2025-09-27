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

import com.apuntesdejava.jakartacoffeebuilder.util.PomUtil;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.project.ProjectBuildingException;

import java.io.IOException;


/**
 * Mojo that adds a data source to the Jakarta EE project.
 * <p>
 * This Mojo is invoked with the goal <code>add-datasource</code> and is responsible for
 * adding a new data source configuration to the project, updating the persistence unit,
 * and ensuring all necessary dependencies are present.
 * </p>
 */
@Mojo(
    name = "add-datasource"
)
public class AddDataSourceMojo extends AddAbstractPersistenceMojo {

    /**
     * Parameters used by this Mojo:
     * <ul>
     *     <li>{@code datasourceName}: The name of the data source to be added.</li>
     *     <li>{@code persistenceUnitName}: The name of the persistence unit to which the data source will be added.</li>
     *     <li>{@code declare}: A boolean flag indicating whether to declare the data source.</li>
     * </ul>
     * <p>
     * Default constructor for the AddDataSourceMojo class.
     * <p>
     * This constructor initializes the Mojo with default values.
     * </p>
     */
    public AddDataSourceMojo() {
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
        try {
            var log = getLog();
            init();

            log.debug("Project name:%s".formatted(mavenProject.getName()));
            log.info("Adding datasource %s".formatted(datasourceName));
            var json = getDataSourceParameters();
            createPersistenceUnit(json);
            addDataSourceConfiguration(log, json);
            PomUtil.saveMavenProject(fullProject, log);

        } catch (IOException | ProjectBuildingException e) {
            throw new MojoExecutionException(e);
        }
    }

}
