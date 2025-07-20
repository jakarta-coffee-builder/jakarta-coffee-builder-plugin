package com.apuntesdejava.jakartacoffeebuilder.mojo.jakartaee;

import com.apuntesdejava.jakartacoffeebuilder.helper.JakartaEeHelper;
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

@Mojo(
    name = "add-validation-api"
)
public class AddValidationApiMojo extends AbstractMojo {
    /**
     * The Maven project.
     */
    @Parameter(defaultValue = "${project}", readonly = true)
    private MavenProject mavenProject;

    @Parameter(
        defaultValue = "${session}",
        readonly = true,
        required = true
    )
    private MavenSession mavenSession;


    @Component
    private ProjectBuilder projectBuilder;

    /**
     * Constructor por defecto.
     */
    public AddValidationApiMojo() {
    }

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        var log = getLog();
        var jakartaEeHelper = JakartaEeHelper.getInstance();
        try {
            MavenProject fullProject = MavenProjectUtil.getFullProject(mavenSession, projectBuilder, mavenProject);

            var jakartaEeVersion = PomUtil.getJakartaEeCurrentVersion(fullProject, log).orElseThrow();

            jakartaEeHelper.addJakartaValidationApiDependency(mavenProject, log, jakartaEeVersion);
        } catch (ProjectBuildingException | IOException e) {
            throw new MojoFailureException(e);
        }
    }
}
