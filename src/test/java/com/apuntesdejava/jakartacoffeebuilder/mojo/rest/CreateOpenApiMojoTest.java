package com.apuntesdejava.jakartacoffeebuilder.mojo.rest;

import com.apuntesdejava.jakartacoffeebuilder.helper.JakartaEeHelper;
import com.apuntesdejava.jakartacoffeebuilder.helper.OpenApiGeneratorHelper;
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

import java.io.File;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CreateOpenApiMojoTest {

    @Mock
    private MavenProject mavenProject;

    @Mock
    private MavenSession mavenSession;

    @Mock
    private ProjectBuilder projectBuilder;

    @Mock
    private Log mockLog;

    @InjectMocks
    private CreateOpenApiMojo mojo;

    private MockedStatic<MavenProjectUtil> mavenProjectUtilMockedStatic;
    private MockedStatic<PomUtil> pomUtilMockedStatic;
    private MockedStatic<JakartaEeHelper> jakartaEeHelperMockedStatic;
    private MockedStatic<OpenApiGeneratorHelper> openApiGeneratorHelperMockedStatic;

    @Mock
    private JakartaEeHelper jakartaEeHelperMock;

    @Mock
    private OpenApiGeneratorHelper openApiGeneratorHelperMock;

    private File tempFile;

    @BeforeEach
    void setUp() throws Exception {
        mojo.setLog(mockLog);

        mavenProjectUtilMockedStatic = mockStatic(MavenProjectUtil.class);
        pomUtilMockedStatic = mockStatic(PomUtil.class);
        jakartaEeHelperMockedStatic = mockStatic(JakartaEeHelper.class);
        openApiGeneratorHelperMockedStatic = mockStatic(OpenApiGeneratorHelper.class);

        jakartaEeHelperMockedStatic.when(JakartaEeHelper::getInstance).thenReturn(jakartaEeHelperMock);
        openApiGeneratorHelperMockedStatic.when(OpenApiGeneratorHelper::getInstance).thenReturn(openApiGeneratorHelperMock);

        tempFile = File.createTempFile("openapi", ".yml");
        tempFile.deleteOnExit();

        java.lang.reflect.Field openApiFileServerField = CreateOpenApiMojo.class.getDeclaredField("openApiFileServer");
        openApiFileServerField.setAccessible(true);
        openApiFileServerField.set(mojo, tempFile);
    }

    @AfterEach
    void tearDown() {
        mavenProjectUtilMockedStatic.close();
        pomUtilMockedStatic.close();
        jakartaEeHelperMockedStatic.close();
        openApiGeneratorHelperMockedStatic.close();
    }

    @Test
    @DisplayName("execute: should add OpenAPI configurations successfully")
    void execute_ValidProject_AddsConfigurations() throws Exception {
        MavenProject fullProject = mock(MavenProject.class);

        mavenProjectUtilMockedStatic.when(() -> MavenProjectUtil.getFullProject(mavenSession, projectBuilder, mavenProject))
                .thenReturn(fullProject);

        pomUtilMockedStatic.when(() -> PomUtil.getJakartaEeCurrentVersion(fullProject, mockLog))
                .thenReturn(Optional.of("10.0.0"));

        pomUtilMockedStatic.when(() -> PomUtil.saveMavenProject(mavenProject, mockLog)).thenAnswer(inv -> null);

        mojo.execute();

        verify(jakartaEeHelperMock).addJacksonDependency(mavenProject, mockLog);
        verify(jakartaEeHelperMock).addMicroprofileOpenApiApiDependency(mavenProject, mockLog);
        verify(jakartaEeHelperMock).addJakartaValidationApiDependency(mavenProject, mockLog, "10.0.0");
        verify(jakartaEeHelperMock).addHelperGenerateSource(mavenProject, mockLog);
        
        verify(openApiGeneratorHelperMock).processServer(mavenProject, tempFile, mockLog);

        pomUtilMockedStatic.verify(() -> PomUtil.saveMavenProject(mavenProject, mockLog));
    }

    @Test
    @DisplayName("execute: should throw exception when Jakarta EE version is missing")
    void execute_MissingJakartaEeVersion_ThrowsException() throws Exception {
        MavenProject fullProject = mock(MavenProject.class);
        mavenProjectUtilMockedStatic.when(() -> MavenProjectUtil.getFullProject(mavenSession, projectBuilder, mavenProject))
                .thenReturn(fullProject);

        pomUtilMockedStatic.when(() -> PomUtil.getJakartaEeCurrentVersion(fullProject, mockLog))
                .thenReturn(Optional.empty());

        MojoExecutionException exception = assertThrows(MojoExecutionException.class, () -> mojo.execute());
        assertTrue(exception.getMessage().contains("Jakarta EE dependency not found"));
    }
}
