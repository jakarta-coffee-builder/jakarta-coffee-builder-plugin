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

import java.util.Set;

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
     * Identifier for the Jakarta EE Core API.
     */
    public static final String JAKARTA_JAKARTAEE_CORE_API = "jakarta.jakartaee-core-api";

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
     * Key for the type of an entity or field.
     */
    public static final String TYPE = "type";
    /**
     * Key for the table name of an entity.
     */
    public static final String TABLE_NAME = "table";
    /**
     * Key to identify if a field is an ID.
     */
    public static final String IS_ID = "isId";
    public static final String GENERATED_VALUED = "generatedValue";
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
    public static final String DEV_BASE_URL = "https://raw.githubusercontent.com/jakarta-coffee-builder/configuration/refs/heads/develop";
    /**
     * Production base URL for retrieving configured dependencies.
     */
    public static final String PRD_BASE_URL = "https://jakarta-coffee-builder.github.io/configuration";
    /**
     * URL path for retrieving dependency configurations.
     */
    public static final String DEPENDENCIES_URL = "/dependencies.json";

    /**
     * URL path for retrieving server configurations.
     */
    public static final String SERVERS_URL = "/servers.json";
    /**
     * URL path for retrieving specification configurations.
     */
    public static final String SPECIFICATIONS_URL = "/specifications.json";

    /**
     * URL path for retrieving class definitions.
     */
    public static final String CLASSES_DEFINITIONS = "/classes-definitions.json";

    /**
     * URL to retrieve Hibernate dialect configurations.
     */
    public static final String DIALECT_URL = "/hibernate-dialect.json";
    /**
     * URL to retrieve configured properties.
     */
    public static final String PROPERTIES_URL = "/properties.json";

    /**
     * Hibernate persistence provider.
     */
    public static final String HIBERNATE_PROVIDER = "org.hibernate.jpa.HibernatePersistenceProvider";

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

    /**
     * Generic key for a value.
     */
    public static final String VALUE = "value";
    /**
     * URL to retrieve schema definitions.
     */
    public static final String SCHEMAS_URL = "/schemas.json";
    /**
     * URL to retrieve OpenAPI Generator configurations.
     */
    public static final String OPEN_API_GENERATOR_CONFIGURATION = "/openapi-generator-config.json";

    /**
     * Key for the Maven groupId.
     */
    public static final String GROUP_ID = "groupId";
    /**
     * Key for the Maven artifactId.
     */
    public static final String ARTIFACT_ID = "artifactId";

    /**
     * Group ID for PrimeFaces.
     */
    public static final String ORG_PRIMEFACES = "org.primefaces";
    /**
     * Artifact ID for PrimeFaces.
     */
    public static final String PRIMEFACES = "primefaces";

    /**
     * Key for configuration settings.
     */
    public static final String CONFIGURATION = "configuration";

    /**
     * Key for a list of goals.
     */
    public static final String GOALS = "goals";
    /**
     * Key for a single goal.
     */
    public static final String GOAL = "goal";

    public static final String ORG_APACHE_MAVEN_PLUGINS = "org.apache.maven.plugins";
    public static final String MAVEN_COMPILER_PLUGIN = "maven-compiler-plugin";
    public static final String ORG_MAPSTRUCT = "org.mapstruct";
    public static final String MAPSTRUCT = "mapstruct";
    public static final String ENTITY = "entity";
    public static final Set<String> SEARCH_ANNOTATIONS_FIELD_KEYS = Set.of("Column", "JoinColumn", "ManyToOne",
        "ElementCollection");
    public static final Set<String> GENERATION_TYPES = Set.of("AUTO", "IDENTITY", "SEQUENCE", "TABLE");

    public static final String MODEL_NAME = "modelName";

    private Constants() {
    }
}
