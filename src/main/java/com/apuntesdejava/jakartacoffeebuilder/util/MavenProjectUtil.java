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

import static com.apuntesdejava.jakartacoffeebuilder.util.Constants.*;

/**
 * A utility class for common Maven project-related operations.
 * <p>
 * This class provides static helper methods to interact with a {@link MavenProject} object,
 * such as resolving the full project with dependencies, deriving package names from project
 * coordinates, and manipulating profiles and build configurations.
 *
 * @author Diego Silva diego.silva at apuntesdejava.com
 */
public final class MavenProjectUtil {

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private MavenProjectUtil() {
    }

    /**
     * Resolves and returns the full {@link MavenProject} instance, including all its dependencies.
     *
     * @param mavenSession   The current {@link MavenSession}.
     * @param projectBuilder The {@link ProjectBuilder} component for constructing projects.
     * @param mavenProject   The initial {@link MavenProject} to resolve.
     * @return The fully resolved {@link MavenProject} with all dependencies.
     * @throws ProjectBuildingException if an error occurs while building the project.
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
     * Derives a base package name from the project's group ID and artifact ID.
     * Any characters that are not alphanumeric are replaced with a dot (.).
     *
     * @param mavenProject The Maven project.
     * @return The generated base package name as a string.
     */
    public static String getProjectPackage(MavenProject mavenProject) {
        return "%s.%s".formatted(RegExUtils.replaceAll(mavenProject.getGroupId(), "[^a-zA-Z0-9]", "."),
                RegExUtils.replaceAll(mavenProject.getArtifactId(), "[^a-zA-Z0-9]", "."));
    }

    /**
     * Constructs the package name for the persistence entity layer.
     *
     * @param mavenProject The Maven project.
     * @return The package name for the entity layer (e.g., {@code com.example.project.entity}).
     */
    public static String getEntityPackage(MavenProject mavenProject) {
        return "%s.%s.%s".formatted(getProjectPackage(mavenProject), INFRASTRUCTURE, ENTITY);
    }

    /**
     * Constructs the package name for the DTO/model layer.
     *
     * @param mavenProject The Maven project.
     * @return The package name for the model layer (e.g., {@code com.example.project.model}).
     */
    public static String getModelPackage(MavenProject mavenProject) {
        return "%s.%s.%s".formatted(getProjectPackage(mavenProject), DOMAIN, "model");
    }

    /**
     * Constructs the package name for the mapper layer (e.g., for MapStruct).
     *
     * @param mavenProject The Maven project.
     * @return The package name for the mapper layer (e.g., {@code com.example.project.mapper}).
     */
    public static String getMapperPackage(MavenProject mavenProject) {
        return "%s.%s.%s".formatted(getProjectPackage(mavenProject), INFRASTRUCTURE, "mapper");
    }

    /**
     * Constructs the package name for the service layer.
     *
     * @param mavenProject The Maven project.
     * @return The package name for the service layer (e.g., {@code com.example.project.service}).
     */
    public static String getServicePackage(MavenProject mavenProject) {
        return "%s.%s.%s".formatted(getProjectPackage(mavenProject), DOMAIN, "service");
    }

    /**
     * Constructs the package name for Java enums.
     *
     * @param mavenProject The Maven project.
     * @return The package name for the enums layer (e.g., {@code com.example.project.enums}).
     */
    public static String getEnumsPackage(MavenProject mavenProject) {
        return "%s.%s".formatted(getProjectPackage(mavenProject), "enums");
    }

    /**
     * Constructs the package name for the repository/DAO layer.
     *
     * @param mavenProject The Maven project.
     * @return The package name for the repository layer (e.g., {@code com.example.project.repository}).
     */
    public static String getRepositoryPackage(MavenProject mavenProject) {
        return "%s.%s.%s".formatted(getProjectPackage(mavenProject), INFRASTRUCTURE, "repository");
    }

    /**
     * Constructs the package name for a provider layer (e.g., for JAX-RS providers).
     *
     * @param mavenProject The Maven project.
     * @return The package name for the provider layer (e.g., {@code com.example.project.provider}).
     */
    public static String getProviderPackage(MavenProject mavenProject) {
        return "%s.%s.%s".formatted(getProjectPackage(mavenProject), INFRASTRUCTURE, "provider");
    }

    /**
     * Constructs the package name for the JSF/Faces managed bean layer.
     *
     * @param mavenProject The Maven project.
     * @return The package name for the faces layer (e.g., {@code com.example.project.faces}).
     */
    public static String getFacesPackage(MavenProject mavenProject) {
        return "%s.%s.%s".formatted(getProjectPackage(mavenProject),APP, "faces");
    }

    /**
     * Constructs the package name for the REST API resources layer.
     *
     * @param mavenProject The Maven project.
     * @return The package name for the resources layer (e.g., {@code com.example.project.resources}).
     */
    public static String getApiResourcesPackage(MavenProject mavenProject) {
        return "%s.%s.%s".formatted(getProjectPackage(mavenProject), APP, "resources");
    }

    /**
     * Constructs the package name for the domain model layer.
     *
     * @param mavenProject The Maven project.
     * @return The package name for the model layer (e.g., {@code com.example.project.model}).
     */
    public static String getDomainModelPackage(MavenProject mavenProject) {
        return "%s.%s.%s".formatted(getProjectPackage(mavenProject), DOMAIN, "model");
    }

    /**
     * Retrieves a profile from the project's model by its ID. If the profile does not exist,
     * it is created and added to the model.
     *
     * @param mavenProject The Maven project.
     * @param profileId    The ID of the profile to find or create.
     * @return The existing or newly created {@link Profile}.
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
     * Gets the parent directory of the Maven project's POM file.
     *
     * @param mavenProject The Maven project.
     * @return The {@link Path} to the directory containing the {@code pom.xml} file.
     */
    public static Path getParent(MavenProject mavenProject) {
        return mavenProject.getFile().toPath().getParent();
    }


    /**
     * Retrieves the {@link BuildBase} section from a specific profile. If the profile or its build
     * section does not exist, they are created.
     *
     * @param mavenProject The Maven project.
     * @param profileId    The ID of the profile containing the build section.
     * @return The existing or newly created {@link BuildBase} instance.
     */
    public static BuildBase getBuild(MavenProject mavenProject, String profileId) {
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
