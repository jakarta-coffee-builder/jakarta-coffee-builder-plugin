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
package com.apuntesdejava.jakartacoffeebuilder.mojo.arch;

import com.apuntesdejava.jakartacoffeebuilder.helper.ArchitectureHelper;
import com.apuntesdejava.jakartacoffeebuilder.util.JsonUtil;
import com.apuntesdejava.jakartacoffeebuilder.util.MavenProjectUtil;
import com.apuntesdejava.jakartacoffeebuilder.util.PomUtil;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
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
 * A Maven Mojo that generates the domain model architecture, including DTOs, Mappers, and Services,
 * based on a provided JSON file defining the entities. This goal is intended to scaffold the
 * application's layers from a single source of truth.
 */
@Mojo(
    name = "add-domain-models"
)
public class AddDomainModelsMojo extends AbstractMojo {

    /**
     * The Maven Project Builder component, used to build a full Maven project from a POM file.
     */
    @Component
    protected ProjectBuilder projectBuilder;
    /**
     * The current Maven session. This is automatically injected by Maven.
     */
    @Parameter(
        defaultValue = "${session}",
        readonly = true,
        required = true
    )
    protected MavenSession mavenSession;
    protected MavenProject fullProject;
    /**
     * The path to the JSON file that contains the entity definitions. This file serves as the input
     * for generating the domain models.
     */
    @Parameter(
        required = true,
        property = "entities-file"
    )
    private File entitiesFile;
    /**
     * The current Maven project instance. This is automatically injected by Maven and provides
     * access to the project's configuration and files.
     */
    @Parameter(
        defaultValue = "${project}",
        readonly = true
    )
    private MavenProject mavenProject;

    /**
     * Default constructor.
     */
    public AddDomainModelsMojo() {

    }

    protected void init() throws ProjectBuildingException {
        this.fullProject = MavenProjectUtil.getFullProject(mavenSession, projectBuilder, mavenProject);

    }

    /**
     * Executes the Mojo's primary logic. This method orchestrates the entire process of
     * reading the entity definitions, generating the corresponding architectural components
     * (DTOs, Mappers, Services), and updating the project configuration.
     *
     * @throws MojoExecutionException if the entities file is not found or a critical error occurs.
     * @throws MojoFailureException   if an I/O error or other recoverable error occurs during the process.
     */
    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        try {
            var log = getLog();
            init();
            var jakartaEeVersion = PomUtil.getJakartaEeCurrentVersion(fullProject, log).orElseThrow();
            var formsPath = validateFile(entitiesFile);
            var architectureHelper = ArchitectureHelper.getInstance();
            architectureHelper.checkDependency(mavenProject, log, jakartaEeVersion);

            var jsonContent = JsonUtil.readJsonValue(formsPath).asJsonObject();

            architectureHelper.createDtos(mavenProject, log, jsonContent);
            architectureHelper.createMappers(mavenProject, log, jsonContent);
            architectureHelper.createModelRepositoryInterfaces(mavenProject, log, jsonContent);
            architectureHelper.createServices(mavenProject, log, jsonContent);

            PomUtil.saveMavenProject(mavenProject, log);
        } catch (IOException | ProjectBuildingException e) {
            throw new MojoFailureException(e.getMessage(), e);
        }
    }

    private Path validateFile(File formsFile) throws MojoExecutionException {
        if (!Files.exists(formsFile.toPath())) {
            throw new MojoExecutionException("File not found:" + formsFile);
        }
        return formsFile.toPath();
    }
}
