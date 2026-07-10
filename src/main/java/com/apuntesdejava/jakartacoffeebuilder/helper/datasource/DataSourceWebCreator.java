/*
 * Copyright 2024 Diego Silva diego.silva at apuntesdejava.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.apuntesdejava.jakartacoffeebuilder.helper.datasource;

import com.apuntesdejava.jakartacoffeebuilder.util.WebXmlUtil;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;

import java.io.IOException;

/**
 * Class responsible for creating the DataSource in a web context.
 * <p>
 * This class extends {@link DataSourceCreator} and provides the implementation
 * for adding the DataSource configuration to the `web.xml` file of a Jakarta EE project.
 * It uses the {@link WebXmlUtil} utility to handle operations related to the `web.xml` file.
 * </p>
 */
public class DataSourceWebCreator extends DataSourceCreator {


    /**
     * Creates a DataSourceWebCreator instance.
     *
     * @param mavenProject the current Maven project
     * @param log          the logger used to record messages
     */
    public DataSourceWebCreator(MavenProject mavenProject, Log log) {
        super(mavenProject, log);
    }

    /**
     * Builds and configures the DataSource in the `web.xml` file.
     * <p>
     * This method checks whether the `web.xml` file exists in the current project,
     * obtains the DataSource parameters, and adds them to the file. Finally, it saves
     * the changes made to the `web.xml` file.
     * </p>
     */
    @Override
    public void build() throws IOException {
        var webXmlUtil = WebXmlUtil.getInstance();

        webXmlUtil.checkExistsFile(mavenProject, log)
                  .ifPresent(document -> {
                      var properties = getDataSourceParameters();
                      webXmlUtil.addDataSource(document, log, properties);
                      webXmlUtil.saveDocument(mavenProject, document, log);
                  });
    }
}
