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

import com.apuntesdejava.jakartacoffeebuilder.helper.datasource.DataSourceCreatorFactory;
import com.apuntesdejava.jakartacoffeebuilder.util.CoffeeBuilderUtil;
import com.apuntesdejava.jakartacoffeebuilder.util.MavenProjectUtil;
import com.apuntesdejava.jakartacoffeebuilder.util.PathsUtil;
import com.apuntesdejava.jakartacoffeebuilder.util.PomUtil;
import com.apuntesdejava.jakartacoffeebuilder.util.TemplateUtil;
import com.apuntesdejava.jakartacoffeebuilder.util.WebXmlUtil;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import org.apache.commons.lang3.Strings;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.apuntesdejava.jakartacoffeebuilder.util.Constants.ARTIFACT_ID;
import static com.apuntesdejava.jakartacoffeebuilder.util.Constants.CLASS_NAME;
import static com.apuntesdejava.jakartacoffeebuilder.util.Constants.FIELDS;
import static com.apuntesdejava.jakartacoffeebuilder.util.Constants.GOAL;
import static com.apuntesdejava.jakartacoffeebuilder.util.Constants.GOALS;
import static com.apuntesdejava.jakartacoffeebuilder.util.Constants.GROUP_ID;
import static com.apuntesdejava.jakartacoffeebuilder.util.Constants.JAKARTAEE_VERSION_11;
import static com.apuntesdejava.jakartacoffeebuilder.util.Constants.JAKARTA_DATA;
import static com.apuntesdejava.jakartacoffeebuilder.util.Constants.JAKARTA_DATA_API;
import static com.apuntesdejava.jakartacoffeebuilder.util.Constants.JAKARTA_ENTERPRISE;
import static com.apuntesdejava.jakartacoffeebuilder.util.Constants.JAKARTA_ENTERPRISE_CDI_API;
import static com.apuntesdejava.jakartacoffeebuilder.util.Constants.JAKARTA_FACES;
import static com.apuntesdejava.jakartacoffeebuilder.util.Constants.JAKARTA_FACES_API;
import static com.apuntesdejava.jakartacoffeebuilder.util.Constants.JAKARTA_PERSISTENCE;
import static com.apuntesdejava.jakartacoffeebuilder.util.Constants.JAKARTA_PERSISTENCE_API;
import static com.apuntesdejava.jakartacoffeebuilder.util.Constants.MAVEN_COMPILER_PLUGIN;
import static com.apuntesdejava.jakartacoffeebuilder.util.Constants.NAME;
import static com.apuntesdejava.jakartacoffeebuilder.util.Constants.ORG_APACHE_MAVEN_PLUGINS;
import static com.apuntesdejava.jakartacoffeebuilder.util.Constants.ORG_PRIMEFACES;
import static com.apuntesdejava.jakartacoffeebuilder.util.Constants.PACKAGE_NAME;
import static com.apuntesdejava.jakartacoffeebuilder.util.Constants.PRIMEFACES;
import static com.apuntesdejava.jakartacoffeebuilder.util.Constants.PROVIDED_SCOPE;
import static com.apuntesdejava.jakartacoffeebuilder.util.Constants.TYPE;
import static java.util.Collections.emptyMap;

/**
 * A singleton helper class that provides methods for managing Jakarta EE dependencies, configurations,
 * and artifacts within a Maven project. It simplifies tasks such as adding dependencies for various
 * Jakarta EE specifications, creating configuration files like {@code persistence.xml}, and setting up
 * data sources.
 *
 * @author Diego Silva &lt;diego.silva at apuntesdejava.com&gt;
 */
public final class JakartaEeHelper {

    private JsonObject specifications;

    /**
     * Returns the singleton instance of the {@code JakartaEeHelper}.
     *
     * @return The singleton instance.
     */
    public static JakartaEeHelper getInstance() {
        return JakartaEeUtilHolder.INSTANCE;
    }

    private JakartaEeHelper() {
        try {
            CoffeeBuilderUtil.getSpecificationsDefinitions().ifPresent(specs -> this.specifications = specs);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * Adds the Jakarta CDI API dependency to the Maven project for a specific Jakarta EE version.
     *
     * @param mavenProject     The Maven project to modify.
     * @param log              The Maven logger for output.
     * @param jakartaEeVersion The target Jakarta EE version (e.g., "10.0.0").
     * @throws MojoExecutionException if an error occurs while adding the dependency.
     */
    public void addJakartaCdiDependency(MavenProject mavenProject,
                                        Log log,
                                        String jakartaEeVersion) throws MojoExecutionException {
        var jakartaCdiVersion = specifications.getJsonObject(jakartaEeVersion).getString(JAKARTA_ENTERPRISE_CDI_API);
        PomUtil.addDependency(mavenProject, log, JAKARTA_ENTERPRISE, JAKARTA_ENTERPRISE_CDI_API, jakartaCdiVersion,
            PROVIDED_SCOPE);
    }

    /**
     * Adds the Jakarta Faces API dependency to the Maven project for a specific Jakarta EE version.
     *
     * @param mavenProject     The Maven project to modify.
     * @param log              The Maven logger for output.
     * @param jakartaEeVersion The target Jakarta EE version (e.g., "10.0.0").
     */
    public void addJakartaFacesDependency(MavenProject mavenProject,
                                          Log log,
                                          String jakartaEeVersion) {
        var jakartaFacesVersion = specifications.getJsonObject(jakartaEeVersion).getString(JAKARTA_FACES_API);
        PomUtil.addDependency(mavenProject, log, JAKARTA_FACES, JAKARTA_FACES_API, jakartaFacesVersion, PROVIDED_SCOPE);
    }

    /**
     * Checks if the project already has a dependency on the Jakarta Faces API.
     *
     * @param mavenProject The Maven project to check.
     * @param log          The Maven logger for output.
     * @return {@code true} if the dependency exists, {@code false} otherwise.
     */
    public boolean hasJakartaFacesDependency(MavenProject mavenProject, Log log) {
        return PomUtil.existsDependency(mavenProject, log, JAKARTA_FACES, JAKARTA_FACES_API);
    }

    /**
     * Checks if the project does NOT have a dependency on the Jakarta CDI API.
     *
     * @param mavenProject The Maven project to check.
     * @param log          The Maven logger for output.
     * @return {@code true} if the dependency does not exist, {@code false} otherwise.
     */
    public boolean hasNotJakartaCdiDependency(MavenProject mavenProject, Log log) {
        return !PomUtil.existsDependency(mavenProject, log, JAKARTA_ENTERPRISE, JAKARTA_ENTERPRISE_CDI_API);
    }

    /**
     * Adds the Jakarta Faces Servlet declaration and mapping to the project's {@code web.xml} file.
     *
     * @param mavenProject The Maven project to modify.
     * @param log          The Maven logger for output.
     * @throws IOException if an I/O error occurs while reading or writing the {@code web.xml} file.
     */
    public void addJakartaFacesDeclaration(MavenProject mavenProject,
                                           Log log) throws IOException {

        var packageDefinition = MavenProjectUtil.getFacesPackage(mavenProject);
        var className = "FacesConfiguration";
        var facesConfiguratinClassPath = PathsUtil.getJavaPath(mavenProject, packageDefinition, className);

        TemplateUtil.getInstance().createFacesConfigurationFile(log,
            Map.of(PACKAGE_NAME, packageDefinition,
                CLASS_NAME, className
            ),
            facesConfiguratinClassPath
        );
    }

    /**
     * Adds a welcome file to the project's {@code web.xml} file.
     *
     * @param mavenProject The Maven project to modify.
     * @param welcomeFile  The name of the welcome file (e.g., "index.xhtml").
     * @param log          The Maven logger for output.
     * @throws IOException if an I/O error occurs while reading or writing the {@code web.xml} file.
     */
    public void addWelcomePages(MavenProject mavenProject, String welcomeFile, Log log) throws IOException {
        var webXmlUtil = WebXmlUtil.getInstance();
        webXmlUtil.checkExistsFile(mavenProject, log)
            .ifPresent(document -> {
                webXmlUtil.addWelcomePages(document, welcomeFile, log);
                webXmlUtil.saveDocument(mavenProject, document, log);
            });
    }

    /**
     * Checks if the project does NOT have a dependency on the Jakarta Persistence API.
     *
     * @param mavenProject The Maven project to check.
     * @param log          The Maven logger for output.
     * @return {@code true} if the dependency does not exist, {@code false} otherwise.
     */
    public boolean hasNotJakartaPersistenceDependency(MavenProject mavenProject, Log log) {
        return !PomUtil.existsDependency(mavenProject, log, JAKARTA_PERSISTENCE, JAKARTA_PERSISTENCE_API);
    }

    /**
     * Checks if the project does NOT have a dependency on the Jakarta Data API.
     *
     * @param mavenProject The Maven project to check.
     * @param log          The Maven logger for output.
     * @return {@code true} if the dependency does not exist, {@code false} otherwise.
     */
    public boolean hasNotJakartaDataDependency(MavenProject mavenProject, Log log) {
        return !PomUtil.existsDependency(mavenProject, log, JAKARTA_DATA, JAKARTA_DATA_API);
    }

    /**
     * Adds the Jakarta Persistence API dependency to the Maven project for a specific Jakarta EE version.
     *
     * @param mavenProject     The Maven project to modify.
     * @param log              The Maven logger for output.
     * @param jakartaEeVersion The target Jakarta EE version (e.g., "10.0.0").
     * @throws MojoExecutionException if an error occurs while adding the dependency.
     */
    public void addJakartaPersistenceDependency(MavenProject mavenProject,
                                                Log log,
                                                String jakartaEeVersion) throws MojoExecutionException {
        var jakartaPersistenceVersion = specifications.getJsonObject(jakartaEeVersion)
            .getString(JAKARTA_PERSISTENCE_API);
        PomUtil.addDependency(mavenProject, log, JAKARTA_PERSISTENCE, JAKARTA_PERSISTENCE_API,
            jakartaPersistenceVersion, PROVIDED_SCOPE);
    }

    /**
     * Creates a {@code persistence.xml} file in the project's {@code META-INF} directory with a specified
     * persistence unit name.
     *
     * @param mavenProject        The Maven project.
     * @param log                 The Maven logger for output.
     * @param persistenceUnitName The name for the persistence unit.
     */
    public void createPersistenceXml(MavenProject mavenProject, Log log, String persistenceUnitName) {
        var persistenceXmlUtil = PersistenceXmlHelper.getInstance();
        persistenceXmlUtil.createPersistenceXml(mavenProject, log, persistenceUnitName)
            .ifPresent(document -> {
                var currentPath = mavenProject.getFile().toPath().getParent();
                persistenceXmlUtil.savePersistenceXml(currentPath, log, document);
            });
    }

    /**
     * Delegates the creation of a data source to a specialized creator based on the {@code declare} strategy.
     *
     * @param mavenProject The Maven project.
     * @param log          The Maven logger for output.
     * @param declare      The strategy for declaring the data source (e.g., "web.xml", "payara").
     * @param json         A {@link JsonObject} containing the data source configuration parameters.
     */
    public void addDataSource(MavenProject mavenProject,
                              Log log,
                              String declare,
                              JsonObject json) {
        log.debug("Datasource:%s".formatted(json));
        DataSourceCreatorFactory
            .getDataSourceCreator(mavenProject, log, declare)
            .ifPresent(dataSourceCreator -> {
                try {
                    dataSourceCreator
                        .dataSourceParameters(json)
                        .build();
                } catch (IOException e) {
                    log.error("Error creating datasource", e);
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * Adds the Jakarta Data API dependency to the Maven project for a specific Jakarta EE version.
     *
     * @param mavenProject     The Maven project to modify.
     * @param log              The Maven logger for output.
     * @param jakartaEeVersion The target Jakarta EE version (e.g., "11.0.0").
     * @throws MojoExecutionException if an error occurs while adding the dependency.
     */
    public void addJakartaDataDependency(MavenProject mavenProject,
                                         Log log,
                                         String jakartaEeVersion) throws MojoExecutionException {
        var jakartaPersistenceVersion = specifications.getJsonObject(jakartaEeVersion).getString(JAKARTA_DATA_API);
        PomUtil.addDependency(mavenProject, log, JAKARTA_DATA, JAKARTA_DATA_API, jakartaPersistenceVersion);
    }

    /**
     * Validates if the Jakarta Data dependency can be added, which requires the presence of the
     * Jakarta Persistence API for Jakarta EE 11.
     *
     * @param mavenProject The Maven project to check.
     * @param log          The Maven logger for output.
     * @return {@code true} if the required dependency is present, {@code false} otherwise.
     */
    public boolean isValidAddJakartaDataDependency(MavenProject mavenProject, Log log) {
        return PomUtil.existsDependency(mavenProject, log, JAKARTA_PERSISTENCE, JAKARTA_PERSISTENCE_API,
            specifications.getJsonObject(JAKARTAEE_VERSION_11).getString(JAKARTA_PERSISTENCE_API));
    }

    /**
     * Orchestrates the addition of data-related dependencies (like Jakarta Data, Hibernate, and JDBC drivers)
     * based on the project's current Jakarta EE version and a provided database dialect definition.
     *
     * @param mavenProject The Maven project to modify.
     * @param log          The Maven logger for output.
     * @param definition   A {@link JsonObject} containing dialect and JDBC driver coordinate information.
     */
    public void checkDataDependencies(MavenProject mavenProject, Log log, JsonObject definition) {
        PomUtil.getDependency(mavenProject, log, JAKARTA_ENTERPRISE, JAKARTA_ENTERPRISE_CDI_API).ifPresent(
            artifact -> {
                var version = artifact.getVersion();
                log.debug("Jakarta CDI dependency found: " + version);
                specifications.entrySet().stream()
                    .filter(entry -> {
                        JsonObject specObject = entry.getValue().asJsonObject();
                        return specObject.containsKey(JAKARTA_ENTERPRISE_CDI_API)
                            && Strings.CS.equals(specObject.getString(JAKARTA_ENTERPRISE_CDI_API), version);
                    })
                    .map(Map.Entry::getKey)
                    .findFirst()
                    .ifPresent(jakartaEEVersion -> {
                        log.debug("Jakarta EE version: %s".formatted(jakartaEEVersion));
                        try {
                            if (Strings.CS.equals(jakartaEEVersion, JAKARTAEE_VERSION_11)) {
                                addJakartaDataDependency(mavenProject, log, jakartaEEVersion);
                                addHibernateDependency(mavenProject, log);
                                addHibernateProvider(mavenProject, log, definition.getString("dialect"));
                                addJakartaInjectDependencyManagement(mavenProject, log);
                            }
                            PomUtil.addDependency(mavenProject, log, definition.getString("coordinates"));
                        } catch (MojoExecutionException | IOException e) {
                            log.error("Error adding Jakarta dependency", e);
                        }
                    });
            });
    }

    private void addHibernateProvider(MavenProject mavenProject, Log log, String dialectClass) {
        PersistenceXmlHelper.getInstance()
            .addProviderToPersistenceXml(mavenProject.getFile().toPath().getParent(), log,
                dialectClass);
    }

    /**
     * Gets the path to the {@code persistence.xml} file if it exists.
     *
     * @param mavenProject The Maven project.
     * @return An {@link Optional} containing the {@link Path} to the file, or empty if it does not exist.
     */
    public Optional<Path> getPersistenceXmlPath(MavenProject mavenProject) {
        var persistenceXmlPah = PersistenceXmlHelper.getInstance()
            .getPersistencePath(mavenProject.getFile().toPath().getParent());
        if (Files.exists(persistenceXmlPah)) {
            return Optional.of(persistenceXmlPah);
        }
        return Optional.empty();
    }

    private void addHibernateDependency(MavenProject mavenProject, Log log) throws MojoExecutionException, IOException {
        CoffeeBuilderUtil.getDependencyConfiguration("hibernate")
            .ifPresent(hibernate -> PomUtil.setProperty(mavenProject, log, "hibernate.version",
                hibernate.getString("version")));
        PomUtil.addDependency(mavenProject, log, "org.hibernate.orm", "hibernate-core", "${hibernate.version}");
        PomUtil.addDependency(mavenProject, log, "org.hibernate.orm", "hibernate-processor", "${hibernate.version}");

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
                                                .add(GROUP_ID, "org.hibernate.orm")
                                                .add(ARTIFACT_ID, "hibernate-jpamodelgen")
                                                .add("version", "${hibernate.version}")
                                        )
                                )
                        )
                        .build()));

    }

    /**
     * Creates a CDI producer class that provides an {@code EntityManager} instance.
     *
     * @param mavenProject The Maven project where the class will be created.
     * @param log          The Maven logger for output.
     * @throws IOException if an I/O error occurs while creating the file.
     */
    public void addPersistenceClassProvider(MavenProject mavenProject, Log log) throws IOException {
        var packageDefinition = MavenProjectUtil.getProviderPackage(mavenProject);
        var className = "PersistenceProvider";
        var persistenceProviderClassPath = PathsUtil.getJavaPath(mavenProject, packageDefinition, className);
        var annotationClasses = Map.of(
            "jakarta.enterprise.context.ApplicationScoped", emptyMap()
        );
        var fields = List.of(Map.of(
            NAME, "entityManager",
            TYPE, "jakarta.persistence.EntityManager",
            "annotations", Map.of(
                "jakarta.persistence.PersistenceContext", Map.of(
                    "unitName", "example-pu"
                ),
                "jakarta.enterprise.inject.Produces", Map.of()
            )
        ));
        TemplateUtil.getInstance().createJavaBeanFile(log,
            Map.of(PACKAGE_NAME, packageDefinition,
                CLASS_NAME, className,
                "annotations", annotationClasses,
                "setters", false,
                "getters", false,
                FIELDS, fields
            ),
            persistenceProviderClassPath
        );
    }

    /**
     * Adds Jackson Core and Jackson Annotations dependencies to the project.
     *
     * @param mavenProject The Maven project to modify.
     * @param log          The Maven logger for output.
     * @throws IOException If an I/O error occurs while fetching dependency configurations.
     */
    public void addJacksonDependency(MavenProject mavenProject, Log log) throws IOException {
        CoffeeBuilderUtil.getDependencyConfiguration("jackson-core")
            .ifPresent(hibernate -> PomUtil
                .setProperty(mavenProject, log, "jackson-core.version",
                    hibernate.getString("version")));
        PomUtil.addDependency(mavenProject, log, "com.fasterxml.jackson.core", "jackson-core",
            "${jackson-core.version}");
        PomUtil.addDependency(mavenProject, log, "com.fasterxml.jackson.core", "jackson-annotations",
            "${jackson-core.version}");
    }

    /**
     * Adds the MicroProfile OpenAPI API dependency to the project.
     *
     * @param mavenProject The Maven project to modify.
     * @param log          The Maven logger for output.
     * @throws IOException If an I/O error occurs while fetching dependency configurations.
     */
    public void addMicroprofileOpenApiApiDependency(MavenProject mavenProject,
                                                    Log log) throws IOException {
        CoffeeBuilderUtil.getDependencyConfiguration("microprofile-openapi-api")
            .ifPresent(
                openApi -> PomUtil
                    .setProperty(mavenProject, log, "microprofile-openapi-api.version",
                        openApi.getString("version")));
        PomUtil.addDependency(mavenProject, log, "org.eclipse.microprofile.openapi", "microprofile-openapi-api",
            "${microprofile-openapi-api.version}", "provided");

    }

    /**
     * Adds the Jakarta Validation API dependency to the project for a specific Jakarta EE version.
     *
     * @param mavenProject     The Maven project to modify.
     * @param log              The Maven logger for output.
     * @param jakartaEeVersion The target Jakarta EE version.
     * @throws IOException            If an I/O error occurs while fetching dependency configurations.
     * @throws MojoExecutionException If the specified dependency configuration is not found.
     */
    public void addJakartaValidationApiDependency(MavenProject mavenProject,
                                                  Log log,
                                                  String jakartaEeVersion) throws IOException, MojoExecutionException {
        var openApi = CoffeeBuilderUtil.getDependencyConfiguration("jakarta.validation-api-" + jakartaEeVersion)
            .orElseThrow(() -> new MojoExecutionException("Dependency not found"));
        PomUtil.setProperty(mavenProject, log, "jakarta.validation-api.version",
            openApi.getString("version"));
        PomUtil.addDependency(mavenProject, log, "jakarta.validation", "jakarta.validation-api",
            "${jakarta.validation-api.version}", "provided");
    }

    /**
     * Configures the {@code build-helper-maven-plugin} to add a generated source directory.
     *
     * @param mavenProject The Maven project to modify.
     * @param log          The Maven logger for output.
     * @throws IOException If an I/O error occurs.
     */
    public void addHelperGenerateSource(MavenProject mavenProject, Log log) throws IOException {
        var executions
            = Json.createArrayBuilder()
            .add(Json.createObjectBuilder()
                    .add("id", "add-source")
                    .add("phase",
                        "generate-sources")
                    .add(GOALS,
                        Json.createArrayBuilder()
                            .add(
                                Json.createObjectBuilder().add(GOAL, "add-source")))
                /*.add(CONFIGURATION,
                    Json.createObjectBuilder()
                        .add("sources",
                            Json.createArrayBuilder()
                                .add(Json.createObjectBuilder()
                                    .add("source",
                                        "${project.build.directory}/generated-sources/openapi"))
                        )
                )*/
            ).build();
        PomUtil
            .findLatestPluginVersion("org.codehaus.mojo", "build-helper-maven-plugin")
            .ifPresent(
                version -> PomUtil.addPlugin(mavenProject.getOriginalModel().getBuild(), log, "org.codehaus.mojo",
                    "build-helper-maven-plugin", version, null, executions));
    }

    /**
     * Checks if the project does NOT have a dependency on PrimeFaces.
     *
     * @param mavenProject The Maven project to check.
     * @param log          The Maven logger for output.
     * @return {@code true} if the dependency does not exist, {@code false} otherwise.
     */
    public boolean hasNotPrimeFacesDependency(MavenProject mavenProject, Log log) {
        return !PomUtil.existsDependency(mavenProject, log, ORG_PRIMEFACES, PRIMEFACES);
    }

    /**
     * Adds the PrimeFaces dependency (with the "jakarta" classifier) to the project.
     *
     * @param mavenProject The Maven project to modify.
     * @param log          The Maven logger for output.
     */
    public void addPrimeFacesDependency(MavenProject mavenProject, Log log) {
        PomUtil.addDependency(mavenProject, log, "%s:%s".formatted(ORG_PRIMEFACES, PRIMEFACES), "jakarta");
    }

    /**
     * Placeholder method for creating a domain model.
     *
     * @param mavenProject      The Maven project.
     * @param entityName        The name of the entity.
     * @param entityDescription A {@link JsonObject} describing the entity.
     */
    public void createDomain(MavenProject mavenProject, String entityName, JsonObject entityDescription) {
        var modelPackage = MavenProjectUtil.getDomainModelPackage(mavenProject);
    }

    private void addJakartaInjectDependencyManagement(MavenProject mavenProject, Log log) {
        PomUtil.addDependencyManagement(mavenProject, log, "jakarta.inject", "jakarta.inject-api", "provided");
    }

    private static class JakartaEeUtilHolder {

        private static final JakartaEeHelper INSTANCE = new JakartaEeHelper();
    }
}
