package com.apuntesdejava.jakartacoffeebuilder.helper.datasource;

import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static com.apuntesdejava.jakartacoffeebuilder.util.Constants.DATASOURCE_DECLARE_ASADMIN;
import static com.apuntesdejava.jakartacoffeebuilder.util.Constants.DATASOURCE_DECLARE_CLASS;
import static com.apuntesdejava.jakartacoffeebuilder.util.Constants.DATASOURCE_DECLARE_WEB;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

class DataSourceCreatorFactoryTest {

    private final MavenProject mockProject = mock(MavenProject.class);
    private final Log mockLog = mock(Log.class);

    @Test
    @DisplayName("should return DataSourceWebCreator for web.xml declaration")
    void returnsWebCreatorForWebXml() {
        Optional<DataSourceCreator> creator = DataSourceCreatorFactory.getDataSourceCreator(
            mockProject, mockLog, DATASOURCE_DECLARE_WEB);
        assertTrue(creator.isPresent());
        assertInstanceOf(DataSourceWebCreator.class, creator.get());
    }

    @Test
    @DisplayName("should return DataSourceClassCreator for class declaration")
    void returnsClassCreatorForClass() {
        Optional<DataSourceCreator> creator = DataSourceCreatorFactory.getDataSourceCreator(
            mockProject, mockLog, DATASOURCE_DECLARE_CLASS);
        assertTrue(creator.isPresent());
        assertInstanceOf(DataSourceClassCreator.class, creator.get());
    }

    @Test
    @DisplayName("should return DataSourceAsAdminCreator for asadmin declaration")
    void returnsAsAdminCreatorForAsadmin() {
        Optional<DataSourceCreator> creator = DataSourceCreatorFactory.getDataSourceCreator(
            mockProject, mockLog, DATASOURCE_DECLARE_ASADMIN);
        assertTrue(creator.isPresent());
        assertInstanceOf(DataSourceAsAdminCreator.class, creator.get());
    }

    @Test
    @DisplayName("should return empty Optional for unknown declaration")
    void returnsEmptyForUnknown() {
        Optional<DataSourceCreator> creator = DataSourceCreatorFactory.getDataSourceCreator(
            mockProject, mockLog, "unknown");
        assertTrue(creator.isEmpty());
    }
}
