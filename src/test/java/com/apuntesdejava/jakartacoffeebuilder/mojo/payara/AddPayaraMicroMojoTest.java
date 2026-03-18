package com.apuntesdejava.jakartacoffeebuilder.mojo.payara;

import com.apuntesdejava.jakartacoffeebuilder.helper.PayaraMicroHelper;
import com.apuntesdejava.jakartacoffeebuilder.util.MavenProjectUtil;
import com.apuntesdejava.jakartacoffeebuilder.util.PomUtil;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.MojoExecutionException;
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
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AddPayaraMicroMojoTest {

    @Mock
    private MavenProject mavenProject;

    @Mock
    private MavenSession mavenSession;

    @Mock
    private ProjectBuilder projectBuilder;

    @Mock
    private Log mockLog;

    @InjectMocks
    private AddPayaraMicroMojo mojo;

    private MockedStatic<MavenProjectUtil> mavenProjectUtilMockedStatic;
    private MockedStatic<PomUtil> pomUtilMockedStatic;
    private MockedStatic<PayaraMicroHelper> payaraMicroHelperMockedStatic;

    @Mock
    private PayaraMicroHelper payaraMicroHelperMock;

    @BeforeEach
    void setUp() throws Exception {
        mojo.setLog(mockLog);

        mavenProjectUtilMockedStatic = mockStatic(MavenProjectUtil.class);
        pomUtilMockedStatic = mockStatic(PomUtil.class);
        payaraMicroHelperMockedStatic = mockStatic(PayaraMicroHelper.class);

        payaraMicroHelperMockedStatic.when(PayaraMicroHelper::getInstance).thenReturn(payaraMicroHelperMock);

        java.lang.reflect.Field profileIdField = AddPayaraMicroMojo.class.getDeclaredField("profileId");
        profileIdField.setAccessible(true);
        profileIdField.set(mojo, "payaramicro");
    }

    @AfterEach
    void tearDown() {
        mavenProjectUtilMockedStatic.close();
        pomUtilMockedStatic.close();
        payaraMicroHelperMockedStatic.close();
    }

    @Test
    @DisplayName("execute: should add Payara Micro plugin successfully")
    void execute_ValidProject_AddsPlugin() throws Exception {
        MavenProject fullProject = mock(MavenProject.class);

        mavenProjectUtilMockedStatic.when(() -> MavenProjectUtil.getFullProject(mavenSession, projectBuilder, mavenProject))
                .thenReturn(fullProject);

        pomUtilMockedStatic.when(() -> PomUtil.getJakartaEeCurrentVersion(fullProject, mockLog))
                .thenReturn(Optional.of("10.0.0"));

        doNothing().when(payaraMicroHelperMock).addPlugin(mavenProject, mockLog, "payaramicro", "10.0.0");
        pomUtilMockedStatic.when(() -> PomUtil.saveMavenProject(mavenProject, mockLog)).thenAnswer(inv -> null);

        mojo.execute();

        verify(payaraMicroHelperMock).addPlugin(mavenProject, mockLog, "payaramicro", "10.0.0");
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
    }

    @Test
    @DisplayName("execute: should catch and log exception on project building failure")
    void execute_ProjectBuilderFails_LogsError() throws Exception {
        ProjectBuildingException buildException = new ProjectBuildingException("test", "Error building", new Exception());
        mavenProjectUtilMockedStatic.when(() -> MavenProjectUtil.getFullProject(mavenSession, projectBuilder, mavenProject))
                .thenThrow(buildException);

        mojo.execute();

        verify(mockLog).error(eq("Error building for project test"), eq(buildException));
    }
}
