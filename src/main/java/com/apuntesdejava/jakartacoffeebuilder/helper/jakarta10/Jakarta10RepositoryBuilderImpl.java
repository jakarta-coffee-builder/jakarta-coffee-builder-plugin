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
import com.apuntesdejava.jakartacoffeebuilder.util.MavenProjectUtil;
import com.apuntesdejava.jakartacoffeebuilder.util.PathsUtil;
import com.apuntesdejava.jakartacoffeebuilder.util.TemplateUtil;
import jakarta.json.JsonObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import static com.apuntesdejava.jakartacoffeebuilder.util.CoffeeBuilderUtil.getFieldIdClass;
import static com.apuntesdejava.jakartacoffeebuilder.util.Constants.CLASS_NAME;
import static com.apuntesdejava.jakartacoffeebuilder.util.Constants.PACKAGE_NAME;


/**
 * An implementation of {@link RepositoryBuilder} for Jakarta EE 10.
 * <p>
 * This class is responsible for generating the repository layer for a given entity,
 * based on its JSON definition. It handles the specific requirements and APIs
 * available in Jakarta EE 10.
 *
 * @author Diego Silva diego.silva at apuntesdejava.com
 */
public class Jakarta10RepositoryBuilderImpl implements RepositoryBuilder {

    /**
     * Constructs a new instance of {@code Jakarta10RepositoryBuilderImpl}.
     */
    public Jakarta10RepositoryBuilderImpl() {
    }

    /**
     * Builds a repository based on the provided entity definition for a Jakarta EE 10 project.
     *
     * @param mavenProject The Maven project context, used to access project details like base directory.
     * @param log          A logger for outputting information or errors during the build process.
     * @param entityName   The name of the entity for which to create the repository.
     * @param entity       The JSON object containing the detailed definition of the entity.
     * @param imports      A collection of fully qualified class names to be added as import statements
     *                     in the generated repository file.
     */
    @Override
    public void buildRepository(MavenProject mavenProject, Log log, String entityName, JsonObject entity, Collection<String> imports) {

        try {
            log.info("Building Jakarta 10 Repository for entity: " + entityName);
            var packageDefinition = MavenProjectUtil.getRepositoryPackage(mavenProject);
            var packageEntity = MavenProjectUtil.getEntityPackage(mavenProject);
            var className = entityName + "Repository";
            log.debug("entity:" + entity);
            var repositoryPath = PathsUtil.getJavaPath(mavenProject, packageDefinition, className);
            var classRepository = StringUtils.capitalize(entity.getString("repository", "crud"));

            TemplateUtil.getInstance()
                    .createRepositoryFile(log, Map.of(
                        PACKAGE_NAME, packageDefinition,
                        CLASS_NAME, className,
                        "entityName", entityName,
                        "classRepository", classRepository,
                        "packageEntity", packageEntity,
                        "importsList", imports,
                        "idType", getFieldIdClass(entity, "Long")
                    ), repositoryPath);
        } catch (IOException e) {
            log.error("Error building Jakarta 10 Repository for entity: " + entityName, e);
        }
    }
}
