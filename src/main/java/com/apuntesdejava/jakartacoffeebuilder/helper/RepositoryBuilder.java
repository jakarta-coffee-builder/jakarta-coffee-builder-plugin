/*
 * Copyright 2025 Diego Silva <diego.silva at apuntesdejava.com>.
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

import java.util.Optional;

import static com.apuntesdejava.jakartacoffeebuilder.util.Constants.NAME;

public interface RepositoryBuilder {

    static RepositoryBuilder getInstance() {
        return new Jakarta11RepositoryBuilderImpl();
    }
    default String getEntityName(JsonObject entity) {
        return entity.getString(NAME);
    }

    default Optional< JsonObject> getFieldId(JsonObject entity) {
        return entity.getJsonArray("fields").stream()
                .map(JsonObject.class::cast)
                .filter(f -> f.getBoolean("isId", false))
                .findFirst();
    }

    void buildRepository(MavenProject mavenProject, Log log, JsonObject entity);
}
