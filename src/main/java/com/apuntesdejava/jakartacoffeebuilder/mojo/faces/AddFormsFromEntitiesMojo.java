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
 * A Maven Mojo that generates Jakarta Server Faces (JSF) views (XHTML files) and associated
 * backing beans from entity definitions. It uses a JSON file to define the structure of the forms
 * and another JSON file for the entity definitions. This goal automates the creation of CRUD
 * user interfaces, ensuring that the necessary PrimeFaces dependency is included in the project.
 *
 * @author Diego Silva diego.silva at apuntesdejava.com
 */
@Mojo(
    name = "add-forms-from-entities"
)
public class AddFormsFromEntitiesMojo extends AbstractMojo {

    /**
     * The path to the JSON file that defines the structure and layout of the forms to be generated.
     */
    @Parameter(
        required = true,
        property = "forms-file"
    )
    private File formsFile;

    /**
     * The path to the JSON file that contains the entity definitions. These entities are used as the
     * data model for the generated forms.
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
    @Parameter(defaultValue = "${project}", readonly = true)
    private MavenProject mavenProject;

    /**
     * The Maven Project Builder component, used to build a full Maven project from a POM file,
     * including all dependencies and parent POM data.
     */
    @Component
    private ProjectBuilder projectBuilder;

    /**
     * The current Maven session. This is automatically injected by Maven and provides access to the
     * current execution environment.
     */
    @Parameter(
        defaultValue = "${session}",
        readonly = true,
        required = true
    )
    private MavenSession mavenSession;


    /**
     * Constructor público sin argumentos.
     * <p>
     * Maven y el sistema de plugins requieren un constructor público sin argumentos
     * para instanciar el Mojo. No realiza ninguna inicialización explícita aquí;
     * los campos anotados son inyectados por Maven en tiempo de ejecución.
     */
    public AddFormsFromEntitiesMojo() {

    }

    /**
     * Executes the Mojo's primary logic. This method orchestrates the process of:
     * <ol>
     *     <li>Validating the existence of the input files.</li>
     *     <li>Resolving the full Maven project to access all dependencies.</li>
     *     <li>Checking for and adding the PrimeFaces dependency if it's missing.</li>
     *     <li>Invoking the helper to generate the JSF forms and backing beans.</li>
     *     <li>Saving any modifications to the project's POM file.</li>
     * </ol>
     *
     * @throws MojoExecutionException if a required file is not found or a critical error occurs.
     * @throws MojoFailureException   if an I/O error or a project building error occurs during execution.
     */
    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        try {
            var log = getLog();
            var formsPath = validateFile(formsFile);
            validateFile(entitiesFile); // Also validate the entities file
            MavenProject fullProject = MavenProjectUtil.getFullProject(mavenSession, projectBuilder, mavenProject);
            checkDependency(log, fullProject);
            PrimeFacesHelper.getInstance().addFormsFromEntities(fullProject, log, formsPath, entitiesFile.toPath());

            PomUtil.saveMavenProject(mavenProject, log);
        } catch (ProjectBuildingException | IOException e) {
            throw new MojoFailureException(e.getMessage(), e);
        }
    }

    private void checkDependency(Log log, MavenProject fullProject) {
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
