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
package com.apuntesdejava.jakartacoffeebuilder.util;

import org.apache.commons.lang3.StringUtils;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;

import java.io.FileWriter;
import java.io.IOException;

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
 * </p>
 * <p>
 * Note: This class is thread-safe.
 * </p>
 * <p>
 * Author: Diego Silva &lt;diego.silva at apuntesdejava.com&gt;
 * </p>
 */
public class PomUtil {


    public static PomUtil getInstance() {
        return PomUtilHolder.INSTANCE;
    }

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
    public void addDependency(MavenProject mavenProject,
                              Log log,
                              String groupId,
                              String artifactId,
                              String version,
                              String scope) {
        var dependency = new Dependency();
        dependency.setArtifactId(artifactId);
        dependency.setGroupId(groupId);
        dependency.setVersion(version);
        if (StringUtils.isNotBlank(scope)) {
            dependency.setScope(scope);
        }
        log.debug("adding dependency %s".formatted(dependency));
        mavenProject.getOriginalModel().addDependency(dependency);
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
    public boolean existsDependency(MavenProject mavenProject, Log log, String groupId, String artifactId) {
        log.debug("groupId:%s | artifactId:%s".formatted(groupId, artifactId));
        return mavenProject.getArtifacts().stream().
                           anyMatch(artifact -> StringUtils.
                               equals(artifact.getGroupId(), groupId) && StringUtils.
                               equals(artifact.getArtifactId(), artifactId));

    }

    /**
     * Saves the current state of the given Maven project to its POM file.
     *
     * @param mavenProject the Maven project to save
     * @param log          the logger to use for logging messages
     * @throws MojoExecutionException if an error occurs while saving the POM file
     */
    public void saveMavenProject(MavenProject mavenProject, Log log) throws MojoExecutionException {
        try (var writer = new FileWriter(mavenProject.getFile())) {
            var mavenWriter = new MavenXpp3Writer();
            mavenWriter.write(writer, mavenProject.getOriginalModel());
            log.info("POM file saved");
        } catch (IOException e) {
            throw new MojoExecutionException("Error saving POM file", e);
        }
    }


    private static class PomUtilHolder {

        private static final PomUtil INSTANCE = new PomUtil();
    }
}
