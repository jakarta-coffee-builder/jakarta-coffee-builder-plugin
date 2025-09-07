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
package com.apuntesdejava.jakartacoffeebuilder.helper;

import com.apuntesdejava.jakartacoffeebuilder.helper.jakarta11.Jakarta11RepositoryBuilderImpl;
import jakarta.json.JsonObject;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;

import java.util.Collection;

/**
 * Interface for building repository classes.
 * <p>
 * This interface defines methods for creating repository classes based on entity definitions.
 * It includes default methods for retrieving entity names and identifying fields, as well as
 * an abstract method for building the repository.
 * </p>
 */
public interface RepositoryBuilder {


    /**
     * Obtains an instance of the default implementation of the RepositoryBuilder.
     *
     * @return an instance of {@link Jakarta11RepositoryBuilderImpl}
     */
    static RepositoryBuilder getInstance() {
        return new Jakarta11RepositoryBuilderImpl();
    }


    /**
     * Builds the repository class for the specified entity.
     *
     * @param mavenProject      the Maven project instance
     * @param log               the logger for logging messages
     * @param entityName        the name of the entity
     * @param entity            the JSON object representing the entity
     * @param additionalImports a collection of additional imports to be added to the repository class
     */
    void buildRepository(MavenProject mavenProject, Log log, String entityName, JsonObject entity, Collection<String> additionalImports);
}
