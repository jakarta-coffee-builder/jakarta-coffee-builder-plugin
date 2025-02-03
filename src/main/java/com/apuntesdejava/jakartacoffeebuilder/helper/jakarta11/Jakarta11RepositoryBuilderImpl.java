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
package com.apuntesdejava.jakartacoffeebuilder.helper.jakarta11;

import com.apuntesdejava.jakartacoffeebuilder.helper.MavenProjectHelper;
import com.apuntesdejava.jakartacoffeebuilder.helper.RepositoryBuilder;
import com.apuntesdejava.jakartacoffeebuilder.util.PathsUtil;
import com.apuntesdejava.jakartacoffeebuilder.util.TemplateUtil;
import jakarta.json.JsonObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;

import java.io.IOException;
import java.util.Map;

import static com.apuntesdejava.jakartacoffeebuilder.util.Constants.CLASS_NAME;
import static com.apuntesdejava.jakartacoffeebuilder.util.Constants.PACKAGE_NAME;

public class Jakarta11RepositoryBuilderImpl implements RepositoryBuilder {
    @Override
    public void buildRepository(MavenProject mavenProject, Log log, JsonObject entity) {
        var entityName = getEntityName(entity);
        try {
            log.info("Building Jakarta 11 Repository for entity: " + entityName);
            var packageDefinition = MavenProjectHelper.getInstance().getRepositoryPackage(mavenProject);
            var packageEntity = MavenProjectHelper.getInstance().getEntityPackage(mavenProject);
            var className = entityName + "Repository";
            var fieldId = getFieldId(entity);
            var repositoryPath = PathsUtil.getJavaPath(mavenProject, packageDefinition, className);
            var classRepository = StringUtils.capitalize(entity.getString("repository", "crud"));

            TemplateUtil.getInstance()
                        .createRepositoryFile(log, Map.of(
                            PACKAGE_NAME, packageDefinition,
                            CLASS_NAME, className,
                            "entityName", entityName,
                            "classRepository", classRepository,
                            "packageEntity", packageEntity,
                            "idType", fieldId.map(f -> f.getString("type")).orElse("Long")
                        ), repositoryPath);
        } catch (IOException e) {
            log.error("Error building Jakarta 11 Repository for entity: " + entityName, e);
        }


    }
}
