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
import com.apuntesdejava.jakartacoffeebuilder.helper.PersistenceXmlHelper;
import com.apuntesdejava.jakartacoffeebuilder.util.CoffeeBuilderUtil;
import com.apuntesdejava.jakartacoffeebuilder.util.MavenProjectUtil;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonValue;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.ProjectBuilder;
import org.apache.maven.project.ProjectBuildingException;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static com.apuntesdejava.jakartacoffeebuilder.util.Constants.CLASS_NAME;
import static com.apuntesdejava.jakartacoffeebuilder.util.Constants.DATASOURCE_DECLARE_WEB;
import static com.apuntesdejava.jakartacoffeebuilder.util.Constants.NAME;
import static com.apuntesdejava.jakartacoffeebuilder.util.DataSourceUtil.validateDataSourceName;

/**
 * An abstract base class for Maven Mojos that handle the addition of persistence-related configurations,
 * such as data sources and persistence units. This class provides common parameters and helper methods
 * to simplify the creation of concrete Mojo implementations.
 */
public abstract class AddAbstractPersistenceMojo extends AbstractMojo {

    /**
     * The current Maven project instance. This is automatically injected by Maven.
     */
    @Parameter(defaultValue = "${project}",
        readonly = true)
    protected MavenProject mavenProject;
    /**
     * The JNDI name for the data source.
     */
    @Parameter(
        property = "datasource-name",
        required = true,
        defaultValue = "defaultDatasource"
    )
    protected String datasourceName;
    /**
     * The JDBC URL for the database connection.
     */
    @Parameter(
        property = "url",
        defaultValue = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1"
    )
    protected String url;
    /**
     * The password for the database user.
     */
    @Parameter(
        property = "password"
    )
    protected String password;
    /**
     * The username for the database connection.
     */
    @Parameter(
        property = "user"
    )
    protected String user;
    /**
     * The database server name. Used for data sources that require it.
     */
    @Parameter(
        property = "server-name"
    )
    protected String serverName;
    /**
     * The port number for the database server.
     */
    @Parameter(
        property = "port-number"
    )
    protected Integer portNumber;
    /**
     * A comma-separated list of additional properties for the data source.
     * (e.g., "prop1=value1,prop2=value2").
     */
    @Parameter(
        property = "properties"
    )
    protected String properties;
    /**
     * Specifies where the data source should be declared.
     * For example, 'web' for web.xml or a server-specific location.
     */
    @Parameter(
        property = "declare",
        required = true,
        defaultValue = DATASOURCE_DECLARE_WEB
    )
    protected String declare;

    /**
     * The Maven Project Builder component, used to build a full Maven project from a POM file.
     */
    @Component
    protected ProjectBuilder projectBuilder;

    /**
     * The current Maven session. This is automatically injected by Maven.
     */
    @Parameter(
        defaultValue = "${session}",
        readonly = true,
        required = true
    )
    protected MavenSession mavenSession;

    /**
     * The name of the persistence unit to be created or updated in the {@code persistence.xml} file.
     */
    @Parameter(
        defaultValue = "defaultPU",
        property = "persistence-unit-name"
    )
    protected String persistenceUnitName;
    /**
     * Holds the fully resolved Maven project, including all dependencies and inherited profiles.
     * This is initialized by the {@link #init()} method.
     */
    protected MavenProject fullProject;

    public AddAbstractPersistenceMojo(){

    }

    /**
     * Initializes the Mojo by resolving the full Maven project, which includes all dependencies and parent POM data.
     * This method should be called at the beginning of the {@code execute} method in subclasses.
     *
     * @throws ProjectBuildingException if the full project cannot be resolved.
     */
    protected void init() throws ProjectBuildingException {
        this.fullProject = MavenProjectUtil.getFullProject(mavenSession, projectBuilder, mavenProject);

    }

    /**
     * Gathers all the data source-related parameters provided to the Mojo and organizes them into a
     * {@link JsonObject}. It also validates the data source name.
     *
     * @return A {@link JsonObject} containing the key-value pairs of the data source configuration.
     */
    protected JsonObject getDataSourceParameters() {
        datasourceName = validateDataSourceName(declare, datasourceName);
        var jsonBuilder = Json.createObjectBuilder()
            .add(NAME, datasourceName);

        if (StringUtils.isNotBlank(serverName)) {
            jsonBuilder.add("serverName", serverName);
        }
        if (portNumber != null) {
            jsonBuilder.add("portNumber", portNumber);
        }
        if (StringUtils.isNotBlank(url)) {
            jsonBuilder.add("url", url);
        }
        if (StringUtils.isNotBlank(user)) {
            jsonBuilder.add("user", user);
        }
        if (StringUtils.isNotBlank(password)) {
            jsonBuilder.add("password", password);
        }
        if (StringUtils.isNotBlank(properties)) {
            var propertiesBuilder = Json.createArrayBuilder();
            Arrays.stream(properties.split(",")).forEach(propertiesBuilder::add);
            jsonBuilder.add("properties", propertiesBuilder);
        }
        return jsonBuilder.build();
    }

    /**
     * Creates or updates a persistence unit in the {@code persistence.xml} file, associating it with the
     * configured data source.
     *
     * @param json The {@link JsonObject} containing the data source parameters, primarily to get the data source name.
     * @throws ProjectBuildingException if there is an error processing the project configuration.
     */
    protected void createPersistenceUnit(JsonObject json) throws ProjectBuildingException {
        var log = getLog();
        if (StringUtils.isNotBlank(persistenceUnitName)) {
            PersistenceXmlHelper
                .getInstance()
                .addDataSourceToPersistenceXml(fullProject, log, persistenceUnitName,
                    json.getString(NAME));
        }
    }

    /**
     * Adds the necessary JDBC driver dependencies to the project and configures the data source in the
     * location specified by the {@code declare} parameter (e.g., {@code web.xml}).
     *
     * @param log  The Maven plugin logger.
     * @param json The {@link JsonObject} containing the data source parameters.
     * @throws ProjectBuildingException if there is an error processing the project configuration.
     * @throws IOException              if an I/O error occurs.
     */
    protected void addDataSourceConfiguration(Log log, JsonObject json) throws ProjectBuildingException, IOException {
        var jakartaEeHelper = JakartaEeHelper.getInstance();
        CoffeeBuilderUtil.getJdbcConfiguration(url)
            .ifPresent(definition -> {
                jakartaEeHelper.checkDataDependencies(fullProject, log, definition);
                jakartaEeHelper.addDataSource(fullProject, log, declare,
                    getDataSourceProperties(json, definition.getString("dataSourceClass"))
                );
            });

//        CoffeeBuilderUtil.updateProjectConfiguration(mavenProject.getFile().toPath().getParent(), "jdbc", json);
    }

    private JsonObject getDataSourceProperties(JsonObject json, String className) {
        var newValues = Json.createObjectBuilder(json)
            .add(CLASS_NAME, className).build();
        var dataSourceProps = Json.createObjectBuilder();

        List<String> datasourceProperties = Arrays.asList(
            "description",
            "name",
            "className",
            "serverName",
            "portNumber",
            "databaseName",
            "url",
            "user",
            "password",
            "property",
            "loginTimeout",
            "transactional",
            "isolationLevel",
            "initialPoolSize",
            "maxPoolSize",
            "minPoolSize",
            "maxIdleTime",
            "maxStatements");
        datasourceProperties.stream().filter(newValues::containsKey).forEach(prop -> {
            var value = newValues.get(prop);
            var type = value.getValueType();
            if (type == JsonValue.ValueType.OBJECT) {
                dataSourceProps.add(prop, value.asJsonObject());
            } else {
                dataSourceProps.add(prop, newValues.getString(prop));
            }
        });
        return dataSourceProps.build();
    }

}
