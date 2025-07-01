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

import jakarta.json.JsonObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.model.Build;
import org.apache.maven.model.BuildBase;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Plugin;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.xml.Xpp3Dom;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Optional;

import static com.apuntesdejava.jakartacoffeebuilder.util.HttpUtil.STRING_TO_JSON_OBJECT_RESPONSE_CONVERTER;

/**
 * Utility class for handling Maven POM file operations.
 * <p>
 * This class provides methods to add dependencies to a Maven project, check for existing dependencies,
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

    private PomUtil() {
    }


    /**
     * Adds a dependency to the given Maven project.
     *
     * @param mavenProject the Maven project to which the dependency will be added
     * @param log          the logger to use for logging messages
     * @param groupId      the group ID of the dependency
     * @param artifactId   the artifact ID of the dependency
     * @param version      the version of the dependency
     * @param scope        the scope of the dependency
     */
    public static void addDependency(MavenProject mavenProject,
                                     Log log,
                                     String groupId,
                                     String artifactId,
                                     String version,
                                     String scope) {
        var model = mavenProject.getOriginalModel();
        if (model.getDependencies().stream()
                 .filter(dependency -> StringUtils.equals(dependency.getGroupId(), groupId) &&
                     StringUtils.equals(dependency.getArtifactId(), artifactId))
                 .findFirst().isEmpty()) {
            var dependency = new Dependency();
            dependency.setArtifactId(artifactId);
            dependency.setGroupId(groupId);
            dependency.setVersion(version);
            if (StringUtils.isNotBlank(scope)) {
                dependency.setScope(scope);
            }
            log.debug("adding dependency %s".formatted(dependency));
            model.addDependency(dependency);
        }
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
        addDependency(mavenProject, log, groupId, artifactId, version, null);
    }

    /**
     * Adds a dependency to the given Maven project using the specified coordinates.
     *
     * @param mavenProject the Maven project to which the dependency will be added
     * @param log          the logger to use for logging messages
     * @param coordinates  the coordinates of the dependency in the format groupId:artifactId:version
     */
    public static void addDependency(MavenProject mavenProject,
                                     Log log,
                                     String coordinates) {
        try {
            var coordinatesSplit = StringUtils.split(coordinates, ":");
            var groupId = coordinatesSplit[0];
            var artifactId = coordinatesSplit[1];
            var version = coordinatesSplit.length == 3 ? coordinatesSplit[2] : getLastVersion(groupId, artifactId);
            log.debug("adding dependency %s".formatted(coordinates));
            log.debug("groupId:%s | artifactId:%s | version:%s".formatted(groupId, artifactId, version));
            addDependency(mavenProject, log, groupId, artifactId, version);
        } catch (IOException ex) {
            log.error("Error getting last version of %s".formatted(coordinates), ex);
        }
    }

    private static String getLastVersion(String groupId, String artifactId) throws IOException {
        var params = "p:jar AND a:%s AND g:%s".formatted(artifactId, groupId);
        var response = HttpUtil.getContent("https://search.maven.org/solrsearch/select",
            STRING_TO_JSON_OBJECT_RESPONSE_CONVERTER, new HttpUtil.Parameter("q", params));
        return response.getJsonObject("response")
                       .getJsonArray("docs")
                       .getJsonObject(0)
                       .getString("latestVersion");
    }

    /**
     * Checks if a dependency with the specified group ID and artifact ID exists in the given Maven project.
     *
     * @param mavenProject the Maven project to check for the dependency
     * @param log          the logger to use for logging messages
     * @param groupId      the group ID of the dependency to check
     * @param artifactId   the artifact ID of the dependency to check
     * @return true if the dependency exists, false otherwise
     */
    public static boolean existsDependency(MavenProject mavenProject, Log log, String groupId, String artifactId) {
        log.debug("groupId:%s | artifactId:%s".formatted(groupId, artifactId));
        return getDependency(mavenProject, log, groupId, artifactId).isPresent();

    }

    /**
     * Retrieves a dependency with the specified group ID and artifact ID from the given Maven project.
     *
     * @param mavenProject the Maven project to retrieve the dependency from
     * @param log          the logger to use for logging messages
     * @param groupId      the group ID of the dependency to retrieve
     * @param artifactId   the artifact ID of the dependency to retrieve
     * @return an Optional containing the dependency if found, or an empty Optional if not found
     */
    public static Optional<Artifact> getDependency(MavenProject mavenProject,
                                                   Log log,
                                                   String groupId,
                                                   String artifactId) {
        log.debug("groupId:%s | artifactId:%s".formatted(groupId, artifactId));
        return mavenProject.getArtifacts().stream().
                           filter(artifact ->
                               StringUtils.equals(artifact.getGroupId(), groupId)
                                   && StringUtils.equals(artifact.getArtifactId(), artifactId)
                           ).findFirst();

    }

    /**
     * Checks if a dependency with the specified group ID, artifact ID, and version exists in the given Maven project.
     *
     * @param mavenProject the Maven project to check for the dependency
     * @param log          the logger to use for logging messages
     * @param groupId      the group ID of the dependency to check
     * @param artifactId   the artifact ID of the dependency to check
     * @param version      the version of the dependency to check
     * @return true if the dependency exists, false otherwise
     */
    public static boolean existsDependency(MavenProject mavenProject,
                                           Log log,
                                           String groupId,
                                           String artifactId,
                                           String version) {
        log.debug("groupId:%s | artifactId:%s | version: %s".formatted(groupId, artifactId, version));
        return mavenProject.getArtifacts().stream().
                           anyMatch(artifact ->
                               StringUtils.equals(artifact.getGroupId(), groupId)
                                   && StringUtils.equals(artifact.getArtifactId(), artifactId)
                                   && StringUtils.equals(artifact.getVersion(), version)
                           );

    }

    /**
     * Saves the current state of the given Maven project to its POM file.
     *
     * @param mavenProject the Maven project to save
     * @param log          the logger to use for logging messages
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
    public static void setProperty(MavenProject mavenProject, Log log, String propertyName, String propertyValue) {
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
     * @return  the Plugin object that was added
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
     * @param build the Maven build base to which the plugin will be added (e.g., {@code Build} or {@code PluginManagement})
     * @param log the logger to use for logging messages
     * @param groupId the group ID of the plugin
     * @param artifactId the artifact ID of the plugin
     * @param version the version of the plugin
     * @param configuration the configuration of the plugin as a JsonObject
     * @return the Plugin object that was added or updated
     */
    public static Plugin addPlugin(BuildBase build,
                                   Log log,
                                   String groupId, String artifactId,
                                   String version,
                                   JsonObject configuration) {
        var plugin = build
            .getPlugins()
            .stream().filter(plg -> StringUtils.equals(plg.getArtifactId(), artifactId))
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
                .orElseGet(() -> new Xpp3Dom("configuration"));
            var config = JsonUtil.jsonToXpp3Dom(configDom, configuration);
            plugin.setConfiguration(config);
        }
        log.debug("adding plugin %s".formatted(plugin));
        return plugin;
    }
}
