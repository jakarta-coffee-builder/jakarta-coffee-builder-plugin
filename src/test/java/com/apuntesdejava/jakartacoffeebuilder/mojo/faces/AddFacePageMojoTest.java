package com.apuntesdejava.jakartacoffeebuilder.mojo.faces;

import com.apuntesdejava.jakartacoffeebuilder.helper.JakartaFacesHelper;
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

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyBoolean;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AddFacePageMojoTest {

    @Mock
    private MavenProject mavenProject;

    @Mock
    private Log mockLog;

    @InjectMocks
    private AddFacePageMojo mojo;

    private MockedStatic<JakartaFacesHelper> jakartaFacesHelperMockedStatic;

    @Mock
    private JakartaFacesHelper jakartaFacesHelperMock;

    @BeforeEach
    void setUp() throws Exception {
        mojo.setLog(mockLog);

        jakartaFacesHelperMockedStatic = mockStatic(JakartaFacesHelper.class);
        jakartaFacesHelperMockedStatic.when(JakartaFacesHelper::getInstance).thenReturn(jakartaFacesHelperMock);
    }

    private void setField(String fieldName, Object value) throws Exception {
        java.lang.reflect.Field field = AddFacePageMojo.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(mojo, value);
    }

    @AfterEach
    void tearDown() {
        jakartaFacesHelperMockedStatic.close();
    }

    @Test
    @DisplayName("execute: should add face page without template and with managed bean")
    void execute_NoTemplateWithBean_AddsFacePage() throws Exception {
        setField("pageName", "index.xhtml");
        setField("createManagedBean", true);
        setField("templateFacelet", null);

        mojo.execute();

        verify(jakartaFacesHelperMock).addFacePage(mavenProject, mockLog, "index.xhtml", true);
        verify(jakartaFacesHelperMock).createManagedBean(mavenProject, mockLog, "index.xhtml");
        verify(jakartaFacesHelperMock, never()).addFacePageWithFaceletTemplate(any(),
            any(),
            any(),
            any(),
            anyBoolean());
    }

    @Test
    @DisplayName("execute: should add face page with template and without managed bean")
    void execute_WithTemplateNoBean_AddsFacePageWithTemplate() throws Exception {
        setField("pageName", "index.xhtml");
        setField("createManagedBean", false);
        setField("templateFacelet", "template.xhtml");

        mojo.execute();

        verify(jakartaFacesHelperMock).addFacePageWithFaceletTemplate(mavenProject,
            mockLog,
            "index.xhtml",
            "template.xhtml",
            false);
        verify(jakartaFacesHelperMock, never()).addFacePage(any(), any(), any(), anyBoolean());
        verify(jakartaFacesHelperMock, never()).createManagedBean(any(), any(), any());
    }
}
