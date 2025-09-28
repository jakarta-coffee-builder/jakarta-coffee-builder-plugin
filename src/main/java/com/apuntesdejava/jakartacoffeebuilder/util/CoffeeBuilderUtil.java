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

import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonValue;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

import static com.apuntesdejava.jakartacoffeebuilder.util.Constants.IS_ID;
import static com.apuntesdejava.jakartacoffeebuilder.util.Constants.TYPE;
import static com.apuntesdejava.jakartacoffeebuilder.util.HttpUtil.STRING_TO_JSON_OBJECT_RESPONSE_CONVERTER;

/**
 * A utility class providing helper methods for the Jakarta Coffee Builder plugin.
 * <p>
 * This class offers static methods to fetch various configurations from remote JSON files,
 * such as dependency details, server definitions, and class structures. It also provides
 * helpers for parsing entity definitions.
 */
public class CoffeeBuilderUtil {

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private CoffeeBuilderUtil() {

    }

    /**
     * Retrieves a specific dependency configuration from a remote repository.
     *
     * @param name The name of the dependency to retrieve (e.g., "maven-compiler-plugin").
     * @return An {@link Optional} containing the dependency's configuration as a {@link JsonObject},
     * or empty if not found.
     * @throws IOException if an I/O error occurs during the HTTP request.
     */
    public static Optional<JsonObject> getDependencyConfiguration(String name) throws IOException {
        var response = HttpUtil.getContent(HttpUtil.getUrl(Constants.DEPENDENCIES_URL),
            STRING_TO_JSON_OBJECT_RESPONSE_CONVERTER);
        return Optional.ofNullable(response.getJsonObject(name));
    }

    /**
     * Retrieves a specific server definition from a remote repository.
     *
     * @param name The name of the server to retrieve (e.g., "payara").
     * @return An {@link Optional} containing the server's definition as a {@link JsonObject},
     * or empty if not found.
     * @throws IOException if an I/O error occurs during the HTTP request.
     */
    public static Optional<JsonObject> getServerDefinition(String name) throws IOException {
        var response = HttpUtil.getContent(HttpUtil.getUrl(Constants.SERVERS_URL),
            STRING_TO_JSON_OBJECT_RESPONSE_CONVERTER);
        return Optional.ofNullable(response.getJsonObject(name));
    }

    /**
     * Retrieves all Jakarta EE specification definitions from a remote repository.
     *
     * @return An {@link Optional} containing the complete set of specification definitions.
     * @throws IOException if an I/O error occurs during the HTTP request.
     */
    public static Optional<JsonObject> getSpecificationsDefinitions() throws IOException {
        return Optional.ofNullable(
            HttpUtil.getContent(HttpUtil.getUrl(Constants.SPECIFICATIONS_URL),
                STRING_TO_JSON_OBJECT_RESPONSE_CONVERTER)
        );
    }

    /**
     * Retrieves all class type definitions from a remote repository.
     *
     * @return An {@link Optional} containing the complete set of class definitions.
     * @throws IOException if an I/O error occurs during the HTTP request.
     */
    public static Optional<JsonObject> getClassesDefinitions() throws IOException {
        return Optional.ofNullable(
            HttpUtil.getContent(HttpUtil.getUrl(Constants.CLASSES_DEFINITIONS),
                STRING_TO_JSON_OBJECT_RESPONSE_CONVERTER)
        );
    }

    /**
     * Retrieves the OpenAPI generator configuration from a remote repository.
     *
     * @return An {@link Optional} containing the OpenAPI generator configuration.
     * @throws IOException if an I/O error occurs during the HTTP request.
     */
    public static Optional<JsonObject> getOpenApiGeneratorConfiguration() throws IOException {
        return Optional.ofNullable(
            HttpUtil.getContent(HttpUtil.getUrl(Constants.OPEN_API_GENERATOR_CONFIGURATION),
                STRING_TO_JSON_OBJECT_RESPONSE_CONVERTER)
        );
    }

    /**
     * Retrieves a specific set of properties configuration from a remote repository.
     *
     * @param name The name of the properties configuration to retrieve.
     * @return An {@link Optional} containing the properties as a {@link JsonArray}, or empty if not found.
     * @throws IOException if an I/O error occurs during the HTTP request.
     */
    public static Optional<JsonArray> getPropertiesConfiguration(String name) throws IOException {
        var response = HttpUtil.getContent(HttpUtil.getUrl(Constants.PROPERTIES_URL),
            STRING_TO_JSON_OBJECT_RESPONSE_CONVERTER);
        return Optional.ofNullable(response.getJsonArray(name));
    }

    /**
     * Retrieves the complete dialect configuration for JDBC drivers from a remote repository.
     *
     * @return An {@link Optional} containing the dialect configurations.
     * @throws IOException if an I/O error occurs during the HTTP request.
     */
    public static Optional<JsonObject> getDialectConfiguration() throws IOException {
        var response = HttpUtil.getContent(HttpUtil.getUrl(Constants.DIALECT_URL),
            STRING_TO_JSON_OBJECT_RESPONSE_CONVERTER);
        return Optional.ofNullable(response);
    }

    /**
     * Retrieves a specific XML schema definition based on the Jakarta EE version and schema name.
     *
     * @param jakartaEeVersion The version of Jakarta EE (e.g., "10.0.0").
     * @param name             The name of the schema to retrieve (e.g., "web-app").
     * @return An {@link Optional} containing the schema definition as a {@link JsonObject}, or empty if not found.
     * @throws IOException if an I/O error occurs during the HTTP request.
     */
    public static Optional<JsonObject> getSchema(String jakartaEeVersion, String name) throws IOException {
        var response = HttpUtil.getContent(HttpUtil.getUrl(Constants.SCHEMAS_URL),
            STRING_TO_JSON_OBJECT_RESPONSE_CONVERTER);
        return Optional.ofNullable(response.getJsonObject(jakartaEeVersion).getJsonObject(name));
    }


    /**
     * Retrieves the JDBC configuration for a given database URL by extracting the dialect key.
     *
     * @param url The JDBC URL (e.g., "jdbc:h2:mem:test").
     * @return An {@link Optional} containing the JDBC configuration as a {@link JsonObject}, or empty if not found.
     * @throws IOException if an I/O error occurs while fetching the dialect configuration.
     */
    public static Optional<JsonObject> getJdbcConfiguration(String url) throws IOException {
        final String dialectKey = StringUtils.substringBetween(url, "jdbc:", ":");
        return getDialectConfiguration().map(
            dialectConfiguration -> dialectConfiguration.getJsonObject(dialectKey));

    }

    /**
     * Finds the field that is marked as the primary identifier (ID) within an entity's JSON definition.
     *
     * @param entity The {@link JsonObject} representing the entity.
     * @return An {@link Optional} containing a {@link Map.Entry} of the ID field's name and its JSON value,
     * or empty if no ID field is found.
     */
    public static Optional<Map.Entry<String, JsonValue>> getFieldId(JsonObject entity) {
        return entity.getJsonObject("fields")
            .entrySet().stream()
            .filter(entry -> {

                var val = entry.getValue().asJsonObject();
                return val.containsKey(IS_ID) && val.get(IS_ID).getValueType() == JsonValue.ValueType.TRUE;
            })
            .findFirst();
    }

    /**
     * Retrieves the class type of the entity's primary identifier (ID) field.
     *
     * @param entity       The {@link JsonObject} representing the entity.
     * @param defaultValue The default value to return if no ID field or type is found.
     * @return The class type of the ID field as a {@link String}, or the {@code defaultValue} if not found.
     */
    public static String getFieldIdClass(JsonObject entity, String defaultValue) {
        return getFieldId(entity).map(f -> f.getValue().asJsonObject().getString(TYPE)).orElse(defaultValue);
    }
}
