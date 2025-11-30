/*
 * Copyright 2025 dsilva.
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

import com.apuntesdejava.jakartacoffeebuilder.util.CoffeeBuilderUtil;
import com.apuntesdejava.jakartacoffeebuilder.util.MavenProjectUtil;
import com.apuntesdejava.jakartacoffeebuilder.util.PathsUtil;
import com.apuntesdejava.jakartacoffeebuilder.util.PomUtil;
import com.apuntesdejava.jakartacoffeebuilder.util.TemplateUtil;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonValue;
import org.apache.commons.lang3.Strings;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import static com.apuntesdejava.jakartacoffeebuilder.util.Constants.ARTIFACT_ID;
import static com.apuntesdejava.jakartacoffeebuilder.util.Constants.CLASS_NAME;
import static com.apuntesdejava.jakartacoffeebuilder.util.Constants.FIELDS;
import static com.apuntesdejava.jakartacoffeebuilder.util.Constants.GROUP_ID;
import static com.apuntesdejava.jakartacoffeebuilder.util.Constants.IMPORTS_LIST;
import static com.apuntesdejava.jakartacoffeebuilder.util.Constants.MAPSTRUCT;
import static com.apuntesdejava.jakartacoffeebuilder.util.Constants.MAVEN_COMPILER_PLUGIN;
import static com.apuntesdejava.jakartacoffeebuilder.util.Constants.MODEL_NAME;
import static com.apuntesdejava.jakartacoffeebuilder.util.Constants.ORG_APACHE_MAVEN_PLUGINS;
import static com.apuntesdejava.jakartacoffeebuilder.util.Constants.ORG_MAPSTRUCT;
import static com.apuntesdejava.jakartacoffeebuilder.util.Constants.PACKAGE_NAME;
import static com.apuntesdejava.jakartacoffeebuilder.util.Constants.TYPE;

/**
 * A singleton helper class for scaffolding architecture layers, including DTOs, Mappers, and Services.
 * It reads entity definitions from a JSON structure and generates the corresponding Java classes.
 */
public class ArchitectureHelper {

    private ArchitectureHelper() {
    }

    /**
     * Returns the singleton instance of the {@code ArchitectureHelper}.
     *
     * @return The single instance of this class.
     */
    public static ArchitectureHelper getInstance() {
        return ArchitectureHelperHolder.INSTANCE;
    }

    private static void addMapStructDependency(MavenProject mavenProject, Log log) throws IOException {
        var version = PomUtil.findLatestDependencyVersion(ORG_MAPSTRUCT, MAPSTRUCT).orElseThrow();
        PomUtil.setProperty(mavenProject, log, "org.mapstruct.version", version);
        PomUtil.addDependency(mavenProject, log, ORG_MAPSTRUCT, MAPSTRUCT, "${org.mapstruct.version}");

        CoffeeBuilderUtil.getDependencyConfiguration(MAVEN_COMPILER_PLUGIN)
            .ifPresent(
                mavenCompilerPlugin -> PomUtil.addPlugin(mavenProject, log,
                    ORG_APACHE_MAVEN_PLUGINS,
                    MAVEN_COMPILER_PLUGIN,
                    mavenCompilerPlugin.getString("version"),
                    Json.createObjectBuilder()
                        .add("annotationProcessorPaths",
                            Json.createObjectBuilder()
                                .add("path",
                                    Json.createArrayBuilder()
                                        .add(
                                            Json.createObjectBuilder()
                                                .add(GROUP_ID, ORG_MAPSTRUCT)
                                                .add(ARTIFACT_ID, "mapstruct-processor")
                                                .add("version", "${org.mapstruct.version}")
                                        )
                                )
                        )
                        .build()));
    }

    /**
     * Checks if the MapStruct dependency is present in the project. If not, it adds the dependency
     * and configures the Maven Compiler Plugin to use the MapStruct annotation processor.
     *
     * @param mavenProject     The current Maven project.
     * @param log              The logger for outputting information.
     * @param jakartaEeVersion Jakarta EE Version
     * @throws IOException if an I/O error occurs while modifying the POM.
     */
    public void checkDependency(MavenProject mavenProject, Log log, String jakartaEeVersion) throws IOException {
        log.debug("Checking org.mapstruct depending");
        if (!PomUtil.existsDependency(mavenProject, log, ORG_MAPSTRUCT, MAPSTRUCT)) {
            addMapStructDependency(mavenProject, log);
        }
        var jakartaEeHelper = JakartaEeHelper.getInstance();
        if (jakartaEeHelper.hasNotJakartaTransactionDependency(mavenProject, log)) {
            jakartaEeHelper.addJakartaTransactionDependency(mavenProject, log, jakartaEeVersion);
        }
    }

    /**
     * Creates Data Transfer Object (DTO) classes based on the provided JSON entity definitions.
     *
     * @param mavenProject The Maven project where the DTOs will be created.
     * @param log          The logger for status and error messages.
     * @param jsonContent  A {@link JsonObject} where keys are entity names and values are their definitions.
     * @throws IOException if an error occurs during file creation.
     */
    public void createDtos(MavenProject mavenProject,
                           Log log,
                           JsonObject jsonContent) throws IOException {

        jsonContent.forEach(
            (entityName, entityDefinition) -> createDto(mavenProject, log, entityName, entityDefinition.asJsonObject()
            ));
    }

    private void createDto(MavenProject mavenProject,
                           Log log,
                           String modelName,
                           JsonObject modelDefinition) {
        try {
            var packageDefinition = MavenProjectUtil.getModelPackage(mavenProject);
            log.debug("Model:" + modelName);
            var modelPath = PathsUtil.getJavaPath(mavenProject, packageDefinition, modelName);
            var classDefinitionHelper = ClassDefinitionHelper.getInstance();
            var fieldsJson = modelDefinition.getJsonObject(FIELDS);
            var fields = classDefinitionHelper.createFieldsDefinitions(fieldsJson,
                (fieldName, field, annotations) -> {
                    var type = field.getString(TYPE);
                    if (field.getBoolean("list", false)) {

                    }
                    if (Strings.CS.equals(type, "enum")) {

                    }

                    return type;
                });
            log.debug("fields:" + fields);
            Collection<String> importsList = new LinkedHashSet<>(
                classDefinitionHelper.importsFromFieldsClassesType(fieldsJson));
            Map<String, Object> fieldsMap = Map.ofEntries(
                Map.entry(PACKAGE_NAME, packageDefinition),
                Map.entry(CLASS_NAME, modelName),
                Map.entry(IMPORTS_LIST, importsList),
                Map.entry(FIELDS, fields)
            );

            TemplateUtil.getInstance().createPojoFile(log, fieldsMap, modelPath);

        } catch (IOException ex) {
            log.error("Error creating model " + modelName, ex);
        }

    }

    /**
     * Creates MapStruct mapper interfaces for converting between entities and DTOs.
     *
     * @param mavenProject The Maven project where the mappers will be created.
     * @param log          The logger for status and error messages.
     * @param jsonContent  A {@link JsonObject} where keys are entity names.
     * @throws IOException if an error occurs during file creation.
     */
    public void createMappers(MavenProject mavenProject,
                              Log log,
                              JsonObject jsonContent) throws IOException {
        jsonContent.forEach((entityName, entityDefinition) -> createMapper(mavenProject, log, entityName));
    }

    private void createMapper(MavenProject mavenProject,
                              Log log,
                              String modelName) {
        try {
            var mapperName = modelName + "Mapper";
            var packageDefinition = MavenProjectUtil.getMapperPackage(mavenProject);
            log.debug("Mapper:" + modelName);
            var mapperPath = PathsUtil.getJavaPath(mavenProject, packageDefinition, mapperName);

            Collection<String> importsList = List.of(
                MavenProjectUtil.getEntityPackage(mavenProject) + "." + modelName + "Entity",
                MavenProjectUtil.getModelPackage(mavenProject) + "." + modelName
            );
            Map<String, Object> fieldsMap = Map.ofEntries(
                Map.entry(PACKAGE_NAME, packageDefinition),
                Map.entry(CLASS_NAME, mapperName),
                Map.entry(IMPORTS_LIST, importsList),
                Map.entry(MODEL_NAME, modelName)
            );

            TemplateUtil.getInstance().createMapperFile(log, fieldsMap, mapperPath);

        } catch (IOException ex) {
            log.error("Error creating Mapper " + modelName, ex);
        }
    }

    /**
     * Creates service classes that encapsulate business logic for each entity.
     *
     * @param mavenProject The Maven project where the services will be created.
     * @param log          The logger for status and error messages.
     * @param jsonContent  A {@link JsonObject} where keys are entity names and values are their definitions.
     */
    public void createServices(MavenProject mavenProject, Log log, JsonObject jsonContent) {
        jsonContent.forEach((entityName, entityDefinition) -> createService(mavenProject,
            log,
            entityName,
            entityDefinition));

    }

    private void createService(MavenProject mavenProject, Log log, String modelName, JsonValue entityDefinitionValue) {
        try {
            var serviceName = modelName + "RepositoryImpl";
            var packageDefinition = MavenProjectUtil.getServicePackage(mavenProject);
            var servicePath = PathsUtil.getJavaPath(mavenProject, packageDefinition, serviceName);
            var entityDefinition = entityDefinitionValue.asJsonObject();

            Collection<String> importsList = new ArrayList<>(
                ClassDefinitionHelper.getInstance().importsFromFieldsClassesType(entityDefinition.getJsonObject(FIELDS))
            );

            importsList.addAll(List.of(
                MavenProjectUtil.getModelRepositoryPackage(mavenProject) + "." + modelName + "Repository",
                MavenProjectUtil.getMapperPackage(mavenProject) + "." + modelName + "Mapper",
                MavenProjectUtil.getModelPackage(mavenProject) + "." + modelName,
                MavenProjectUtil.getRepositoryPackage(mavenProject) + "." + modelName + "EntityRepository"
            ));
            String idClass = CoffeeBuilderUtil.getFieldIdClass(entityDefinition, "Long");

            var fieldsMap = Map.ofEntries(
                Map.entry(PACKAGE_NAME, packageDefinition),
                Map.entry("idClass", idClass),
                Map.entry(IMPORTS_LIST, importsList),
                Map.entry(MODEL_NAME, modelName)
            );
            TemplateUtil.getInstance().createServiceFile(log, fieldsMap, servicePath);
        } catch (IOException ex) {
            log.error("Error creating Service " + modelName, ex);
        }
    }

    public void createModelRepositoryInterfaces(MavenProject mavenProject, Log log, JsonObject jsonContent) {
        createAbstractModelRepositoryInterface(mavenProject, log);
        jsonContent.forEach((entityName, entityDefinition) -> createModelRepositoryInterface(mavenProject,
            log,
            entityName,
            entityDefinition));
    }

    private void createAbstractModelRepositoryInterface(MavenProject mavenProject, Log log) {
        try {
            var packageDefinition = MavenProjectUtil.getModelRepositoryPackage(mavenProject);
            var abstractModelRepositoryPath = PathsUtil.getJavaPath(mavenProject,
                packageDefinition,
                "AbstractModelRepository");

            var fieldsMap = Map.ofEntries(
                Map.entry(PACKAGE_NAME, (Object) packageDefinition)
            );
            TemplateUtil.getInstance().createAbstractModelRepositoryFile(log, fieldsMap, abstractModelRepositoryPath);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void createModelRepositoryInterface(MavenProject mavenProject,
                                                Log log,
                                                String modelName,
                                                JsonValue entityDefinitionValue) {
        try {
            var serviceName = modelName + "Repository";
            var packageDefinition = MavenProjectUtil.getModelRepositoryPackage(mavenProject);
            var servicePath = PathsUtil.getJavaPath(mavenProject, packageDefinition, serviceName);
            var entityDefinition = entityDefinitionValue.asJsonObject();

            Collection<String> importsList = new ArrayList<>(
                ClassDefinitionHelper.getInstance().importsFromFieldsClassesType(entityDefinition.getJsonObject(FIELDS))
            );

            importsList.add(
                MavenProjectUtil.getModelPackage(mavenProject) + "." + modelName
            );
            String idClass = CoffeeBuilderUtil.getFieldIdClass(entityDefinition, "Long");

            var fieldsMap = Map.ofEntries(
                Map.entry(PACKAGE_NAME, packageDefinition),
                Map.entry("idClass", idClass),
                Map.entry(IMPORTS_LIST, importsList),
                Map.entry(MODEL_NAME, modelName)
            );
            TemplateUtil.getInstance().createModelRepositoryFile(log, fieldsMap, servicePath);
        } catch (IOException ex) {
            log.error("Error creating Repository Model " + modelName, ex);
        }
    }

    private static class ArchitectureHelperHolder {

        private static final ArchitectureHelper INSTANCE = new ArchitectureHelper();
    }
}
