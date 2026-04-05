package com.apuntesdejava.jakartacoffeebuilder.mojo.faces;

import com.apuntesdejava.jakartacoffeebuilder.helper.JakartaEeHelper;
import com.apuntesdejava.jakartacoffeebuilder.util.MavenProjectUtil;
import com.apuntesdejava.jakartacoffeebuilder.util.PomUtil;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.ProjectBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AddFacesMojoTest {

    @Mock
    private MavenProject mavenProject;

    @Mock
    private MavenSession mavenSession;

    @Mock
    private ProjectBuilder projectBuilder;

    @Mock
    private Log mockLog;

    @InjectMocks
    private AddFacesMojo mojo;

    private MockedStatic<MavenProjectUtil> mavenProjectUtilMockedStatic;
    private MockedStatic<PomUtil> pomUtilMockedStatic;
    private MockedStatic<JakartaEeHelper> jakartaEeHelperMockedStatic;

    @Mock
    private JakartaEeHelper jakartaEeHelperMock;

    @BeforeEach
    void setUp() {
        mojo.setLog(mockLog);

        mavenProjectUtilMockedStatic = mockStatic(MavenProjectUtil.class);
        pomUtilMockedStatic = mockStatic(PomUtil.class);
        jakartaEeHelperMockedStatic = mockStatic(JakartaEeHelper.class);

        jakartaEeHelperMockedStatic.when(JakartaEeHelper::getInstance).thenReturn(jakartaEeHelperMock);
    }

    @AfterEach
    void tearDown() {
        mavenProjectUtilMockedStatic.close();
        pomUtilMockedStatic.close();
        jakartaEeHelperMockedStatic.close();
    }

    @Test
    @DisplayName("execute: should add Faces configurations successfully")
    void execute_ValidProject_AddsConfigurations() throws Exception {
        MavenProject fullProject = mock(MavenProject.class);

        mavenProjectUtilMockedStatic.when(() -> MavenProjectUtil.getFullProject(mavenSession,
                projectBuilder,
                mavenProject))
            .thenReturn(fullProject);

        pomUtilMockedStatic.when(() -> PomUtil.getJakartaEeCurrentVersion(fullProject, mockLog))
            .thenReturn(Optional.of("10.0.0"));

        lenient().when(jakartaEeHelperMock.hasJakartaFacesDependency(fullProject, mockLog)).thenReturn(false);
        lenient().when(jakartaEeHelperMock.hasNotJakartaCdiDependency(fullProject, mockLog)).thenReturn(true);

        pomUtilMockedStatic.when(() -> PomUtil.saveMavenProject(mavenProject, mockLog)).thenAnswer(inv -> null);

        mojo.execute();

        verify(jakartaEeHelperMock).addJakartaFacesDependency(mavenProject, mockLog, "10.0.0");
        verify(jakartaEeHelperMock).addJakartaCdiDependency(mavenProject, mockLog, "10.0.0");
        verify(jakartaEeHelperMock).addJakartaFacesDeclaration(fullProject, mockLog);
        verify(jakartaEeHelperMock).addWelcomePages(eq(fullProject), any(), eq(mockLog));
        pomUtilMockedStatic.verify(() -> PomUtil.saveMavenProject(mavenProject, mockLog));
    }

    @Test
    @DisplayName("execute: should skip adding dependencies if already present")
    void execute_DependenciesPresent_SkipsDependencies() throws Exception {
        MavenProject fullProject = mock(MavenProject.class);

        mavenProjectUtilMockedStatic.when(() -> MavenProjectUtil.getFullProject(mavenSession,
                projectBuilder,
                mavenProject))
            .thenReturn(fullProject);

        pomUtilMockedStatic.when(() -> PomUtil.getJakartaEeCurrentVersion(fullProject, mockLog))
            .thenReturn(Optional.of("10.0.0"));

        lenient().when(jakartaEeHelperMock.hasJakartaFacesDependency(fullProject, mockLog)).thenReturn(true);
        lenient().when(jakartaEeHelperMock.hasNotJakartaCdiDependency(fullProject, mockLog)).thenReturn(false);

        mojo.execute();

        verify(jakartaEeHelperMock, never()).addJakartaFacesDependency(any(), any(), any());
        verify(jakartaEeHelperMock, never()).addJakartaCdiDependency(any(), any(), any());
        verify(jakartaEeHelperMock).addJakartaFacesDeclaration(fullProject, mockLog);
    }

    @Test
    @DisplayName("execute: should throw MojoExecutionException when Jakarta EE version is missing")
    void execute_MissingJakartaEeVersion_ThrowsMojoExecutionException() throws Exception {
        MavenProject fullProject = mock(MavenProject.class);
        mavenProjectUtilMockedStatic.when(() -> MavenProjectUtil.getFullProject(mavenSession,
                projectBuilder,
                mavenProject))
            .thenReturn(fullProject);

        pomUtilMockedStatic.when(() -> PomUtil.getJakartaEeCurrentVersion(fullProject, mockLog))
            .thenReturn(Optional.empty());

        MojoExecutionException exception = assertThrows(MojoExecutionException.class, () -> mojo.execute());
        assertTrue(exception.getMessage().contains("Jakarta EE dependency not found"));
    }
}
