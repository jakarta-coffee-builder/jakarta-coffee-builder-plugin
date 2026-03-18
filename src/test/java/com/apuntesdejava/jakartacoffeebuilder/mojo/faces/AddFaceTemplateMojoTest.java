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

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AddFaceTemplateMojoTest {

    @Mock
    private MavenProject mavenProject;

    @Mock
    private Log mockLog;

    @InjectMocks
    private AddFaceTemplateMojo mojo;

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
        java.lang.reflect.Field field = AddFaceTemplateMojo.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(mojo, value);
    }

    @AfterEach
    void tearDown() {
        jakartaFacesHelperMockedStatic.close();
    }

    @Test
    @DisplayName("execute: should add face template successfully")
    void execute_ValidTemplate_AddsTemplate() throws Exception {
        setField("templateName", "myTemplate");
        List<String> inserts = Arrays.asList("header", "footer");
        setField("inserts", inserts);

        mojo.execute();

        verify(jakartaFacesHelperMock).addFaceTemplate(mavenProject, mockLog, "myTemplate", inserts);
    }
}
