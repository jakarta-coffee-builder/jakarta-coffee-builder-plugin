/*
 * Copyright 2024 Diego Silva <diego.silva at apuntesdejava.com>.
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

import com.apuntesdejava.jakartacoffeebuilder.util.JsonUtil;
import com.apuntesdejava.jakartacoffeebuilder.util.PathsUtil;
import com.apuntesdejava.jakartacoffeebuilder.util.TemplateUtil;
import jakarta.json.JsonObject;
import jakarta.json.JsonValue;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;

import java.io.IOException;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Diego Silva <diego.silva at apuntesdejava.com>
 */
public class JakartaPersistenceHelper {

    private JakartaPersistenceHelper() {
    }

    public static JakartaPersistenceHelper getInstance() {
        return JakartaPersistenceUtilHolder.INSTANCE;
    }

    public void addEntities(MavenProject mavenProject, Log log, Path jsonPath) throws IOException {
        var jsonContent = JsonUtil.readJsonValue(jsonPath);
        if (jsonContent.getValueType() == JsonValue.ValueType.ARRAY)
            jsonContent.asJsonArray().stream()
                       .map(JsonValue::asJsonObject)
                       .forEach(entity -> addEntity(mavenProject, log, entity));
        else if (jsonContent.getValueType() == JsonValue.ValueType.OBJECT)
            addEntity(mavenProject, log, jsonContent.asJsonObject());
    }

    private void addEntity(MavenProject mavenProject, Log log, JsonObject entity) {
        try {
            var entityName = entity.getString("name");
            log.debug("Adding entity: " + entityName);
            var packageDefinition = MavenProjectHelper.getInstance().getProjectPackage(mavenProject) + ".entity";
            var entityPath = PathsUtil.getJavaPath(mavenProject, "entity", entityName);

            var fieldsJson = entity.getJsonArray("fields");
            var fields = fieldsJson.stream()
                                   .map(JsonValue::asJsonObject)
                                   .map(field -> {
                                       Map<String, Object> item = new LinkedHashMap<>();
                                       item.put("name", field.getString("name"));
                                       item.put("type", field.getString("type"));
                                       if (field.containsKey("length")) {
                                           item.put("length", field.getInt("length"));
                                       }
                                       if (field.containsKey("isId")) {
                                           item.put("isId", field.getBoolean("isId", false));
                                       }
                                       return item;
                                   })
                                   .toList();

            TemplateUtil.getInstance()
                        .createEntityFile(log,
                            Map.of("packageName", packageDefinition, "className", entityName, "fields", fields),
                            entityPath);
        } catch (Exception ex) {
            log.error("Error adding entity: " + entity.getString("name"), ex);
        }
    }

    private static class JakartaPersistenceUtilHolder {

        private static final JakartaPersistenceHelper INSTANCE = new JakartaPersistenceHelper();
    }
}
