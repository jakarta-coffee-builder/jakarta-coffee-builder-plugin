package com.apuntesdejava.jakartacoffeebuilder.helper.datasource;

import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;

public class DataSourceWebCreator extends DataSourceCreator {

    public DataSourceWebCreator(MavenProject mavenProject, Log log) {
        super(mavenProject,log);
    }

    @Override
    public void build() {

    }
}
