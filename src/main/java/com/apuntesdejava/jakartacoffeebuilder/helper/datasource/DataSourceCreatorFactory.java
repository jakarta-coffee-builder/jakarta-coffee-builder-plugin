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

import com.apuntesdejava.jakartacoffeebuilder.util.Constants;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;

import java.util.Optional;

/**
 * Factory class for creating instances of DataSourceCreator.
 */
public class DataSourceCreatorFactory {
    private DataSourceCreatorFactory() {
    }

    /**
     * Returns an Optional containing a DataSourceCreator based on the given
     * declaration.
     *
     * @param mavenProject the Maven project
     * @param log          the logger
     * @param declare      the type of data source declaration
     * @return an Optional containing a DataSourceCreator if the declaration
     *         matches, otherwise an empty Optional
     */
    public static Optional<DataSourceCreator> getDataSourceCreator(MavenProject mavenProject, Log log, String declare) {
        return switch (declare) {
            case Constants.DATASOURCE_DECLARE_WEB -> Optional.of(new DataSourceWebCreator(mavenProject, log));
            case Constants.DATASOURCE_DECLARE_CLASS -> Optional.of(new DataSourceClassCreator(mavenProject, log));
            case Constants.DATASOURCE_DECLARE_ASADMIN -> Optional.of(new DataSourceAsAdminCreator(mavenProject, log));
            default -> Optional.empty();
        };
    }
}
