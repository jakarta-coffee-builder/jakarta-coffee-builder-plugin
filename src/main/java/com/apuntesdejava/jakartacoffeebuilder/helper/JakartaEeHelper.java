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
package com.apuntesdejava.jakartacoffeebuilder.helper;

import com.apuntesdejava.jakartacoffeebuilder.helper.datasource.DataSourceCreatorFactory;
import com.apuntesdejava.jakartacoffeebuilder.util.CoffeeBuilderUtil;
import com.apuntesdejava.jakartacoffeebuilder.util.PomUtil;
import com.apuntesdejava.jakartacoffeebuilder.util.WebXmlUtil;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;

import java.io.IOException;
import java.nio.file.Path;

import static com.apuntesdejava.jakartacoffeebuilder.util.Constants.*;

/**
 * Utility class for handling Jakarta EE dependencies in Maven projects.
 * <p>
 * This class provides methods to add Jakarta Faces dependencies to a Maven project
 * and to check if a Maven project already has a Jakarta Faces dependency.
 * </p>
 * <p>
 * This class follows the Singleton design pattern to ensure only one instance is created.
 * </p>
 * <p>
 * Usage example:
 * <pre>
 *     JakartaEeUtil jakartaEeUtil = JakartaEeUtil.getInstance();
 *     jakartaEeUtil.addJakartaFacesDependency(mavenProject, log, jakartaEeVersion);
 * </pre>
 * </p>
 * <p>
 * Note: This class is thread-safe.
 * </p>
 * <p>
 * Author: Diego Silva &lt;diego.silva at apuntesdejava.com&gt;
 * </p>
 */
public class JakartaEeHelper {

    public static JakartaEeHelper getInstance() {
        return JakartaEeUtilHolder.INSTANCE;
    }

    private JakartaEeHelper() {
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
        var jakartaCdiVersion = SPECS_VERSIONS.get(jakartaEeVersion).get(JAKARTA_ENTERPRISE_CDI_API);
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
        var jakartaFacesVersion = SPECS_VERSIONS.get(jakartaEeVersion).get(JAKARTA_FACES_API);
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
     * @param currentPath the path to the Maven project
     * @param urlPattern  the URL pattern to use for the servlet
     * @param log         the logger to use for logging messages
     * @throws IOException if an error occurs while adding the servlet declaration
     */
    public void addJakartaFacesServletDeclaration(Path currentPath,
                                                  String urlPattern,
                                                  Log log) throws IOException {
        var webXmlUtil = WebXmlUtil.getInstance();
        webXmlUtil.checkExistsFile(log, currentPath)
                  .ifPresent(document -> {
                      webXmlUtil.addServletDeclaration(document, urlPattern, log, JAKARTA_FACES_SERVLET,
                          JAKARTA_FACES_SERVLET_DEFINITION);
                      webXmlUtil.saveDocument(document, log, currentPath);
                  });
    }

    /**
     * Adds a welcome file to the web.xml of the given Maven project.
     *
     * @param currentPath the path to the Maven project
     * @param welcomeFile the welcome file to add
     * @param log         the logger to use for logging messages
     * @throws IOException if an error occurs while adding the welcome file
     */
    public void addWelcomePages(Path currentPath, String welcomeFile, Log log) throws IOException {
        var webXmlUtil = WebXmlUtil.getInstance();
        webXmlUtil.checkExistsFile(log, currentPath)
                  .ifPresent(document -> {
                      webXmlUtil.addWelcomePages(document, welcomeFile, log);
                      webXmlUtil.saveDocument(document, log, currentPath);
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
        var jakartaPersistenceVersion = SPECS_VERSIONS.get(jakartaEeVersion).get(JAKARTA_PERSISTENCE_API);
        PomUtil.addDependency(mavenProject, log, JAKARTA_PERSISTENCE, JAKARTA_PERSISTENCE_API,
            jakartaPersistenceVersion, PROVIDED_SCOPE);
        PomUtil.saveMavenProject(mavenProject, log);
    }

    /**
     * Creates a `persistence.xml` file in the given Maven project.
     *
     * @param currentPath         the path to the Maven project
     * @param log                 the logger to use for logging messages
     * @param persistenceUnitName the name of the persistence unit to be created
     */
    public void createPersistenceXml(Path currentPath, Log log, String persistenceUnitName) {
        var persistenceXmlUtil = PersistenceXmlHelper.getInstance();
        persistenceXmlUtil.createPersistenceXml(currentPath, log, persistenceUnitName)
                          .ifPresent(document -> persistenceXmlUtil.savePersistenceXml(currentPath, log, document));
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
            .ifPresent(
                dataSourceCreator -> {
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
        var jakartaPersistenceVersion = SPECS_VERSIONS.get(jakartaEeVersion).get(JAKARTA_DATA_API);
        PomUtil.addDependency(mavenProject, log, JAKARTA_DATA, JAKARTA_DATA_API,
            jakartaPersistenceVersion);
        PomUtil.saveMavenProject(mavenProject, log);
    }

    public boolean isValidAddJakartaDataDependency(MavenProject mavenProject, Log log) {
        return PomUtil.existsDependency(mavenProject, log, JAKARTA_PERSISTENCE, JAKARTA_PERSISTENCE_API,
            SPECS_VERSIONS.get(JAKARTAEE_VERSION_11).get(JAKARTA_PERSISTENCE_API));
    }

    /**
     * Checks and adds the necessary Jakarta EE dependencies to the Maven project.
     *
     * @param mavenProject the Maven project to which the dependencies will be added
     * @param log          the logger used to log messages
     * @param dialectClass the class name of the dialect to be added
     */
    public void checkDataDependencies(MavenProject mavenProject, Log log, String dialectClass) {
        PomUtil.getDependency(mavenProject, log, JAKARTA_ENTERPRISE, JAKARTA_ENTERPRISE_CDI_API).ifPresent(
            artifact -> {
                var version = artifact.getVersion();
                log.debug("Jakarta CDI dependency found: %s".formatted(version));
                var jakartaSpec = SPECS_VERSIONS
                    .entrySet()
                    .stream()
                    .filter(spec -> spec
                        .getValue()
                        .entrySet()
                        .stream()
                        .anyMatch(
                            entry -> StringUtils.equals(entry.getKey(),
                                JAKARTA_ENTERPRISE_CDI_API)
                                && StringUtils.equals(entry.getValue(),
                                version))).findFirst();
                jakartaSpec.ifPresent(spec -> {
                    var jakartaEEVersion = spec.getKey();
                    log.debug("Jakarta EE version: %s".formatted(jakartaEEVersion));
                    try {
                        if (StringUtils.equals(spec.getKey(), JAKARTAEE_VERSION_11)) {
                            addJakartaDataDependency(mavenProject, log, jakartaEEVersion);
                            addHibernateDependency(mavenProject, log);
                            addHibernateProvider(mavenProject, log, dialectClass);
                        }
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
        PomUtil.addDependency(mavenProject, log, "org.hibernate.orm", "hibernate-core", "${hibernate.version}");
        CoffeeBuilderUtil.getDependencyConfiguration("maven-compiler-plugin")
                         .ifPresent(mavenCompilerPlugin -> PomUtil.addPlugin(mavenProject, log, "maven-compiler-plugin",
                             mavenCompilerPlugin.getString("version"),
                             Json.createObjectBuilder()
                                 .add("annotationProcessorPaths",
                                     Json.createObjectBuilder()
                                         .add("path",
                                             Json.createObjectBuilder()
                                                 .add("groupId",
                                                     "org.hibernate.orm")
                                                 .add("artifactId",
                                                     "hibernate-jpamodelgen")
                                                 .add("version",
                                                     "${hibernate.version}"))).build()));

        PomUtil.saveMavenProject(mavenProject, log);
    }

    private static class JakartaEeUtilHolder {

        private static final JakartaEeHelper INSTANCE = new JakartaEeHelper();
    }
}
