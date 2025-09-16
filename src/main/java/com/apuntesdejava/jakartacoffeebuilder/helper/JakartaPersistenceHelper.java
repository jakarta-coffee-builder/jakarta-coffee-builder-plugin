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

import com.apuntesdejava.jakartacoffeebuilder.util.*;
import jakarta.json.JsonObject;
import jakarta.json.JsonString;
import jakarta.json.JsonValue;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.apuntesdejava.jakartacoffeebuilder.util.Constants.*;
import static org.apache.commons.lang3.StringUtils.EMPTY;

/**
 * A helper class for managing Jakarta Persistence entities within a Maven project. Provides methods to read JSON data
 * and add entity definitions to the project. Utilizes various utilities for JSON processing, string manipulation, and
 * template generation. This class follows the singleton pattern to ensure a single instance is used.
 * <p>
 * <p>
 * Key functionalities include:</p>
 * <ul>
 * <li>Reading JSON data from a specified path and processing it to add entities to a Maven project.</li>
 * <li>Generating field definitions and annotations from JSON data.</li>
 * <li>Creating import statements for Jakarta Persistence annotations.</li>
 * </ul>
 * <p>
 * <p>
 * Note: This class is not intended to be instantiated directly. Use {@link #getInstance()} to obtain the singleton
 * instance.</p>
 *
 * @author Diego Silva diego.silva at apuntesdejava.com
 * @see JsonUtil
 * @see StringsUtil
 * @see TemplateUtil
 * @see PathsUtil
 */
public class JakartaPersistenceHelper {

    private final JsonObject classesDefinitions;
    private final String suffix;

    private JakartaPersistenceHelper() {
        try {
            this.classesDefinitions = CoffeeBuilderUtil.getClassesDefinitions().orElseThrow();
            this.suffix = "Entity";
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Retrieves the singleton instance of the `JakartaPersistenceHelper` class. This method ensures that only one
     * instance of the class is created and reused.
     *
     * @return the singleton instance of `JakartaPersistenceHelper`
     */
    public static JakartaPersistenceHelper getInstance() {
        return JakartaPersistenceUtilHolder.INSTANCE;
    }

    /**
     * Adds entities to the specified Maven project by reading JSON data from the given path. The JSON data can be
     * either an array or an object representing entities. Each entity is processed and added to the project using the
     * addEntity method.
     *
     * @param mavenProject       the Maven project to which entities will be added
     * @param log                the logger used for logging debug and error messages
     * @param jsonPath           the path to the JSON file containing entity definitions
     * @param persistenceXmlPath the path to the persistence.xml file
     * @throws IOException if an I/O error occurs while reading the JSON file
     */
    public void addEntities(MavenProject mavenProject,
                            Log log,
                            Path jsonPath, Path persistenceXmlPath) throws IOException {
        log.debug("Adding entities from file: " + jsonPath);
        var xmlUtil = XmlUtil.getInstance();
        var persistenceDoc = xmlUtil.getDocument(log, persistenceXmlPath).orElseThrow();
        var jsonContent = JsonUtil.readJsonValue(jsonPath).asJsonObject();
        var entitiesName = jsonContent.keySet()
            .stream()
            .map(className -> className + suffix)
            .collect(Collectors.toSet());
        jsonContent.forEach((key, value) -> addEntity(mavenProject, log, key, value.asJsonObject(), entitiesName));
        addEntitiesToPersistenceXml(mavenProject, log, persistenceDoc, entitiesName);
        xmlUtil.saveDocument(persistenceDoc, log, persistenceXmlPath);

    }

    private void addEntitiesToPersistenceXml(MavenProject mavenProject,
                                             Log log,
                                             Document persistenceDoc,
                                             Set<String> entitiesName) {
        log.debug("Adding all entities to persistence.xml");
        Element root = persistenceDoc.getRootElement();
        Element persistenceUnit = (Element) root.selectSingleNode("//*[local-name()='persistence-unit']");
        if (persistenceUnit == null) {
            log.warn("No <persistence-unit> found in persistence.xml. Skipping class registration.");
            return;
        }
        String entityPackage = MavenProjectUtil.getEntityPackage(mavenProject);
        List<Node> content = persistenceUnit.content();
        int propertiesIndex = content.indexOf(persistenceUnit.element("properties"));

        if (propertiesIndex != -1) {
            entitiesName.stream().map(entityName -> entityPackage + "." + entityName).forEach(fqcn -> {
                log.info("Adding class to persistence.xml: " + fqcn);
                Element classElement = persistenceUnit.addElement("class").addText(fqcn);

                content.remove(classElement);
                content.add(propertiesIndex, classElement);

            });
        }
    }

    private void addEntity(MavenProject mavenProject,
                           Log log,
                           String entityName,
                           JsonObject entity,
                           Set<String> entitiesName) {
        addEntityClass(mavenProject, log, entityName + suffix, entity, entitiesName);
        addRepositoryClass(mavenProject, log, entityName, entity);
    }

    private void addRepositoryClass(MavenProject mavenProject, Log log, String entityName, JsonObject entity) {
        log.debug("Adding repository for entity: " + entityName);
        var repositoryBuilder = RepositoryBuilder.getInstance();
        repositoryBuilder
            .buildRepository(mavenProject, log, entityName + suffix, entity,
                ClassDefinitionHelper.getInstance()
                    .importsFromFieldsClassesType(entity
                        .getJsonObject(FIELDS)));

    }

    private void addEntityClass(MavenProject mavenProject,
                                Log log,
                                String entityName,
                                JsonObject entity,
                                Set<String> entitiesName) {
        try {
            var tableName = entity.getString(TABLE_NAME, EMPTY);
            log.debug("Adding entity: " + entityName);
            var packageDefinition = MavenProjectUtil.getEntityPackage(mavenProject);
            var entityPath = PathsUtil.getJavaPath(mavenProject, packageDefinition, entityName);

            var fieldsJson = entity.getJsonObject(FIELDS);
            Collection<String> importsList = new LinkedHashSet<>();
            var classDefinitionHelper = ClassDefinitionHelper.getInstance();
            var fields = classDefinitionHelper.createFieldsDefinitions(fieldsJson,
                (fieldName, field, annotations) -> getFieldType(mavenProject,
                    log,
                    entityName,
                    entitiesName,
                    fieldName,
                    field,
                    annotations,
                    importsList));
            importsList.addAll(createImportsCollection(fieldsJson));
            importsList.addAll(classDefinitionHelper.importsFromFieldsClassesType(fieldsJson));

            Map<String, Object> fieldsMap = new LinkedHashMap<>(
                Map.of(PACKAGE_NAME, packageDefinition,
                    CLASS_NAME, entityName,
                    IMPORTS_LIST, importsList,
                    FIELDS, fields));
            if (StringUtils.isNotBlank(tableName)) {
                fieldsMap.put("tableName", tableName);
            }
            fieldsMap.put("entityName", Strings.CS.removeEnd(entityName, suffix));

            TemplateUtil.getInstance()
                .createEntityFile(log, fieldsMap, entityPath);
        } catch (IOException ex) {
            log.error("Error adding entity: " + entity.getString(NAME), ex);
        }
    }

    private String getFieldType(MavenProject mavenProject,
                                Log log,
                                String entityName,
                                Set<String> entitiesName,
                                String fieldName,
                                JsonObject field,
                                List<Map<String, Object>> annotations,
                                Collection<String> importsList) {
        var type = field.getString(TYPE);
        if (field.getBoolean("list", false)) {
            return createTypeListField(field, importsList, annotations, entitiesName::contains);
        }
        if (Strings.CS.equals(type, "enum")) {
            return evaluateFieldEnumType(mavenProject, log, entityName, fieldName, field, importsList,
                annotations);
        }
        if (entitiesName.contains(type + suffix)) {
            return type + suffix;
        }
        return type;
    }

    private String evaluateFieldEnumType(MavenProject mavenProject,
                                         Log log,
                                         String entityName,
                                         String fieldName,
                                         JsonObject field,
                                         Collection<String> importsList, List<Map<String, Object>> annotations) {
        var enumValues = field
            .getJsonArray("values")
            .stream()
            .map(jsonValue -> (JsonString) jsonValue)
            .map(JsonString::getString)
            .toList();
        var fullName = createEnum(mavenProject, log, entityName + StringUtils.capitalize(fieldName),
            enumValues);
        importsList.add(fullName);
        importsList.add("jakarta.persistence.Enumerated");
        /*   importsList.add("jakarta.persistence.EnumType");
        annotations.add(Map.of(
            "name", "Enumerated",
            "description", Map.of("value", "EnumType.STRING")
        ));*/
        return StringUtils.substringAfterLast(fullName, ".");
    }

    private String createTypeListField(JsonObject field,
                                       Collection<String> importsList,
                                       List<Map<String, Object>> annotations,
                                       Predicate<String> typeIsEntity) {
        var type = field.getString(TYPE);
        if (classesDefinitions.containsKey(type)) {
            var fullName = classesDefinitions.getJsonObject(type).getString("fullName");
            importsList.add(fullName);
        }
        if (typeIsEntity.test(type)) {
            importsList.add("jakarta.persistence.OneToMany");
            annotations.add(Map.of("name", "OneToMany"));
        } else {
            importsList.add("jakarta.persistence.ElementCollection");
            annotations.add(Map.of("name", "ElementCollection"));
        }
        importsList.add("java.util.List");
        return "List<%s>".formatted(type);
    }

    private String createEnum(MavenProject mavenProject,
                              Log log,
                              String enumName,
                              List<String> values) {
        try {
            var packageDefinition = MavenProjectUtil.getEnumsPackage(mavenProject);
            var enumPath = PathsUtil.getJavaPath(mavenProject, packageDefinition, enumName);
            log.debug("Creating enum: " + enumName + " at " + enumPath);
            Map<String, Object> model = Map.of(
                PACKAGE_NAME, packageDefinition,
                CLASS_NAME, enumName,
                "values", values
            );
            TemplateUtil.getInstance().createEnumFile(log, model, enumPath);
            return packageDefinition + "." + enumName;
        } catch (IOException ex) {
            log.error("Error creating enum: " + enumName, ex);
            return null;
        }

    }

    private Collection<String> createImportsCollection(JsonObject fieldsJson) {
        return fieldsJson.values().stream()
            .map(JsonValue::asJsonObject)
            .map(JsonObject::keySet)
            .flatMap(Set::stream)
            .map(key -> StringsUtil.findIgnoreCase(SEARCH_ANNOTATIONS_FIELD_KEYS, key))
            .filter(Objects::nonNull)
            .map(key -> "jakarta.persistence." + key)
            .collect(Collectors.toSet());
    }

    private static class JakartaPersistenceUtilHolder {

        private static final JakartaPersistenceHelper INSTANCE = new JakartaPersistenceHelper();
    }
}
