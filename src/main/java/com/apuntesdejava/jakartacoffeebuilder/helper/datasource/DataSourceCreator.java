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
package com.apuntesdejava.jakartacoffeebuilder.helper.datasource;

import jakarta.json.JsonNumber;
import jakarta.json.JsonObject;
import jakarta.json.JsonString;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Abstract class for creating a data source.
 */
public abstract class DataSourceCreator {
    protected final MavenProject mavenProject;
    protected final Log log;
    protected String coordinatesJdbcDriver;
    protected JsonObject dataSourceParameters;

    /**
     * Constructor for DataSourceCreator.
     *
     * @param mavenProject the Maven project
     * @param log          the logger
     */
    public DataSourceCreator(MavenProject mavenProject, Log log) {
        this.log = log;
        this.mavenProject = mavenProject;
    }

    /**
     * Sets the coordinates for the JDBC driver.
     *
     * @param coordinatesJdbcDriver the coordinates of the JDBC driver
     * @return the current instance of DataSourceCreator
     */
    public DataSourceCreator coordinatesJdbcDriver(String coordinatesJdbcDriver) {
        this.coordinatesJdbcDriver = coordinatesJdbcDriver;
        return this;
    }

    /**
     * Sets the data source parameters.
     *
     * @param dataSourceParameters the data source parameters as a JsonObject
     * @return the current instance of DataSourceCreator
     */
    public DataSourceCreator dataSourceParameters(JsonObject dataSourceParameters) {
        this.dataSourceParameters = dataSourceParameters;
        return this;
    }

    /**
     * Retrieves the data source parameters as a map.
     *
     * @return a map containing the data source parameters
     */
    protected Map<String, Object> getDataSourceParameters() {
        Map<String, Object> properties = new LinkedHashMap<>();
        Optional.ofNullable(dataSourceParameters).ifPresent(parameters -> {
            parameters.forEach((key, value) -> {
                if (value != null) {
                    properties.put(key, switch (value.getValueType()) {
                        case STRING -> ((JsonString) value).getString();
                        case NUMBER -> ((JsonNumber) value).intValue();
                        case ARRAY -> value.asJsonArray()
                                           .stream()
                                           .map(JsonString.class::cast)
                                           .map(JsonString::getString)
                                           .toList();
                        default -> value;
                    });

                }
            });
        });
        return properties;
    }

    /**
     * Abstract method to build the data source.
     *
     * @throws IOException if an I/O error occurs
     */
    public abstract void build() throws IOException;
}
