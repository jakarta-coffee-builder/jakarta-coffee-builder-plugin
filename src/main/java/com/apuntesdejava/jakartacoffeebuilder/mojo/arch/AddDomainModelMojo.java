package com.apuntesdejava.jakartacoffeebuilder.mojo.arch;

import com.apuntesdejava.jakartacoffeebuilder.helper.ArchitectureHelper;
import com.apuntesdejava.jakartacoffeebuilder.util.MavenProjectUtil;
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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;


@Mojo(
    name = "add-domain-model"
)
public class AddDomainModelMojo extends AbstractMojo {
    @Parameter(
        required = true,
        property = "entities-file"
    )
    private File entitiesFile;


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

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        try {
            var log = getLog();
            var formsPath = validateFile(entitiesFile);
            MavenProject fullProject = MavenProjectUtil.getFullProject(mavenSession, projectBuilder, mavenProject);
            ArchitectureHelper.getInstance().checkDependency(fullProject, log);
        } catch (ProjectBuildingException | IOException e) {
            throw new MojoFailureException(e.getMessage(), e);
        }
    }

    private Path validateFile(File formsFile) throws MojoExecutionException {
        if (!Files.exists(formsFile.toPath()))
            throw new MojoExecutionException("File not found:" + formsFile);
        return formsFile.toPath();
    }
}
