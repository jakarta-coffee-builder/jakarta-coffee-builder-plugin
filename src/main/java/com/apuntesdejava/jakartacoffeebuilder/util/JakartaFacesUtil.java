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
import java.util.List;
import java.util.Map;

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
                            String pageName, boolean createManagedBean) throws IOException {
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
            if (createManagedBean) {
                var labelElem = xmlUtil.addElementNS(bodyElem, FACES_NS_HTML, "h:outputText");
                labelElem.setAttribute("value", "#{%sBean.name}".formatted(pageName));
            }


        }).orElseThrow();
        xmlUtil.saveDocument(facePage, log, xhtml, XHTML_XSLT);
    }

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
        var className = StringUtils.capitalize(pageName) + "Bean";
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

    private static class JakartaFacesUtilHolder {

        private static final JakartaFacesUtil INSTANCE = new JakartaFacesUtil();
    }
}
