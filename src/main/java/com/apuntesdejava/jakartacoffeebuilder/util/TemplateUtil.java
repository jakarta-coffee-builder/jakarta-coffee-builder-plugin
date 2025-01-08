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
package com.apuntesdejava.jakartacoffeebuilder.util;

import freemarker.template.Configuration;
import freemarker.template.TemplateException;
import org.apache.maven.plugin.logging.Log;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

/**
 * Utility class for generating files using predefined templates with Freemarker.
 * <p>
 * This singleton class provides methods to process template files with a specified data model.
 * It centralizes template configuration and ensures consistent usage throughout the application.
 * <p>
 * The templates are loaded from a predefined location on the classpath, and the default encoding is set to UTF-8.
 */
public class TemplateUtil {

    private final Configuration configuration;

    private TemplateUtil() {
        this.configuration = new Configuration(Configuration.VERSION_2_3_34);
        configuration.setClassForTemplateLoading(TemplateUtil.class, "/freemaker");
        configuration.setDefaultEncoding("UTF-8");
    }

    public static TemplateUtil getInstance() {
        return TemplateUtilHolder.INSTANCE;
    }

    /**
     * Generates a Java Bean file using a template and writes it to the specified file path.
     *
     * @param log      the logger used to log messages and errors
     * @param data     the data model map containing values to populate the Java Bean template
     * @param javaPath the file path where the generated Java Bean will be written
     * @throws IOException if an I/O error occurs during file writing
     */
    public void createJavaBeanFile(Log log, Map<String, Object> data, Path javaPath) throws IOException {
        createJavaFile(log, data, javaPath, "JavaBean.ftl");
    }

    /**
     * Generates an Entity file using a template and writes it to the specified file path.
     *
     * @param log      the logger used to log messages and errors
     * @param data     the data model map containing values to populate the Entity template
     * @param javaPath the file path where the generated Entity file will be written
     * @throws IOException if an I/O error occurs during file writing
     */
    public void createEntityFile(Log log, Map<String, Object> data, Path javaPath) throws IOException {
        createJavaFile(log, data, javaPath, "Entity.ftl");
    }

    private void createJavaFile(Log log,
                                Map<String, Object> data,
                                Path javaPath,
                                String templateName) throws IOException {
        var template = configuration.getTemplate(templateName);
        try (var writer = new FileWriter(javaPath.toFile())) {
            template.process(data, writer);
        } catch (TemplateException e) {
            log.error(e.getMessage(), e);
        }
    }

    private static class TemplateUtilHolder {

        private static final TemplateUtil INSTANCE = new TemplateUtil();
    }
}
