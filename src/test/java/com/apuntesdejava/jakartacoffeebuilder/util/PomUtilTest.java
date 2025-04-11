package com.apuntesdejava.jakartacoffeebuilder.util;

import com.apuntesdejava.jakartacoffeebuilder.LogTest;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PomUtilTest {

    private static final Log LOG = new LogTest();


    @Test
    void addDependencyAddsNewDependencyWhenNotPresent() {
        MavenProject mockProject = mock(MavenProject.class);
        mockProject.setOriginalModel(new org.apache.maven.model.Model());
        when(mockProject.getFile()).thenReturn(new File("pom.xml"));
        when(mockProject.getOriginalModel()).thenReturn(new org.apache.maven.model.Model());
        when(mockProject.getDependencies()).thenReturn(new ArrayList<>());

        var model = mockProject.getOriginalModel();
        when(mockProject.getDependencies()).thenReturn(new ArrayList<>());

        PomUtil.addDependency(mockProject, LOG, "com.example", "example-artifact", "1.0.0");

        assertEquals(1, model.getDependencies().size());
        assertEquals("com.example", model.getDependencies().getFirst().getGroupId());
        assertEquals("example-artifact", model.getDependencies().getFirst().getArtifactId());
        assertEquals("1.0.0", model.getDependencies().getFirst().getVersion());
    }

    @Test
    void addDependencyDoesNotAddDuplicateDependency() {
        MavenProject mockProject = mock(MavenProject.class);
        mockProject.setOriginalModel(new org.apache.maven.model.Model());
        when(mockProject.getFile()).thenReturn(new File("pom.xml"));
        when(mockProject.getOriginalModel()).thenReturn(new org.apache.maven.model.Model());
        when(mockProject.getDependencies()).thenReturn(new ArrayList<>());
        var model = mockProject.getOriginalModel();
        var dependency = new Dependency();
        dependency.setGroupId("com.example");
        dependency.setArtifactId("example-artifact");
        dependency.setVersion("1.0.0");
        model.getDependencies().add(dependency);

        PomUtil.addDependency(mockProject, LOG, "com.example", "example-artifact", "1.0.0");

        assertEquals(1, model.getDependencies().size());
    }

    @Test
    void existsDependencyReturnsTrueForExistingDependency() {
        MavenProject mockProject = mock(MavenProject.class);
        var artifact = mock(Artifact.class);
        when(artifact.getGroupId()).thenReturn("com.example");
        when(artifact.getArtifactId()).thenReturn("example-artifact");
        when(mockProject.getArtifacts()).thenReturn(Set.of(artifact));

        boolean result = PomUtil.existsDependency(mockProject, LOG, "com.example", "example-artifact");

        assertTrue(result);
    }

    @Test
    void existsDependencyReturnsFalseForNonExistingDependency() {
        MavenProject mockProject = mock(MavenProject.class);
        when(mockProject.getArtifacts()).thenReturn(Set.of());

        boolean result = PomUtil.existsDependency(mockProject, LOG, "com.example", "example-artifact");

        assertFalse(result);
    }

    @Test
    void saveMavenProjectThrowsExceptionWhenFileCannotBeWritten() {
        MavenProject mockProject = mock(MavenProject.class);
        when(mockProject.getFile()).thenReturn(new File("/invalid/path/pom.xml"));

        assertThrows(MojoExecutionException.class, () -> PomUtil.saveMavenProject(mockProject, LOG));
    }

}
