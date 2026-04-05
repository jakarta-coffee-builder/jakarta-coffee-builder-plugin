package com.apuntesdejava.jakartacoffeebuilder.mojo.arch;

import com.apuntesdejava.jakartacoffeebuilder.helper.ArchitectureHelper;
import com.apuntesdejava.jakartacoffeebuilder.util.JsonUtil;
import com.apuntesdejava.jakartacoffeebuilder.util.MavenProjectUtil;
import com.apuntesdejava.jakartacoffeebuilder.util.PomUtil;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonValue;
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
import java.nio.file.Path;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AddDomainModelsMojoTest {

    @Mock
    private MavenProject mavenProject;

    @Mock
    private MavenSession mavenSession;

    @Mock
    private ProjectBuilder projectBuilder;

    @Mock
    private Log mockLog;

    @InjectMocks
    private AddDomainModelsMojo mojo;

    private MockedStatic<MavenProjectUtil> mavenProjectUtilMockedStatic;
    private MockedStatic<PomUtil> pomUtilMockedStatic;
    private MockedStatic<JsonUtil> jsonUtilMockedStatic;
    private MockedStatic<ArchitectureHelper> architectureHelperMockedStatic;

    @Mock
    private ArchitectureHelper architectureHelperMock;

    private File tempFile;

    @BeforeEach
    void setUp() throws Exception {
        mojo.setLog(mockLog);

        mavenProjectUtilMockedStatic = mockStatic(MavenProjectUtil.class);
        pomUtilMockedStatic = mockStatic(PomUtil.class);
        jsonUtilMockedStatic = mockStatic(JsonUtil.class);
        architectureHelperMockedStatic = mockStatic(ArchitectureHelper.class);

        architectureHelperMockedStatic.when(ArchitectureHelper::getInstance).thenReturn(architectureHelperMock);

        tempFile = File.createTempFile("entities", ".json");
        tempFile.deleteOnExit();

        java.lang.reflect.Field entitiesFileField = AddDomainModelsMojo.class.getDeclaredField("entitiesFile");
        entitiesFileField.setAccessible(true);
        entitiesFileField.set(mojo, tempFile);
    }

    @AfterEach
    void tearDown() {
        mavenProjectUtilMockedStatic.close();
        pomUtilMockedStatic.close();
        jsonUtilMockedStatic.close();
        architectureHelperMockedStatic.close();
    }

    @Test
    @DisplayName("execute: should add domain models successfully")
    void execute_ValidProjectAndFile_AddsModels() throws Exception {
        MavenProject fullProject = mock(MavenProject.class);

        mavenProjectUtilMockedStatic.when(() -> MavenProjectUtil.getFullProject(mavenSession,
                projectBuilder,
                mavenProject))
            .thenReturn(fullProject);

        pomUtilMockedStatic.when(() -> PomUtil.getJakartaEeCurrentVersion(fullProject, mockLog))
            .thenReturn(Optional.of("10.0.0"));

        JsonObject dummyJson = Json.createObjectBuilder().add("test", "data").build();
        JsonValue dummyJsonValue = org.mockito.Mockito.mock(JsonValue.class);
        lenient().when(dummyJsonValue.asJsonObject()).thenReturn(dummyJson);

        jsonUtilMockedStatic.when(() -> JsonUtil.readJsonValue(any(Path.class)))
            .thenReturn(dummyJsonValue);

        pomUtilMockedStatic.when(() -> PomUtil.saveMavenProject(mavenProject, mockLog)).thenAnswer(inv -> null);

        mojo.execute();

        verify(architectureHelperMock).checkDependency(mavenProject, mockLog, "10.0.0");
        verify(architectureHelperMock).createDtos(mavenProject, mockLog, dummyJson);
        verify(architectureHelperMock).createMappers(mavenProject, mockLog, dummyJson);
        verify(architectureHelperMock).createModelRepositoryInterfaces(mavenProject, mockLog, dummyJson);
        verify(architectureHelperMock).createServices(mavenProject, mockLog, dummyJson);

        pomUtilMockedStatic.verify(() -> PomUtil.saveMavenProject(mavenProject, mockLog));
    }

    @Test
    @DisplayName("execute: should throw MojoExecutionException when entities file does not exist")
    void execute_MissingFile_ThrowsException() throws Exception {
        File nonExistent = new File("non-existent-file-12345.json");
        java.lang.reflect.Field entitiesFileField = AddDomainModelsMojo.class.getDeclaredField("entitiesFile");
        entitiesFileField.setAccessible(true);
        entitiesFileField.set(mojo, nonExistent);

        MavenProject fullProject = mock(MavenProject.class);
        mavenProjectUtilMockedStatic.when(() -> MavenProjectUtil.getFullProject(mavenSession,
                projectBuilder,
                mavenProject))
            .thenReturn(fullProject);
        pomUtilMockedStatic.when(() -> PomUtil.getJakartaEeCurrentVersion(fullProject, mockLog))
            .thenReturn(Optional.of("10.0.0"));

        MojoExecutionException exception = assertThrows(MojoExecutionException.class, () -> mojo.execute());
        assertTrue(exception.getMessage().contains("File not found"));
    }

    @Test
    @DisplayName("execute: should throw exception when Jakarta EE version missing")
    void execute_MissingJakartaEeVersion_ThrowsException() throws Exception {
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
