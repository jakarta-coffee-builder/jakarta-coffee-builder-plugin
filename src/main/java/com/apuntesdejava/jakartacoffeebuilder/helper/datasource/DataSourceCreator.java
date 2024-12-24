package com.apuntesdejava.jakartacoffeebuilder.helper.datasource;

import jakarta.json.JsonObject;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;

import java.io.IOException;

public abstract class DataSourceCreator {
    protected final MavenProject mavenProject;
    protected final Log log;
    protected String coordinatesJdbcDriver;
    protected String persistenceUnit;
    protected JsonObject dataSourceParameters;

    public DataSourceCreator(MavenProject mavenProject, Log log) {
        this.log = log;
        this.mavenProject = mavenProject;
    }

    public DataSourceCreator coordinatesJdbcDriver(String coordinatesJdbcDriver) {
        this.coordinatesJdbcDriver = coordinatesJdbcDriver;
        return this;
    }

    public DataSourceCreator persistenceUnit(String persistenceUnit) {
        this.persistenceUnit = persistenceUnit;
        return this;
    }

    public DataSourceCreator dataSourceParameters(JsonObject dataSourceParameters) {
        this.dataSourceParameters = dataSourceParameters;
        return this;
    }

    public abstract void build() throws IOException;
}
