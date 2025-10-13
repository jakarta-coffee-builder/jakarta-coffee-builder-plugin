/*
 * Copyright 2024 Diego Silva diego.silva at apuntesdejava.com.
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

/**
 * <p>Utility class for generating files using predefined templates with Freemarker.</p>
 * <p>This singleton class provides methods to process template files with a specified data model.
 * It centralizes template configuration and ensures consistent usage throughout the application.</p>
 *
 * <p>
 * The templates are loaded from a predefined location on the classpath, and the default encoding is set to UTF-8.
 */
public class TemplateUtil {
    /**
     * Provides access to the singleton instance of the `TemplateUtil` class.
     *
     * @return the singleton instance of `TemplateUtil`
     */
    public static TemplateUtil getInstance() {
        return TemplateUtilHolder.INSTANCE;
    }

    private final Configuration configuration;

    private TemplateUtil() {
        // Set the Freemarker configuration version.
        // Using the latest stable version is recommended for new projects.
        this.configuration = new Configuration(Configuration.VERSION_2_3_32);
        // Load templates from the "/freemaker" directory within the classpath.
        configuration.setClassForTemplateLoading(TemplateUtil.class, "/freemaker");
        configuration.setDefaultEncoding("UTF-8");
    }


    /**
     * <p>Generates a Java Bean file using a template and writes it to the specified file path.</p>
     *
     * @param log The logger used to log messages and errors.
     * @param data The data model map containing values to populate the Java Bean template.
     * @param javaPath The file path where the generated Java Bean will be written.
     * @throws IOException If an I/O error occurs during file writing.
     */
    public void createJavaBeanFile(Log log, Map<String, Object> data, Path javaPath) throws IOException {
        createJavaFile(log, data, javaPath, "java/JavaBean.ftl");
    }

    /**
     * Generates an Entity file using a template and writes it to the specified file path.
     *
     * @param log The logger used to log messages and errors.
     * @param data The data model map containing values to populate the Entity template.
     * @param javaPath The file path where the generated Entity file will be written.
     * @throws IOException If an I/O error occurs during file writing.
     */
    public void createEntityFile(Log log, Map<String, Object> data, Path javaPath) throws IOException {
        createJavaFile(log, data, javaPath, "java/Entity.ftl");
    }

    /**
     * Generates a Repository file using a template and writes it to the specified file path.
     *
     * @param log The logger used to log messages and errors.
     * @param data The data model map containing values to populate the Repository template.
     * @param javaPath The file path where the generated Repository file will be written.
     * @throws IOException If an I/O error occurs during file writing.
     */
    public void createRepositoryFile(Log log, Map<String, Object> data, Path javaPath) throws IOException {
        createJavaFile(log, data, javaPath, "java/Repository.ftl");

    }

    /**
     * Generates a Java file using a specified template and writes it to the given path.
     * <p>
     * This is a generic method used by other `create*File` methods to handle the common logic
     * of loading a Freemarker template, processing it with provided data, and writing the output to a file.</p>
     * @param log The logger for reporting errors.
     * @param data The data model to be used by the Freemarker template.
     * @param javaPath The path where the generated Java file will be written.
     * @param templateName The name of the Freemarker template to use (e.g., "java/JavaBean.ftl").
     * @throws IOException If an I/O error occurs during file operations.
     */
    private void createJavaFile(Log log,
                                Map<String, Object> data,
                                Path javaPath,
                                String templateName) throws IOException {
        var template = configuration.getTemplate(templateName);
        Files.createDirectories(javaPath.getParent());
        try (var writer = new FileWriter(javaPath.toFile())) {
            template.process(data, writer);
        } catch (TemplateException e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * Generates an Enum file using a template and writes it to the specified file path.
     *
     * @param log The logger used to log messages and errors.
     * @param data The data model map containing values to populate the Enum template.
     * @param enumPath The file path where the generated Enum file will be written.
     * @throws IOException If an I/O error occurs during file writing.
     */
    public void createEnumFile(Log log, Map<String, Object> data, Path enumPath) throws IOException {
        createJavaFile(log, data, enumPath, "java/Enum.ftl");
    }

    /**
     * Generates a Mapper file using a template and writes it to the specified file path.
     *
     * @param log The logger used to log messages and errors.
     * @param data The data model map containing values to populate the Mapper template.
     * @param mapperPath The file path where the generated Mapper file will be written.
     * @throws IOException If an I/O error occurs during file writing.
     */
    public void createMapperFile(Log log, Map<String, Object> data, Path mapperPath) throws IOException {
        createJavaFile(log, data, mapperPath, "java/Mapper.ftl");
    }

    /**
     * Generates a Service file using a template and writes it to the specified file path.
     *
     * @param log The logger used to log messages and errors.
     * @param data The data model map containing values to populate the Service template.
     * @param servicePath The file path where the generated Service file will be written.
     * @throws IOException If an I/O error occurs during file writing.
     */
    public void createServiceFile(Log log, Map<String, Object> data, Path servicePath) throws IOException {
        createJavaFile(log, data, servicePath, "java/Service.ftl");
    }

    public void createModelRepositoryFile(Log log, Map<String, Object> data, Path servicePath) throws IOException {
        createJavaFile(log, data, servicePath, "java/ModelRepository.ftl");
    }


    /**
     * Generates a Plain Old Java Object (POJO) file using a template and writes it to the specified file path.
     *
     * @param log The logger used to log messages and errors.
     * @param data The data model map containing values to populate the POJO template.
     * @param pojoPath The file path where the generated POJO file will be written.
     * @throws IOException If an I/O error occurs during file writing.
     */
    public void createPojoFile(Log log, Map<String, Object> data, Path pojoPath) throws IOException {
        createJavaFile(log, data, pojoPath, "java/Pojo.ftl");
    }

    /**
     * Generates a JavaServer Faces (JSF) template file (XHTML) using a template and writes it to the specified file path.
     *
     * @param log The logger used to log messages and errors.
     * @param data The data model map containing values to populate the JSF template.
     * @param xhtmlPath The file path where the generated XHTML file will be written.
     * @throws IOException If an I/O error occurs during file writing.
     */
    public void createFacesTemplateFile(Log log, Map<String, Object> data, Path xhtmlPath) throws IOException {
        createJavaFile(log, data, xhtmlPath, "xhtml/template.ftl");
    }

    /**
     * Generates a JavaServer Faces (JSF) CRUD (Create, Read, Update, Delete) file (XHTML) using a template and writes it to the specified file path.
     *
     * <p>This method is specifically for PrimeFaces-based CRUD templates.</p>
     * @param log The logger used to log messages and errors.
     * @param data The data model map containing values to populate the JSF CRUD template.
     * @param xhtmlPath The file path where the generated XHTML file will be written.
     * @throws IOException If an I/O error occurs during file writing.
     */
    public void createFacesCrudFile(Log log, Map<String, Object> data, Path xhtmlPath) throws IOException {
        createJavaFile(log, data, xhtmlPath, "xhtml/crud_prime.ftl");
    }
    
    /**
     * Generates a Managed Bean CRUD file using a template and writes it to the specified file path.
     *
     * @param log The logger used to log messages and errors.
     * @param fieldsMap The data model map containing values to populate the Managed Bean CRUD template.
     * @param javaPath The file path where the generated Managed Bean CRUD file will be written.
     * @throws IOException If an I/O error occurs during file writing.
     */
    public void createManagedBeanCrudFile(Log log, Map<String, Object> fieldsMap, Path javaPath) throws IOException {
        createJavaFile(log, fieldsMap, javaPath, "java/ManagedBeanCrud.ftl");
    }

    private static class TemplateUtilHolder {

        private static final TemplateUtil INSTANCE = new TemplateUtil();
    }
}
