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

import static com.apuntesdejava.jakartacoffeebuilder.util.Constants.DATASOURCE_DECLARE_WEB;
import static com.apuntesdejava.jakartacoffeebuilder.util.Constants.NAME;


/**
 * Abstract base class for Mojos that add persistence-related configurations.
 */
public abstract class AddAbstractPersistenceMojo extends AbstractMojo {
    @Parameter(defaultValue = "${project}", readonly = true)
    protected MavenProject mavenProject;
    @Parameter(
        property = NAME,
        required = true,
        defaultValue = "defaultDatasource"
    )
    protected String datasourceName;
    @Parameter(
        property = "url",
        defaultValue = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1"
    )
    protected String url;
    @Parameter(
        property = "password"
    )
    protected String password;
    @Parameter(
        property = "user"
    )
    protected String user;
    @Parameter(
        property = "server-name"
    )
    protected String serverName;
    @Parameter(
        property = "port-number"
    )
    protected Integer portNumber;
    @Parameter(
        property = "properties"
    )
    protected String properties;
    @Parameter(
        property = "declare",
        required = true,
        defaultValue = DATASOURCE_DECLARE_WEB
    )
    protected String declare;

    @Component
    protected ProjectBuilder projectBuilder;

    @Parameter(
        defaultValue = "${session}",
        readonly = true,
        required = true
    )
    protected MavenSession mavenSession;

    @Parameter(
        defaultValue = "defaultPU",
        property = "persistence-unit-name"
    )
    protected String persistenceUnitName;

    protected JsonObject getDataSourceParameters() {
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

    protected void createPersistenceUnit(JsonObject json) {
        var log = getLog();
        if (StringUtils.isNotBlank(persistenceUnitName)) {
            var currentPath = mavenProject.getFile().toPath().getParent();
            PersistenceXmlHelper
                .getInstance()
                .addDataSourceToPersistenceXml(currentPath, log, persistenceUnitName,
                    json.getString(NAME));
        }
    }

    protected void addDataSourceConfiguration(Log log, JsonObject json) throws ProjectBuildingException, IOException {
        var jakartaEeHelper = JakartaEeHelper.getInstance();
        var fullProject = MavenProjectUtil.getFullProject(mavenSession, projectBuilder, mavenProject);
        jakartaEeHelper.addDataSource(mavenProject, log, declare, json);
        CoffeeBuilderUtil.getJdbcConfiguration(MavenProjectUtil.getParent(mavenProject))
                         .ifPresent(definition ->
                             jakartaEeHelper.checkDataDependencies(fullProject, log, definition));

        CoffeeBuilderUtil.updateProjectConfiguration(mavenProject.getFile().toPath().getParent(), "jdbc", json);
    }
}
