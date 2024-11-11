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
package com.apuntesdejava.jakartacoffeebuilder.util;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;

import static com.apuntesdejava.jakartacoffeebuilder.util.Constants.JAKARTA_FACES;
import static com.apuntesdejava.jakartacoffeebuilder.util.Constants.JAKARTA_FACES_API;
import static com.apuntesdejava.jakartacoffeebuilder.util.Constants.PROVIDED_SCOPE;
import static com.apuntesdejava.jakartacoffeebuilder.util.Constants.SPECS_VERSIONS;

/**
 * Utility class for handling Jakarta EE dependencies in Maven projects.
 * <p>
 * This class provides methods to add Jakarta Faces dependencies to a Maven project
 * and to check if a Maven project already has a Jakarta Faces dependency.
 * </p>
 * <p>
 * This class follows the Singleton design pattern to ensure only one instance is created.
 * </p>
 * <p>
 * Usage example:
 * <pre>
 *     JakartaEeUtil jakartaEeUtil = JakartaEeUtil.getInstance();
 *     jakartaEeUtil.addJakartaFacesDependency(mavenProject, log, jakartaEeVersion);
 * </pre>
 * </p>
 * <p>
 * Note: This class is thread-safe.
 * </p>
 * <p>
 * Author: Diego Silva &lt;diego.silva at apuntesdejava.com&gt;
 * </p>
 */
public class JakartaEeUtil {

    public static JakartaEeUtil getInstance() {
        return JakartaEeUtilHolder.INSTANCE;
    }

    private JakartaEeUtil() {
    }

    /**
     * Adds a Jakarta Faces dependency to the given Maven project.
     *
     * @param mavenProject     the Maven project to which the dependency will be added
     * @param log              the logger to use for logging messages
     * @param jakartaEeVersion the version of Jakarta EE to use for the dependency
     * @throws MojoExecutionException if an error occurs while adding the dependency
     */
    public void addJakartaFacesDependency(MavenProject mavenProject, Log log,
                                          String jakartaEeVersion) throws MojoExecutionException {
        var pomUtil = PomUtil.getInstance();
        var jakartaFacesVersion = SPECS_VERSIONS.get(jakartaEeVersion).get(JAKARTA_FACES_API);
        pomUtil.addDependency(mavenProject, log, JAKARTA_FACES, JAKARTA_FACES_API, jakartaFacesVersion, PROVIDED_SCOPE);
        pomUtil.saveMavenProject(mavenProject, log);
    }

    /**
     * Checks if the given Maven project has a dependency on Jakarta Faces.
     *
     * @param mavenProject the Maven project to check
     * @param log          the logger to use for logging messages
     * @return true if the project has a Jakarta Faces dependency, false otherwise
     */
    public boolean hasJakartaFacesDependency(MavenProject mavenProject, Log log) {
        return PomUtil.getInstance().existsDependency(mavenProject, log, JAKARTA_FACES, JAKARTA_FACES_API);
    }

    private static class JakartaEeUtilHolder {

        private static final JakartaEeUtil INSTANCE = new JakartaEeUtil();
    }
}
