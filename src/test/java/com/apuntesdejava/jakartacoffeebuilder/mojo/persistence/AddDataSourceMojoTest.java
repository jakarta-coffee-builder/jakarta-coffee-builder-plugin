package com.apuntesdejava.jakartacoffeebuilder.mojo.persistence;

import com.apuntesdejava.jakartacoffeebuilder.helper.JakartaEeHelper;
import com.apuntesdejava.jakartacoffeebuilder.helper.PersistenceXmlHelper;
import com.apuntesdejava.jakartacoffeebuilder.util.CoffeeBuilderUtil;
import com.apuntesdejava.jakartacoffeebuilder.util.MavenProjectUtil;
import com.apuntesdejava.jakartacoffeebuilder.util.PomUtil;
import jakarta.json.Json;
import org.apache.maven.execution.MavenSession;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AddDataSourceMojoTest {

    @Mock
    private MavenProject mavenProject;

    @Mock
    private MavenSession mavenSession;

    @Mock
    private ProjectBuilder projectBuilder;

    @Mock
    private Log mockLog;

    @InjectMocks
    private AddDataSourceMojo mojo;

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
    @DisplayName("execute: should add data source successfully")
    void execute_ValidProject_AddsDataSource() throws Exception {
        MavenProject fullProject = mock(MavenProject.class);

        mavenProjectUtilMockedStatic.when(() -> MavenProjectUtil.getFullProject(mavenSession, projectBuilder, mavenProject))
                .thenReturn(fullProject);

        var jdbcConfig = Json.createObjectBuilder().add("dataSourceClass", "org.h2.jdbcx.JdbcDataSource").build();
        coffeeBuilderUtilMockedStatic.when(() -> CoffeeBuilderUtil.getJdbcConfiguration("jdbc:h2:mem:test"))
                .thenReturn(Optional.of(jdbcConfig));

        pomUtilMockedStatic.when(() -> PomUtil.saveMavenProject(fullProject, mockLog)).thenAnswer(inv -> null);

        mojo.execute();

        verify(persistenceXmlHelperMock).addDataSourceToPersistenceXml(eq(fullProject), eq(mockLog), eq("myPU"), eq("jdbc/myDS"));
        verify(jakartaEeHelperMock).checkDataDependencies(eq(fullProject), eq(mockLog), eq(jdbcConfig));
        verify(jakartaEeHelperMock).addDataSource(eq(fullProject), eq(mockLog), eq("web"), any());
        pomUtilMockedStatic.verify(() -> PomUtil.saveMavenProject(fullProject, mockLog));
    }
}
