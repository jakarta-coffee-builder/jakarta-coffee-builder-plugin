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
 * A utility class for programmatically manipulating a Maven {@code pom.xml} file.
 * <p>
 * This class provides static methods to add and manage dependencies, plugins, and properties
 * within a {@link MavenProject} model. It also includes helpers for querying Maven Central for
 * the latest artifact versions and for saving the modified POM file back to disk.
 * <p>
 * This is a final utility class and cannot be instantiated.
 *
 * @author Diego Silva &lt;diego.silva at apuntesdejava.com&gt;
 */
public final class PomUtil {

    /**
     * Adds a new dependency to the project's POM model if a dependency with the same
     * groupId and artifactId does not already exist.
     *
     * @param mavenProject The Maven project whose model will be modified.
     * @param log          The Maven plugin logger for outputting information.
     * @param groupId      The group ID of the dependency.
     * @param artifactId   The artifact ID of the dependency.
     * @param version      The version of the dependency.
     * @param scope        The scope of the dependency (e.g., "compile", "provided"). Can be null.
     * @param classifier   The classifier of the dependency (e.g., "sources", "javadoc"). Can be null.
     * @param exclusions   A list of maps, where each map represents a dependency to exclude.
     *                     Each map should contain "groupId" and "artifactId" keys. Can be null.
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

    /**
     * Adds a new dependency with a specific scope and exclusions.
     *
     * @param mavenProject The Maven project.
     * @param log          The Maven logger.
     * @param groupId      The group ID of the dependency.
     * @param artifactId   The artifact ID of the dependency.
     * @param version      The version of the dependency.
     * @param scope        The scope of the dependency.
     * @param exclusions   A list of exclusions.
     */
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
     * Adds a new dependency with a specific version and exclusions.
     *
     * @param mavenProject The Maven project.
     * @param log          The Maven logger.
     * @param groupId      The group ID of the dependency.
     * @param artifactId   The artifact ID of the dependency.
     * @param version      The version of the dependency.
     * @param exclusions   A list of exclusions.
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
     * Adds a new dependency with a specific scope.
     *
     * @param mavenProject The Maven project.
     * @param log          The Maven logger.
     * @param groupId      The group ID of the dependency.
     * @param artifactId   The artifact ID of the dependency.
     * @param version      The version of the dependency.
     * @param scope        The scope of the dependency.
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
     * Adds a new dependency with a compile scope by default.
     *
     * @param mavenProject The Maven project.
     * @param log          The Maven logger.
     * @param groupId      The group ID of the dependency.
     * @param artifactId   The artifact ID of the dependency.
     * @param version      The version of the dependency.
     */
    public static void addDependency(MavenProject mavenProject,
                                     Log log,
                                     String groupId,
                                     String artifactId,
                                     String version) {
        addDependency(mavenProject, log, groupId, artifactId, version, null, null, null);
    }

    /**
     * Adds a new dependency using its Maven coordinates (groupId:artifactId:version).
     *
     * @param mavenProject The Maven project.
     * @param log          The Maven logger.
     * @param coordinates  The dependency coordinates string.
     */
    public static void addDependency(MavenProject mavenProject,
                                     Log log,
                                     String coordinates) {
        addDependency(mavenProject, log, coordinates, null);
    }

    /**
     * Adds a new dependency using its Maven coordinates and an optional classifier.
     * If the version is not specified in the coordinates, it attempts to find the latest version.
     *
     * @param mavenProject The Maven project.
     * @param log          The Maven logger.
     * @param coordinates  The dependency coordinates string (e.g., "groupId:artifactId" or "groupId:artifactId:version").
     * @param classifier   The classifier for the dependency. Can be null.
     */
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
     * Finds the latest version of a Maven dependency by querying the Maven Central search API.
     *
     * @param groupId    The group ID of the dependency.
     * @param artifactId The artifact ID of the dependency.
     * @return An {@link Optional} containing the latest version string, or empty if not found.
     * @throws IOException if an error occurs during the HTTP request to Maven Central.
     */
    public static Optional<String> findLatestDependencyVersion(String groupId, String artifactId) throws IOException {
        return findLatestVersion(groupId, artifactId, "jar");
    }

    /**
     * Finds the latest version of a Maven plugin by querying the Maven Central search API.
     *
     * @param groupId    The group ID of the plugin.
     * @param artifactId The artifact ID of the plugin.
     * @return An {@link Optional} containing the latest version string, or empty if not found.
     * @throws IOException if an error occurs during the HTTP request to Maven Central.
     */
    public static Optional<String> findLatestPluginVersion(String groupId, String artifactId) throws IOException {
        return findLatestVersion(groupId, artifactId, "maven-plugin");
    }

    /**
     * Finds the latest version of a Maven artifact with a specific packaging type from Maven Central.
     *
     * @param groupId    The group ID of the artifact.
     * @param artifactId The artifact ID of the artifact.
     * @param packaging  The packaging type (e.g., "jar", "maven-plugin").
     * @return An {@link Optional} containing the latest version string, or empty if not found.
     * @throws IOException if an error occurs during the HTTP request.
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
     * Retrieves detailed information for a specific artifact version from Maven Central.
     *
     * @param groupId    The group ID of the artifact.
     * @param artifactId The artifact ID of the artifact.
     * @param version    The version of the artifact.
     * @return A {@link JsonObject} containing the artifact's information.
     * @throws IOException if an error occurs during the HTTP request.
     */
    public static JsonObject getArtifactInfo(String groupId, String artifactId, String version) throws IOException {
        var params = "v:%s AND a:%s AND g:%s".formatted(version, artifactId, groupId);
        var response = HttpUtil.getContent("https://search.maven.org/solrsearch/select",
            STRING_TO_JSON_OBJECT_RESPONSE_CONVERTER, new HttpUtil.Parameter("q", params));
        return response.getJsonObject("response").getJsonArray("docs").getJsonObject(0);
    }

    /**
     * Checks if a dependency exists in the project's resolved artifacts.
     *
     * @param mavenProject The Maven project.
     * @param log          The Maven logger.
     * @param groupId      The group ID of the dependency to check.
     * @param artifactId   The artifact ID of the dependency to check.
     * @return {@code true} if the dependency exists, {@code false} otherwise.
     */
    public static boolean existsDependency(MavenProject mavenProject, Log log, String groupId,
                                           String artifactId) {
        log.debug("groupId:%s | artifactId:%s".formatted(groupId, artifactId));
        return getDependency(mavenProject, log, groupId, artifactId).isPresent();

    }

    /**
     * Retrieves a resolved dependency artifact from the project.
     *
     * @param mavenProject The Maven project.
     * @param log          The Maven logger.
     * @param groupId      The group ID of the dependency.
     * @param artifactId   The artifact ID of the dependency.
     * @return An {@link Optional} containing the resolved {@link Artifact}, or empty if not found.
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
     * Gets the Jakarta EE version used in the project by inspecting the version of the
     * {@code jakarta.platform:jakarta.jakartaee-core-api} dependency.
     *
     * @param mavenProject The Maven project to inspect.
     * @param log          The logger for logging messages.
     * @return An {@link Optional} containing the version string if found, otherwise empty.
     */
    public static Optional<String> getJakartaEeCurrentVersion(MavenProject mavenProject, Log log) {
        return getDependency(mavenProject, log, JAKARTA_PLATFORM, JAKARTA_JAKARTAEE_CORE_API)
            .map(Artifact::getVersion);
    }

    /**
     * Checks if a dependency with a specific version exists in the project's resolved artifacts.
     *
     * @param mavenProject The Maven project.
     * @param log          The Maven logger.
     * @param groupId      The group ID of the dependency.
     * @param artifactId   The artifact ID of the dependency.
     * @param version      The version of the dependency.
     * @return {@code true} if the dependency with the specified version exists, {@code false} otherwise.
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
     * Saves the current state of the Maven project model back to its {@code pom.xml} file.
     *
     * @param mavenProject The Maven project to save.
     * @param log          The logger for logging messages.
     * @throws MojoExecutionException if an I/O error occurs while writing the file.
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
     * Sets a property in the {@code <properties>} section of the project's POM.
     *
     * @param mavenProject  The Maven project.
     * @param log           The Maven logger.
     * @param propertyName  The name of the property to set.
     * @param propertyValue The value of the property.
     */
    public static void setProperty(MavenProject mavenProject, Log log, String propertyName,
                                   String propertyValue) {
        var model = mavenProject.getOriginalModel();
        model.getProperties().setProperty(propertyName, propertyValue);
        log.debug("setting property %s=%s".formatted(propertyName, propertyValue));
    }

    /**
     * Adds or updates a plugin in the main {@code <build>} section of the POM.
     *
     * @param mavenProject  The Maven project.
     * @param log           The Maven logger.
     * @param groupId       The group ID of the plugin.
     * @param artifactId    The artifact ID of the plugin.
     * @param version       The version of the plugin.
     * @param configuration A {@link JsonObject} representing the plugin's {@code <configuration>}.
     * @return The created or updated {@link Plugin} instance.
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
     * Adds or updates a plugin in the specified {@link BuildBase} section (e.g., from a profile).
     *
     * @param build         The build section to modify.
     * @param log           The Maven logger.
     * @param groupId       The group ID of the plugin.
     * @param artifactId    The artifact ID of the plugin.
     * @param version       The version of the plugin.
     * @param configuration A {@link JsonObject} representing the plugin's {@code <configuration>}.
     * @return The created or updated {@link Plugin} instance.
     */
    public static Plugin addPlugin(BuildBase build,
                                   Log log,
                                   String groupId, String artifactId,
                                   String version,
                                   JsonObject configuration) {
        return addPlugin(build, log, groupId, artifactId, version, configuration, null);
    }

    /**
     * Adds or updates a plugin in the specified {@link BuildBase} section, including configuration and executions.
     * If the plugin already exists, its configuration and executions are merged.
     *
     * @param build         The build section to modify.
     * @param log           The Maven logger.
     * @param groupId       The group ID of the plugin.
     * @param artifactId    The artifact ID of the plugin.
     * @param version       The version of the plugin.
     * @param configuration A {@link JsonObject} representing the plugin's {@code <configuration>}. Can be null.
     * @param executions    A {@link JsonArray} defining plugin executions. Can be null.
     * @return The created or updated {@link Plugin} instance.
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
     * Adds a dependency to the {@code <dependencyManagement>} section of the POM.
     *
     * @param mavenProject The Maven project.
     * @param log          The Maven logger.
     * @param groupId      The group ID of the dependency.
     * @param artifactId   The artifact ID of the dependency.
     * @param scope        The scope of the dependency (e.g., "import").
     */
    public static void addDependencyManagement(MavenProject mavenProject, Log log, String groupId,
                                               String artifactId, String scope) {
        var dependencyManagement = Optional
            .ofNullable(mavenProject.getModel().getDependencyManagement())
            .orElseGet(() -> {
                var dm = new DependencyManagement();
                mavenProject.getModel().setDependencyManagement(dm);
                return dm;
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

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private PomUtil() {
    }
}
