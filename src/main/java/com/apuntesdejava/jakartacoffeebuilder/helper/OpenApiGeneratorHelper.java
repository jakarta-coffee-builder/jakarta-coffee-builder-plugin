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
import org.openapitools.codegen.OpenAPIGenerator;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

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

    /**
     * Processes the OpenAPI file to generate server-side code using the OpenAPI Generator Maven plugin.
     * <p>
     * This method uses the OpenAPI Generator to create server-side code for a Maven project.
     * The generated code includes models and APIs, and it is configured to use the Helidon server framework.
     * </p>
     * 
     * @see <a href="https://github.com/OpenAPITools/openapi-generator/blob/master/docs/generators/jaxrs-spec.md" >
     * Documentation for the jaxrs-spec Generator</a>
     *
     * @param mavenProject the Maven project containing the POM file.
     * @param openApiFile  the OpenAPI specification file to be processed.
     * @throws URISyntaxException if there is an error with the URI syntax.
     * @throws IOException        if an I/O error occurs during processing.
     */
    public void processServer(MavenProject mavenProject,
                              File openApiFile) throws URISyntaxException, IOException {
        var apiResourcesPackage = MavenProjectUtil.getApiResourcesPackage(mavenProject);
        OpenAPIGenerator.main(
            new String[]{
                "generate",
                "--input-spec", openApiFile.getAbsolutePath(),
                "--generator-name", "jaxrs-spec",
                "--output", mavenProject.getBuild().getDirectory()+ "/generated-sources/openapi",
                "--model-package", apiResourcesPackage + ".model",
                "--invoker-package", apiResourcesPackage + ".invoker",
                "--api-package", apiResourcesPackage ,
                "--global-property", "modelTests=false,apiTests=false,apiDocs=false,modelDocs=false",
                "--additional-properties",
                "returnResponse=true,useJakartaEe=true,generateBuilders=true,interfaceOnly=true,"
                    + "useSwaggerAnnotations=false,dateLibrary=java8,sourceFolder=,generatePom=false,"
                    + "useMicroProfileOpenAPIAnnotations=true",
            }
        );

    }

    private static class OpenApiGeneratorHelperHolder {

        private static final OpenApiGeneratorHelper INSTANCE = new OpenApiGeneratorHelper();
    }

}
