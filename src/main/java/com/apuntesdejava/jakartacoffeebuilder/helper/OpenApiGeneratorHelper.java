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
package com.apuntesdejava.jakartacoffeebuilder.helper;

import com.apuntesdejava.jakartacoffeebuilder.util.PathsUtil;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.openapitools.codegen.OpenAPIGenerator;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @author Diego Silva <diego.silva at apuntesdejava.com>
 */
public class OpenApiGeneratorHelper {

    public static OpenApiGeneratorHelper getInstance() {
        return OpenApiGeneratorHelperHolder.INSTANCE;
    }

    private OpenApiGeneratorHelper() {
    }

    private Path getConfigPath(MavenProject mavenProject) throws URISyntaxException, IOException {
        var apiResourcesPackage = MavenProjectHelper.getApiResourcesPackage(mavenProject);
        var jsonFileContents = PathsUtil.getContentFromResource("openapi-generator/server-config.json")
                                        .map(line -> line.replace("${packageResource}", apiResourcesPackage))
                                        .toList();

        var configTemp = Files.createTempFile("config-generator-coffee-builder", ".json");
        Files.write(configTemp, jsonFileContents);
        return configTemp;
    }

    /**
     * Processes the OpenAPI file to generate code using the OpenAPI Generator Maven plugin.
     *
     * @param log          the Maven plugin logger to log messages
     * @param mavenProject the Maven project containing the POM file
     * @param openApiFile  the OpenAPI specification file to be processed
     */
    public void processServer(Log log,
                              MavenProject mavenProject,
                              File openApiFile) throws URISyntaxException, IOException {
        var configTemp = getConfigPath(mavenProject);
        OpenAPIGenerator.main(
            new String[]{
                "generate",
                "--input-spec", openApiFile.getAbsolutePath(),
                "--generator-name", "java-helidon-server",
                "--output",
                mavenProject.getBasedir().getAbsolutePath() + "/target/generated-sources/openapi",
                "--config", configTemp.toAbsolutePath().toString()
            }
        );

    }

    private static class OpenApiGeneratorHelperHolder {

        private static final OpenApiGeneratorHelper INSTANCE = new OpenApiGeneratorHelper();
    }

}
