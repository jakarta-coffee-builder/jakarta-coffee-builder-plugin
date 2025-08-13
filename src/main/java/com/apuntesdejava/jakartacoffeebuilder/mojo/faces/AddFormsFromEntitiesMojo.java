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
import com.apuntesdejava.jakartacoffeebuilder.helper.PrimeFacesHelper;
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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * This Mojo adds forms based on entities defined in a specified file.
 * It also ensures that PrimeFaces is a dependency in the project.
 *
 * @author Diego Silva diego.silva at apuntesdejava.com
 */

@Mojo(
    name = "add-forms-from-entities"
)
public class AddFormsFromEntitiesMojo extends AbstractMojo {

    @Parameter(
        required = true,
        property = "forms-file"
    )
    private File formsFile;

    @Parameter(
        required = true,
        property = "entities-file"
    )
    private File entitiesFile;


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
        try {
            var log = getLog();
            var formsPath = validateFile(formsFile);
            MavenProject fullProject = MavenProjectUtil.getFullProject(mavenSession, projectBuilder, mavenProject);
            checkDependency(log, fullProject);
            PrimeFacesHelper.getInstance().addFormsFromEntities(fullProject, log, formsPath, entitiesFile.toPath());

            PomUtil.saveMavenProject(fullProject, log);
        } catch (ProjectBuildingException | IOException e) {
            throw new MojoFailureException(e.getMessage(), e);
        }
    }

    private void checkDependency(Log log, MavenProject fullProject)   {
        log.debug("Checking PrimeFaces dependency");
        var jakartaEeUtil = JakartaEeHelper.getInstance();
        if (jakartaEeUtil.hasNotPrimeFacesDependency(fullProject, log))
            jakartaEeUtil.addPrimeFacesDependency(mavenProject, log);

    }

    private Path validateFile(File formsFile) throws MojoExecutionException {
        if (!Files.exists(formsFile.toPath()))
            throw new MojoExecutionException("File not found:" + formsFile);
        return formsFile.toPath();
    }
}
