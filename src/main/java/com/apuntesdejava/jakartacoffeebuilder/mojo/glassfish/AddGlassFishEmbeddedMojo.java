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
package com.apuntesdejava.jakartacoffeebuilder.mojo.glassfish;

import com.apuntesdejava.jakartacoffeebuilder.helper.GlassFishHelper;
import com.apuntesdejava.jakartacoffeebuilder.util.PomUtil;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

/**
 * Adds a GlassFish Embedded profile to the Maven project.
 * This mojo is used to configure the project to run with an embedded GlassFish server.
 * It adds a new profile to the `pom.xml` file with the specified GlassFish Embedded configuration.
 *
 * @author Diego Silva diego.silva at apuntesdejava.com
 */
@Mojo(
    name = "add-glassfish-embedded"
)
public class AddGlassFishEmbeddedMojo extends AbstractMojo {

    /**
     * The ID of the Maven profile to be added.
     */

    @Parameter(
        property = "profile",
        required = true,
        defaultValue = "glassfish"
    )
    private String profileId;

    @Parameter(
        property = "port",
        required = true,
        defaultValue = "8080"
    )
    private int port;

    @Parameter(
        property = "contextRoot",
        required = true,
        defaultValue = "${project.build.finalName}"
    )
    private String contextRoot;

    /**
     * The Maven project.
     */
    @Parameter(defaultValue = "${project}", readonly = true)
    private MavenProject mavenProject;

    /**
     * Constructor por defecto.
     */
    public AddGlassFishEmbeddedMojo() {
    }

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        var log = getLog();
        GlassFishHelper.getInstance()
                       .addPlugin(mavenProject, log, profileId, port, contextRoot);
        PomUtil.saveMavenProject(mavenProject, log);

    }
}
