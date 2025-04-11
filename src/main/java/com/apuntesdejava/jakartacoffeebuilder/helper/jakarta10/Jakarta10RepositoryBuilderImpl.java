/*
 * Copyright 2025 Diego Silva diego.silva at apuntesdejava.com.
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
package com.apuntesdejava.jakartacoffeebuilder.helper.jakarta10;

import com.apuntesdejava.jakartacoffeebuilder.helper.RepositoryBuilder;
import jakarta.json.JsonObject;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;

/**
 * Jakarta 10 Repository Builder implementation.
 * This class provides the logic to build a repository based on an entity
 * defined in a JSON object.
 *
 * <p>Implements the {@link RepositoryBuilder} interface.</p>
 *
 * <p>Usage example:</p>
 * <pre>
 * {@code
 * Jakarta10RepositoryBuilderImpl builder = new Jakarta10RepositoryBuilderImpl();
 * builder.buildRepository(mavenProject, log, entityJson);
 * }
 * </pre>
 *
 * @author Diego Silva diego.silva at apuntesdejava.com
 */
public class Jakarta10RepositoryBuilderImpl implements RepositoryBuilder {

    /**
     * Default constructor for Jakarta10RepositoryBuilderImpl.<br>
     * Initializes a new instance of the Jakarta10RepositoryBuilderImpl class.
     */
    public Jakarta10RepositoryBuilderImpl() {
    }

    /**
     * Builds a repository for a specific entity using Jakarta 10.
     *
     * @param mavenProject The Maven project being worked on.
     * @param log          The logger object to log messages during the build process.
     * @param entity       The JSON object representing the entity for which the repository will be built.
     */
    @Override
    public void buildRepository(MavenProject mavenProject, Log log, JsonObject entity) {
        var entityName = getEntityName(entity);

        log.info("Building Jakarta 10 Repository for entity: " + entityName);
    }
}
