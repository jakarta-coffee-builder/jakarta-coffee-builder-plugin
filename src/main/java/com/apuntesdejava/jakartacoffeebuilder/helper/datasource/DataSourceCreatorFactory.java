package com.apuntesdejava.jakartacoffeebuilder.helper.datasource;

import com.apuntesdejava.jakartacoffeebuilder.util.Constants;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;

import java.util.Optional;

public class DataSourceCreatorFactory {
    private DataSourceCreatorFactory() {
    }

    public static Optional<DataSourceCreator> getDataSourceCreator(MavenProject mavenProject, Log log, String declare) {
        if (declare.equals(Constants.DATASOURCE_DECLARE_WEB)) {
            return Optional.of(new DataSourceWebCreator(mavenProject, log));
        }
        if (declare.equals(Constants.DATASOURCE_DECLARE_CLASS)) {
            return Optional.of(new DataSourceClassCreator(mavenProject, log));
        }
        return Optional.empty();
    }
}
