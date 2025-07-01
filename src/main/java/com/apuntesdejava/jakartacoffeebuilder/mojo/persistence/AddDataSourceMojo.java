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

import com.apuntesdejava.jakartacoffeebuilder.builder.DataSourceParameterBuilder;
import com.apuntesdejava.jakartacoffeebuilder.config.DataSourceConfigProvider;
import com.apuntesdejava.jakartacoffeebuilder.helper.JakartaEeHelper;
import com.apuntesdejava.jakartacoffeebuilder.helper.PersistenceXmlHelper;
import com.apuntesdejava.jakartacoffeebuilder.util.CoffeeBuilderUtil;
import com.apuntesdejava.jakartacoffeebuilder.util.MavenProjectUtil;
import jakarta.json.JsonObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.ProjectBuilder;
import org.apache.maven.project.ProjectBuildingException;

import java.io.IOException;

import static com.apuntesdejava.jakartacoffeebuilder.util.Constants.DATASOURCE_DECLARE_WEB;
import static com.apuntesdejava.jakartacoffeebuilder.util.Constants.NAME;
import static com.apuntesdejava.jakartacoffeebuilder.util.DataSourceUtil.validateDataSourceName;

/**
 * Mojo implementation for adding a data source to a Jakarta EE project.
 * <p>
 * This Mojo performs the following tasks:
 * <ul>
 *   <li>Validates the data source name to ensure it follows the required format.</li>
 *   <li>Generates the necessary parameters for the data source configuration.</li>
 *   <li>Adds the data source to the project configuration based on the specified declaration type.</li>
 *   <li>Creates a persistence unit in the `persistence.xml` file if specified.</li>
 *   <li>Adds the JDBC driver dependency to the Maven project if required.</li>
 * </ul>
 * <p>
 * Configuration Parameters:
 * <ul>
 *   <li><code>name</code>: The name of the data source (required).</li>
 *   <li><code>declare</code>: The declaration type for the data source (default: `java:global/jdbc`).</li>
 *   <li><code>coordinates-jdbc</code>: The Maven coordinates of the JDBC driver (default: `com.h2database:h2`).</li>
 *   <li><code>class-name</code>: The fully qualified class name of the JDBC data source (default: `org.h2.jdbcx.JdbcDataSource`).</li>
 *   <li><code>url</code>: The JDBC URL for the data source (optional).</li>
 *   <li><code>user</code>: The username for the data source (optional).</li>
 *   <li><code>password</code>: The password for the data source (optional).</li>
 *   <li><code>server-name</code>: The server name for the data source (optional).</li>
 *   <li><code>port-number</code>: The port number for the data source (optional).</li>
 *   <li><code>properties</code>: Additional properties for the data source, separated by commas (optional).</li>
 *   <li><code>persistence-unit</code>: The name of the persistence unit to be created (optional).</li>
 * </ul>
 * <p>
 * Execution:
 * <ul>
 *   <li>Validates the data source name using a regular expression.</li>
 *   <li>Generates a JSON object with the data source parameters.</li>
 *   <li>Updates the project configuration with the data source details.</li>
 *   <li>Creates or updates the `persistence.xml` file with the data source information.</li>
 *   <li>Adds the JDBC driver dependency to the `pom.xml` file if specified.</li>
 * </ul>
 * <p>
 * Exceptions:
 * <ul>
 *   <li>Throws <code>MojoExecutionException</code> if an error occurs during execution.</li>
 *   <li>Throws <code>MojoFailureException</code> if the data source name is invalid.</li>
 * </ul>
 * <p>
 * This Mojo is part of the Jakarta Coffee Builder Plugin and is designed to simplify
 * the configuration of Jakarta EE projects.
 * </p>
 *
 * @author Diego Silva
 */
@Mojo(
    name = "add-datasource"
)
public class AddDataSourceMojo extends AbstractMojo implements DataSourceConfigProvider {
    @Parameter(
        property = NAME,
        required = true
    )
    private String datasourceName;

    @Parameter(
        property = "declare",
        required = true,
        defaultValue = DATASOURCE_DECLARE_WEB
    )
    private String declare;

    @Parameter(
        property = "class-name",
        required = true,
        defaultValue = "org.h2.jdbcx.JdbcDataSource"
    )
    private String className;

    @Parameter(
        property = "url"
    )
    private String url;

    @Parameter(
        property = "password"
    )
    private String password;

    @Parameter(
        property = "user"
    )
    private String user;

    @Parameter(
        property = "server-name"
    )
    private String serverName;

    @Parameter(
        property = "port-number"
    )
    private Integer portNumber;

    @Parameter(
        property = "properties"
    )
    private String properties;

    @Parameter(
        property = "persistence-unit"
    )
    private String persistenceUnit;

    @Parameter(defaultValue = "${project}", readonly = true)
    private MavenProject mavenProject;
    @Parameter(
        defaultValue = "${session}",
        readonly = true,
        required = true
    )
    private MavenSession mavenSession;

    @Component
    private ProjectBuilder projectBuilder;
    /**
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
            datasourceName = validateDataSourceName(declare, datasourceName);
            log.debug("Project name:%s".formatted(mavenProject.getName()));
            log.info("Adding datasource %s".formatted(datasourceName));
            var json = new DataSourceParameterBuilder(this).build();
            createPersistenceUnit(json);
            var jakartaEeHelper = JakartaEeHelper.getInstance();
            var fullProject = MavenProjectUtil.getFullProject(mavenSession, projectBuilder, mavenProject);

            jakartaEeHelper.addDataSource(mavenProject, log, declare, json);
            CoffeeBuilderUtil.getJdbcConfiguration(MavenProjectUtil.getParent(mavenProject))
                             .ifPresent(definition ->
                                 jakartaEeHelper.checkDataDependencies(fullProject, log, definition));

            CoffeeBuilderUtil.updateProjectConfiguration(mavenProject.getFile().toPath().getParent(), "jdbc", json);
        } catch (IOException | ProjectBuildingException e) {
            throw new MojoExecutionException(e);
        }
    }

    private void createPersistenceUnit(JsonObject json) {
        var log = getLog();
        if (StringUtils.isNotBlank(persistenceUnit)) {
            var currentPath = mavenProject.getFile().toPath().getParent();
            PersistenceXmlHelper
                .getInstance()
                .addDataSourceToPersistenceXml(currentPath, log, persistenceUnit,
                    json.getString(NAME));
        }
    }

    @Override
    public String getDatasourceName() {
        return datasourceName;
    }

    @Override
    public String getServerName() {
        return serverName;
    }

    @Override
    public Integer getPortNumber() {
        return portNumber;
    }

    @Override
    public String getUrl() {
        return url;
    }

    @Override
    public String getUser() {
        return user;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getProperties() {
        return properties;
    }
}
