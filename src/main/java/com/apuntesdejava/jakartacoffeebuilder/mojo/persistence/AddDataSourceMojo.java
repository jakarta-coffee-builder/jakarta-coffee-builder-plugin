/*
 * Copyright 2024 Diego Silva <diego.silva at apuntesdejava.com>.
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
import com.apuntesdejava.jakartacoffeebuilder.util.PomUtil;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.RegexValidator;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.IOException;
import java.util.Arrays;

import static com.apuntesdejava.jakartacoffeebuilder.util.Constants.*;

/**
 * Mojo to add a datasource to the Maven project.
 */
@Mojo(
    name = "add-datasource"
)
public class AddDataSourceMojo extends AbstractMojo {
    @Parameter(
        property = "name",
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
        property = "coordinates-jdbc",
        required = true,
        defaultValue = "com.h2database:h2"
    )
    private String coordinatesJdbcDriver;

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

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        try {
            var log = getLog();
            validateDataSourceName();
            log.debug("Project name:%s".formatted(mavenProject.getName()));
            log.info("Adding datasource %s".formatted(datasourceName));
            var json = createDataSourceParameters();
            var jakartaEeHelper = JakartaEeHelper.getInstance();

            jakartaEeHelper.addDataSource(mavenProject, log, declare, json);
            createPersistenceUnit(json);
            addJdbcDriver();

            CoffeeBuilderUtil.updateProjectConfiguration(mavenProject.getFile().toPath().getParent(), "jdbc", json);
        } catch (IOException e) {
            throw new MojoExecutionException(e);
        }
    }

    private String getPrefix() {
        return switch (declare) {
            case DATASOURCE_DECLARE_WEB -> "java:global/";
            case DATASOURCE_DECLARE_CLASS -> "java:app/";
            default -> StringUtils.EMPTY;
        } + "jdbc/";
    }

    private void validateDataSourceName() {
        RegexValidator validator = new RegexValidator("^[a-zA-Z][a-zA-Z0-9]*$");
        if (!validator.isValid(datasourceName)) {
            throw new IllegalArgumentException("Invalid datasource name");
        }

        datasourceName = getPrefix() + datasourceName;
    }

    private void addJdbcDriver() throws MojoExecutionException {
        var log = getLog();
        if (StringUtils.isNotBlank(coordinatesJdbcDriver)) {
            PomUtil.addDependency(mavenProject, log, coordinatesJdbcDriver);
            PomUtil.saveMavenProject(mavenProject, log);
        }
    }

    private void createPersistenceUnit(JsonObject json) {
        var log = getLog();
        if (StringUtils.isNotBlank(persistenceUnit)) {
            var currentPath = mavenProject.getFile().toPath().getParent();
            PersistenceXmlHelper
                .getInstance()
                .addDataSourceToPersistenceXml(currentPath, log, persistenceUnit,
                    json.getString("name"));
        }
    }

    private JsonObject createDataSourceParameters() {
        var jsonBuilder = Json.createObjectBuilder()
                              .add("name", datasourceName)
                              .add(CLASS_NAME, className);
        if (StringUtils.isNotBlank(serverName)) jsonBuilder.add("serverName", serverName);
        if (portNumber != null) jsonBuilder.add("portNumber", portNumber);
        if (StringUtils.isNotBlank(url)) jsonBuilder.add("url", url);
        if (StringUtils.isNotBlank(user)) jsonBuilder.add("user", user);
        if (StringUtils.isNotBlank(password)) jsonBuilder.add("password", password);
        if (StringUtils.isNotBlank(properties)) {
            var propertiesBuilder = Json.createArrayBuilder();
            Arrays.stream(properties.split(",")).forEach(propertiesBuilder::add);
            jsonBuilder.add("properties", propertiesBuilder);
        }
        return jsonBuilder.build();
    }
}
