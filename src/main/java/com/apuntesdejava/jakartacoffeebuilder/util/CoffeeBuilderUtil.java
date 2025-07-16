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
package com.apuntesdejava.jakartacoffeebuilder.util;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import static com.apuntesdejava.jakartacoffeebuilder.util.HttpUtil.STRING_TO_JSON_OBJECT_RESPONSE_CONVERTER;

/**
 * Utility class for handling CoffeeBuilder operations.
 * <p>
 * This class provides methods to retrieve dependency configurations, properties configurations,
 * and dialect configurations from remote sources. It also allows updating the project configuration
 * and retrieving the dialect from the project configuration.
 * </p>
 */
public class CoffeeBuilderUtil {

    private CoffeeBuilderUtil() {

    }

    /**
     * Retrieves the dependency configuration for a given name.
     *
     * @param name the name of the dependency
     * @return an Optional containing the JsonObject of the dependency configuration if present
     * @throws IOException if an error occurs while obtaining the content
     */
    public static Optional<JsonObject> getDependencyConfiguration(String name) throws IOException {
        var response = HttpUtil.getContent(HttpUtil.getUrl(Constants.DEPENDENCIES_URL),
            STRING_TO_JSON_OBJECT_RESPONSE_CONVERTER);
        return Optional.ofNullable(response.getJsonObject(name));
    }

    public static Optional<JsonObject> getServerDefinition(String name) throws IOException {
        var response = HttpUtil.getContent(HttpUtil.getUrl(Constants.SERVERS_URL),
            STRING_TO_JSON_OBJECT_RESPONSE_CONVERTER);
        return Optional.ofNullable(response.getJsonObject(name));
    }

    public static Optional<JsonObject> getSpecificationsDefinitions() throws IOException {
        return Optional.ofNullable(
            HttpUtil.getContent(HttpUtil.getUrl(Constants.SPECIFICATIONS_URL),
                STRING_TO_JSON_OBJECT_RESPONSE_CONVERTER)
        );
    }

    public static Optional<JsonObject> getClassesDefinitions() throws IOException {
        return Optional.ofNullable(
            HttpUtil.getContent(HttpUtil.getUrl(Constants.CLASSES_DEFINITIONS),
                STRING_TO_JSON_OBJECT_RESPONSE_CONVERTER)
        );
    }

    /**
     * Retrieves the properties configuration for a given name.
     *
     * @param name the name of the properties configuration
     * @return an Optional containing the JsonArray of the properties configuration if present
     * @throws IOException if an error occurs while obtaining the content
     */
    public static Optional<JsonArray> getPropertiesConfiguration(String name) throws IOException {
        var response = HttpUtil.getContent(HttpUtil.getUrl(Constants.PROPERTIES_URL),
            STRING_TO_JSON_OBJECT_RESPONSE_CONVERTER);
        return Optional.ofNullable(response.getJsonArray(name));
    }

    /**
     * Retrieves the dialect configuration from a remote source.
     *
     * @return an Optional containing the JsonObject of the dialect configuration if present
     * @throws IOException if an error occurs while obtaining the content
     */
    public static Optional<JsonObject> getDialectConfiguration() throws IOException {
        var response = HttpUtil.getContent(HttpUtil.getUrl(Constants.DIALECT_URL),
            STRING_TO_JSON_OBJECT_RESPONSE_CONVERTER);
        return Optional.ofNullable(response);
    }

    public static Optional<JsonObject> getSchema(String jakartaEeVersion, String name) throws IOException {
        var response = HttpUtil.getContent(HttpUtil.getUrl(Constants.SCHEMAS_URL),
            STRING_TO_JSON_OBJECT_RESPONSE_CONVERTER);
        return Optional.ofNullable(response.getJsonObject(jakartaEeVersion).getJsonObject(name));
    }

    /**
     * Updates the project configuration with the given configuration name and JSON object.
     *
     * @param currentDirectoryPath the path to the current directory
     * @param configurationName    the name of the configuration to update
     * @param configuration        the JSON object containing the configuration data
     * @throws IOException if an error occurs while updating the configuration
     */
    public static void updateProjectConfiguration(Path currentDirectoryPath,
                                                  String configurationName,
                                                  JsonObject configuration) throws IOException {
        var configurationJson = currentDirectoryPath.resolve("project.json");
        var configurationObject = Files.exists(configurationJson)
            ? JsonUtil.readJsonValue(configurationJson).asJsonObject()
            : Json.createObjectBuilder().build();
        var json = Json.createObjectBuilder(configurationObject)
                       .add(configurationName, configuration)
                       .build();
        JsonUtil.saveJsonValue(configurationJson, json);

    }

    /**
     * Retrieves the dialect from the project configuration.
     *
     * @param url@return an Optional containing the dialect string if present
     * @throws IOException if an error occurs while reading the configuration
     */
    public static Optional<JsonObject> getJdbcConfiguration(String url) throws IOException {
        final String dialectKey = StringUtils.substringBetween(url, "jdbc:", ":");
        return getDialectConfiguration().map(
            dialectConfiguration -> dialectConfiguration.getJsonObject(dialectKey));

    }
}
