package com.apuntesdejava.jakartacoffeebuilder.mojo.persistence;

import com.apuntesdejava.jakartacoffeebuilder.helper.JakartaEeHelper;
import com.apuntesdejava.jakartacoffeebuilder.helper.PersistenceXmlHelper;
import com.apuntesdejava.jakartacoffeebuilder.util.CoffeeBuilderUtil;
import com.apuntesdejava.jakartacoffeebuilder.util.MavenProjectUtil;
import com.apuntesdejava.jakartacoffeebuilder.util.PomUtil;
import jakarta.json.Json;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AddPersistenceMojoTest {

    @Mock
    private MavenProject mavenProject;

    @Mock
    private MavenSession mavenSession;

    @Mock
    private ProjectBuilder projectBuilder;

    @Mock
    private Log mockLog;

    @InjectMocks
    private AddPersistenceMojo mojo;

    private MockedStatic<MavenProjectUtil> mavenProjectUtilMockedStatic;
    private MockedStatic<PomUtil> pomUtilMockedStatic;
    private MockedStatic<JakartaEeHelper> jakartaEeHelperMockedStatic;
    private MockedStatic<PersistenceXmlHelper> persistenceXmlHelperMockedStatic;
    private MockedStatic<CoffeeBuilderUtil> coffeeBuilderUtilMockedStatic;

    @Mock
    private JakartaEeHelper jakartaEeHelperMock;

    @Mock
    private PersistenceXmlHelper persistenceXmlHelperMock;

    @BeforeEach
    void setUp() throws Exception {
        mojo.setLog(mockLog);

        mavenProjectUtilMockedStatic = mockStatic(MavenProjectUtil.class);
        pomUtilMockedStatic = mockStatic(PomUtil.class);
        jakartaEeHelperMockedStatic = mockStatic(JakartaEeHelper.class);
        persistenceXmlHelperMockedStatic = mockStatic(PersistenceXmlHelper.class);
        coffeeBuilderUtilMockedStatic = mockStatic(CoffeeBuilderUtil.class);

        jakartaEeHelperMockedStatic.when(JakartaEeHelper::getInstance).thenReturn(jakartaEeHelperMock);
        persistenceXmlHelperMockedStatic.when(PersistenceXmlHelper::getInstance).thenReturn(persistenceXmlHelperMock);

        setField("datasourceName", "myDS");
        setField("url", "jdbc:h2:mem:test");
        setField("declare", "web");
        setField("persistenceUnitName", "myPU");
        setField("mavenProject", mavenProject);
        setField("mavenSession", mavenSession);
        setField("projectBuilder", projectBuilder);
        setField("profile", "default");
    }

    private void setField(String fieldName, Object value) throws Exception {
        java.lang.reflect.Field field = AddAbstractPersistenceMojo.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(mojo, value);
    }

    @AfterEach
    void tearDown() {
        mavenProjectUtilMockedStatic.close();
        pomUtilMockedStatic.close();
        jakartaEeHelperMockedStatic.close();
        persistenceXmlHelperMockedStatic.close();
        coffeeBuilderUtilMockedStatic.close();
    }

    @Test
    @DisplayName("execute: should add persistence configuration successfully")
    void execute_ValidProject_AddsPersistence() throws Exception {
        MavenProject fullProject = mock(MavenProject.class);

        mavenProjectUtilMockedStatic.when(() -> MavenProjectUtil.getFullProject(mavenSession, projectBuilder, mavenProject))
                .thenReturn(fullProject);

        var jdbcConfig = Json.createObjectBuilder().add("dataSourceClass", "org.h2.jdbcx.JdbcDataSource").build();
        coffeeBuilderUtilMockedStatic.when(() -> CoffeeBuilderUtil.getJdbcConfiguration("jdbc:h2:mem:test"))
                .thenReturn(Optional.of(jdbcConfig));

        pomUtilMockedStatic.when(() -> PomUtil.getJakartaEeCurrentVersion(fullProject, mockLog))
                .thenReturn(Optional.of("10.0.0"));

        lenient().when(jakartaEeHelperMock.hasNotJakartaCdiDependency(fullProject, mockLog)).thenReturn(true);
        lenient().when(jakartaEeHelperMock.hasNotJakartaPersistenceDependency(fullProject, mockLog)).thenReturn(true);

        pomUtilMockedStatic.when(() -> PomUtil.saveMavenProject(fullProject, mockLog)).thenAnswer(inv -> null);

        mojo.execute();

        verify(jakartaEeHelperMock).createPersistenceXml(eq(fullProject), eq(mockLog), eq("myPU"));
        verify(persistenceXmlHelperMock).addDataSourceToPersistenceXml(eq(fullProject), eq(mockLog), eq("myPU"), eq("jdbc/myDS"));
        verify(jakartaEeHelperMock).addDataSource(eq(fullProject), eq(mockLog), eq("web"), any(), any());

        verify(jakartaEeHelperMock).addJakartaCdiDependency(mavenProject, mockLog, "10.0.0");
        verify(jakartaEeHelperMock).addJakartaPersistenceDependency(fullProject, mockLog, "10.0.0");
        verify(jakartaEeHelperMock).addPersistenceClassProvider(mavenProject, mockLog);

        pomUtilMockedStatic.verify(() -> PomUtil.saveMavenProject(fullProject, mockLog));
    }

    @Test
    @DisplayName("execute: should throw MojoExecutionException when Jakarta EE version missing")
    void execute_MissingJakartaEeVersion_ThrowsException() throws Exception {
        MavenProject fullProject = mock(MavenProject.class);
        mavenProjectUtilMockedStatic.when(() -> MavenProjectUtil.getFullProject(mavenSession, projectBuilder, mavenProject))
                .thenReturn(fullProject);

        var jdbcConfig = Json.createObjectBuilder().add("dataSourceClass", "org.h2.jdbcx.JdbcDataSource").build();
        coffeeBuilderUtilMockedStatic.when(() -> CoffeeBuilderUtil.getJdbcConfiguration("jdbc:h2:mem:test"))
                .thenReturn(Optional.of(jdbcConfig));

        pomUtilMockedStatic.when(() -> PomUtil.getJakartaEeCurrentVersion(fullProject, mockLog))
                .thenReturn(Optional.empty());

        MojoExecutionException exception = assertThrows(MojoExecutionException.class, () -> mojo.execute());
        assertTrue(exception.getMessage().contains("Jakarta EE dependency not found"));
    }
}
