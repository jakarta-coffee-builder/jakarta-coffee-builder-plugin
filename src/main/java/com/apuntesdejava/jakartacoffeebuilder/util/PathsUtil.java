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
package com.apuntesdejava.jakartacoffeebuilder.util;

import org.apache.maven.project.MavenProject;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.stream.Stream;

import org.apache.commons.io.IOUtils;

/**
 * Utility class for working with file paths within a Maven project. Provides methods for obtaining paths to common
 * directories like 'src/main/java' and 'src/main/webapp', and for creating package directories.
 *
 * @author Diego Silva diego.silva at apuntesdejava.com
 */
public class PathsUtil {

    private PathsUtil(){

    }

    /**
     * Returns the path to the 'src/main/java' directory within the Maven project. Creates the directory if it doesn't
     * exist.
     *
     * @param mavenProject The Maven project object.
     * @return The Path object representing the 'src/main/java' directory.
     * @throws IOException If an I/O error occurs during directory creation.
     */
    public static Path getJavaPath(MavenProject mavenProject) throws IOException {
        var javaDir = mavenProject.getBasedir().toPath().resolve("src").resolve("main").resolve("java");
        if (!Files.exists(javaDir)) {
            Files.createDirectories(javaDir);
        }
        return javaDir;
    }

    public static Path getResourcePath(MavenProject mavenProject) throws IOException {
        var resourcesDir = mavenProject.getBasedir().toPath().resolve("src").resolve("main").resolve("resources");
        if (!Files.exists(resourcesDir)) {
            Files.createDirectories(resourcesDir);
        }
        return resourcesDir;
    }

    /**
     * Returns the path to the 'src/main/webapp' directory within the Maven project. Creates the directory if it doesn't
     * exist.
     *
     * @param mavenProject The Maven project object.
     * @return The Path object representing the 'src/main/webapp' directory.
     * @throws IOException If an I/O error occurs during directory creation.
     */
    public static Path getWebappPath(MavenProject mavenProject) throws IOException {
        var webappDir = mavenProject.getBasedir().toPath().resolve("src").resolve("main").resolve("webapp");
        if (!Files.exists(webappDir)) {
            Files.createDirectories(webappDir);
        }
        return webappDir;
    }

    /**
     * Returns the path to a package directory within the specified base directory. Creates the directory if it doesn't
     * exist. The package name is converted to a path using '/' as a separator.
     *
     * @param javaDir The base directory.
     * @param packageName The package name (e.g., "com.example.mypackage").
     * @return The Path object representing the package directory.
     * @throws IOException If an I/O error occurs during directory creation.
     */
    public static Path packageToPath(Path javaDir, String packageName) throws IOException {
        var packageDir = javaDir.resolve(packageName.replace(".", "/"));
        if (!Files.exists(packageDir)) {
            Files.createDirectories(packageDir);
        }
        return packageDir;
    }

    /**
     * Returns the path to a Java source file within a specified package directory in the Maven project.
     * Creates necessary directories if they don't exist.
     *
     * @param mavenProject The Maven project object.
     * @param packageDefinition The package name (e.g., "com.example.mypackage").
     * @param javaClassName The name of the Java class (without the .java extension).
     * @return The Path object representing the Java source file.
     * @throws IOException If an I/O error occurs during directory creation.
     */
    public static Path getJavaPath(MavenProject mavenProject,
                                   String packageDefinition,
                                   String javaClassName) throws IOException {
        var javaDir = PathsUtil.getJavaPath(mavenProject);
        var packageDir = PathsUtil.packageToPath(javaDir, packageDefinition);
        return packageDir.resolve(javaClassName + ".java");
    }

    /**
     * Reads the content of a resource file and returns it as a stream of lines.
     *
     * @param resourcePath The path to the resource file within the classpath.
     * @return A Stream of strings, where each string represents a line from the resource file.
     * @throws IOException If an I/O error occurs while reading the resource.
     * @throws NullPointerException If the resource cannot be found.
     */
    public static Stream<String> getContentFromResource(String resourcePath) throws IOException {
        try (var is = PathsUtil.class.getClassLoader().getResourceAsStream(resourcePath)) {
           return IOUtils.toString(Objects.requireNonNull(is), Charset.defaultCharset()).lines();
        }
    }
}
