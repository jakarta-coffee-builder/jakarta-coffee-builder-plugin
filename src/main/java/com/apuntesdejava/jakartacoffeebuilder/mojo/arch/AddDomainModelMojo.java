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
import com.apuntesdejava.jakartacoffeebuilder.util.PomUtil;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Mojo(
        name = "add-domain-model"
)
public class AddDomainModelMojo extends AbstractMojo {

    @Parameter(
            required = true,
            property = "entities-file"
    )
    private File entitiesFile;

    @Parameter(
            defaultValue = "${project}",
            readonly = true
    )
    private MavenProject mavenProject;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        try {
            var log = getLog();
            var formsPath = validateFile(entitiesFile);
            var architectureHelper = ArchitectureHelper.getInstance();
            architectureHelper.checkDependency(mavenProject, log);

            var jsonContent = JsonUtil.readJsonValue(formsPath).asJsonObject();

            architectureHelper.createDtos(mavenProject, log, jsonContent);
            architectureHelper.createMappers(mavenProject, log, jsonContent);
            architectureHelper.createServices(mavenProject, log, jsonContent);

            PomUtil.saveMavenProject(mavenProject, log);
        } catch (IOException e) {
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
