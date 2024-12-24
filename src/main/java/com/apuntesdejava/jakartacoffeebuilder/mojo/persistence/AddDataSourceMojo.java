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
import jakarta.json.Json;
import jakarta.json.JsonObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.util.Map;

import static com.apuntesdejava.jakartacoffeebuilder.util.Constants.DATASOURCE_DECLARE_WEB;

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
        defaultValue = "com.h2database:h2:2.3.232"
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
    private Map<String, String> properties;


    @Parameter(
        property = "persistence-unit"
    )
    private String persistenceUnit;

    @Parameter(defaultValue = "${project}", readonly = true)
    private MavenProject mavenProject;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        var log = getLog();
        log.debug("Project name:%s".formatted(mavenProject.getName()));
        log.info("Adding datasource %s".formatted(datasourceName));
        var json = createDataSourceParameters();
        JakartaEeHelper.getInstance()
                       .addDataSource(mavenProject, log, declare, coordinatesJdbcDriver, persistenceUnit, json);
    }

    private JsonObject createDataSourceParameters() {
        var jsonBuilder = Json.createObjectBuilder()
                              .add("name", datasourceName)
                              .add("className", className);
        if (StringUtils.isNotBlank(url)) jsonBuilder.add("url", url);
        if (StringUtils.isNotBlank(password)) jsonBuilder.add("password", password);
        if (StringUtils.isNotBlank(user)) jsonBuilder.add("user", user);
        if (StringUtils.isNotBlank(serverName)) jsonBuilder.add("serverName", serverName);
        if (portNumber != null) jsonBuilder.add("portNumber", portNumber);
        if (properties != null && !properties.isEmpty()) {
            var propertiesBuilder = Json.createObjectBuilder();
            properties.forEach(propertiesBuilder::add);
            jsonBuilder.add("properties", propertiesBuilder);
        }
        return jsonBuilder.build();
    }
}
