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
package com.apuntesdejava.jakartacoffeebuilder.helper;

import com.apuntesdejava.jakartacoffeebuilder.util.JsonUtil;
import com.apuntesdejava.jakartacoffeebuilder.util.PathsUtil;
import com.apuntesdejava.jakartacoffeebuilder.util.StringsUtil;
import com.apuntesdejava.jakartacoffeebuilder.util.TemplateUtil;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonValue;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

import static com.apuntesdejava.jakartacoffeebuilder.util.Constants.*;
import static org.apache.commons.lang3.StringUtils.EMPTY;

/**
 * A helper class for managing Jakarta Persistence entities within a Maven project.
 * Provides methods to read JSON data and add entity definitions to the project.
 * Utilizes various utilities for JSON processing, string manipulation, and template generation.
 * This class follows the singleton pattern to ensure a single instance is used.
 *
 * <p>Key functionalities include:</p>
 * <ul>
 *   <li>Reading JSON data from a specified path and processing it to add entities to a Maven project.</li>
 *   <li>Generating field definitions and annotations from JSON data.</li>
 *   <li>Creating import statements for Jakarta Persistence annotations.</li>
 * </ul>
 *
 * <p>Note: This class is not intended to be instantiated directly. Use {@link #getInstance()} to obtain the singleton instance.</p>
 *
 * @author Diego Silva diego.silva at apuntesdejava.com
 * @see JsonUtil
 * @see StringsUtil
 * @see TemplateUtil
 * @see PathsUtil
 */
public class JakartaPersistenceHelper {

    private static final Set<String> SEARCH_ANNOTATIONS = Set.of("Column", "JoinColumn", "ManyToOne");

    private JakartaPersistenceHelper() {
    }

    /**
     * Retrieves the singleton instance of the `JakartaPersistenceHelper` class.
     * This method ensures that only one instance of the class is created and reused.
     *
     * @return the singleton instance of `JakartaPersistenceHelper`
     */
    public static JakartaPersistenceHelper getInstance() {
        return JakartaPersistenceUtilHolder.INSTANCE;
    }

    private static Map<String, Object> getMapFromJsonObject(JsonObject column) {
        return column.entrySet()
                     .stream()
                     .collect(LinkedHashMap::new,
                         (map, entry) -> map.put(entry.getKey(), JsonUtil.getJsonValue(entry.getValue())), Map::putAll);
    }

    private static String getKeyName(Set<String> keys, String otherName) {
        return keys.stream().filter(key -> key.equalsIgnoreCase(otherName)).findFirst().orElse(null);
    }

    private static List<Map<String, Object>> createFieldsDefinitions(JsonArray fieldsJson) {
        return fieldsJson.stream().map(JsonValue::asJsonObject).map(field -> {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put(NAME, field.getString(NAME));
            item.put("type", field.getString("type"));
            var keys = field.keySet();
            if (StringsUtil.containsAnyIgnoreCase(SEARCH_ANNOTATIONS, keys)) {
                var annotationsList = StringsUtil.findIgnoreCase(SEARCH_ANNOTATIONS, keys);
                List<Map<String, Object>> annotations = annotationsList.stream().map(annotationName -> {
                    Map<String, Object> annotationMap = new LinkedHashMap<>();
                    annotationMap.put(NAME, annotationName);
                    var aField = field.get(getKeyName(keys, annotationName));
                    if (aField.getValueType() == JsonValue.ValueType.OBJECT) {
                        annotationMap.put("description", getMapFromJsonObject(aField.asJsonObject()));
                    }
                    return annotationMap;
                }).toList();
                item.put("annotations", annotations);
            }
            if (field.containsKey(IS_ID)) {
                item.put(IS_ID, field.getBoolean(IS_ID, false));
            }
            return item;
        }).toList();
    }

    /**
     * Adds entities to the specified Maven project by reading JSON data from the given path.
     * The JSON data can be either an array or an object representing entities.
     * Each entity is processed and added to the project using the addEntity method.
     *
     * @param mavenProject the Maven project to which entities will be added
     * @param log          the logger used for logging debug and error messages
     * @param jsonPath     the path to the JSON file containing entity definitions
     * @throws IOException if an I/O error occurs while reading the JSON file
     */
    public void addEntities(MavenProject mavenProject, Log log, Path jsonPath) throws IOException {
        var jsonContent = JsonUtil.readJsonValue(jsonPath);
        if (jsonContent.getValueType() == JsonValue.ValueType.ARRAY)
            jsonContent.asJsonArray()
                       .stream()
                       .map(JsonValue::asJsonObject)
                       .forEach(entity -> addEntity(mavenProject, log, entity));
        else if (jsonContent.getValueType() == JsonValue.ValueType.OBJECT)
            addEntity(mavenProject, log, jsonContent.asJsonObject());
    }

    private void addEntity(MavenProject mavenProject, Log log, JsonObject entity) {
        addEntityClass(mavenProject, log, entity);
        addRepositoryClass(mavenProject, log, entity);
    }

    private void addRepositoryClass(MavenProject mavenProject, Log log, JsonObject entity) {
        var entityName = entity.getString(NAME);
        log.debug("Adding repository for entity: " + entityName);
        var repositoryBuilder = RepositoryBuilder.getInstance();
        repositoryBuilder.buildRepository(mavenProject, log, entity);

    }

    private void addEntityClass(MavenProject mavenProject, Log log, JsonObject entity) {
        try {
            var entityName = entity.getString(NAME);
            var tableName = entity.getString(TABLE_NAME, EMPTY);
            log.debug("Adding entity: " + entityName);
            var packageDefinition = MavenProjectHelper.getEntityPackage(mavenProject);
            var entityPath = PathsUtil.getJavaPath(mavenProject, packageDefinition, entityName);

            var fieldsJson = entity.getJsonArray(FIELDS);
            var fields = createFieldsDefinitions(fieldsJson);
            Collection<String> importsList = createImportsCollection(fieldsJson);

            Map<String, Object> fieldsMap = new LinkedHashMap<>(
                Map.of(PACKAGE_NAME, packageDefinition, CLASS_NAME, entityName, IMPORTS_LIST,
                    importsList, FIELDS, fields));
            if (StringUtils.isNotBlank(tableName)) {
                fieldsMap.put("tableName", tableName);
            }

            TemplateUtil.getInstance()
                        .createEntityFile(log, fieldsMap, entityPath);
        } catch (IOException ex) {
            log.error("Error adding entity: " + entity.getString(NAME), ex);
        }
    }

    private Collection<String> createImportsCollection(JsonArray fieldsJson) {
        return fieldsJson.stream()
                         .map(JsonValue::asJsonObject)
                         .map(JsonObject::keySet)
                         .flatMap(Set::stream)
                         .map(key -> StringsUtil.findIgnoreCase(SEARCH_ANNOTATIONS, key))
                         .filter(Objects::nonNull)
                         .map(key -> "jakarta.persistence." + key)
                         .toList();
    }

    private static class JakartaPersistenceUtilHolder {

        private static final JakartaPersistenceHelper INSTANCE = new JakartaPersistenceHelper();
    }
}
