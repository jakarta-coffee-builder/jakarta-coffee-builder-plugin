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
package com.apuntesdejava.jakartacoffeebuilder.mojo.faces;

import com.apuntesdejava.jakartacoffeebuilder.helper.JakartaFacesHelper;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.IOException;
import java.util.List;

/**
 * Mojo for adding a new template to a Jakarta Faces project.
 * <p>
 * This Mojo allows the creation of a new face template in the project. It uses the
 * {@link JakartaFacesHelper} to handle the template creation and supports optional
 * inserts for customization.
 * </p>
 * <p>
 * Usage:
 * <ul>
 *   <li>Configure the Mojo in the Maven POM file.</li>
 *   <li>Specify the template name and optional inserts as parameters.</li>
 * </ul>
 * <p>
 * Example configuration in the POM file:
 * <pre>
 * {@code
 * <plugin>
 *   <groupId>com.apuntesdejava</groupId>
 *   <artifactId>jakarta-coffee-builder-plugin</artifactId>
 *   <version>1.0.0</version>
 *   <executions>
 *     <execution>
 *       <goals>
 *         <goal>add-face-template</goal>
 *       </goals>
 *       <configuration>
 *         <name>myTemplate</name>
 *         <inserts>
 *           <insert>header</insert>
 *           <insert>footer</insert>
 *         </inserts>
 *       </configuration>
 *     </execution>
 *   </executions>
 * </plugin>
 * }
 * </pre>
 * <p>
 * This Mojo is part of the Jakarta Coffee Builder Plugin and simplifies the creation
 * of face templates in Jakarta Faces projects.
 * </p>
 *
 * @author Diego Silva diego.silva at apuntesdejava.com
 */
@Mojo(
    name = "add-face-template"
)
public class AddFaceTemplateMojo extends AbstractMojo {

    @Parameter(
        required = true,
        property = "name"
    )
    private String templateName;

    @Parameter(
        property = "inserts"
    )
    private List<String> inserts;

    @Parameter(defaultValue = "${project}", readonly = true)
    private MavenProject mavenProject;

    /**
     * Default constructor.
     * <p>
     * This constructor is used by Maven to create an instance of this Mojo.
     * </p>
     */
    public AddFaceTemplateMojo() {
    }

    /**
     * Executes the Mojo to add a new face template to the project.
     * <p>
     * This method uses the {@link JakartaFacesHelper} to create the template and handles
     * any exceptions that may occur during the process.
     * </p>
     *
     * @throws MojoExecutionException if an error occurs during execution.
     * @throws MojoFailureException   if the template cannot be created.
     */
    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        var log = getLog();
        try {
            log.info("Adding Template face page " + templateName);
            JakartaFacesHelper.getInstance().addFaceTemplate(mavenProject, log, templateName, inserts);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new MojoExecutionException(e);
        }
    }

}
