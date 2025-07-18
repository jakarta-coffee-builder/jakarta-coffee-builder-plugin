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

import com.apuntesdejava.jakartacoffeebuilder.util.MavenProjectUtil;
import org.apache.maven.project.MavenProject;
import org.openapitools.codegen.DefaultGenerator;
import org.openapitools.codegen.config.CodegenConfigurator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

/**
 * Helper class for processing OpenAPI specifications and generating server-side code.
 * <p>
 * This class provides utility methods to integrate the OpenAPI Generator with Maven projects,
 * enabling the generation of server-side code based on OpenAPI specifications.
 * </p>
 * <p>
 * The generated code is configured to use the Helidon server framework with Jakarta EE and JSON-B serialization.
 * </p>
 *
 * <p>
 * Usage:
 * <ul>
 *   <li>Obtain an instance of this helper using {@link #getInstance()}.</li>
 *   <li>Call {@link #processServer(MavenProject, File)} to process an OpenAPI file.</li>
 * </ul>
 *
 * @author Diego Silva diego.silva at apuntesdejava.com
 */
public class OpenApiGeneratorHelper {

    private final Path ignoreFilePath;

    /**
     * Retrieves the singleton instance of the {@code OpenApiGeneratorHelper}.
     *
     * @return the singleton instance of {@code OpenApiGeneratorHelper}.
     */
    public static OpenApiGeneratorHelper getInstance() {
        return OpenApiGeneratorHelperHolder.INSTANCE;
    }

    private OpenApiGeneratorHelper() {
        try {
            this.ignoreFilePath = Files.createTempFile("oag", "ignore");
            List<String> ignoreList = List.of(
                "**/invoker/RestApplication.java",
                "**/invoker/RestResourceRoot.java"
            );
            Files.write(ignoreFilePath, ignoreList);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * Processes the OpenAPI file to generate server-side code using the OpenAPI Generator Maven plugin.
     * <p>
     * This method uses the OpenAPI Generator to create server-side code for a Maven project.
     * The generated code includes models and APIs, and it is configured to use the Helidon server framework.
     * </p>
     *
     * @param mavenProject the Maven project containing the POM file.
     * @param openApiFile  the OpenAPI specification file to be processed.
     * @throws URISyntaxException if there is an error with the URI syntax.
     * @throws IOException        if an I/O error occurs during processing.
     * @see <a href="https://github.com/OpenAPITools/openapi-generator/blob/master/docs/generators/jaxrs-spec.md" >
     * Documentation for the jaxrs-spec Generator</a>
     */
    public void processServer(MavenProject mavenProject,
                              File openApiFile) throws URISyntaxException, IOException {

        if (!Files.exists(openApiFile.toPath()))
            throw new FileNotFoundException("File not found:" + openApiFile);
        var apiResourcesPackage = MavenProjectUtil.getApiResourcesPackage(mavenProject);
        CodegenConfigurator configurator = getCodegenConfigurator(mavenProject, openApiFile, apiResourcesPackage);

        new DefaultGenerator()
            .opts(configurator.toClientOptInput())
            .generate();

    }

    private CodegenConfigurator getCodegenConfigurator(MavenProject mavenProject,
                                                       File openApiFile,
                                                       String apiResourcesPackage) {
        CodegenConfigurator configurator = new CodegenConfigurator();
        configurator.setGeneratorName("jaxrs-spec");
        configurator.setInputSpec(openApiFile.getAbsolutePath());
        configurator.setOutputDir(mavenProject.getBuild().getDirectory() + "/generated-sources/openapi");
        configurator.setModelPackage(apiResourcesPackage + ".model");
        configurator.setInvokerPackage(apiResourcesPackage + ".invoker");
        configurator.setApiPackage(apiResourcesPackage);
        configurator.setIgnoreFileOverride(ignoreFilePath.toString());
        configurator.setGlobalProperties(Map.of(
            "modelTests", "false",
            "apiTests", "false",
            "apiDocs", "false",
            "modelDocs", "false",
            "verbose", "true"
        ));
        configurator.setAdditionalProperties(Map.ofEntries(
            Map.entry("returnResponse", "true"),
            Map.entry("useJakartaEe", "true"),
            Map.entry("generateBuilders", "true"),
            Map.entry("interfaceOnly", "true"),
            Map.entry("useSwaggerAnnotations", "false"),
            Map.entry("dateLibrary", "java8"),
            Map.entry("sourceFolder", ""),
            Map.entry("generatePom", "false"),
            Map.entry("useMicroProfileOpenAPIAnnotations", "true")

        ));
        return configurator;
    }

    private static class OpenApiGeneratorHelperHolder {

        private static final OpenApiGeneratorHelper INSTANCE = new OpenApiGeneratorHelper();
    }

}
