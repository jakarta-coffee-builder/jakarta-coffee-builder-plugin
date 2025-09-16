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

    public static Optional<JsonObject> getOpenApiGeneratorConfiguration() throws IOException {
        return Optional.ofNullable(
            HttpUtil.getContent(HttpUtil.getUrl(Constants.OPEN_API_GENERATOR_CONFIGURATION),
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
     * Retrieves the JDBC configuration for the given database URL.
     *
     * @param url the JDBC URL from which the dialect key will be extracted
     * @return an Optional containing the JsonObject of the JDBC configuration if present
     * @throws IOException if an error occurs while obtaining the dialect configuration
     */
    public static Optional<JsonObject> getJdbcConfiguration(String url) throws IOException {
        final String dialectKey = StringUtils.substringBetween(url, "jdbc:", ":");
        return getDialectConfiguration().map(
            dialectConfiguration -> dialectConfiguration.getJsonObject(dialectKey));

    }

    /**
     * Retrieves the field marked as the identifier (ID) from the entity definition.
     *
     * @param entity the JSON object representing the entity
     * @return an {@link Optional} containing the JSON object of the ID field, or empty if not found
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
     * Retrieves the class type associated with the field marked as the identifier (ID) from the entity definition.
     * If no ID field is found, the provided default value is returned.
     *
     * @param entity       the JSON object representing the entity
     * @param defaultValue the default value to return if no ID field or class type is found
     * @return the class type as a string, or the default value if no ID field or class type is found
     */
    public static String getFieldIdClass(JsonObject entity, String defaultValue) {
        return getFieldId(entity).map(f -> f.getValue().asJsonObject().getString(TYPE)).orElse(defaultValue);
    }
}
