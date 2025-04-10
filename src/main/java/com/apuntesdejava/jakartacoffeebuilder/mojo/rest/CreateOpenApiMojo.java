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
package com.apuntesdejava.jakartacoffeebuilder.mojo.rest;

import com.apuntesdejava.jakartacoffeebuilder.helper.OpenApiGeneratorHelper;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Optional;

@Mojo(
    name = "create-openapi"
)
public class CreateOpenApiMojo extends AbstractMojo {

    @Parameter(
        property = "openapi-server",
        required = true,
        defaultValue = "${project.basedir}/openapi.yml"
    )
    private File openApiFileServer;

    @Parameter(defaultValue = "${project}", readonly = true)
    private MavenProject mavenProject;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        var log = getLog();
        Optional.ofNullable(openApiFileServer).ifPresent(openApiFile -> {
            log.info("Creating open api server side with %s".formatted(openApiFile));
            try {
                OpenApiGeneratorHelper.getInstance().processServer(log, mavenProject, openApiFile);
            } catch (URISyntaxException | IOException e) {
                throw new RuntimeException(new MojoFailureException(e));
            }

        });
    }
}
