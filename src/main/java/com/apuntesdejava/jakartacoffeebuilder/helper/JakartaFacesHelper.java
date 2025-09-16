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
package com.apuntesdejava.jakartacoffeebuilder.helper;

import com.apuntesdejava.jakartacoffeebuilder.util.MavenProjectUtil;
import com.apuntesdejava.jakartacoffeebuilder.util.PathsUtil;
import com.apuntesdejava.jakartacoffeebuilder.util.StringsUtil;
import com.apuntesdejava.jakartacoffeebuilder.util.TemplateUtil;
import com.apuntesdejava.jakartacoffeebuilder.util.XmlUtil;
import jakarta.json.JsonObject;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.QName;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

import static com.apuntesdejava.jakartacoffeebuilder.util.Constants.*;

/**
 * Utility class for managing Jakarta Faces (JSF) operations in a Maven project. This class provides methods to create
 * JSF face pages, managed beans, and Facelet templates, enabling integration of JSF features into a Java web
 * application.
 */
public class JakartaFacesHelper {

    protected static final Namespace FACES_NS_HTML_NAMESPACE = new Namespace("h", FACES_NS_HTML);
    protected static final Namespace FACES_NS_CORE_NAMESPACE = new Namespace("f", FACES_NS_CORE);
    protected static final Namespace FACES_NS_UI_NAMESPACE = new Namespace("ui", FACES_NS_UI);

    /**
     * Retrieves the singleton instance of the JakartaFacesHelper class. This method ensures that only one instance of
     * the helper is created and provides a global point of access to it.
     *
     * @return the singleton instance of JakartaFacesHelper
     */
    public static JakartaFacesHelper getInstance() {
        return JakartaFacesUtilHolder.INSTANCE;
    }

    protected JakartaFacesHelper() {
    }

    private Optional<Path> createXhtmlFile(MavenProject mavenProject, Log log, String pageName) throws IOException {
        var webdir = PathsUtil.getWebappPath(mavenProject);
        var xhtml = webdir.resolve(pageName + ".xhtml");
        if (Files.exists(xhtml)) {
            log.warn("File " + xhtml + " already exists");
            return Optional.empty();
        }
        return Optional.of(xhtml);
    }

    /**
     * Adds a new JSF face page to the specified Maven project's web application directory. The method generates an
     * XHTML page with optional managed bean support.
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
        var beanClassName = StringsUtil.toPascalCase(pageName) + "Bean";
        createXhtmlFile(mavenProject, log, pageName).ifPresent(xhtml -> {
            var xmlUtil = XmlUtil.getInstance();
            var facePage = createFacePage(log, xhtml, (bodyElem) -> {
                if (createManagedBean) {
                    var labelElem = xmlUtil.addElement(bodyElem, "outputText", FACES_NS_HTML_NAMESPACE);
                    labelElem.addAttribute(VALUE, "#{%s.name}".formatted(StringUtils.uncapitalize(
                            beanClassName)));
                }
            });
            xmlUtil.saveDocument(facePage, log, xhtml);
        });
    }

    protected Document createFacePage(Log log, Path xhtml, Consumer<Element> processInBody) {
        var xmlUtil = XmlUtil.getInstance();
        return xmlUtil.getDocument(log, xhtml, () -> {
            Document document = DocumentHelper.createDocument();
            String rootElementName = "html";
            String publicId = "-//W3C//DTD XHTML 1.0 Transitional//EN";
            String systemId = "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd";
            document.addDocType(rootElementName, publicId, systemId);
            return document;
        }, document -> {
            var htmlElem = document.addElement("html");

            htmlElem.addAttribute("xmlns", "http://www.w3.org/1999/xhtml");
            htmlElem.add(FACES_NS_HTML_NAMESPACE);
            htmlElem.add(FACES_NS_CORE_NAMESPACE);

            var viewElem = xmlUtil.addElement(htmlElem, "view", FACES_NS_CORE_NAMESPACE);
            var bodyElem = xmlUtil.addElement(viewElem, "body", FACES_NS_HTML_NAMESPACE);
            if (processInBody != null) {
                processInBody.accept(bodyElem);
            }
        }).orElseThrow();
    }

    public void createManagedBean(MavenProject mavenProject, Log log, String pageName) throws IOException {
        createManagedBean(mavenProject, log, pageName, null);
    }

    /**
     * Creates a managed bean file for a given page in the specified Maven project. The method generates a Java class
     * file for the managed bean, placing it in the appropriate package directory inside the project structure.
     *
     * @param mavenProject     the Maven project for which the managed bean will be created
     * @param log              the logger used to output debug and error messages
     * @param pageName         the name of the page for which the managed bean is being created
     * @param entityDefinition
     * @throws IOException if an input/output error occurs during file operations
     */
    public void createManagedBean(MavenProject mavenProject,
                                  Log log,
                                  String pageName,
                                  JsonObject entityDefinition) throws IOException {
        log.debug("Creating managed bean for " + pageName);
        var packageDefinition = MavenProjectUtil.getFacesPackage(mavenProject);
        var className = StringsUtil.toPascalCase(pageName) + "Bean";
        var managedBean = PathsUtil.getJavaPath(mavenProject, packageDefinition, className);
        var annotationsClasses = Map.of(
                "jakarta.enterprise.context.RequestScoped", Map.of(),
                "jakarta.inject.Named", Map.of()
        );
        var fields = getFieldsDefinitions(entityDefinition);
        TemplateUtil.getInstance().createJavaBeanFile(log,
                Map.of(PACKAGE_NAME, packageDefinition,
                        CLASS_NAME, className,
                        FIELDS, fields,
                        "annotations", annotationsClasses), managedBean);
    }

    private List<Map<String, String>> getFieldsDefinitions(JsonObject entityDefinition) {
        return entityDefinition == null
                ? List.of(
                Map.of(TYPE, "String",
                        NAME, NAME)
        ) : entityDefinition.getJsonObject(FIELDS)
                .entrySet()
                .stream()
                .map(entry
                        -> Map.of(
                        TYPE, entry.getValue().asJsonObject().getString(TYPE),
                        NAME, entry.getKey())
                )
                .toList();
    }

    /**
     * Adds a new JSF face page to a Maven-based web application using a specified Facelet template. This method
     * generates an XHTML page based on the provided Facelet template and, optionally, includes support for a managed
     * bean. The generated page will match the naming conventions and settings defined in the project structure.
     *
     * @param mavenProject      the Maven project where the face page will be added
     * @param log               the logger used to log warnings, errors, or informational messages
     * @param pageName          the name of the face page to be created, without the file extension
     * @param templateFacelet   the path to the Facelet template that will be used as the base for the page
     * @param createManagedBean a boolean indicating whether a managed bean should be created and referenced in the
     *                          generated page
     * @throws IOException if an I/O error occurs during file operations
     */
    public void addFacePageWithFaceletTemplate(MavenProject mavenProject,
                                               Log log,
                                               String pageName,
                                               String templateFacelet,
                                               boolean createManagedBean) throws IOException {
        var beanClassName = StringsUtil.toPascalCase(pageName) + "Bean";

        createXhtmlFile(mavenProject, log, pageName).ifPresent(xhtml -> {

            var xmlUtil = XmlUtil.getInstance();
            var facePage = createFacePageWithTemplate(log, xhtml, templateFacelet, (defineTag) -> {
                var labelElem = xmlUtil.addElement(defineTag, "outputText", FACES_NS_HTML_NAMESPACE);
                labelElem.addAttribute(VALUE, createManagedBean ? "#{%s.name}".formatted(
                        StringUtils.uncapitalize(beanClassName)) : "-");

            });
            xmlUtil.saveDocument(facePage, log, xhtml);
        });
    }

    protected Document createFacePageWithTemplate(Log log,
                                                  Path xhtml,
                                                  String templateFacelet,
                                                  Consumer<Element> processInBody,
                                                  Namespace... additionalNamespaces) {
        var xmlUtil = XmlUtil.getInstance();
        return xmlUtil.getDocument(log, xhtml, document -> {
            Namespace[] fullNamespaces = ArrayUtils.addAll(
                    new Namespace[]{FACES_NS_HTML_NAMESPACE, FACES_NS_UI_NAMESPACE}, additionalNamespaces);
            var htmlElem = getRootElementForTemplate(document, fullNamespaces);
            htmlElem.addAttribute("template", templateFacelet);
            var templatePath = xhtml.getParent().resolve(StringsUtil.
                    removeCharacterRoot(templateFacelet));
            if (!Files.exists(templatePath)) {
                throw new RuntimeException(new FileNotFoundException("%s template not found".
                        formatted(templatePath)));
            }
            var template = xmlUtil.getDocument(log, templatePath).orElseThrow();

            xmlUtil.findElementsStream(template, "//ui:insert", Map.of("ui", FACES_NS_UI))
                    .forEach(insertElem -> {
                        var name = insertElem.attributeValue(NAME);
                        var defineTag = xmlUtil.addElement(htmlElem, "define", FACES_NS_UI_NAMESPACE);
                        defineTag.addAttribute(NAME, name);
                        if (processInBody != null) {
                            processInBody.accept(defineTag);
                        }
                    });
        }).orElseThrow();
    }

    /**
     * Adds a new Facelet template to the specified Maven project's web application directory. This method generates an
     * XHTML file based on the provided templateName, and optionally includes &lt;ui:insert&gt; elements with the names
     * specified in the inserts list.
     *
     * @param mavenProject the Maven project for which the template will be created
     * @param log          the logger for outputting warnings and informational messages
     * @param templateName the name of the Facelet template, which determines the base structure of the generated XHTML
     *                     page
     * @param inserts      a list of insert names to be added as &lt;ui:insert&gt; elements in the generated XHTML file;
     *                     may be null or empty
     * @throws IOException if an input/output error occurs during file creation or modification
     */
    public void addFaceTemplate(MavenProject mavenProject,
                                Log log,
                                String templateName,
                                List<String> inserts) throws IOException {
        var fileName = Strings.CS.removeStart(Strings.CS.removeEnd(templateName, ".xhtml"), SLASH);
        if (!Strings.CS.endsWith(fileName, ".xhtml")) {
            fileName += ".xhtml";
        }
        var xhtmlPath = PathsUtil.getWebappPath(mavenProject).resolve(fileName);
        Map<String, Object> fieldsMap = Map.of(
                "contents", inserts,
                "title", xhtmlPath.getFileName()
        );
        TemplateUtil.getInstance().createFacesTemplateFile(log, fieldsMap, xhtmlPath);

    }

    private Element getRootElementForTemplate(Document document, Namespace... namespaces) {
        QName rootQName = new QName("composition", FACES_NS_UI_NAMESPACE);
        Element rootElement = document.addElement(rootQName);
        for (var namespace : namespaces) {
            rootElement.add(namespace);
        }

        rootElement.addAttribute("xmlns", "http://www.w3.org/1999/xhtml");
        return rootElement;
    }

    private static class JakartaFacesUtilHolder {

        private static final JakartaFacesHelper INSTANCE = new JakartaFacesHelper();
    }

}
