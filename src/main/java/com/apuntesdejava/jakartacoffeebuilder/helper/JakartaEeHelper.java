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
import java.util.List;
import java.util.Map;

import static com.apuntesdejava.jakartacoffeebuilder.util.Constants.*;
import static java.util.Collections.emptyMap;

/**
 * Utility class for handling Jakarta EE dependencies in Maven projects.
 * <p>
 * This class provides methods to add Jakarta Faces dependencies to a Maven project and to check if a Maven project
 * already has a Jakarta Faces dependency.
 * <p>
 * This class follows the Singleton design pattern to ensure only one instance is created.
 * <p>
 * Usage example:
 * <pre>
 *     JakartaEeUtil jakartaEeUtil = JakartaEeUtil.getInstance();
 *     jakartaEeUtil.addJakartaFacesDependency(mavenProject, log, jakartaEeVersion);
 * </pre>
 * <p>
 * Note: This class is thread-safe.
 *
 * @author Diego Silva &lt;diego.silva at apuntesdejava.com&gt;
 */
public class JakartaEeHelper {

    private JsonObject specifications;

    /**
     * Retrieves the singleton instance of the `JakartaEeHelper` class.
     * <p>
     * This method ensures that only one instance of the class is created (Singleton design pattern).
     *
     * @return the singleton instance of `JakartaEeHelper`
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
     * Adds a Jakarta CDI dependency to the given Maven project.
     *
     * @param mavenProject     the Maven project to which the dependency will be added
     * @param log              the logger to use for logging messages
     * @param jakartaEeVersion the version of Jakarta EE to use for the dependency
     * @throws MojoExecutionException if an error occurs while adding the dependency
     */
    public void addJakartaCdiDependency(MavenProject mavenProject,
                                        Log log,
                                        String jakartaEeVersion) throws MojoExecutionException {
        var jakartaCdiVersion = specifications.getJsonObject(jakartaEeVersion).getString(JAKARTA_ENTERPRISE_CDI_API);
        PomUtil.addDependency(mavenProject, log, JAKARTA_ENTERPRISE, JAKARTA_ENTERPRISE_CDI_API, jakartaCdiVersion,
            PROVIDED_SCOPE);
        PomUtil.saveMavenProject(mavenProject, log);
    }

    /**
     * Adds a Jakarta Faces dependency to the given Maven project.
     *
     * @param mavenProject     the Maven project to which the dependency will be added
     * @param log              the logger to use for logging messages
     * @param jakartaEeVersion the version of Jakarta EE to use for the dependency
     * @throws MojoExecutionException if an error occurs while adding the dependency
     */
    public void addJakartaFacesDependency(MavenProject mavenProject,
                                          Log log,
                                          String jakartaEeVersion) throws MojoExecutionException {
        var jakartaFacesVersion = specifications.getJsonObject(jakartaEeVersion).getString(JAKARTA_FACES_API);
        PomUtil.addDependency(mavenProject, log, JAKARTA_FACES, JAKARTA_FACES_API, jakartaFacesVersion, PROVIDED_SCOPE);
        PomUtil.saveMavenProject(mavenProject, log);
    }

    /**
     * Checks if the given Maven project has a dependency on Jakarta Faces.
     *
     * @param mavenProject the Maven project to check
     * @param log          the logger to use for logging messages
     * @return true if the project has a Jakarta Faces dependency, false otherwise
     */
    public boolean hasJakartaFacesDependency(MavenProject mavenProject, Log log) {
        return PomUtil.existsDependency(mavenProject, log, JAKARTA_FACES, JAKARTA_FACES_API);
    }

    /**
     * Checks if the given Maven project has a dependency on Jakarta CDI.
     *
     * @param mavenProject the Maven project to check
     * @param log          the logger to use for logging messages
     * @return true if the project has a Jakarta CDI dependency, false otherwise
     */
    public boolean hasNotJakartaCdiDependency(MavenProject mavenProject, Log log) {
        return !PomUtil.existsDependency(mavenProject, log, JAKARTA_ENTERPRISE, JAKARTA_ENTERPRISE_CDI_API);
    }

    /**
     * Adds a Jakarta Faces servlet declaration to the given Maven project.
     *
     * @param mavenProject The Maven project to modify.
     * @param log          the logger to use for logging messages
     * @param urlPattern   the URL pattern to use for the servlet
     * @throws IOException if an error occurs while adding the servlet declaration
     */
    public void addJakartaFacesServletDeclaration(MavenProject mavenProject,
                                                  Log log,
                                                  String urlPattern) throws IOException {
        var webXmlUtil = WebXmlUtil.getInstance();
        webXmlUtil.checkExistsFile(mavenProject, log)
                  .ifPresent(document -> {
                      webXmlUtil.addServletDeclaration(document, urlPattern, log, JAKARTA_FACES_SERVLET,
                          JAKARTA_FACES_SERVLET_DEFINITION);
                      webXmlUtil.saveDocument(mavenProject, document, log);
                  });
    }

    /**
     * Adds a welcome file to the web.xml of the given Maven project.
     *
     * @param mavenProject the Maven project to modify
     * @param welcomeFile  the welcome file to add
     * @param log          the logger to use for logging messages
     * @throws IOException if an error occurs while adding the welcome file
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
     * Checks if the given Maven project has a dependency on Jakarta Persistence.
     *
     * @param mavenProject the Maven project to check
     * @param log          the logger to use for logging messages
     * @return true if the project has a Jakarta Persistence dependency, false otherwise
     */
    public boolean hasNotJakartaPersistenceDependency(MavenProject mavenProject, Log log) {
        return !PomUtil.existsDependency(mavenProject, log, JAKARTA_PERSISTENCE, JAKARTA_PERSISTENCE_API);
    }

    /**
     * Checks if the Jakarta Data dependency can be added to the given Maven project.
     *
     * @param mavenProject the Maven project to check
     * @param log          the logger to use for logging messages
     * @return true if the Jakarta Data dependency can be added, false otherwise
     */
    public boolean hasNotJakartaDataDependency(MavenProject mavenProject, Log log) {
        return !PomUtil.existsDependency(mavenProject, log, JAKARTA_DATA, JAKARTA_DATA_API);
    }

    /**
     * Adds a Jakarta Persistence dependency to the given Maven project.
     *
     * @param mavenProject     the Maven project to which the dependency will be added
     * @param log              the logger to use for logging messages
     * @param jakartaEeVersion the version of Jakarta EE to use for the dependency
     * @throws MojoExecutionException if an error occurs while adding the dependency
     */
    public void addJakartaPersistenceDependency(MavenProject mavenProject,
                                                Log log,
                                                String jakartaEeVersion) throws MojoExecutionException {
        var jakartaPersistenceVersion = specifications.getJsonObject(jakartaEeVersion)
                                                      .getString(JAKARTA_PERSISTENCE_API);
        PomUtil.addDependency(mavenProject, log, JAKARTA_PERSISTENCE, JAKARTA_PERSISTENCE_API,
            jakartaPersistenceVersion, PROVIDED_SCOPE);
        PomUtil.saveMavenProject(mavenProject, log);
    }

    /**
     * Creates a `persistence.xml` file in the given Maven project.
     *
     * @param mavenProject        the Maven project to create the file in
     * @param log                 the logger to use for logging messages
     * @param persistenceUnitName the name of the persistence unit to be created
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
     * Adds a data source to the given Maven project.
     *
     * @param mavenProject the Maven project to which the data source will be added
     * @param log          the logger to use for logging messages
     * @param declare      the declaration string for the data source
     * @param json         the JSON object containing data source parameters
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
     * Adds a Jakarta Data dependency to the given Maven project.
     *
     * @param mavenProject     the Maven project to which the dependency will be added
     * @param log              the logger to use for logging messages
     * @param jakartaEeVersion the version of Jakarta EE to use for the dependency
     * @throws MojoExecutionException if an error occurs while adding the dependency
     */
    public void addJakartaDataDependency(MavenProject mavenProject,
                                         Log log,
                                         String jakartaEeVersion) throws MojoExecutionException {
        var jakartaPersistenceVersion = specifications.getJsonObject(jakartaEeVersion).getString(JAKARTA_DATA_API);
        PomUtil.addDependency(mavenProject, log, JAKARTA_DATA, JAKARTA_DATA_API,
            jakartaPersistenceVersion);
        PomUtil.saveMavenProject(mavenProject, log);
    }

    /**
     * Checks if the Jakarta Data dependency can be added to the given Maven project.
     *
     * @param mavenProject the Maven project to check
     * @param log          the logger to use for logging messages
     * @return true if the Jakarta Data dependency can be added, false otherwise
     */
    public boolean isValidAddJakartaDataDependency(MavenProject mavenProject, Log log) {
        return PomUtil.existsDependency(mavenProject, log, JAKARTA_PERSISTENCE, JAKARTA_PERSISTENCE_API,
            specifications.getJsonObject(JAKARTAEE_VERSION_11).getString(JAKARTA_PERSISTENCE_API));
    }

    /**
     * Checks and adds the necessary Jakarta EE dependencies to the Maven project.
     *
     * @param mavenProject the Maven project to which the dependencies will be added
     * @param log          the logger used to log messages
     * @param definition   the JSON object containing the dialect information
     */
    public void checkDataDependencies(MavenProject mavenProject, Log log, JsonObject definition) {
        PomUtil.getDependency(mavenProject, log, JAKARTA_ENTERPRISE, JAKARTA_ENTERPRISE_CDI_API).ifPresent(
            artifact -> {
                var version = artifact.getVersion();
                log.debug("Jakarta CDI dependency found: " + version);
                specifications.entrySet().stream()
                              .filter(entry -> {
                                  JsonObject specObject = entry.getValue().asJsonObject();
                                  return specObject.containsKey(JAKARTA_ENTERPRISE_CDI_API) &&
                                      Strings.CS.equals(specObject.getString(JAKARTA_ENTERPRISE_CDI_API), version);
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
                                      }
                                      PomUtil.addDependency(mavenProject, log, definition.getString("coordinates"));
                                      PomUtil.saveMavenProject(mavenProject, log);
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

    private void addHibernateDependency(MavenProject mavenProject, Log log) throws MojoExecutionException, IOException {
        CoffeeBuilderUtil.getDependencyConfiguration("hibernate")
                         .ifPresent(hibernate -> PomUtil.setProperty(mavenProject, log, "hibernate.version",
                             hibernate.getString("version")));
        PomUtil.addDependency(mavenProject, log, "org.hibernate.orm", "hibernate-core", "${hibernate.version}",
            List.of(
                Map.of(GROUP_ID, "jakarta.inject", ARTIFACT_ID, "jakarta.inject-api"),
                Map.of(GROUP_ID, "jakarta.persistence", ARTIFACT_ID, "jakarta.persistence-api")
            ));
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
                                                 Json.createObjectBuilder()
                                                     .add(GROUP_ID, "org.hibernate.orm")
                                                     .add(ARTIFACT_ID, "hibernate-jpamodelgen")
                                                     .add("version", "${hibernate.version}")))
                                     .build()));

        PomUtil.saveMavenProject(mavenProject, log);
    }

    /**
     * Adds a persistence class provider to the given Maven project.
     *
     * @param mavenProject the Maven project to which the provider will be added
     * @param log          the logger to use for logging messages
     * @throws IOException if an error occurs while adding the provider
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
     * Adds the Jackson Core and Jackson Annotations dependencies to the Maven project. This method also sets the
     * "jackson-core.version" property in the pom.xml based on the configuration found using
     * {@link CoffeeBuilderUtil#getDependencyConfiguration(String)}.
     *
     * @param mavenProject The Maven project to modify.
     * @param log          The logger for logging messages.
     * @throws IOException            If an I/O error occurs.
     * @throws MojoExecutionException If a Maven execution error occurs.
     */
    public void addJacksonDependency(MavenProject mavenProject, Log log) throws IOException, MojoExecutionException {
        CoffeeBuilderUtil.getDependencyConfiguration("jackson-core")
                         .ifPresent(hibernate -> PomUtil
                             .setProperty(mavenProject, log, "jackson-core.version",
                                 hibernate.getString("version")));
        PomUtil.addDependency(mavenProject, log, "com.fasterxml.jackson.core", "jackson-core",
            "${jackson-core.version}");
        PomUtil.addDependency(mavenProject, log, "com.fasterxml.jackson.core", "jackson-annotations",
            "${jackson-core.version}");
        PomUtil.saveMavenProject(mavenProject, log);
    }

    /**
     * Adds the MicroProfile OpenAPI API dependency to the Maven project. This method retrieves the version from the
     * configuration and sets the "microprofile-openapi-api.version" property in the pom.xml.
     *
     * @param mavenProject The Maven project to modify.
     * @param log          The logger for logging messages.
     * @throws IOException            If an I/O error occurs.
     * @throws MojoExecutionException If a Maven execution error occurs.
     */
    public void addMicroprofileOpenApiApiDependency(MavenProject mavenProject,
                                                    Log log) throws IOException, MojoExecutionException {
        CoffeeBuilderUtil.getDependencyConfiguration("microprofile-openapi-api")
                         .ifPresent(
                             openApi -> PomUtil
                                 .setProperty(mavenProject, log, "microprofile-openapi-api.version",
                                     openApi.getString("version")));
        PomUtil.addDependency(mavenProject, log, "org.eclipse.microprofile.openapi", "microprofile-openapi-api",
            "${microprofile-openapi-api.version}", "provided");
        PomUtil.saveMavenProject(mavenProject, log);
    }

    /**
     * Adds the Jakarta Validation API dependency to the Maven project. This method retrieves the version from the
     * configuration and sets the "jakarta.validation-api.version" property in the pom.xml.
     *
     * @param mavenProject     The Maven project to modify.
     * @param log              The logger for logging messages.
     * @param jakartaEeVersion The version of Jakarta EE to use for the dependency.
     * @throws IOException            If an I/O error occurs.
     * @throws MojoExecutionException If a Maven execution error occurs.
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
        PomUtil.saveMavenProject(mavenProject, log);
    }

    public void addHelperGenerateSource(MavenProject mavenProject, Log log) throws MojoExecutionException, IOException {
        var executions =
            Json.createArrayBuilder()
                .add(Json.createObjectBuilder()
                         .add("id", "add-source")
                         .add("phase",
                             "generate-sources")
                         .add(GOALS,
                             Json.createArrayBuilder()
                                 .add(
                                     Json.createObjectBuilder().add(GOAL, "add-source")))
                         .add(CONFIGURATION,
                             Json.createObjectBuilder()
                                 .add("sources",

                                     Json.createArrayBuilder()
                                         .add(Json.createObjectBuilder()
                                                  .add("source",
                                                      "${project.build.directory}/generated-sources/openapi"))
                                 )
                         )
                ).build();
        PomUtil
            .findLatestPluginVersion("org.codehaus.mojo", "build-helper-maven-plugin")
            .ifPresent(
                version -> PomUtil.addPlugin(mavenProject.getOriginalModel().getBuild(), log, "org.codehaus.mojo",
                    "build-helper-maven-plugin", version, null, executions));
        PomUtil.saveMavenProject(mavenProject, log);
    }

    public boolean hasNotPrimeFacesDependency(MavenProject mavenProject, Log log) {
        return !PomUtil.existsDependency(mavenProject, log, ORG_PRIMEFACES, PRIMEFACES);
    }

    public void addPrimeFacesDependency(MavenProject mavenProject, Log log) throws MojoExecutionException {
        PomUtil.addDependency(mavenProject, log, "%s:%s".formatted(ORG_PRIMEFACES, PRIMEFACES), "jakarta");
        PomUtil.saveMavenProject(mavenProject, log);
    }

    public void createDomain(MavenProject mavenProject, String entityName, JsonObject entityDescription) {
        var modelPackage = MavenProjectUtil.getDomainModelPackage(mavenProject);
    }

    private static class JakartaEeUtilHolder {

        private static final JakartaEeHelper INSTANCE = new JakartaEeHelper();
    }
}
