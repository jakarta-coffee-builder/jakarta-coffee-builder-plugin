package com.apuntesdejava.jakartacoffeebuilder.mojo.jakartaee;

import com.apuntesdejava.jakartacoffeebuilder.helper.JakartaEeHelper;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.IOException;

import static com.apuntesdejava.jakartacoffeebuilder.util.Constants.JAKARTAEE_VERSION_11;

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
        property = "jakartaee-version",
        required = true,
        defaultValue = JAKARTAEE_VERSION_11

    )
    private String jakartaEeVersion;

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
            jakartaEeHelper.addJakartaValidationApiDependency(mavenProject, log, jakartaEeVersion);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }
}
