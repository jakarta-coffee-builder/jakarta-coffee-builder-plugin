package com.apuntesdejava.jakartacoffeebuilder.helper.datasource;

import org.apache.commons.lang3.Strings;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Set;

import static com.apuntesdejava.jakartacoffeebuilder.util.Constants.CLASS_NAME;
import static com.apuntesdejava.jakartacoffeebuilder.util.Constants.NAME;

public class DataSourceAsAdminCreator extends DataSourceCreator {

    public DataSourceAsAdminCreator(MavenProject mavenProject, Log log) {
        super(mavenProject, log);
    }

    @Override
    public void build() throws IOException {
        var currentPath = mavenProject.getFile().toPath().getParent();
        var postBootPath = currentPath.resolve("post-boot-commands.asadmin");
        log.debug("Creating %s file".formatted(postBootPath));
        var properties = getDataSourceParameters();
        var commands = new StringBuilder();
        commands.append("create-jdbc-connection-pool  ");
        commands.append("--restype javax.sql.DataSource ");
        commands.append("--datasourceclassname %s ".formatted(properties.get(CLASS_NAME)));
        commands.append("--property \"");
        properties.entrySet()
            .stream()
            .filter(entry -> !Set.of(CLASS_NAME, NAME).contains(entry.getKey()))
            .forEach((entry) -> commands.append(entry.getKey())
                .append("=")
                .append(replaceColons((String) entry.getValue()))
                .append(":"));
        commands.deleteCharAt(commands.length() - 1);
        commands.append("\" ");

        Files.writeString(postBootPath, commands.toString());

    }

    private static String replaceColons(String value) {
        return Strings.CS.replace(value, ":", "\\\\:");
    }
}
