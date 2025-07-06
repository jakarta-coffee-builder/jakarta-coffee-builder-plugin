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
package com.apuntesdejava.jakartacoffeebuilder.helper;

import com.apuntesdejava.jakartacoffeebuilder.util.MavenProjectUtil;
import com.apuntesdejava.jakartacoffeebuilder.util.PomUtil;
import jakarta.json.Json;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;

/**
 * Helper class for managing GlassFish-related operations, specifically for adding the embedded GlassFish Maven plugin to a project.
 * This class follows the Singleton design pattern to ensure only one instance exists.
 */
public class GlassFishHelper {
    private GlassFishHelper() {

    }

    private static GlassFishHelper INSTANCE;

    /**
     * Returns the singleton instance of GlassFishHelper.
     *
     * @return The singleton instance of GlassFishHelper.
     */
    public static synchronized GlassFishHelper getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new GlassFishHelper();
        }
        return INSTANCE;
    }


    /**
     * Adds the embedded GlassFish Maven plugin to the project's POM.
     *
     * @param mavenProject The Maven project to which the plugin will be added.
     * @param log          The Maven log for logging messages.
     * @param profileId    The ID of the Maven profile under which the plugin will be added.
     * @param port         The port for the embedded GlassFish server.
     * @param contextRoot  The context root for the deployed application.
     * @throws MojoExecutionException If an error occurs during the plugin addition.
     */
    public void addPlugin(MavenProject mavenProject,
                          Log log,
                          String profileId,
                          int port,
                          String contextRoot) throws MojoExecutionException {
        var configuration = Json.createObjectBuilder()
                                .add("app", "${project.build.directory}/${project.build.finalName}.war")
                                .add("port", String.valueOf(port))
                                .add("contextRoot", contextRoot)
                                .add("autoDelete", "true")
                                .build();
        var build = MavenProjectUtil.getBuild(mavenProject, profileId);
        var plugin = PomUtil.addPlugin(build, log, "org.glassfish.embedded",
            "embedded-glassfish-maven-plugin", "7.0",
            configuration);
        log.debug("plugin: %s".formatted(plugin));

        PomUtil.saveMavenProject(mavenProject, log);

    }
}
