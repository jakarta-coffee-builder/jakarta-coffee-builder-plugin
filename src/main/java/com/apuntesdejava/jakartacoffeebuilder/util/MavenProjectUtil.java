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

import org.apache.commons.lang3.RegExUtils;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Build;
import org.apache.maven.model.BuildBase;
import org.apache.maven.model.Model;
import org.apache.maven.model.Profile;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.ProjectBuilder;
import org.apache.maven.project.ProjectBuildingException;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Optional;

/**
 * Helper class for Maven project operations.
 * Provides methods to retrieve full Maven projects with resolved dependencies
 * and to construct package names based on Maven project details.
 *
 * @author Diego Silva diego.silva at apuntesdejava.com
 */
public class MavenProjectUtil {

    private MavenProjectUtil() {
    }

    /**
     * Retrieves the full Maven project with resolved dependencies.
     *
     * @param mavenSession   the current Maven session
     * @param projectBuilder the project builder to use
     * @param mavenProject   the Maven project to build
     * @return the fully built Maven project with resolved dependencies
     * @throws ProjectBuildingException if an error occurs during project building
     */
    public static MavenProject getFullProject(MavenSession mavenSession,
                                              ProjectBuilder projectBuilder,
                                              MavenProject mavenProject) throws ProjectBuildingException {
        var buildingRequest = mavenSession.getProjectBuildingRequest();
        buildingRequest.setResolveDependencies(true);
        var result = projectBuilder.build(mavenProject.getFile(), buildingRequest);
        return result.getProject();
    }

    /**
     * Constructs a package name based on the group ID and artifact ID of the given Maven project.
     * Non-alphanumeric characters in the group ID and artifact ID are replaced with dots.
     *
     * @param mavenProject the Maven project containing the group ID and artifact ID
     * @return the generated package name as a string
     */
    public static String getProjectPackage(MavenProject mavenProject) {
        return "%s.%s".formatted(RegExUtils.replaceAll(mavenProject.getGroupId(), "[^a-zA-Z0-9]", "."),
            RegExUtils.replaceAll(mavenProject.getArtifactId(), "[^a-zA-Z0-9]", "."));
    }

    /**
     * Constructs a package name for the "entity" layer based on the Maven project details.
     *
     * @param mavenProject the Maven project containing the group ID and artifact ID
     * @return the generated package name for the "entity" layer
     */
    public static String getEntityPackage(MavenProject mavenProject) {
        return "%s.%s".formatted(getProjectPackage(mavenProject), "entity");
    }

    /**
     * Constructs a package name for the "repository" layer based on the Maven project details.
     *
     * @param mavenProject the Maven project containing the group ID and artifact ID
     * @return the generated package name for the "repository" layer
     */
    public static String getRepositoryPackage(MavenProject mavenProject) {
        return "%s.%s".formatted(getProjectPackage(mavenProject), "repository");
    }

    /**
     * Constructs a package name for the "provider" layer based on the Maven project details.
     *
     * @param mavenProject the Maven project containing the group ID and artifact ID
     * @return the generated package name for the "provider" layer
     */
    public static String getProviderPackage(MavenProject mavenProject) {
        return "%s.%s".formatted(getProjectPackage(mavenProject), "provider");
    }

    /**
     * Constructs a package name for the "faces" layer based on the Maven project details.
     *
     * @param mavenProject the Maven project containing the group ID and artifact ID
     * @return the generated package name for the "faces" layer
     */
    public static String getFacesPackage(MavenProject mavenProject) {
        return "%s.%s".formatted(getProjectPackage(mavenProject), "faces");
    }

    /**
     * Constructs a package name for the "resources" API layer based on the Maven project details.
     *
     * @param mavenProject the Maven project containing the group ID and artifact ID
     * @return the generated package name for the "resources" API layer
     */
    public static String getApiResourcesPackage(MavenProject mavenProject) {
        return "%s.%s".formatted(getProjectPackage(mavenProject), "resources");
    }

    /**
     * Retrieves an active profile from the Maven project by its ID.
     * If the profile does not exist, a new one is created, added to the active profiles, and returned.
     *
     * @param mavenProject The Maven project.
     * @param profileId    The ID of the profile to retrieve or create.
     * @return The found or newly created {@link Profile}.
     */
    public static Profile getProfile(MavenProject mavenProject, String profileId) {
        var model = getOriginalModel(mavenProject);
        return model.getProfiles()
                    .stream()
                    .filter(profile -> profile.getId().equals(profileId))
                    .findFirst()
                    .orElseGet(() -> {
                        var profile = new Profile();
                        profile.setId(profileId);
                        model.addProfile(profile);
                        return profile;
                    });

    }

    private static Model getOriginalModel(MavenProject mavenProject) {
        return Optional.ofNullable(mavenProject.getOriginalModel()).orElse(mavenProject.getModel());
    }

    /**
     * Retrieves the parent directory of the Maven project's POM file.
     *
     * @param mavenProject The Maven project.
     * @return The {@link Path} to the parent directory of the POM file.
     */
    public static Path getParent(MavenProject mavenProject) {
        return mavenProject.getFile().toPath().getParent();
    }


    public static BuildBase getBuild(MavenProject mavenProject,String profileId){
        var profile = MavenProjectUtil.getProfile(mavenProject, profileId);

        return Optional.ofNullable(profile.getBuild())
                       .orElseGet(() -> {
                                Build bld = new Build();
                                profile.setBuild(bld);
                                bld.setPlugins(new ArrayList<>());
                                return bld;
                            });
    }
}
