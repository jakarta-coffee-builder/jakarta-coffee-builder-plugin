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

import com.apuntesdejava.jakartacoffeebuilder.util.CoffeeBuilderUtil;
import com.apuntesdejava.jakartacoffeebuilder.util.MavenProjectUtil;
import com.apuntesdejava.jakartacoffeebuilder.util.PomUtil;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;

import java.io.IOException;
import java.util.Optional;

/**
 * Helper for adding and configuring the Payara Micro Maven plugin in a Maven project.
 *
 * <p>This singleton class provides a convenience method to add the Payara Micro
 * plugin configuration to a project's POM. It reads server definitions from
 * resources, validates the requested Jakarta EE version, builds the plugin
 * configuration as a JSON object and delegates POM modifications to project
 * utilities.</p>
 *
 * <p>Intended to be used in a Maven plugin/runtime context. The
 * {@link #addPlugin(org.apache.maven.project.MavenProject, org.apache.maven.plugin.logging.Log, String, String)}
 * method can throw {@link java.io.IOException} or {@link org.apache.maven.plugin.MojoExecutionException}
 * when I/O or execution errors occur.</p>
 *
 * @see com.apuntesdejava.jakartacoffeebuilder.util.CoffeeBuilderUtil
 * @see com.apuntesdejava.jakartacoffeebuilder.util.MavenProjectUtil
 * @see com.apuntesdejava.jakartacoffeebuilder.util.PomUtil
 */
public class PayaraMicroHelper {

    private PayaraMicroHelper() {
    }

    public static PayaraMicroHelper getInstance() {
        return PayaraMicroHelperHolder.INSTANCE;
    }

    /**
     * Adds the Payara Micro Maven plugin to the project's POM file.
     *
     * @param mavenProject     The Maven project.
     * @param log              The Maven logger.
     * @param profileId        The ID of the profile to which the plugin should be added.
     * @param jakartaEeVersion The Jakarta EE version to be used with Payara Micro.
     * @throws IOException            If an I/O error occurs while reading or writing the POM.
     * @throws MojoExecutionException If an error occurs during plugin execution.
     */
    public void addPlugin(MavenProject mavenProject,
                          Log log,
                          String profileId, String jakartaEeVersion) throws IOException, MojoExecutionException {
        Optional<JsonObject> definitionOpt = CoffeeBuilderUtil.getServerDefinition("payara");
        if (definitionOpt.isEmpty()) return;
        var definition = definitionOpt.get();
        if (!definition.containsKey(jakartaEeVersion))
            throw new MojoExecutionException("Jakarta EE version " + jakartaEeVersion + " doesn't exist");
        var configuration = Json.createObjectBuilder()
            .add("payaraVersion", definition.getString(jakartaEeVersion))
            .add("deployWar", "false")
            .add("commandLineOptions",
                Json.createObjectBuilder()
                    .add("option",
                        Json.createArrayBuilder()
                            .add(
                                Json.createObjectBuilder()
                                    .add("key", "--autoBindHttp")
                            )
                            .add(
                                Json.createObjectBuilder()
                                    .add("key", "--deploy")
                                    .add("value",
                                        "${project.build.directory}/${project.build.finalName}")
                            )
                    )
            )
            .build();
        var build = MavenProjectUtil.getBuild(mavenProject, profileId);
        var plugin = PomUtil.addPlugin(build, log, "fish.payara.maven.plugins",
            "payara-micro-maven-plugin", "2.4",
            configuration);
        log.debug("plugin: %s".formatted(plugin));


    }

    private static class PayaraMicroHelperHolder {

        private static final PayaraMicroHelper INSTANCE = new PayaraMicroHelper();
    }
}
