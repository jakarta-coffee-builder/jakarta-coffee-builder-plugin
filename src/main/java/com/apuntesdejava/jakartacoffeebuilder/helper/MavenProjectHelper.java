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

import org.apache.commons.lang3.RegExUtils;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.ProjectBuilder;
import org.apache.maven.project.ProjectBuildingException;

/**
 * Helper class for Maven project operations.
 * Provides methods to retrieve full Maven projects with resolved dependencies
 * and to construct package names based on Maven project details.
 *
 * @author Diego Silva <diego.silva at apuntesdejava.com>
 */
public class MavenProjectHelper {

    private MavenProjectHelper() {
    }

    public static MavenProjectHelper getInstance() {
        return MavenProjectUtilHolder.INSTANCE;
    }

    private static class MavenProjectUtilHolder {

        private static final MavenProjectHelper INSTANCE = new MavenProjectHelper();
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
    public MavenProject getFullProject(MavenSession mavenSession,
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
    public String getProjectPackage(MavenProject mavenProject) {
        return "%s.%s".formatted(RegExUtils.replaceAll(mavenProject.getGroupId(), "[^a-zA-Z0-9]", "."),
            RegExUtils.replaceAll(mavenProject.getArtifactId(), "[^a-zA-Z0-9]", "."));
    }
}
