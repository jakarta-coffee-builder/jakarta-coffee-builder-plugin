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

import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.apuntesdejava.jakartacoffeebuilder.util.Constants.*;

/**
 * @author Diego Silva <diego.silva at apuntesdejava.com>
 */
public class JakartaFacesUtil {

    private JakartaFacesUtil() {
    }

    public static JakartaFacesUtil getInstance() {
        return JakartaFacesUtilHolder.INSTANCE;
    }

    private Optional<Path> createXhtmlFile(MavenProject mavenProject, Log log, String pageName) throws IOException {
        var currentDir = mavenProject.getBasedir();
        var webdir = currentDir.toPath().resolve("src").resolve("main").resolve("webapp");
        if (!Files.exists(webdir))
            Files.createDirectories(webdir);
        var xhtml = webdir.resolve(pageName + ".xhtml");
        if (Files.exists(xhtml)) {
            log.warn("File " + xhtml + " already exists");
            return Optional.empty();
        }
        return Optional.of(xhtml);
    }

    /**
     * Adds a new JSF face page to the specified Maven project's web application directory.
     * The method generates an XHTML page with optional managed bean support.
     *
     * @param mavenProject      the Maven project for which the face page will be created
     * @param log               the logger used to output warnings and informational messages
     * @param pageName          the name of the face page to be created, without file extension
     * @param createManagedBean a boolean indicating if a managed bean reference should be added
     * @throws IOException if an input/output error occurs during file operations
     */
    public void addFacePage(MavenProject mavenProject,
                            Log log,
                            String pageName,
                            boolean createManagedBean) throws IOException {
        var beanClassName = StringsUtil.getInstance().toPascalCase(pageName) + "Bean";
        createXhtmlFile(mavenProject, log, pageName).ifPresent(xhtml -> {
            var xmlUtil = XmlUtil.getInstance();
            var facePage = xmlUtil.getDocument(log, xhtml, (documentBuilder) -> {
                var docType = documentBuilder.getDOMImplementation()
                                             .createDocumentType("html", "-//W3C//DTD XHTML 1.0 Transitional//EN",
                                                 "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd");
                return documentBuilder.getDOMImplementation().createDocument(null, "html", docType);
            }, document -> {
                var htmlElem = document.getDocumentElement();
                htmlElem.setAttribute("xmlns", "http://www.w3.org/1999/xhtml");
                htmlElem.setAttribute("xmlns:h", FACES_NS_HTML);
                htmlElem.setAttribute("xmlns:f", FACES_NS_CORE);

                var viewElem = xmlUtil.addElementNS(htmlElem, FACES_NS_CORE, "f:view");
                var bodyElem = xmlUtil.addElementNS(viewElem, FACES_NS_HTML, "h:body");
                if (createManagedBean) {
                    var labelElem = xmlUtil.addElementNS(bodyElem, FACES_NS_HTML, "h:outputText");
                    labelElem.setAttribute("value", "#{%s.name}".formatted(beanClassName));
                }
            }).orElseThrow();
            xmlUtil.saveDocument(facePage, log, xhtml, XHTML_XSLT);
        });
    }

    /**
     * Creates a managed bean file for a given page in the specified Maven project.
     * The method generates a Java class file for the managed bean, placing it
     * in the appropriate package directory inside the project structure.
     *
     * @param mavenProject the Maven project for which the managed bean will be created
     * @param log          the logger used to output debug and error messages
     * @param pageName     the name of the page for which the managed bean is being created
     * @throws IOException if an input/output error occurs during file operations
     */
    public void createManagedBean(MavenProject mavenProject, Log log, String pageName) throws IOException {
        log.debug("Creating managed bean for " + pageName);
        var currentDir = mavenProject.getBasedir();
        var javaDir = currentDir.toPath().resolve("src").resolve("main").resolve("java");
        if (!Files.exists(javaDir))
            Files.createDirectories(javaDir);
        var mavenProjectUtil = MavenProjectUtil.getInstance();
        var packageDefinition = mavenProjectUtil.getProjectPackage(mavenProject) + ".managedbean";
        var packageDir = javaDir.resolve(packageDefinition.replace(".", "/"));
        if (!Files.exists(packageDir))
            Files.createDirectories(packageDir);
        log.debug("packageDir: " + packageDir);
        var className = StringsUtil.getInstance().toPascalCase(pageName) + "Bean";
        var managedBean = packageDir.resolve(className + ".java");
        TemplateUtil.getInstance().createJavaBeanFile(log,
            Map.of("packageName", packageDefinition,
                "className", className,
                "fields", List.of(
                    Map.of("type", "String",
                        "name", "name")
                ),
                "importsList", List.of(
                    "jakarta.enterprise.context.Dependent",
                    "jakarta.inject.Named"
                ),
                "annotations", List.of("Dependent", "Named")), managedBean);
    }

    /**
     * Adds a new JSF face page to a Maven-based web application using a specified Facelet template.
     * This method generates an XHTML page based on the provided Facelet template and, optionally,
     * includes support for a managed bean. The generated page will match the naming conventions
     * and settings defined in the project structure.
     *
     * @param mavenProject      the Maven project where the face page will be added
     * @param log               the logger used to log warnings, errors, or informational messages
     * @param pageName          the name of the face page to be created, without the file extension
     * @param templateFacelet   the path to the Facelet template that will be used as the base for the page
     * @param createManagedBean a boolean indicating whether a managed bean should be created and
     *                          referenced in the generated page
     * @throws IOException if an I/O error occurs during file operations
     */
    public void addFacePageWithFaceletTemplate(MavenProject mavenProject,
                                               Log log,
                                               String pageName,
                                               String templateFacelet,
                                               boolean createManagedBean) throws IOException {
        var stringsUtil = StringsUtil.getInstance();
        var beanClassName = stringsUtil.toPascalCase(pageName) + "Bean";

        createXhtmlFile(mavenProject, log, pageName).ifPresent(xhtml -> {

            var xmlUtil = XmlUtil.getInstance();
            var facePage = xmlUtil.getDocument(log, xhtml, (documentBuilder) -> {
                var docType = documentBuilder.getDOMImplementation()
                                             .createDocumentType("composition",
                                                 "-//W3C//DTD XHTML 1.0 Transitional//EN",
                                                 "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd");
                return documentBuilder.getDOMImplementation()
                                      .createDocument(FACES_NS_UI, "ui:composition", docType);
            }, document -> {
                var htmlElem = document.getDocumentElement();
                htmlElem.setAttribute("xmlns", "http://www.w3.org/1999/xhtml");
                htmlElem.setAttribute("xmlns:ui", FACES_NS_UI);
                htmlElem.setAttribute("xmlns:h", FACES_NS_HTML);
                htmlElem.setAttribute("template", templateFacelet);

                var template = xmlUtil.getDocument(log,
                    xhtml.getParent().resolve(stringsUtil.removeCharacterRoot(templateFacelet))).orElseThrow();

                xmlUtil.findElementsStream(template, log, "//ui:insert",
                           Map.of("ui", FACES_NS_UI))
                       .forEach(insertElem -> {
                           var name = insertElem.getAttribute("name");
                           var defineTag = xmlUtil.addElementNS(htmlElem, FACES_NS_UI, "ui:define");
                           defineTag.setAttribute("name", name);
                           var labelElem = xmlUtil.addElementNS(defineTag, FACES_NS_HTML, "h:outputText");
                           labelElem.setAttribute("value", createManagedBean ? "#{%s.name}".formatted(
                               StringUtils.uncapitalize(beanClassName)) : name);
                       });
            }).orElseThrow();
            xmlUtil.saveDocument(facePage, log, xhtml, XHTML_COMPOSITION_XSLT);
        });
    }


    private static class JakartaFacesUtilHolder {

        private static final JakartaFacesUtil INSTANCE = new JakartaFacesUtil();
    }
}
