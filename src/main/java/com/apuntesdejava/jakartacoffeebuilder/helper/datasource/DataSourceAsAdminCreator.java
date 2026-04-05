package com.apuntesdejava.jakartacoffeebuilder.helper.datasource;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;
import org.apache.maven.model.ConfigurationContainer;
import org.apache.maven.model.Profile;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.xml.Xpp3Dom;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Objects;
import java.util.Set;

import static com.apuntesdejava.jakartacoffeebuilder.util.Constants.CLASS_NAME;
import static com.apuntesdejava.jakartacoffeebuilder.util.Constants.NAME;

public class DataSourceAsAdminCreator extends DataSourceCreator {

    public DataSourceAsAdminCreator(MavenProject mavenProject, Log log, String profile) {
        super(mavenProject, log, profile);
    }

    @Override
    public void build() throws IOException, MojoExecutionException {
        var currentPath = mavenProject.getFile().toPath().getParent();
        var postBootPath = currentPath.resolve("post-boot-commands.asadmin");
        createPostBootPath(postBootPath);
        insertOption();
    }

    private void insertOption() throws MojoExecutionException {
        var model = mavenProject.getOriginalModel();
        var build = Objects.isNull(profile)
                ? model.getBuild()
                : model.getProfiles().stream()
                  .filter(p -> p.getId().equals(profile))
                  .findFirst()
                  .map(Profile::getBuild)
                  .orElseThrow();
        var config = build.getPlugins()
                .stream()
                .filter(p ->
                        Strings.CI.equals(p.getGroupId(), "fish.payara.maven.plugins")
                                && Strings.CI.equals(p.getArtifactId(), "payara-micro-maven-plugin")
                ).map(ConfigurationContainer::getConfiguration)
                .map(Xpp3Dom.class::cast)
                .findFirst()
                .orElseThrow();
        var commandLineOptions = config.getChild("commandLineOptions");
        var options = commandLineOptions.getChildren("option");
        if (options == null)
            throw new MojoExecutionException("commandLineOptions must have at least one option child");
        var notFound = Arrays.stream(options)
                .filter(option -> {
                    var key = option.getChild("key");
                    if (key == null)
                        return false;
                    return key.getValue().equals("--postbootcommandfile");
                }).findFirst()
                .isEmpty();
        if (notFound) {
            var newOption = new Xpp3Dom("option");
            var keyTag = new Xpp3Dom("key");
            keyTag.setValue("--postbootcommandfile");

            var valueTag = new Xpp3Dom("value");
            valueTag.setValue("post-boot-commands.asadmin");

            newOption.addChild(keyTag);
            newOption.addChild(valueTag);
            commandLineOptions.addChild(newOption);
        }
    }

    private void createPostBootPath(Path postBootPath) throws IOException {
        log.debug("Creating %s file".formatted(postBootPath));
        var properties = getDataSourceParameters();
        var commands = new StringBuilder();
        commands.append("create-jdbc-connection-pool ");
        commands.append("--restype javax.sql.DataSource ");
        commands.append("--datasourceclassname %s ".formatted(properties.get(CLASS_NAME)));
        commands.append("--property \"");
        properties.entrySet()
                .stream()
                .filter(entry -> !Set.of(CLASS_NAME, NAME).contains(entry.getKey()))
                .forEach((entry) -> commands.append(entry.getKey())
                        .append("=")
                        .append(replaceSpecialCharacters((String) entry.getValue()))
                        .append(":"));
        commands.deleteCharAt(commands.length() - 1);
        commands.append("\" ");
        var name = String.valueOf(getDataSourceParameters().get(NAME));
        var dataSourceName = StringUtils.substringAfterLast(name, "/") + "Pool";
        commands.append(dataSourceName);
        commands.append(System.lineSeparator());

        commands.append("create-jdbc-resource ");
        commands.append("--connectionpoolid %s ".formatted(dataSourceName));
        commands.append(name);

        Files.writeString(postBootPath, commands.toString());
    }

    private static String replaceSpecialCharacters(String value) {
        String[] searchList = {":", "=", ";"};
        String[] replaceList = {"\\:", "\\=", "\\;"};
        return StringUtils.replaceEach(value, searchList, replaceList);
    }
}
