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

import org.apache.maven.plugin.logging.Log;
import org.w3c.dom.Document;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

import static com.apuntesdejava.jakartacoffeebuilder.util.Constants.JAKARTA_FACES_WEBAPP_FACES_SERVLET;

/**
 * Utility class for handling operations related to the `web.xml` file in a Jakarta EE Maven project.
 * <p>
 * This class provides methods to check for the existence of the `web.xml` file, add servlet declarations,
 * and save the `web.xml` file.
 * </p>
 * <p>
 * This class follows the Singleton design pattern to ensure only one instance is created.
 * </p>
 * <p>
 * Usage example:
 * <pre>
 *     WebXmlUtil webXmlUtil = WebXmlUtil.getInstance();
 *     webXmlUtil.checkExistsFile(log, currentPath);
 *     webXmlUtil.addServletDeclaration(document, urlPattern, log, servletName, description);
 *     webXmlUtil.saveDocument(document, log, currentPath);
 * </pre>
 * </p>
 * <p>
 * Note: This class is thread-safe.
 * </p>
 * <p>
 * Author: Diego Silva &lt;diego.silva at apuntesdejava.com&gt;
 * </p>
 */
public class WebXmlUtil {

    private WebXmlUtil() {
    }

    public static WebXmlUtil getInstance() {
        return WebUtilHolder.INSTANCE;
    }

    /**
     * Checks if the `web.xml` file exists in the given Maven project path.
     * If the file does not exist, it creates a new `web.xml` file with a basic `web-app` element.
     *
     * @param log         the logger to use for logging messages
     * @param currentPath the path to the Maven project
     * @return an Optional containing the XML Document if the file exists or was created successfully, otherwise an empty Optional
     * @throws IOException if an error occurs while accessing or creating the `web.xml` file
     */
    public Optional<Document> checkExistsFile(Log log, Path currentPath) throws IOException {
        var webXmlPath = currentPath.resolve("src/main/webapp/WEB-INF/web.xml");
        return XmlUtil.getInstance().getDocument(log, webXmlPath, document -> {
            var webAppElement = document.createElement("web-app");
            webAppElement.setAttribute("version", "6.0");
            webAppElement.setAttribute("xmlns", "https://jakarta.ee/xml/ns/jakartaee");
            webAppElement.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
            webAppElement.setAttribute("xsi:schemaLocation",
                "https://jakarta.ee/xml/ns/jakartaee https://jakarta.ee/xml/ns/jakartaee/web-app_6_0.xsd");
            document.appendChild(webAppElement);
        });

    }

    /**
     * Adds a servlet declaration for Jakarta Faces to the given XML document.
     *
     * @param document    the XML document to which the servlet declaration will be added
     * @param urlPattern  the URL pattern to use for the servlet mapping
     * @param log         the logger to use for logging messages
     * @param servletName the name of the servlet
     * @param description the description of the servlet
     */
    public void addServletDeclaration(Document document,
                                      String urlPattern,
                                      Log log,
                                      String servletName,
                                      String description) {
        var xmlUtil = XmlUtil.getInstance();


        var nodeList = xmlUtil.findElements(document, log,
            "//servlet-class[text()='%s']".formatted(JAKARTA_FACES_WEBAPP_FACES_SERVLET));
        if (nodeList.getLength() == 0) {
            xmlUtil.addElement(document, log, "//web-app", "servlet", servlet -> {
                xmlUtil.addElement(servlet, "description", description);
                xmlUtil.addElement(servlet, "servlet-name", servletName);
                xmlUtil.addElement(servlet, "servlet-class", JAKARTA_FACES_WEBAPP_FACES_SERVLET);
            });
            xmlUtil.addElement(document, log, "//web-app", "servlet-mapping", servlet -> {
                xmlUtil.addElement(servlet, "servlet-name", servletName);
                xmlUtil.addElement(servlet, "url-pattern", urlPattern);
            });
        }
    }

    /**
     * Saves the given XML document to the `web.xml` file located in the specified Maven project's path.
     *
     * @param document    the XML document to save
     * @param log         the logger to use for logging messages
     * @param currentPath the path to the Maven project
     */
    public void saveDocument(Document document, Log log, Path currentPath) {
        XmlUtil.getInstance().saveDocument(document, log, currentPath.resolve("src/main/webapp/WEB-INF/web.xml"));
    }

    private static class WebUtilHolder {

        private static final WebXmlUtil INSTANCE = new WebXmlUtil();
    }
}
