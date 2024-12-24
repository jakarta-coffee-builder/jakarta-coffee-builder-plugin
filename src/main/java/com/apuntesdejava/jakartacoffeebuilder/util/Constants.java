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

import java.util.Map;

/**
 * The Constants class serves as a centralized repository for constant values
 * used throughout the application. These constants include string identifiers,
 * version numbers, servlet definitions, namespace identifiers, and other
 * fixed values that are shared among various components.
 * <p>
 * The purpose of this class is to provide a consistent and reusable set of values
 * that can be referenced across the application, minimizing hardcoding and
 * potential duplication. Many constants are related to Jakarta EE specifications
 * and versions.
 * <p>
 * Key constant groups include:
 * - Jakarta EE specifications and APIs, such as Jakarta Faces and CDI specifications.
 * - Supported Jakarta EE versions.
 * - Servlet and namespace definitions.
 * - XSLT file names for formatting.
 * <p>
 * Developers should use this class to retrieve any predefined constant values
 * referenced in the application.
 */
public class Constants {

    public static final String JAKARTA_PLATFORM = "jakarta.platform";
    public static final String JAKARTA_JAKARTAEE_WEB_API = "jakarta.jakartaee-web-api";
    public static final String JAKARTA_JAKARTAEE_API = "jakarta.jakartaee-api";

    public static final String JAKARTA_FACES = "jakarta.faces";
    public static final String JAKARTA_FACES_API = "jakarta.faces-api";

    public static final String JAKARTA_PERSISTENCE = "jakarta.persistence";
    public static final String JAKARTA_PERSISTENCE_API = "jakarta.persistence-api";

    public static final String JAKARTA_ENTERPRISE = "jakarta.enterprise";
    public static final String JAKARTA_ENTERPRISE_CDI_API = "jakarta.enterprise.cdi-api";


    public static final String JAKARTAEE_VERSION_10 = "10.0.0";
    public static final String JAKARTAEE_VERSION_11 = "11.0.0";

    public static final String PROVIDED_SCOPE = "provided";

    public static final Map<String, Map<String, String>> SPECS_VERSIONS = Map.
        of(
            JAKARTAEE_VERSION_10, Map.of(
                JAKARTA_FACES_API, "4.0.1",
                JAKARTA_PERSISTENCE_API, "3.1.0",
                JAKARTA_ENTERPRISE_CDI_API, "4.0.1"
            )
        );

    public static final String JAKARTA_FACES_WEBAPP_FACES_SERVLET = "jakarta.faces.webapp.FacesServlet";

    public static final String JAKARTA_FACES_SERVLET = "JakartaServlet";
    public static final String JAKARTA_FACES_SERVLET_DEFINITION = "Jakarta Faces Servlet Definition";

    public static final String FACES_NS_CORE = "jakarta.faces.core";
    public static final String FACES_NS_HTML = "jakarta.faces.html";
    public static final String FACES_NS_UI = "jakarta.faces.facelets";

    public static final String SLASH = "/";

    public static final String XML_XSLT = "format-xml.xslt";
    public static final String XHTML_XSLT = "format-xhtml.xslt";
    public static final String XHTML_COMPOSITION_XSLT = "format-xhtml-composition.xslt";

    public static final String DATASOURCE_DECLARE_WEB = "web.xml";
    public static final String DATASOURCE_DECLARE_CLASS = "class";
}
