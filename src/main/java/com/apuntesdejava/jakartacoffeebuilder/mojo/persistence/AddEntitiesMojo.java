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
package com.apuntesdejava.jakartacoffeebuilder.mojo.persistence;

import com.apuntesdejava.jakartacoffeebuilder.helper.JakartaEeHelper;
import com.apuntesdejava.jakartacoffeebuilder.helper.JakartaPersistenceHelper;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * Mojo for adding JPA entities to a Jakarta EE project.
 * <p>
 * This Mojo reads a file containing JPA entity definitions and integrates them into the project.
 * It validates the existence of the file and uses the {@link JakartaPersistenceHelper} to process the entities.
 * </p>
 * <p>
 * Usage:
 * <ul>
 *   <li>Configure the Mojo in the Maven POM file.</li>
 *   <li>Provide the path to the entities file using the <code>entities-file</code> parameter.</li>
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
 *         <goal>add-entities</goal>
 *       </goals>
 *       <configuration>
 *         <entities-file>${project.basedir}/src/main/resources/entities.json</entities-file>
 *       </configuration>
 *     </execution>
 *   </executions>
 * </plugin>
 * }
 * </pre>
 *
 * @author Diego Silva diego.silva at apuntesdejava.com
 */
@Mojo(
    name = "add-entities"
)
public class AddEntitiesMojo extends AbstractMojo {

    @Parameter(
        required = true,
        property = "entities-file"
    )
    private File entitiesFile;

    @Parameter(defaultValue = "${project}", readonly = true)
    private MavenProject mavenProject;

    /**
     * Default constructor for the AddEntitiesMojo class.
     * <p>
     * This constructor is used by Maven to create an instance of this Mojo.
     * </p>
     */
    public AddEntitiesMojo() {
    }

    /**
     * Executes the Mojo to add JPA entities to the project.
     * <p>
     * This method validates the existence of the entities file and delegates the processing
     * to the {@link JakartaPersistenceHelper}. If the file is not found or an error occurs,
     * appropriate exceptions are thrown.
     * </p>
     *
     * @throws MojoExecutionException if an error occurs during execution.
     * @throws MojoFailureException   if the entities file is not found.
     */
    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        try {
            var log = getLog();
            log.info("Adding entities from file: " + entitiesFile.getAbsolutePath());
            if (!entitiesFile.exists()) {
                log.error("Entities file not found: " + entitiesFile.getAbsolutePath());
                throw new MojoFailureException("Entities file not found: " + entitiesFile.getAbsolutePath());
            }
            var persistenceXmlPath = JakartaEeHelper.getInstance()
                    .getPersistenceXmlPath(mavenProject)
                    .orElseThrow( ()-> new FileNotFoundException("persistence.xml file not found"));
            JakartaPersistenceHelper.getInstance()
                                    .addEntities(mavenProject, log, entitiesFile.toPath(), persistenceXmlPath);
        } catch (Exception ex) {
            throw new MojoExecutionException("Error adding entities", ex);
        }
    }

}
