/*
 * Copyright 2025 Diego Silva diego.silva at apuntesdejava.com.
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
package com.apuntesdejava.jakartacoffeebuilder.helper;

import com.apuntesdejava.jakartacoffeebuilder.util.CoffeeBuilderUtil;
import com.apuntesdejava.jakartacoffeebuilder.util.Constants;
import com.apuntesdejava.jakartacoffeebuilder.util.MavenProjectUtil;
import com.apuntesdejava.jakartacoffeebuilder.util.PomUtil;
import jakarta.json.Json;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

import static com.apuntesdejava.jakartacoffeebuilder.util.Constants.CONFIGURATION;
import static com.apuntesdejava.jakartacoffeebuilder.util.Constants.GOAL;
import static com.apuntesdejava.jakartacoffeebuilder.util.Constants.GOALS;

/**
 * Helper class for processing OpenAPI specifications and generating server-side code.
 * <p>
 * This class provides utility methods to integrate the OpenAPI Generator with Maven projects, enabling the generation
 * of server-side code based on OpenAPI specifications.
 * </p>
 * <p>
 * The generated code is configured to use the Helidon server framework with Jakarta EE and JSON-B serialization.
 * </p>
 * <p>
 * <p>
 * Usage:
 * <ul>
 * <li>Obtain an instance of this helper using {@link #getInstance()}.</li>
 * <li>Call {@link #processServer(MavenProject, File, Log)} to process an OpenAPI file.</li>
 * </ul>
 *
 * @author Diego Silva diego.silva at apuntesdejava.com
 */
public class OpenApiGeneratorHelper {

    public static final String OPENAPI_GENERATOR_IGNORE_FILENAME = ".openapi-generator-ignore";

    /**
     * Retrieves the singleton instance of the {@code OpenApiGeneratorHelper}.
     *
     * @return the singleton instance of {@code OpenApiGeneratorHelper}.
     */
    public static OpenApiGeneratorHelper getInstance() {
        return OpenApiGeneratorHelperHolder.INSTANCE;
    }

    private OpenApiGeneratorHelper() {
    }

    private void createIgnoreFilePath(File baseDir) throws IOException {
        var ignoreFilePath = baseDir.toPath().resolve(OPENAPI_GENERATOR_IGNORE_FILENAME);
        var ignoreList = List.of(
            "**/RestApplication.java",
            "**/RestResourceRoot.java"
        );
        Files.write(ignoreFilePath, ignoreList);
    }

    /**
     * Processes the OpenAPI file to generate server-side code using the OpenAPI Generator Maven plugin.
     * <p>
     * This method uses the OpenAPI Generator to create server-side code for a Maven project. The generated code
     * includes models and APIs, and it is configured to use the Helidon server framework.
     * </p>
     *
     * @param mavenProject the Maven project containing the POM file.
     * @param openApiFile  the OpenAPI specification file to be processed.
     * @param log          the logger to use for logging messages.
     * @throws URISyntaxException                             if there is an error with the URI syntax.
     * @throws IOException                                    if an I/O error occurs during processing.
     * @throws org.apache.maven.plugin.MojoExecutionException if an error occurs during the plugin execution.
     * @see <a href="https://github.com/OpenAPITools/openapi-generator/blob/master/docs/generators/jaxrs-spec.md" >
     * Documentation for the jaxrs-spec Generator</a>
     */
    public void processServer(MavenProject mavenProject,
                              File openApiFile,
                              Log log) throws URISyntaxException, IOException, MojoExecutionException {

        if (!Files.exists(openApiFile.toPath())) {
            throw new FileNotFoundException("File not found:" + openApiFile);
        }
        Path openApiPath = copyToProjectPath(mavenProject.getBasedir(), openApiFile);
        var apiResourcesPackage = MavenProjectUtil.getApiResourcesPackage(mavenProject);
        createIgnoreFilePath(mavenProject.getBasedir());

        CoffeeBuilderUtil
            .getOpenApiGeneratorConfiguration()
            .map(config -> {
                var configOptionsBuilder = Json.createObjectBuilder(config.getJsonObject("configOptions"))
                    .add("modelPackage", apiResourcesPackage + ".model")
                    .add("apiPackage", apiResourcesPackage);
                return Json.createObjectBuilder()
                    .add("generatorName", config.getString("generatorName"))
                    .add("inputSpec", openApiPath.getFileName().toString())
                    .add("ignoreFileOverride", "${project.basedir}/" + OPENAPI_GENERATOR_IGNORE_FILENAME)
                    .add("configOptions", configOptionsBuilder)
                    .build();
            }).ifPresent(configuration -> {
                try {
                    var executions = Json
                        .createArrayBuilder()
                        .add(Json.createObjectBuilder()
                            .add(GOALS,
                                Json.createArrayBuilder()
                                    .add(
                                        Json.createObjectBuilder()
                                            .add(GOAL, "generate")
                                    )
                            ).add(CONFIGURATION, configuration))
                        .build();
                    PomUtil.findLatestPluginVersion(Constants.ORG_OPENAPITOOLS, Constants.OPENAPI_GENERATOR_MAVEN_PLUGIN)
                        .ifPresent(version
                            -> PomUtil.addPlugin(mavenProject.getOriginalModel().getBuild(), log,
                            Constants.ORG_OPENAPITOOLS,
                            Constants.OPENAPI_GENERATOR_MAVEN_PLUGIN, version, null, executions)
                        );
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });

    }

    private Path copyToProjectPath(File basedir, File openApiFile) throws IOException {
        Path path = Paths.get(basedir.getAbsolutePath(), openApiFile.getName());
        Files.copy(openApiFile.toPath(), path, StandardCopyOption.REPLACE_EXISTING);
        return path;
    }

    private static class OpenApiGeneratorHelperHolder {

        private static final OpenApiGeneratorHelper INSTANCE = new OpenApiGeneratorHelper();
    }

}
