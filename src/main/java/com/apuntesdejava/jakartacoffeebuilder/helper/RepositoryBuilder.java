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
import jakarta.json.JsonValue;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;

import java.util.Optional;

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
     * Retrieves the field marked as the identifier (ID) from the entity definition.
     *
     * @param entity the JSON object representing the entity
     * @return an {@link Optional} containing the JSON object of the ID field, or empty if not found
     */
    default Optional<JsonObject> getFieldId(JsonObject entity) {
        return entity.getJsonObject("fields")
                     .values().stream()
                     .map(JsonValue::asJsonObject)
                     .filter(val -> val.containsKey("isId")
                         && val.get("isId").getValueType() == JsonValue.ValueType.TRUE)
                     .findFirst();
    }

    /**
     * Builds the repository class for the specified entity.
     *
     * @param mavenProject the Maven project instance
     * @param log          the logger for logging messages
     * @param entityName
     * @param entity       the JSON object representing the entity
     */
    void buildRepository(MavenProject mavenProject, Log log, String entityName, JsonObject entity);
}
