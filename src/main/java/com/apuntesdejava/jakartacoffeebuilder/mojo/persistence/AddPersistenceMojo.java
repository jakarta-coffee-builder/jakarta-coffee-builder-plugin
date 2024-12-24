/*
 * Copyright 2024 Diego Silva <diego.silva at apuntesdejava.com>.
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
package com.apuntesdejava.jakartacoffeebuilder.mojo.persistence;

import com.apuntesdejava.jakartacoffeebuilder.helper.JakartaEeHelper;
import com.apuntesdejava.jakartacoffeebuilder.helper.MavenProjectHelper;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.ProjectBuilder;
import org.apache.maven.project.ProjectBuildingException;

import static com.apuntesdejava.jakartacoffeebuilder.util.Constants.JAKARTAEE_VERSION_10;

/**
 * @author Diego Silva <diego.silva at apuntesdejava.com>
 */
@Mojo(
    name = "add-persistence"
)
public class AddPersistenceMojo extends AbstractMojo {
    @Parameter(defaultValue = "${project}", readonly = true)
    private MavenProject mavenProject;

    @Parameter(
        property = "jakarta-ee-version",
        defaultValue = JAKARTAEE_VERSION_10
    )
    private String jakartaEeVersion;
    @Component
    private ProjectBuilder projectBuilder;

    @Parameter(
        defaultValue = "${session}",
        readonly = true,
        required = true
    )
    private MavenSession mavenSession;

    @Parameter(
        defaultValue = "defaultPU",
        property = "persistence-unit-name"
    )
    private String persistenceUnitName;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        var log = getLog();
        log.debug("Project name:%s".formatted(mavenProject.getName()));
        checkDependency(log);
        createPersistenceXml(log);

    }

    private void checkDependency(Log log) throws MojoExecutionException {
        log.debug("checking Jakarta Persistence dependency");
        try {
            var fullProject = MavenProjectHelper.getInstance()
                                                .getFullProject(mavenSession, projectBuilder, mavenProject);
            var jakartaEeUtil = JakartaEeHelper.getInstance();
            if (jakartaEeUtil.hasNotJakartaCdiDependency(fullProject, log))
                jakartaEeUtil.addJakartaCdiDependency(mavenProject, log, jakartaEeVersion);
            if (!jakartaEeUtil.hasJakartaPersistenceDependency(fullProject, log))
                jakartaEeUtil.addJakartaPersistenceDependency(mavenProject, log, jakartaEeVersion);

        } catch (ProjectBuildingException ex) {
            log.error(ex);
            throw new MojoExecutionException("Error resolving dependencies", ex);

        }
    }

    private void createPersistenceXml(Log log) {
        var currentPath = mavenProject.getFile().toPath().getParent();
        JakartaEeHelper.getInstance().createPersistenceXml(currentPath, log, jakartaEeVersion, persistenceUnitName);

    }

}
