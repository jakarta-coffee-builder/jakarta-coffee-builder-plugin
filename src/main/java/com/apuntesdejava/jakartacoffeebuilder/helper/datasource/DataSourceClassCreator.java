package com.apuntesdejava.jakartacoffeebuilder.helper.datasource;

import com.apuntesdejava.jakartacoffeebuilder.helper.MavenProjectHelper;
import com.apuntesdejava.jakartacoffeebuilder.util.PathsUtil;
import com.apuntesdejava.jakartacoffeebuilder.util.TemplateUtil;
import jakarta.json.JsonNumber;
import jakarta.json.JsonString;
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
        Optional.ofNullable(dataSourceParameters).ifPresent(parameters -> {
            parameters.forEach((key, value) -> {
                if (value != null) {
                    properties.put(key, switch (value.getValueType()) {
                        case STRING -> ((JsonString) value).getString();
                        case NUMBER -> ((JsonNumber) value).intValue();
                        case ARRAY -> value.asJsonArray()
                                           .stream()
                                           .map(JsonString.class::cast)
                                           .map(JsonString::getString)
                                           .toArray(String[]::new);
                        default -> value;
                    });

                }
            });
        });
        var annotationClasses = Map.of(
            "jakarta.annotation.sql.DataSourceDefinition", properties
        );
        TemplateUtil.getInstance().createJavaBeanFile(log,
            Map.of("packageName", packageDefinition,
                "className", className,
                "annotations", annotationClasses), dataSourceClassPath);
    }
}
