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
import org.apache.commons.lang3.Strings;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.model.Build;
import org.apache.maven.model.BuildBase;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.DependencyManagement;
import org.apache.maven.model.Exclusion;
import org.apache.maven.model.Plugin;
import org.apache.maven.model.PluginExecution;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.xml.Xpp3Dom;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.apuntesdejava.jakartacoffeebuilder.util.Constants.ARTIFACT_ID;
import static com.apuntesdejava.jakartacoffeebuilder.util.Constants.CONFIGURATION;
import static com.apuntesdejava.jakartacoffeebuilder.util.Constants.GOAL;
import static com.apuntesdejava.jakartacoffeebuilder.util.Constants.GOALS;
import static com.apuntesdejava.jakartacoffeebuilder.util.Constants.GROUP_ID;
import static com.apuntesdejava.jakartacoffeebuilder.util.Constants.JAKARTA_JAKARTAEE_CORE_API;
import static com.apuntesdejava.jakartacoffeebuilder.util.Constants.JAKARTA_PLATFORM;
import static com.apuntesdejava.jakartacoffeebuilder.util.HttpUtil.STRING_TO_JSON_OBJECT_RESPONSE_CONVERTER;

/**
 * Utility class for handling Maven POM file operations.
 * <p>
 * This class provides methods to add dependencies to a Maven project, check for existing
 * dependencies,
 * and save the current state of a Maven project to its POM file.
 * </p>
 * <p>
 * This class follows the Singleton design pattern to ensure only one instance is created.
 * </p>
 * <p>
 * Usage example:
 * <pre>
 *     PomUtil pomUtil = PomUtil.getInstance();
 *     pomUtil.addDependency(mavenProject, log, groupId, artifactId, version, scope);
 * </pre>
 * <p>
 * Note: This class is thread-safe.
 * </p>
 * <p>
 * Author: Diego Silva &lt;diego.silva at apuntesdejava.com&gt;
 * </p>
 */
public class PomUtil {

    /**
     * Adds a dependency to the given Maven project.
     *
     * @param mavenProject the Maven project to which the dependency will be added
     * @param log          the logger to use for logging messages
     * @param groupId      the group ID of the dependency
     * @param artifactId   the artifact ID of the dependency
     * @param version      the version of the dependency|
     * @param scope        the scope of the dependency
     * @param classifier   the classifier of the dependency
     * @param exclusions   an array of maps, where each map represents an exclusion with "groupId"
     *                     and "artifactId" keys.
     *                     Each map in the array defines a single exclusion.
     */
    public static void addDependency(MavenProject mavenProject,
                                     Log log,
                                     String groupId,
                                     String artifactId,
                                     String version,
                                     String scope,
                                     String classifier, List<Map<String, String>> exclusions) {
        var model = mavenProject.getOriginalModel();
        if (model.getDependencies().stream()
            .filter(dependency -> Strings.CS.equals(dependency.getGroupId(), groupId)
            && Strings.CS.equals(dependency.getArtifactId(), artifactId))
            .findFirst().isEmpty()) {
            var dependency = new Dependency();
            dependency.setArtifactId(artifactId);
            dependency.setGroupId(groupId);
            dependency.setVersion(version);
            if (StringUtils.isNotBlank(classifier)) {
                dependency.setClassifier(classifier);
            }
            if (StringUtils.isNotBlank(scope)) {
                dependency.setScope(scope);
            }
            if (exclusions != null) {
                exclusions.forEach(exclude -> {
                    var exclusion = new Exclusion();
                    exclusion.setGroupId(exclude.get(GROUP_ID));
                    exclusion.setArtifactId(exclude.get(ARTIFACT_ID));
                    dependency.addExclusion(exclusion);
                });
            }
            log.debug("adding dependency %s".formatted(dependency));
            model.addDependency(dependency);
        }
    }

    public static void addDependency(MavenProject mavenProject,
                                     Log log,
                                     String groupId,
                                     String artifactId,
                                     String version,
                                     String scope,
                                     List<Map<String, String>> exclusions) {
        addDependency(mavenProject, log, groupId, artifactId, version, scope, null, exclusions);
    }

    /**
     * Adds a dependency to the given Maven project with exclusions and no specific scope.
     *
     * @param mavenProject the Maven project to which the dependency will be added
     * @param log          the logger to use for logging messages
     * @param groupId      the group ID of the dependency
     * @param artifactId   the artifact ID of the dependency
     * @param version      the version of the dependency
     * @param exclusions   a list of maps, where each map represents an exclusion with "groupId" and
     *                     "artifactId" keys.
     *                     Each map in the list defines a single exclusion.
     */
    public static void addDependency(MavenProject mavenProject,
                                     Log log,
                                     String groupId,
                                     String artifactId,
                                     String version,
                                     List<Map<String, String>> exclusions) {
        addDependency(mavenProject, log, groupId, artifactId, version, null, null, exclusions);
    }

    /**
     * Adds a dependency to the given Maven project with a specified scope and no exclusions.
     *
     * @param mavenProject the Maven project to which the dependency will be added
     * @param log          the logger to use for logging messages
     * @param groupId      the group ID of the dependency
     * @param artifactId   the artifact ID of the dependency
     * @param version      the version of the dependency
     * @param scope        the scope of the dependency (e.g., "compile", "provided", "test")
     */
    public static void addDependency(MavenProject mavenProject,
                                     Log log,
                                     String groupId,
                                     String artifactId,
                                     String version,
                                     String scope) {
        addDependency(mavenProject, log, groupId, artifactId, version, scope, null, null);
    }

    /**
     * Adds a dependency to the given Maven project.
     *
     * @param mavenProject the Maven project to which the dependency will be added
     * @param log          the logger to use for logging messages
     * @param groupId      the group ID of the dependency
     * @param artifactId   the artifact ID of the dependency
     * @param version      the version of the dependency
     */
    public static void addDependency(MavenProject mavenProject,
                                     Log log,
                                     String groupId,
                                     String artifactId,
                                     String version) {
        addDependency(mavenProject, log, groupId, artifactId, version, null, null, null);
    }

    /**
     * Adds a dependency to the given Maven project using the specified coordinates.
     *
     * @param mavenProject the Maven project to which the dependency will be added
     * @param log          the logger to use for logging messages
     * @param coordinates  the coordinates of the dependency in the format
     *                     groupId:artifactId:version
     */
    public static void addDependency(MavenProject mavenProject,
                                     Log log,
                                     String coordinates) {
        addDependency(mavenProject, log, coordinates, null);
    }

    public static void addDependency(MavenProject mavenProject,
                                     Log log,
                                     String coordinates,
                                     String classifier
    ) {
        try {
            var coordinatesSplit = StringUtils.split(coordinates, ":");
            var groupId = coordinatesSplit[0];
            var artifactId = coordinatesSplit[1];
            var version = coordinatesSplit.length == 3 ? coordinatesSplit[2] : findLatestDependencyVersion(
                groupId,
                artifactId).orElseThrow();
            log.debug("adding dependency %s".formatted(coordinates));
            log.debug("groupId:%s | artifactId:%s | version:%s".formatted(groupId, artifactId,
                version));
            addDependency(mavenProject, log, groupId, artifactId, version, null, classifier,
                Collections.emptyList());
        } catch (IOException ex) {
            log.error("Error getting last version of %s".formatted(coordinates), ex);
        }
    }

    /**
     * Finds the latest version of a Maven dependency from Maven Central.
     *
     * @param groupId    the group ID of the dependency.
     * @param artifactId the artifact ID of the dependency.
     *
     * @return an {@link Optional} containing the latest version string, or an empty Optional if not
     *         found.
     *
     * @throws IOException if an error occurs during the HTTP request.
     */
    public static Optional<String> findLatestDependencyVersion(String groupId, String artifactId) throws IOException {
        return findLatestVersion(groupId, artifactId, "jar");
    }

    /**
     * Finds the latest version of a Maven plugin from Maven Central.
     *
     * @param groupId    the group ID of the plugin.
     * @param artifactId the artifact ID of the plugin.
     *
     * @return an {@link Optional} containing the latest version string, or an empty Optional if not
     *         found.
     *
     * @throws IOException if an error occurs during the HTTP request.
     */
    public static Optional<String> findLatestPluginVersion(String groupId, String artifactId) throws IOException {
        return findLatestVersion(groupId, artifactId, "maven-plugin");
    }

    /**
     * Gets the latest version of a Maven artifact from Maven Central.
     * This method is robust and handles cases where no artifact is found.
     *
     * @param groupId    the group ID of the artifact
     * @param artifactId the artifact ID of the artifact
     * @param packaging  the packaging type of the artifact (e.g., "jar", "maven-plugin")
     *
     * @return an {@link Optional} containing the latest version string, or an empty Optional if not
     *         found.
     *
     * @throws IOException if an error occurs during the HTTP request
     */
    public static Optional<String> findLatestVersion(String groupId,
                                                     String artifactId,
                                                     String packaging) throws IOException {
        var params = "p:%s AND a:%s AND g:%s".formatted(packaging, artifactId, groupId);
        var response = HttpUtil.getContent("https://search.maven.org/solrsearch/select",
            STRING_TO_JSON_OBJECT_RESPONSE_CONVERTER, new HttpUtil.Parameter("q", params));

        JsonArray docs = response.getJsonObject("response").getJsonArray("docs");
        if (docs.isEmpty()) {
            return Optional.empty(); // No results found
        }
        return Optional.of(docs.getJsonObject(0).getString("latestVersion"));
    }

    /**
     * Retrieves detailed information about a specific Maven artifact from Maven Central.
     *
     * @param groupId    the group ID of the artifact.
     * @param artifactId the artifact ID of the artifact.
     * @param version    the version of the artifact.
     *
     * @return a {@link JsonObject} containing the artifact's information.
     *
     * @throws IOException if an error occurs during the HTTP request or if the artifact is not
     *                     found.
     */
    public static JsonObject getArtifactInfo(String groupId, String artifactId, String version) throws IOException {
        var params = "v:%s AND a:%s AND g:%s".formatted(version, artifactId, groupId);
        var response = HttpUtil.getContent("https://search.maven.org/solrsearch/select",
            STRING_TO_JSON_OBJECT_RESPONSE_CONVERTER, new HttpUtil.Parameter("q", params));
        return response.getJsonObject("response").getJsonArray("docs").getJsonObject(0);
    }

    /**
     * Checks if a dependency with the specified group ID and artifact ID exists in the given Maven
     * project.
     *
     * @param mavenProject the Maven project to check for the dependency
     * @param log          the logger to use for logging messages
     * @param groupId      the group ID of the dependency to check
     * @param artifactId   the artifact ID of the dependency to check
     *
     * @return true if the dependency exists, false otherwise
     */
    public static boolean existsDependency(MavenProject mavenProject, Log log, String groupId,
                                           String artifactId) {
        log.debug("groupId:%s | artifactId:%s".formatted(groupId, artifactId));
        return getDependency(mavenProject, log, groupId, artifactId).isPresent();

    }

    /**
     * Retrieves a dependency with the specified group ID and artifact ID from the given Maven
     * project.
     *
     * @param mavenProject the Maven project to retrieve the dependency from
     * @param log          the logger to use for logging messages
     * @param groupId      the group ID of the dependency to retrieve
     * @param artifactId   the artifact ID of the dependency to retrieve
     *
     * @return an Optional containing the dependency if found, or an empty Optional if not found
     */
    public static Optional<Artifact> getDependency(MavenProject mavenProject,
                                                   Log log,
                                                   String groupId,
                                                   String artifactId) {
        log.debug("groupId:%s | artifactId:%s".formatted(groupId, artifactId));
        return mavenProject.getArtifacts().stream().
            filter(artifact
                -> Strings.CS.equals(artifact.getGroupId(), groupId)
            && Strings.CS.equals(artifact.getArtifactId(), artifactId)
            ).findFirst();

    }

    /**
     * Retrieves the current Jakarta EE version from the project's dependencies.
     * It specifically looks for the {@code jakarta.platform:jakarta.jakartaee-core-api} dependency.
     *
     * @param mavenProject The Maven project to inspect.
     * @param log          The logger for logging messages.
     *
     * @return An {@link Optional} containing the version string if found, otherwise an empty
     *         Optional.
     */
    public static Optional<String> getJakartaEeCurrentVersion(MavenProject mavenProject, Log log) {
        return getDependency(mavenProject, log, JAKARTA_PLATFORM, JAKARTA_JAKARTAEE_CORE_API)
            .map(Artifact::getVersion);
    }

    /**
     * Checks if a dependency with the specified group ID, artifact ID, and version exists in the
     * given Maven project.
     *
     * @param mavenProject the Maven project to check for the dependency
     * @param log          the logger to use for logging messages
     * @param groupId      the group ID of the dependency to check
     * @param artifactId   the artifact ID of the dependency to check
     * @param version      the version of the dependency to check
     *
     * @return true if the dependency exists, false otherwise
     */
    public static boolean existsDependency(MavenProject mavenProject,
                                           Log log,
                                           String groupId,
                                           String artifactId,
                                           String version) {
        log.debug("groupId:%s | artifactId:%s | version: %s".formatted(groupId, artifactId, version));
        return mavenProject.getArtifacts().stream().
            anyMatch(artifact
                -> Strings.CS.equals(artifact.getGroupId(), groupId)
            && Strings.CS.equals(artifact.getArtifactId(), artifactId)
            && Strings.CS.equals(artifact.getVersion(), version)
            );

    }

    /**
     * Saves the current state of the given Maven project to its POM file.
     *
     * @param mavenProject the Maven project to save
     * @param log          the logger to use for logging messages
     *
     * @throws MojoExecutionException if an error occurs while saving the POM file
     */
    public static void saveMavenProject(MavenProject mavenProject, Log log) throws MojoExecutionException {
        try (var writer = new FileWriter(mavenProject.getFile())) {
            var mavenWriter = new MavenXpp3Writer();
            mavenWriter.write(writer, mavenProject.getOriginalModel());
            log.info("POM file saved");
        } catch (IOException e) {
            throw new MojoExecutionException("Error saving POM file", e);
        }
    }

    /**
     * Sets a property in the given Maven project.
     *
     * @param mavenProject  the Maven project in which the property will be set
     * @param log           the logger to use for logging messages
     * @param propertyName  the name of the property to set
     * @param propertyValue the value of the property to set
     */
    public static void setProperty(MavenProject mavenProject, Log log, String propertyName,
                                   String propertyValue) {
        var model = mavenProject.getOriginalModel();
        model.getProperties().setProperty(propertyName, propertyValue);
        log.debug("setting property %s=%s".formatted(propertyName, propertyValue));
    }

    /**
     * Adds a plugin to the given Maven project.
     *
     * @param mavenProject  the Maven project to which the plugin will be added
     * @param log           the logger to use for logging messages
     * @param groupId       the group ID of the plugin
     * @param artifactId    the artifact ID of the plugin
     * @param version       the version of the plugin
     * @param configuration the configuration of the plugin as a JsonObject
     *
     * @return the Plugin object that was added
     */
    public static Plugin addPlugin(MavenProject mavenProject,
                                   Log log,
                                   String groupId, String artifactId,
                                   String version,
                                   JsonObject configuration) {
        var model = mavenProject.getOriginalModel();
        Build build = model.getBuild();
        return addPlugin(build, log, groupId, artifactId, version, configuration);
    }

    /**
     * Adds a plugin to the given Maven build base.
     *
     * @param build         the Maven build base to which the plugin will be added (e.g.,
     *                      {@code Build} or {@code PluginManagement})
     * @param log           the logger to use for logging messages
     * @param groupId       the group ID of the plugin
     * @param artifactId    the artifact ID of the plugin
     * @param version       the version of the plugin
     * @param configuration the configuration of the plugin as a JsonObject
     *
     * @return the Plugin object that was added or updated
     */
    public static Plugin addPlugin(BuildBase build,
                                   Log log,
                                   String groupId, String artifactId,
                                   String version,
                                   JsonObject configuration) {
        return addPlugin(build, log, groupId, artifactId, version, configuration, null);
    }

    /**
     * Adds a plugin to the given Maven build base, allowing for configuration and execution
     * definitions.
     * If the plugin already exists, it will be updated.
     *
     * @param build         the Maven build base to which the plugin will be added (e.g.,
     *                      {@code Build} or {@code PluginManagement})
     * @param log           the logger to use for logging messages
     * @param groupId       the group ID of the plugin
     * @param artifactId    the artifact ID of the plugin
     * @param version       the version of the plugin
     * @param configuration the configuration of the plugin as a JsonObject (can be null)
     * @param executions    a JsonArray defining plugin executions (can be null)
     *
     * @return the Plugin object that was added or updated
     */
    public static Plugin addPlugin(BuildBase build,
                                   Log log,
                                   String groupId, String artifactId,
                                   String version,
                                   JsonObject configuration, JsonArray executions) {

        var plugin = build
            .getPlugins()
            .stream().filter(plg -> Strings.CS.equals(plg.getArtifactId(), artifactId))
            .findFirst().orElseGet(() -> {
                var plg = new Plugin();
                plg.setGroupId(groupId);
                plg.setArtifactId(artifactId);
                plg.setVersion(version);
                build.addPlugin(plg);
                return plg;
            });

        if (configuration != null) {
            var configDom = Optional
                .ofNullable((Xpp3Dom) plugin.getConfiguration())
                .orElseGet(() -> new Xpp3Dom(CONFIGURATION));
            var config = JsonUtil.jsonToXpp3Dom(log, configDom, configuration, true);
            plugin.setConfiguration(config);
        }
        if (executions != null) {
            addExecutions(log, plugin, executions);
        }
        log.debug("adding plugin %s".formatted(plugin));
        return plugin;

    }

    private static void addExecutions(Log log, Plugin plugin, JsonArray executions) {
        var pluginExecutions = plugin.getExecutions();
        log.debug("pluginExecutions:" + pluginExecutions);
        executions.stream().map(JsonValue::asJsonObject).forEach(executionDefinition -> {
            var id = executionDefinition.getString("id", StringUtils.EMPTY);
            var phase = executionDefinition.getString("phase", StringUtils.EMPTY);
            var pluginExecution
                = StringUtils.isAllBlank(id, phase)
                ? createPluginExecution(StringUtils.EMPTY, StringUtils.EMPTY, pluginExecutions)
                : pluginExecutions
                    .stream()
                    .filter(pe
                        -> Strings.CS.equals(pe.getId(), id)
                    )
                    .findFirst()
                    .orElseGet(() -> createPluginExecution(id, phase, pluginExecutions));
            if (executionDefinition.containsKey(GOALS)) {
                executionDefinition.getJsonArray(GOALS)
                    .stream()
                    .map(JsonValue::asJsonObject)
                    .map(item -> item.getString(GOAL))
                    .filter(goal -> !pluginExecution.getGoals().contains(goal))
                    .forEach(pluginExecution::addGoal);
            }
            if (executionDefinition.containsKey(CONFIGURATION)) {
                var configDom = (Xpp3Dom) pluginExecution.getConfiguration();
                var config = JsonUtil.jsonToXpp3Dom(log, configDom, executionDefinition.getJsonObject(
                    CONFIGURATION));
                pluginExecution.setConfiguration(config);
            }
        });
    }

    private static PluginExecution createPluginExecution(String id,
                                                         String phase,
                                                         List<PluginExecution> pluginExecutions) {
        var pe = new PluginExecution();
        if (StringUtils.isNotBlank(phase)) {
            pe.setPhase(phase);
        }
        if (StringUtils.isNotBlank(id)) {
            pe.setId(id);
        }
        pe.setGoals(new ArrayList<>());
        pe.setConfiguration(new Xpp3Dom(CONFIGURATION));
        pluginExecutions.add(pe);
        return pe;
    }

    /**
     * Adds a dependency management to the given Maven project.
     *
     * @param mavenProject the Maven project to which the dependency management will be added
     * @param log          the logger to use for logging messages
     * @param groupId      the group ID of the dependency management
     * @param artifactId   the artifact ID of the dependency management
     * @param scope        the scope of the dependency management
     */
    public static void addDependencyManagement(MavenProject mavenProject, Log log, String groupId,
                                               String artifactId, String scope) {
        var dependencyManagement = Optional
            .ofNullable(mavenProject.getModel().getDependencyManagement())
            .orElseGet(() -> {
                var list = new DependencyManagement();
                mavenProject.getModel().setDependencyManagement(list);
                return list;
            });
        var find = dependencyManagement
            .getDependencies()
            .stream()
            .filter(dependency -> Strings.CS.equals(dependency.getGroupId(), groupId)
            && Strings.CS.equals(dependency.getArtifactId(), artifactId))
            .findFirst();
        if (find.isEmpty()) {
            var dependency = new Dependency();
            dependency.setGroupId(groupId);
            dependency.setArtifactId(artifactId);
            if (!StringUtils.isBlank(scope)) {
                dependency.setScope(scope);
            }
            dependencyManagement.addDependency(dependency);
        }

    }

    private PomUtil() {
    }
}
