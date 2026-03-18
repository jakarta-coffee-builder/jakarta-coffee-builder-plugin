package com.apuntesdejava.jakartacoffeebuilder.mojo.faces;

import com.apuntesdejava.jakartacoffeebuilder.helper.JakartaEeHelper;
import com.apuntesdejava.jakartacoffeebuilder.helper.PrimeFacesHelper;
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

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AddFormsFromEntitiesMojoTest {

    @Mock
    private MavenProject mavenProject;

    @Mock
    private MavenSession mavenSession;

    @Mock
    private ProjectBuilder projectBuilder;

    @Mock
    private Log mockLog;

    @InjectMocks
    private AddFormsFromEntitiesMojo mojo;

    private MockedStatic<MavenProjectUtil> mavenProjectUtilMockedStatic;
    private MockedStatic<PomUtil> pomUtilMockedStatic;
    private MockedStatic<JakartaEeHelper> jakartaEeHelperMockedStatic;
    private MockedStatic<PrimeFacesHelper> primeFacesHelperMockedStatic;

    @Mock
    private JakartaEeHelper jakartaEeHelperMock;

    @Mock
    private PrimeFacesHelper primeFacesHelperMock;

    private File tempFormsFile;
    private File tempEntitiesFile;

    @BeforeEach
    void setUp() throws Exception {
        mojo.setLog(mockLog);

        mavenProjectUtilMockedStatic = mockStatic(MavenProjectUtil.class);
        pomUtilMockedStatic = mockStatic(PomUtil.class);
        jakartaEeHelperMockedStatic = mockStatic(JakartaEeHelper.class);
        primeFacesHelperMockedStatic = mockStatic(PrimeFacesHelper.class);

        jakartaEeHelperMockedStatic.when(JakartaEeHelper::getInstance).thenReturn(jakartaEeHelperMock);
        primeFacesHelperMockedStatic.when(PrimeFacesHelper::getInstance).thenReturn(primeFacesHelperMock);

        tempFormsFile = File.createTempFile("forms", ".json");
        tempFormsFile.deleteOnExit();

        tempEntitiesFile = File.createTempFile("entities", ".json");
        tempEntitiesFile.deleteOnExit();

        setField("formsFile", tempFormsFile);
        setField("entitiesFile", tempEntitiesFile);
    }

    private void setField(String fieldName, Object value) throws Exception {
        java.lang.reflect.Field field = AddFormsFromEntitiesMojo.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(mojo, value);
    }

    @AfterEach
    void tearDown() {
        mavenProjectUtilMockedStatic.close();
        pomUtilMockedStatic.close();
        jakartaEeHelperMockedStatic.close();
        primeFacesHelperMockedStatic.close();
    }

    @Test
    @DisplayName("execute: should add forms from entities successfully")
    void execute_ValidFiles_AddsForms() throws Exception {
        MavenProject fullProject = mock(MavenProject.class);

        mavenProjectUtilMockedStatic.when(() -> MavenProjectUtil.getFullProject(mavenSession,
                projectBuilder,
                mavenProject))
            .thenReturn(fullProject);

        when(jakartaEeHelperMock.hasNotPrimeFacesDependency(fullProject, mockLog)).thenReturn(true);
        pomUtilMockedStatic.when(() -> PomUtil.saveMavenProject(mavenProject, mockLog)).thenAnswer(inv -> null);

        mojo.execute();

        verify(jakartaEeHelperMock).addPrimeFacesDependency(mavenProject, mockLog);
        verify(primeFacesHelperMock).addFormsFromEntities(fullProject,
            mockLog,
            tempFormsFile.toPath(),
            tempEntitiesFile.toPath());
        pomUtilMockedStatic.verify(() -> PomUtil.saveMavenProject(mavenProject, mockLog));
    }

    @Test
    @DisplayName("execute: should throw exception when forms file does not exist")
    void execute_MissingFormsFile_ThrowsMojoExecutionException() throws Exception {
        File nonExistent = new File("non-existent-form-file.json");
        setField("formsFile", nonExistent);

        MojoExecutionException exception = assertThrows(MojoExecutionException.class, () -> mojo.execute());
        assertTrue(exception.getMessage().contains("File not found"));
    }
}
