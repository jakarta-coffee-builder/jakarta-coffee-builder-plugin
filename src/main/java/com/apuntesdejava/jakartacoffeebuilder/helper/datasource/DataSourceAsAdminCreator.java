package com.apuntesdejava.jakartacoffeebuilder.helper.datasource;

import java.io.IOException;

import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;

public class DataSourceAsAdminCreator extends DataSourceCreator {

  public DataSourceAsAdminCreator(MavenProject mavenProject, Log log) {
    super(mavenProject, log);
  }

  @Override
  public void build() throws IOException {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'build'");
  }

}
