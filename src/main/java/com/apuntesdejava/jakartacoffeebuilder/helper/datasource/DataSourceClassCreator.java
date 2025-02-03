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
package com.apuntesdejava.jakartacoffeebuilder.helper.datasource;

import com.apuntesdejava.jakartacoffeebuilder.helper.MavenProjectHelper;
import com.apuntesdejava.jakartacoffeebuilder.util.PathsUtil;
import com.apuntesdejava.jakartacoffeebuilder.util.TemplateUtil;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;

import java.io.IOException;
import java.util.Map;

/**
 * This class is responsible for creating the DataSource class.
 */
public class DataSourceClassCreator extends DataSourceCreator {
    public DataSourceClassCreator(MavenProject mavenProject, Log log) {
        super(mavenProject, log);
    }

    @Override
    public void build() throws IOException {
        var packageDefinition = MavenProjectHelper.getInstance().getProviderPackage(mavenProject);
        var className = "DataSourceProvider";
        var dataSourceClassPath = PathsUtil.getJavaPath(mavenProject, packageDefinition, className);
        var properties = getDataSourceParameters();
        var annotationClasses = Map.of(
            "jakarta.annotation.sql.DataSourceDefinition", properties
        );
        TemplateUtil.getInstance().createJavaBeanFile(log,
            Map.of("packageName", packageDefinition,
                "className", className,
                "annotations", annotationClasses), dataSourceClassPath);
    }
}
