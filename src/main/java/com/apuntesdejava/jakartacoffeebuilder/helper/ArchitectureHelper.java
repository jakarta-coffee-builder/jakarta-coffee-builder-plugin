/*
 * Copyright 2025 dsilva.
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
import com.apuntesdejava.jakartacoffeebuilder.util.PomUtil;
import jakarta.json.Json;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;

import java.io.IOException;

import static com.apuntesdejava.jakartacoffeebuilder.util.Constants.ARTIFACT_ID;
import static com.apuntesdejava.jakartacoffeebuilder.util.Constants.GROUP_ID;
import static com.apuntesdejava.jakartacoffeebuilder.util.Constants.MAPSTRUCT;
import static com.apuntesdejava.jakartacoffeebuilder.util.Constants.MAVEN_COMPILER_PLUGIN;
import static com.apuntesdejava.jakartacoffeebuilder.util.Constants.ORG_APACHE_MAVEN_PLUGINS;
import static com.apuntesdejava.jakartacoffeebuilder.util.Constants.ORG_MAPSTRUCT;

public class ArchitectureHelper {

    private ArchitectureHelper() {
    }

    public static ArchitectureHelper getInstance() {
        return ArchitectureHelperHolder.INSTANCE;
    }

    public void checkDependency(MavenProject mavenProject, Log log) throws MojoExecutionException, IOException {
        var jakartaEeUtil = JakartaEeHelper.getInstance();
        log.debug("Checking org.mapstruct depending");
        if (!PomUtil.existsDependency(mavenProject, log, ORG_MAPSTRUCT, MAPSTRUCT)) {
            var version = PomUtil.findLatestDependencyVersion(ORG_MAPSTRUCT, MAPSTRUCT).orElseThrow();
            PomUtil.setProperty(mavenProject, log, "org.mapstruct.version", version);
            PomUtil.addDependency(mavenProject, log, ORG_MAPSTRUCT, MAPSTRUCT,
                "${org.mapstruct.version}");

            CoffeeBuilderUtil.getDependencyConfiguration(MAVEN_COMPILER_PLUGIN)
                .ifPresent(
                    mavenCompilerPlugin -> PomUtil.addPlugin(mavenProject, log,
                        ORG_APACHE_MAVEN_PLUGINS,
                        MAVEN_COMPILER_PLUGIN,
                        mavenCompilerPlugin.getString("version"),
                        Json.createObjectBuilder()
                            .add("annotationProcessorPaths",
                                Json.createObjectBuilder()
                                    .add("path",
                                        Json.createArrayBuilder()
                                            .add(
                                                Json.createObjectBuilder()
                                                    .add(GROUP_ID, ORG_MAPSTRUCT)
                                                    .add(ARTIFACT_ID, "mapstruct-processor")
                                                    .add("version", "${org.mapstruct.version}")
                                            )
                                    )
                            )
                            .build()));
        }
    }

    private static class ArchitectureHelperHolder {

        private static final ArchitectureHelper INSTANCE = new ArchitectureHelper();
    }
}
