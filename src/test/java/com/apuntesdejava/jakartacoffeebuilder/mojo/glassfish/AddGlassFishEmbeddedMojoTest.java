package com.apuntesdejava.jakartacoffeebuilder.mojo.glassfish;

import com.apuntesdejava.jakartacoffeebuilder.helper.GlassFishHelper;
import com.apuntesdejava.jakartacoffeebuilder.util.PomUtil;
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

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AddGlassFishEmbeddedMojoTest {

    @Mock
    private MavenProject mavenProject;

    @Mock
    private Log mockLog;

    @InjectMocks
    private AddGlassFishEmbeddedMojo mojo;

    private MockedStatic<PomUtil> pomUtilMockedStatic;
    private MockedStatic<GlassFishHelper> glassFishHelperMockedStatic;

    @Mock
    private GlassFishHelper glassFishHelperMock;

    @BeforeEach
    void setUp() throws Exception {
        mojo.setLog(mockLog);

        pomUtilMockedStatic = mockStatic(PomUtil.class);
        glassFishHelperMockedStatic = mockStatic(GlassFishHelper.class);

        glassFishHelperMockedStatic.when(GlassFishHelper::getInstance).thenReturn(glassFishHelperMock);

        // Inject private fields that would normally be injected by Maven
        java.lang.reflect.Field profileIdField = AddGlassFishEmbeddedMojo.class.getDeclaredField("profileId");
        profileIdField.setAccessible(true);
        profileIdField.set(mojo, "glassfish");

        java.lang.reflect.Field portField = AddGlassFishEmbeddedMojo.class.getDeclaredField("port");
        portField.setAccessible(true);
        portField.set(mojo, 8080);

        java.lang.reflect.Field contextRootField = AddGlassFishEmbeddedMojo.class.getDeclaredField("contextRoot");
        contextRootField.setAccessible(true);
        contextRootField.set(mojo, "my-app");
    }

    @AfterEach
    void tearDown() {
        pomUtilMockedStatic.close();
        glassFishHelperMockedStatic.close();
    }

    @Test
    @DisplayName("execute: should add GlassFish embedded plugin successfully")
    void execute_ValidProject_AddsPlugin() throws Exception {
        doNothing().when(glassFishHelperMock).addPlugin(mavenProject, mockLog, "glassfish", 8080, "my-app");
        pomUtilMockedStatic.when(() -> PomUtil.saveMavenProject(mavenProject, mockLog)).thenAnswer(inv -> null);

        mojo.execute();

        verify(glassFishHelperMock).addPlugin(mavenProject, mockLog, "glassfish", 8080, "my-app");
        pomUtilMockedStatic.verify(() -> PomUtil.saveMavenProject(mavenProject, mockLog));
    }
}
