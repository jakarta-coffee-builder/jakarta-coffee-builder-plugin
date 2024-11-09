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

import com.apuntesdejava.jakartacoffeebuilder.util.JakartaEeUtil;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.ProjectBuilder;
import org.apache.maven.project.ProjectBuildingRequest;

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
        try {
            ProjectBuildingRequest buildingRequest = mavenSession.getProjectBuildingRequest();
            buildingRequest.setResolveDependencies(true);
            var result = projectBuilder.build(mavenProject.getFile(), buildingRequest);
            MavenProject fullProject = result.getProject();


            log.info("Executing: url-pattern:%s | welcome-file:%s".formatted(urlPattern, welcomeFile));
            log.debug("Project name:%s".formatted(mavenProject.getName()));
            var hasJakartaFacesDependencies = JakartaEeUtil.getInstance().hasJakartaFacesDependency(fullProject, log);
            log.debug("hasJakartaFacesDependencies:%s".formatted(hasJakartaFacesDependencies));
        } catch (Exception ex) {
            log.error(ex);
            throw new MojoExecutionException("Error resolving dependencies", ex);

        }

    }

}
