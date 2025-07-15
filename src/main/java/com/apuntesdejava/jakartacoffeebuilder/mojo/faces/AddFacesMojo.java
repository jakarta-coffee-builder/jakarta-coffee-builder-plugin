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
package com.apuntesdejava.jakartacoffeebuilder.mojo.faces;

import com.apuntesdejava.jakartacoffeebuilder.helper.JakartaEeHelper;
import com.apuntesdejava.jakartacoffeebuilder.util.MavenProjectUtil;
import com.apuntesdejava.jakartacoffeebuilder.util.PomUtil;
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

import java.io.IOException;

/**
 * Mojo implementation for adding necessary Jakarta Faces configurations to a Maven project.
 * This includes adding a URL pattern for Faces requests, configuring the welcome file,
 * and ensuring required dependencies are included in the project.<br/><br/>
 * <p>
 * This Mojo performs the following tasks:<ul><li>
 * Checks and adds the necessary Jakarta Faces and CDI dependencies.</li>
 * <li>Configures and validates the Jakarta Faces servlet declaration in the project descriptor.</li>
 * <li> Updates the web application's welcome file configuration with the specified value.</li>
 * </ul>
 * Goal: add-faces
 * Configuration Parameters:<ul>
 * <li> <code>url-pattern</code>: Specifies the URL pattern to be used for Faces requests (default: "*.faces").</li>
 * <li> <code>welcome-file</code>: Specifies the welcome file name (default: "index.faces").</li>
 * <li> <code>jakartaee-version</code>: Defines the Jakarta EE version to use (default: Jakarta EE 10).</li>
 * <li> <code>mavenProject</code>: Represents the Maven project being processed.</li>
 * <li> <code>mavenSession</code>: Provides the Maven execution session information.</li>
 * <li> <code>projectBuilder</code>: Helper to build Maven project instances.</li>
 * </ul>
 * Execution:<ul><li>
 * Executes the above tasks in sequence, logging relevant information or errors as
 * applicable.</li>
 * <li> Throws MojoExecutionException or MojoFailureException if issues occur during execution.</li>
 * </ul>
 *
 * @author Diego Silva
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

    @Component
    private ProjectBuilder projectBuilder;

    @Parameter(
        defaultValue = "${session}",
        readonly = true,
        required = true
    )
    private MavenSession mavenSession;

    /**
     * Default constructor.<br/>
     * This constructor is used by Maven to create an instance of this Mojo.
     */
    public AddFacesMojo() {
    }

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        var log = getLog();
        try {
            MavenProject fullProject = MavenProjectUtil.getFullProject(mavenSession, projectBuilder, mavenProject);

            var jakartaEeVersion = PomUtil.getJakartaEeCurrentVersion(fullProject, log).orElseThrow();
            log.info("Executing: url-pattern:%s | welcome-file:%s".formatted(urlPattern, welcomeFile));
            log.debug("Project name:%s".formatted(mavenProject.getName()));

            checkDependency(log, jakartaEeVersion);
            checkJakartaFacesServletDeclaration(log);
            checkWelcomePages(log);
        } catch (ProjectBuildingException e) {
            throw new MojoFailureException(e);
        }
    }

    private void checkWelcomePages(Log log) throws MojoExecutionException {
        try {
            log.debug("Checking Welcome Pages configuration");
            var fullProject = MavenProjectUtil.getFullProject(mavenSession, projectBuilder, mavenProject);
            JakartaEeHelper.getInstance().addWelcomePages(fullProject, welcomeFile, log);
        } catch (ProjectBuildingException | IOException ex) {
            log.error(ex);
            throw new MojoExecutionException("Error adding Welcome Pages", ex);
        }
    }

    private void checkJakartaFacesServletDeclaration(Log log) throws MojoExecutionException {
        try {
            log.debug("Checking Jakarta Faces Declaration");
            var fullProject = MavenProjectUtil.getFullProject(mavenSession, projectBuilder, mavenProject);

            JakartaEeHelper.getInstance().addJakartaFacesServletDeclaration(fullProject, log, urlPattern);
        } catch (ProjectBuildingException | IOException ex) {
            log.error(ex);
            throw new MojoExecutionException("Error adding Jakarta Faces Servlet Declaration", ex);
        }

    }

    private void checkDependency(Log log, String jakartaEeVersion) throws MojoExecutionException {
        log.debug("checking Jakarta Faces dependency");
        try {
            var fullProject = MavenProjectUtil.getFullProject(mavenSession, projectBuilder, mavenProject);

            var jakartaEeUtil = JakartaEeHelper.getInstance();
            if (!jakartaEeUtil.hasJakartaFacesDependency(fullProject, log)) {
                jakartaEeUtil.addJakartaFacesDependency(mavenProject, log, jakartaEeVersion);
            }
            if (jakartaEeUtil.hasNotJakartaCdiDependency(fullProject, log))
                jakartaEeUtil.addJakartaCdiDependency(mavenProject, log, jakartaEeVersion);

        } catch (ProjectBuildingException ex) {
            log.error(ex);
            throw new MojoExecutionException("Error resolving dependencies", ex);

        }
    }

}
