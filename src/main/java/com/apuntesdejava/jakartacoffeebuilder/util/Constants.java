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

import java.util.Map;

/**
 * The Constants class serves as a centralized repository for constant values used throughout the application. These
 * constants include string identifiers, version numbers, servlet definitions, namespace identifiers, and other fixed
 * values that are shared among various components.
 * <p>
 * The purpose of this class is to provide a consistent and reusable set of values that can be referenced across the
 * application, minimizing hardcoding and potential duplication. Many constants are related to Jakarta EE specifications
 * and versions.
 * <p>
 * Key constant groups include: - Jakarta EE specifications and APIs, such as Jakarta Faces and CDI specifications. -
 * Supported Jakarta EE versions. - Servlet and namespace definitions. - XSLT file names for formatting.
 * <p>
 * Developers should use this class to retrieve any predefined constant values referenced in the application.
 */
public class Constants {
    /**
     * Identifier for the Jakarta platform.
     */
    public static final String JAKARTA_PLATFORM = "jakarta.platform";

    /**
     * Identifier for the Jakarta EE Web API.
     */
    public static final String JAKARTA_JAKARTAEE_WEB_API = "jakarta.jakartaee-web-api";

    /**
     * Identifier for the full Jakarta EE API.
     */
    public static final String JAKARTA_JAKARTAEE_API = "jakarta.jakartaee-api";

    /**
     * Identifier for Jakarta Faces.
     */
    public static final String JAKARTA_FACES = "jakarta.faces";

    /**
     * Identifier for the Jakarta Faces API.
     */
    public static final String JAKARTA_FACES_API = "jakarta.faces-api";

    /**
     * Identifier for Jakarta Persistence.
     */
    public static final String JAKARTA_PERSISTENCE = "jakarta.persistence";
    /**
     * Identifier for the Jakarta Persistence API.
     */
    public static final String JAKARTA_PERSISTENCE_API = "jakarta.persistence-api";

    /**
     * Identifier for Jakarta Enterprise.
     */
    public static final String JAKARTA_ENTERPRISE = "jakarta.enterprise";
    /**
     * Identifier for the Jakarta Enterprise CDI API.
     */
    public static final String JAKARTA_ENTERPRISE_CDI_API = "jakarta.enterprise.cdi-api";

    /**
     * Identifier for Jakarta Data.
     */
    public static final String JAKARTA_DATA = "jakarta.data";
    /**
     * Identifier for the Jakarta Data API.
     */
    public static final String JAKARTA_DATA_API = "jakarta.data-api";

    /**
     * Identifier for Jakarta MVC.
     */
    public static final String JAKARTA_MVC = "jakarta.mvc";
    /**
     * Identifier for the Jakarta MVC API.
     */
    public static final String JAKARTA_MVC_API = "jakarta.mvc-api";

    /**
     * Jakarta EE version 10.
     */
    public static final String JAKARTAEE_VERSION_10 = "10.0.0";
    /**
     * Jakarta EE version 11.
     */
    public static final String JAKARTAEE_VERSION_11 = "11.0.0";

    /**
     * Dependency scope "provided".
     */
    public static final String PROVIDED_SCOPE = "provided";

    /**
     * Key for the name of an entity.
     */
    public static final String NAME = "name";
    /**
     * Key for the table name of an entity.
     */
    public static final String TABLE_NAME = "table";
    /**
     * Key to identify if a field is an ID.
     */
    public static final String IS_ID = "isId";
    /**
     * Key for the package name.
     */
    public static final String PACKAGE_NAME = "packageName";
    /**
     * Key for the class name.
     */
    public static final String CLASS_NAME = "className";
    /**
     * Key for the fields of an entity.
     */
    public static final String FIELDS = "fields";
    /**
     * Key for the list of imports.
     */
    public static final String IMPORTS_LIST = "importsList";

    /**
     * URL to retrieve configured dependencies.
     */
    public static final String DEPENDENCIES_URL = "https://jakarta-coffee-builder.github.io/configuration/dependencies.json";

    /**
     * URL to retrieve development dependencies.
     * <p>
     * This URL points to the development branch of the configuration repository, which may contain
     * dependencies that are not yet released or are in active development.
     * </p>
     */
    public static final String DEPENDENCIES_DEV_URL = "https://raw.githubusercontent.com/jakarta-coffee-builder/configuration/refs/heads/develop/dependencies.json";
    /**
     * URL to retrieve Hibernate dialect configurations.
     */
    public static final String DIALECT_URL = "https://jakarta-coffee-builder.github.io/configuration/hibernate-dialect.json";
    /**
     * URL to retrieve configured properties.
     */
    public static final String PROPERTIES_URL = "https://jakarta-coffee-builder.github.io/configuration/properties.json";

    /**
     * Hibernate persistence provider.
     */
    public static final String HIBERNATE_PROVIDER = "org.hibernate.jpa.HibernatePersistenceProvider";

    /**
     * Map of supported Jakarta EE specification versions.
     * <p>
     * This map associates Jakarta EE versions with their corresponding API versions for various specifications.
     * </p>
     */
    public static final Map<String, Map<String, String>> SPECS_VERSIONS = Map.
        of(
            JAKARTAEE_VERSION_10, Map.of(
                JAKARTA_FACES_API, "4.0.1",
                JAKARTA_PERSISTENCE_API, "3.1.0",
                JAKARTA_ENTERPRISE_CDI_API, "4.0.1"
            ),
            JAKARTAEE_VERSION_11, Map.of(
                JAKARTA_FACES_API, "4.1.2",
                JAKARTA_PERSISTENCE_API, "3.2.0",
                JAKARTA_ENTERPRISE_CDI_API, "4.1.0",
                JAKARTA_DATA_API, "1.0.1",
                JAKARTA_MVC_API, "3.0.0"
            )
        );

    /**
     * Jakarta Faces servlet class.
     */
    public static final String JAKARTA_FACES_WEBAPP_FACES_SERVLET = "jakarta.faces.webapp.FacesServlet";

    /**
     * Name of the Jakarta Faces servlet.
     */
    public static final String JAKARTA_FACES_SERVLET = "JakartaServlet";
    /**
     * Definition of the Jakarta Faces servlet.
     */
    public static final String JAKARTA_FACES_SERVLET_DEFINITION = "Jakarta Faces Servlet Definition";

    /**
     * Namespace for Jakarta Faces core.
     */
    public static final String FACES_NS_CORE = "jakarta.faces.core";
    /**
     * Namespace for Jakarta Faces HTML.
     */
    public static final String FACES_NS_HTML = "jakarta.faces.html";
    /**
     * Namespace for Jakarta Faces Facelets.
     */
    public static final String FACES_NS_UI = "jakarta.faces.facelets";

    /**
     * Constant for the forward slash "/".
     */
    public static final String SLASH = "/";

    /**
     * Name of the XSLT file for XML formatting.
     */
    public static final String XML_XSLT = "format-xml.xslt";
    /**
     * Name of the XSLT file for XHTML formatting.
     */
    public static final String XHTML_XSLT = "format-xhtml.xslt";
    /**
     * Name of the XSLT file for XHTML composition formatting.
     */
    public static final String XHTML_COMPOSITION_XSLT = "format-xhtml-composition.xslt";

    /**
     * Datasource declaration in `web.xml`.
     */
    public static final String DATASOURCE_DECLARE_WEB = "web.xml";
    /**
     * Datasource declaration in a class.
     */
    public static final String DATASOURCE_DECLARE_CLASS = "class";
    /**
     * Datasource declaration for Payara.
     */
    public static final String DATASOURCE_PAYARA = "payara";

    private Constants() {
    }
}
