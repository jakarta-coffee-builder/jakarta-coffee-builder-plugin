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
 * A utility class for resolving and creating file paths within a Maven project structure.
 * <p>
 * This class provides static methods to obtain paths to standard Maven directories such as
 * {@code src/main/java} and {@code src/main/webapp}, and ensures these directories are created
 * if they do not exist.
 *
 * @author Diego Silva diego.silva at apuntesdejava.com
 */
public final class PathsUtil {

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private PathsUtil() {

    }

    /**
     * Returns the path to the main Java source directory ({@code src/main/java}).
     * If the directory does not exist, it will be created.
     *
     * @param mavenProject The current Maven project.
     * @return The {@link Path} to the Java source directory.
     * @throws IOException If an I/O error occurs while creating the directory.
     */
    public static Path getJavaPath(MavenProject mavenProject) throws IOException {
        var javaDir = mavenProject.getBasedir().toPath().resolve("src").resolve("main").resolve("java");
        if (!Files.exists(javaDir)) {
            Files.createDirectories(javaDir);
        }
        return javaDir;
    }

    /**
     * Returns the path to the main resources directory ({@code src/main/resources}).
     * If the directory does not exist, it will be created.
     *
     * @param mavenProject The current Maven project.
     * @return The {@link Path} to the resources directory.
     * @throws IOException If an I/O error occurs while creating the directory.
     */
    public static Path getResourcePath(MavenProject mavenProject) throws IOException {
        var resourcesDir = mavenProject.getBasedir().toPath().resolve("src").resolve("main").resolve("resources");
        if (!Files.exists(resourcesDir)) {
            Files.createDirectories(resourcesDir);
        }
        return resourcesDir;
    }

    /**
     * Returns the path to the web application source directory ({@code src/main/webapp}).
     * If the directory does not exist, it will be created.
     *
     * @param mavenProject The current Maven project.
     * @return The {@link Path} to the webapp directory.
     * @throws IOException If an I/O error occurs while creating the directory.
     */
    public static Path getWebappPath(MavenProject mavenProject) throws IOException {
        var webappDir = mavenProject.getBasedir().toPath().resolve("src").resolve("main").resolve("webapp");
        if (!Files.exists(webappDir)) {
            Files.createDirectories(webappDir);
        }
        return webappDir;
    }

    /**
     * Converts a package name into a directory path relative to a given base path.
     * For example, "com.example.mypackage" becomes a directory structure {@code com/example/mypackage}.
     * If the directory structure does not exist, it will be created.
     *
     * @param basePath    The base directory {@link Path} where the package structure should be created.
     * @param packageName The package name to convert (e.g., "com.example.mypackage").
     * @return The {@link Path} to the final package directory.
     * @throws IOException If an I/O error occurs while creating the directories.
     */
    public static Path packageToPath(Path basePath, String packageName) throws IOException {
        var packageDir = basePath.resolve(packageName.replace(".", "/"));
        if (!Files.exists(packageDir)) {
            Files.createDirectories(packageDir);
        }
        return packageDir;
    }

    /**
     * Constructs the full path to a Java source file within a specific package.
     * This method combines {@link #getJavaPath(MavenProject)} and {@link #packageToPath(Path, String)}
     * to resolve the final file path.
     *
     * @param mavenProject      The current Maven project.
     * @param packageDefinition The target package name (e.g., "com.example.mypackage").
     * @param javaClassName     The name of the Java class (without the {@code .java} extension).
     * @return The fully resolved {@link Path} to the Java source file.
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
     * Reads the content of a classpath resource and returns it as a stream of lines.
     *
     * @param resourcePath The path to the resource file within the classpath (e.g., "/templates/my-template.txt").
     * @return A {@link Stream} of strings, where each string is a line from the resource file.
     * @throws IOException          If an I/O error occurs while reading the resource.
     * @throws NullPointerException If the resource stream cannot be found.
     */
    public static Stream<String> getContentFromResource(String resourcePath) throws IOException {
        try (var is = PathsUtil.class.getClassLoader().getResourceAsStream(resourcePath)) {
           return IOUtils.toString(Objects.requireNonNull(is), Charset.defaultCharset()).lines();
        }
    }
}
