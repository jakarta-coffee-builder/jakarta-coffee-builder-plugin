package com.apuntesdejava.jakartacoffeebuilder.helper.datasource;

import com.apuntesdejava.jakartacoffeebuilder.helper.MavenProjectHelper;
import com.apuntesdejava.jakartacoffeebuilder.util.PathsUtil;
import com.apuntesdejava.jakartacoffeebuilder.util.TemplateUtil;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public class DataSourceClassCreator extends DataSourceCreator {
    public DataSourceClassCreator(MavenProject mavenProject, Log log) {
        super(mavenProject, log);
    }

    @Override
    public void build() throws IOException {
        var packageDefinition = MavenProjectHelper.getInstance().getProjectPackage(mavenProject) + ".provider";
        var className = "DataSourceProvider";
        var dataSourceClassPath = PathsUtil.getJavaPath(mavenProject, "provider", className);
        Map<String, Object> properties = new LinkedHashMap<>();
        Optional.ofNullable(dataSourceParameters).ifPresent(properties::putAll);
        var annotationClasses = Map.of(
            "jakarta.annotation.sql.DataSourceDefinition", properties
        );
        TemplateUtil.getInstance().createJavaBeanFile(log,
            Map.of("packageName", packageDefinition,
                "className", className,
                "annotations", annotationClasses), dataSourceClassPath);
    }
}
