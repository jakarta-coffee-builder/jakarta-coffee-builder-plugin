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

/**
 * Mojo for generating server-side OpenAPI files.
 * <p>
 * This Mojo processes an OpenAPI specification file to generate server-side code
 * for a Jakarta EE project. It uses the {@link OpenApiGeneratorHelper} to handle
 * the generation process.
 * </p>
 * <p>
 * Usage:
 * <ul>
 *   <li>Configure the Mojo in the Maven POM file.</li>
 *   <li>Specify the OpenAPI file location using the `openapi-server` parameter.</li>
 * </ul>
 * <p>
 * Example configuration in the POM file:
 * <pre>
 * {@code
 * <plugin>
 *   <groupId>com.apuntesdejava</groupId>
 *   <artifactId>jakarta-coffee-builder-plugin</artifactId>
 *   <version>1.0.0</version>
 *   <executions>
 *     <execution>
 *       <goals>
 *         <goal>create-openapi</goal>
 *       </goals>
 *       <configuration>
 *         <openapi-server>${project.basedir}/openapi.yml</openapi-server>
 *       </configuration>
 *     </execution>
 *   </executions>
 * </plugin>
 * }
 * </pre>
 *
 * @author Diego Silva
 */
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

    /**
     * Default constructor for the CreateOpenApiMojo class.
     * <p>
     * This constructor is used by Maven to create an instance of the Mojo.
     * </p>
     */
    public CreateOpenApiMojo() {
    }

    /**
     * Executes the Mojo to generate server-side code from the OpenAPI specification.
     * <p>
     * This method uses the {@link OpenApiGeneratorHelper} to process the OpenAPI file
     * and generate the necessary server-side code. If the file is not found or an error
     * occurs during processing, an exception is thrown.
     * </p>
     *
     * @throws MojoExecutionException if an error occurs during execution.
     * @throws MojoFailureException   if the OpenAPI file cannot be processed.
     */
    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        var log = getLog();
        Optional.ofNullable(openApiFileServer).ifPresent(openApiFile -> {
            log.info("Creating open api server side with %s".formatted(openApiFile));
            try {
                OpenApiGeneratorHelper.getInstance().processServer(mavenProject, openApiFile);
            } catch (URISyntaxException | IOException e) {
                throw new RuntimeException(new MojoFailureException(e));
            }

        });
    }
}
