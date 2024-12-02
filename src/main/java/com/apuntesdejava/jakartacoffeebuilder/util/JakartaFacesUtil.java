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
import org.apache.maven.project.MavenProject;

import java.io.IOException;
import java.nio.file.Files;

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

    public void addFacePage(MavenProject mavenProject,
                            Log log,
                            String pageName,
                            boolean createManagedBean) throws IOException {
        var currentDir = mavenProject.getBasedir();
        var webdir = currentDir.toPath().resolve("src").resolve("main").resolve("webapp");
        if (!Files.exists(webdir))
            Files.createDirectories(webdir);
        var xhtml = webdir.resolve(pageName + ".xhtml");
        if (Files.exists(xhtml)) {
            log.warn("File " + xhtml + " already exists");
            return;
        }
        var xmlUtil = XmlUtil.getInstance();
        var facePage = xmlUtil.getDocument(log, xhtml, (documentBuilder) -> {
            var docType = documentBuilder.getDOMImplementation()
                                         .createDocumentType("html", "-//W3C//DTD XHTML 1.0 Transitional//EN",
                                             "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd");
            return documentBuilder.getDOMImplementation().createDocument(null, "html", docType);
        }, document -> {
            var htmlElem = document.getDocumentElement();
            htmlElem.setAttribute("xmlns", "http://www.w3.org/1999/xhtml");
            htmlElem.setAttribute("xmlns:h", "http://xmlns.jcp.org/jsf/html");
            htmlElem.setAttribute("xmlns:f", "http://xmlns.jcp.org/jsf/core");

            var viewElem = xmlUtil.addElementNS(htmlElem, FACES_NS_CORE, "f:view");
            var bodyElem = xmlUtil.addElementNS(viewElem, FACES_NS_HTML, "h:body");


        }).orElseThrow();
        xmlUtil.saveDocument(facePage, log, xhtml, XHTML_XSLT);
    }

    private static class JakartaFacesUtilHolder {

        private static final JakartaFacesUtil INSTANCE = new JakartaFacesUtil();
    }
}
