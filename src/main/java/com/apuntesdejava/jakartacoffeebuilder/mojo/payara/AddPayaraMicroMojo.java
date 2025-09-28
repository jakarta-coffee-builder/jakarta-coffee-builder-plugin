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
package com.apuntesdejava.jakartacoffeebuilder.mojo.payara;

import com.apuntesdejava.jakartacoffeebuilder.helper.PayaraMicroHelper;
import com.apuntesdejava.jakartacoffeebuilder.util.MavenProjectUtil;
import com.apuntesdejava.jakartacoffeebuilder.util.PomUtil;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.ProjectBuilder;
import org.apache.maven.project.ProjectBuildingException;

import java.io.IOException;

/**
 * Mojo that adds the Payara Micro plugin and the corresponding Maven profile to the project.
 *
 * <p>Resolves the full {@code MavenProject} using the provided {@code ProjectBuilder} and
 * {@code MavenSession}, obtains the current Jakarta EE version via {@link PomUtil}, and delegates
 * the addition of the plugin/profile to {@link PayaraMicroHelper}.</p>
 *
 * <p>Extends {@link org.apache.maven.plugin.AbstractMojo} and is bound to the Maven goal
 * {@code add-payaramicro}.</p>
 *
 * @author Diego Silva
 * @since 1.0.0
 */
@Mojo(
    name = "add-payaramicro"
)

public class AddPayaraMicroMojo extends AbstractMojo {
    @Parameter(
        property = "profile",
        required = true,
        defaultValue = "payaramicro"
    )
    private String profileId;

    /**
     * The Maven project.
     */
    @Parameter(defaultValue = "${project}", readonly = true)
    private MavenProject mavenProject;

    @Component
    private ProjectBuilder projectBuilder;

    @Parameter(
        defaultValue = "${session}",
        readonly = true,
        required = true
    )
    private MavenSession mavenSession;

    /**
     * Default no-argument constructor.
     *
     * <p>Provided for the Maven plugin infrastructure. No initialization required.</p>
     */
    public AddPayaraMicroMojo() {
    }


    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        try {
            var log = getLog();

            MavenProject fullProject = MavenProjectUtil.getFullProject(mavenSession, projectBuilder, mavenProject);

            var jakartaEeVersion = PomUtil.getJakartaEeCurrentVersion(fullProject, log).orElseThrow();

            PayaraMicroHelper.getInstance().addPlugin(mavenProject, getLog(), profileId, jakartaEeVersion);

            PomUtil.saveMavenProject(mavenProject, log);
        } catch (ProjectBuildingException | IOException e) {
            getLog().error(e.getMessage(), e);
        }
    }

}
