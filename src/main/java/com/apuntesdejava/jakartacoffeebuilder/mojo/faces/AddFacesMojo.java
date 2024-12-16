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
package com.apuntesdejava.jakartacoffeebuilder.mojo.faces;

import static com.apuntesdejava.jakartacoffeebuilder.util.Constants.JAKARTAEE_VERSION_10;

import com.apuntesdejava.jakartacoffeebuilder.util.JakartaEeUtil;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.ProjectBuilder;
import org.apache.maven.project.ProjectBuildingException;
import org.apache.maven.project.ProjectBuildingRequest;

import java.io.IOException;

/**
 * Mojo implementation for adding necessary Jakarta Faces configurations to a Maven project.
 * This includes adding a URL pattern for Faces requests, configuring the welcome file,
 * and ensuring required dependencies are included in the project.
 *
 * This Mojo performs the following tasks:
 * - Checks and adds the necessary Jakarta Faces and CDI dependencies.
 * - Configures and validates the Jakarta Faces servlet declaration in the project descriptor.
 * - Updates the web application's welcome file configuration with the specified value.
 *
 * Goal: add-faces
 * Configuration Parameters:
 * - url-pattern: Specifies the URL pattern to be used for Faces requests (default: "*.faces").
 * - welcome-file: Specifies the welcome file name (default: "index.faces").
 * - jakarta-ee-version: Defines the Jakarta EE version to use (default: Jakarta EE 10).
 * - mavenProject: Represents the Maven project being processed.
 * - mavenSession: Provides the Maven execution session information.
 * - projectBuilder: Helper to build Maven project instances.
 *
 * Execution:
 * - Executes the above tasks in sequence, logging relevant information or errors as
 *   applicable.
 * - Throws MojoExecutionException or MojoFailureException if issues occur during execution.
 */
@Mojo(
    name = "add-faces"
)
public class AddFacesMojo extends AbstractMojo {

    @Parameter(
        property = "url-pattern",
        defaultValue = "*.faces"
    )
    private String urlPattern;

    @Parameter(
        property = "welcome-file",
        defaultValue = "index.faces"
    )
    private String welcomeFile;

    @Parameter(defaultValue = "${project}", readonly = true)
    private MavenProject mavenProject;

    @Parameter(
        property = "jakarta-ee-version",
        defaultValue = JAKARTAEE_VERSION_10
    )
    private String jakartaEeVersion;

    @Component
    private ProjectBuilder projectBuilder;

    @Parameter(
        defaultValue = "${session}",
        readonly = true,
        required = true
    )
    private MavenSession mavenSession;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        var log = getLog();
        log.info("Executing: url-pattern:%s | welcome-file:%s".formatted(urlPattern, welcomeFile));
        log.debug("Project name:%s".formatted(mavenProject.getName()));

        checkDependency(log);
        checkJakartaFacesServletDeclaration(log);
        checkWelcomePages(log);
    }

    private void checkWelcomePages(Log log) throws MojoExecutionException {
        try {
            log.debug("Checking Welcome Pages configuration");
            var currentPath = mavenProject.getFile().toPath().getParent();

            var jakartaEeUtil = JakartaEeUtil.getInstance();
            jakartaEeUtil.addWelcomePages(currentPath, welcomeFile, log);
        } catch (IOException ex) {
            log.error(ex);
            throw new MojoExecutionException("Error adding Welcome Pages", ex);
        }
    }

    private void checkJakartaFacesServletDeclaration(Log log) throws MojoExecutionException {
        try {
            log.debug("Checking Jakarta Faces Declaration");
            var currentPath = mavenProject.getFile().toPath().getParent();

            var jakartaEeUtil = JakartaEeUtil.getInstance();
            jakartaEeUtil.addJakartaFacesServletDeclaration(currentPath, urlPattern, log);
        } catch (IOException ex) {
            log.error(ex);
            throw new MojoExecutionException("Error adding Jakarta Faces Servlet Declaration", ex);
        }

    }

    private void checkDependency(Log log) throws MojoExecutionException {
        log.debug("checking Jakarta Faces dependency");
        try {
            ProjectBuildingRequest buildingRequest = mavenSession.getProjectBuildingRequest();
            buildingRequest.setResolveDependencies(true);
            var result = projectBuilder.build(mavenProject.getFile(), buildingRequest);
            MavenProject fullProject = result.getProject();

            var jakartaEeUtil = JakartaEeUtil.getInstance();
            if (!jakartaEeUtil.hasJakartaFacesDependency(fullProject, log)) {
                jakartaEeUtil.addJakartaFacesDependency(mavenProject, log, jakartaEeVersion);
            }
            if(!jakartaEeUtil.hasJakartaCdiDependency(fullProject, log))
                jakartaEeUtil.addJakartaCdiDependency(mavenProject, log, jakartaEeVersion);

        } catch (ProjectBuildingException ex) {
            log.error(ex);
            throw new MojoExecutionException("Error resolving dependencies", ex);

        }
    }

}
