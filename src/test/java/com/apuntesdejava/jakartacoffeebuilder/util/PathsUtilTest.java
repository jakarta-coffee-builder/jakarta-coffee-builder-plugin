package com.apuntesdejava.jakartacoffeebuilder.util;

import org.apache.maven.project.MavenProject;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PathsUtilTest {


    @Test
    void getJavaPathCreatesDirectoryIfNotExists() throws IOException {
        MavenProject mockProject = mock(MavenProject.class);
        Path baseDir = Files.createTempDirectory("mavenProject");
        when(mockProject.getBasedir()).thenReturn(baseDir.toFile());

        Path javaPath = PathsUtil.getJavaPath(mockProject);
        assertTrue(Files.exists(javaPath));
        assertTrue(Files.isDirectory(javaPath));

        Files.deleteIfExists(javaPath);
    }

    @Test
    void getWebappPathCreatesDirectoryIfNotExists() throws IOException {
        MavenProject mockProject = mock(MavenProject.class);
        Path baseDir = Files.createTempDirectory("mavenProject");
        when(mockProject.getBasedir()).thenReturn(baseDir.toFile());

        Path webappPath = PathsUtil.getWebappPath(mockProject);
        assertTrue(Files.exists(webappPath));
        assertTrue(Files.isDirectory(webappPath));

        Files.deleteIfExists(webappPath);
    }

    @Test
    void packageToPathCreatesNestedDirectories() throws IOException {
        Path javaDir = Files.createTempDirectory("javaDir");
        String packageName = "com.example.test";

        Path packagePath = PathsUtil.packageToPath(javaDir, packageName);
        assertTrue(Files.exists(packagePath));
        assertTrue(Files.isDirectory(packagePath));

        Files.deleteIfExists(packagePath);
    }

    @Test
    void getJavaPathCreatesFileInCorrectPackage() throws IOException {
        MavenProject mockProject = mock(MavenProject.class);
        Path baseDir = Files.createTempDirectory("mavenProject");
        when(mockProject.getBasedir()).thenReturn(baseDir.toFile());

        String packageDefinition = "com.example.test";
        String javaClassName = "MyClass";

        Path javaFilePath = PathsUtil.getJavaPath(mockProject, packageDefinition, javaClassName);
        assertTrue(Files.exists(javaFilePath.getParent()));
        assertEquals("MyClass.java", javaFilePath.getFileName().toString());

        Files.deleteIfExists(javaFilePath);
        Files.deleteIfExists(javaFilePath.getParent());
    }

    @Test
    void getContentFromResourceReadsFileContent() throws IOException {
        Stream<String> content = PathsUtil.getContentFromResource("test-resource.txt");
        assertArrayEquals(new String[]{"line1", "line2", "line3"}, content.toArray());
    }
}
