package com.apuntesdejava.jakartacoffeebuilder.mojo.persistence;

import com.apuntesdejava.jakartacoffeebuilder.helper.JakartaEeHelper;
import com.apuntesdejava.jakartacoffeebuilder.helper.JakartaPersistenceHelper;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
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
import java.nio.file.Paths;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AddEntitiesMojoTest {

    @Mock
    private MavenProject mavenProject;

    @Mock
    private Log mockLog;

    @InjectMocks
    private AddEntitiesMojo mojo;

    private MockedStatic<JakartaEeHelper> jakartaEeHelperMockedStatic;
    private MockedStatic<JakartaPersistenceHelper> jakartaPersistenceHelperMockedStatic;

    @Mock
    private JakartaEeHelper jakartaEeHelperMock;

    @Mock
    private JakartaPersistenceHelper jakartaPersistenceHelperMock;

    private File tempFile;

    @BeforeEach
    void setUp() throws Exception {
        mojo.setLog(mockLog);

        jakartaEeHelperMockedStatic = mockStatic(JakartaEeHelper.class);
        jakartaPersistenceHelperMockedStatic = mockStatic(JakartaPersistenceHelper.class);

        jakartaEeHelperMockedStatic.when(JakartaEeHelper::getInstance).thenReturn(jakartaEeHelperMock);
        jakartaPersistenceHelperMockedStatic.when(JakartaPersistenceHelper::getInstance).thenReturn(jakartaPersistenceHelperMock);

        tempFile = File.createTempFile("entities", ".json");
        tempFile.deleteOnExit();

        java.lang.reflect.Field entitiesFileField = AddEntitiesMojo.class.getDeclaredField("entitiesFile");
        entitiesFileField.setAccessible(true);
        entitiesFileField.set(mojo, tempFile);
    }

    @AfterEach
    void tearDown() {
        jakartaEeHelperMockedStatic.close();
        jakartaPersistenceHelperMockedStatic.close();
    }

    @Test
    @DisplayName("execute: should add entities successfully")
    void execute_ValidFileAndProject_AddsEntities() throws Exception {
        Path mockPersistenceXmlPath = Paths.get("src/main/resources/META-INF/persistence.xml");
        when(jakartaEeHelperMock.getPersistenceXmlPath(mavenProject))
                .thenReturn(Optional.of(mockPersistenceXmlPath));

        mojo.execute();

        verify(jakartaPersistenceHelperMock).addEntities(mavenProject, mockLog, tempFile.toPath(), mockPersistenceXmlPath);
    }

    @Test
    @DisplayName("execute: should throw exception when entities file does not exist")
    void execute_MissingEntitiesFile_ThrowsMojoExecutionException() throws Exception {
        File nonExistent = new File("non-existent-file-12345.json");
        java.lang.reflect.Field entitiesFileField = AddEntitiesMojo.class.getDeclaredField("entitiesFile");
        entitiesFileField.setAccessible(true);
        entitiesFileField.set(mojo, nonExistent);

        MojoExecutionException exception = assertThrows(MojoExecutionException.class, () -> mojo.execute());
        assertTrue(exception.getMessage().contains("Error adding entities"));
        verify(mockLog).error("Entities file not found: " + nonExistent.getAbsolutePath());
    }

    @Test
    @DisplayName("execute: should throw exception when persistence.xml not found")
    void execute_MissingPersistenceXml_ThrowsMojoExecutionException() throws Exception {
        when(jakartaEeHelperMock.getPersistenceXmlPath(mavenProject))
                .thenReturn(Optional.empty());

        MojoExecutionException exception = assertThrows(MojoExecutionException.class, () -> mojo.execute());
        assertTrue(exception.getMessage().contains("Error adding entities"));
    }
}
