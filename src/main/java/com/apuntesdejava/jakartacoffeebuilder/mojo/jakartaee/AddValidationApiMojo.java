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

/**
 * A Maven Mojo that adds the Jakarta Validation API dependency to the project's pom.xml.
 * This allows the project to use standard validation annotations like {@code @NotNull}, {@code @Size}, etc.
 */
@Mojo(
    name = "add-validation-api"
)
public class AddValidationApiMojo extends AbstractMojo {
    /**
     * The current Maven project instance. This is automatically injected by Maven.
     */
    @Parameter(defaultValue = "${project}", readonly = true)
    private MavenProject mavenProject;

    /**
     * The current Maven session. This is automatically injected by Maven.
     */
    @Parameter(
        defaultValue = "${session}",
        readonly = true,
        required = true
    )
    private MavenSession mavenSession;


    /**
     * The Maven Project Builder component, used to build a full Maven project from a POM file.
     */
    @Component
    private ProjectBuilder projectBuilder;

    /**
     * Default constructor.
     */
    public AddValidationApiMojo() {
    }

    /**
     * Executes the Mojo's primary logic. This method resolves the full project to determine the
     * correct Jakarta EE version, adds the Jakarta Validation API dependency to the project's POM, and
     * then saves the modified {@code pom.xml} file.
     *
     * @throws MojoExecutionException if a critical error occurs during execution.
     * @throws MojoFailureException   if a recoverable error, such as a project building or I/O issue, occurs.
     */
    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        var log = getLog();
        var jakartaEeHelper = JakartaEeHelper.getInstance();
        try {
            MavenProject fullProject = MavenProjectUtil.getFullProject(mavenSession, projectBuilder, mavenProject);

            var jakartaEeVersion = PomUtil.getJakartaEeCurrentVersion(fullProject, log).orElseThrow();

            jakartaEeHelper.addJakartaValidationApiDependency(mavenProject, log, jakartaEeVersion);

            PomUtil.saveMavenProject(mavenProject, log);
        } catch (ProjectBuildingException | IOException e) {
            throw new MojoFailureException(e);
        }
    }
}
