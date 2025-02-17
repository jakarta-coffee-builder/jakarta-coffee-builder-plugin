/*
 * Copyright 2025 Diego Silva <diego.silva at apuntesdejava.com>.
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
package com.apuntesdejava.jakartacoffeebuilder.mojo.persistence;

import com.apuntesdejava.jakartacoffeebuilder.helper.JakartaEeHelper;
import com.apuntesdejava.jakartacoffeebuilder.helper.JakartaPersistenceHelper;
import com.apuntesdejava.jakartacoffeebuilder.helper.MavenProjectHelper;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.ProjectBuilder;

import java.io.File;

/**
 * @author Diego Silva <diego.silva at apuntesdejava.com>
 */
@Mojo(
    name = "add-entities"
)
public class AddEntitiesMojo extends AbstractMojo {

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
            var fullProject = MavenProjectHelper.getInstance()
                                                .getFullProject(mavenSession, projectBuilder, mavenProject);
            JakartaEeHelper.getInstance().checkDependencies(fullProject, log);
            log.info("Adding entities from file: " + entitiesFile.getAbsolutePath());
            if (!entitiesFile.exists()) {
                log.error("Entities file not found: " + entitiesFile.getAbsolutePath());
                throw new MojoFailureException("Entities file not found: " + entitiesFile.getAbsolutePath());
            }
            JakartaPersistenceHelper.getInstance().addEntities(mavenProject, log, entitiesFile.toPath());
        } catch (Exception ex) {
            throw new MojoExecutionException("Error adding entities", ex);
        }
    }

}
