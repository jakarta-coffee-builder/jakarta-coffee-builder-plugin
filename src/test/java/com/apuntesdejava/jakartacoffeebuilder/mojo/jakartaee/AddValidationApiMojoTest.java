package com.apuntesdejava.jakartacoffeebuilder.mojo.jakartaee;

import com.apuntesdejava.jakartacoffeebuilder.helper.JakartaEeHelper;
import com.apuntesdejava.jakartacoffeebuilder.util.MavenProjectUtil;
import com.apuntesdejava.jakartacoffeebuilder.util.PomUtil;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.ProjectBuilder;
import org.apache.maven.project.ProjectBuildingException;
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
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class AddValidationApiMojoTest {

    @Mock
    private MavenProject mavenProject;

    @Mock
    private MavenSession mavenSession;

    @Mock
    private ProjectBuilder projectBuilder;

    @Mock
    private Log mockLog;

    @InjectMocks
    private AddValidationApiMojo mojo;

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
    @DisplayName("execute: should add validation API when full project is resolved and version exists")
    void execute_ValidProject_AddsDependency() throws Exception {
        MavenProject fullProject = mock(MavenProject.class);
        mavenProjectUtilMockedStatic.when(() -> MavenProjectUtil.getFullProject(mavenSession, projectBuilder, mavenProject))
                .thenReturn(fullProject);

        pomUtilMockedStatic.when(() -> PomUtil.getJakartaEeCurrentVersion(fullProject, mockLog))
                .thenReturn(Optional.of("10.0.0"));

        doNothing().when(jakartaEeHelperMock).addJakartaValidationApiDependency(mavenProject, mockLog, "10.0.0");
        pomUtilMockedStatic.when(() -> PomUtil.saveMavenProject(mavenProject, mockLog)).thenAnswer(inv -> null);

        mojo.execute();

        verify(jakartaEeHelperMock).addJakartaValidationApiDependency(mavenProject, mockLog, "10.0.0");
        pomUtilMockedStatic.verify(() -> PomUtil.saveMavenProject(mavenProject, mockLog));
    }

    @Test
    @DisplayName("execute: should throw MojoExecutionException when Jakarta EE version is missing")
    void execute_MissingJakartaEeVersion_ThrowsMojoExecutionException() throws Exception {
        MavenProject fullProject = mock(MavenProject.class);
        mavenProjectUtilMockedStatic.when(() -> MavenProjectUtil.getFullProject(mavenSession, projectBuilder, mavenProject))
                .thenReturn(fullProject);

        pomUtilMockedStatic.when(() -> PomUtil.getJakartaEeCurrentVersion(fullProject, mockLog))
                .thenReturn(Optional.empty());

        MojoExecutionException exception = assertThrows(MojoExecutionException.class, () -> mojo.execute());
        assertTrue(exception.getMessage().contains("Jakarta EE dependency not found"));
        
        verifyNoInteractions(jakartaEeHelperMock);
    }

    @Test
    @DisplayName("execute: should throw MojoFailureException when project builder fails")
    void execute_ProjectBuilderFails_ThrowsMojoFailureException() throws Exception {
        mavenProjectUtilMockedStatic.when(() -> MavenProjectUtil.getFullProject(mavenSession, projectBuilder, mavenProject))
                .thenThrow(new ProjectBuildingException("test", "Error building", new Exception()));

        assertThrows(MojoFailureException.class, () -> mojo.execute());
    }
}
