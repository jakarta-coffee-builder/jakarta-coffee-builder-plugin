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
package com.apuntesdejava.jakartacoffeebuilder.mojo;

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
 * @author Diego Silva <diego.silva at apuntesdejava.com>
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
    }

    private void checkJakartaFacesServletDeclaration(Log log) throws MojoExecutionException {
        try {
            log.debug("Checking Jakarta Faces Declaration");
            var currentPath = mavenProject.getFile().toPath().getParent();

            var jakartaEeUtil = JakartaEeUtil.getInstance();
            jakartaEeUtil.addJakartaFacesServletDeclaration(currentPath, urlPattern, welcomeFile, log);
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
            var hasJakartaFacesDependencies = jakartaEeUtil.hasJakartaFacesDependency(fullProject, log);
            log.debug("hasJakartaFacesDependencies:%s".formatted(hasJakartaFacesDependencies));
            if (!hasJakartaFacesDependencies) {
                jakartaEeUtil.addJakartaFacesDependency(mavenProject, log, jakartaEeVersion);
            }

        } catch (ProjectBuildingException ex) {
            log.error(ex);
            throw new MojoExecutionException("Error resolving dependencies", ex);

        }
    }

}
