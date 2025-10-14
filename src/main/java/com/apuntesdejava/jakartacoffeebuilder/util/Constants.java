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
 * Provides a central repository for constant values used throughout the Jakarta Coffee Builder plugin.
 * This class prevents the use of magic strings and provides a single source of truth for shared
 * identifiers, keys, and URLs.
 */
public final class Constants {

    // --- Jakarta EE Maven Coordinates ---
    /**
     * The artifact ID for the Jakarta EE platform.
     */
    public static final String JAKARTA_PLATFORM = "jakarta.platform";
    /**
     * The artifact ID for the Jakarta EE Web API.
     */
    public static final String JAKARTA_JAKARTAEE_WEB_API = "jakarta.jakartaee-web-api";
    /**
     * The artifact ID for the full Jakarta EE API.
     */
    public static final String JAKARTA_JAKARTAEE_API = "jakarta.jakartaee-api";
    /**
     * The artifact ID for the Jakarta EE Core API.
     */
    public static final String JAKARTA_JAKARTAEE_CORE_API = "jakarta.jakartaee-core-api";
    /**
     * The group ID for Jakarta Faces.
     */
    public static final String JAKARTA_FACES = "jakarta.faces";
    /**
     * The artifact ID for the Jakarta Faces API.
     */
    public static final String JAKARTA_FACES_API = "jakarta.faces-api";
    /**
     * The group ID for Jakarta Persistence.
     */
    public static final String JAKARTA_PERSISTENCE = "jakarta.persistence";
    /**
     * The artifact ID for the Jakarta Persistence API.
     */
    public static final String JAKARTA_PERSISTENCE_API = "jakarta.persistence-api";
    /**
     * The group ID for Jakarta Enterprise.
     */
    public static final String JAKARTA_ENTERPRISE = "jakarta.enterprise";
    /**
     * The artifact ID for the Jakarta CDI API.
     */
    public static final String JAKARTA_ENTERPRISE_CDI_API = "jakarta.enterprise.cdi-api";
    /**
     * The group ID for Jakarta Data.
     */
    public static final String JAKARTA_DATA = "jakarta.data";
    /**
     * The artifact ID for the Jakarta Data API.
     */
    public static final String JAKARTA_DATA_API = "jakarta.data-api";
    /**
     * The group ID for Jakarta MVC.
     */
    public static final String JAKARTA_MVC = "jakarta.mvc";
    /**
     * The artifact ID for the Jakarta MVC API.
     */
    public static final String JAKARTA_MVC_API = "jakarta.mvc-api";

    // --- Jakarta EE Versions ---
    /**
     * The version string for Jakarta EE 10.
     */
    public static final String JAKARTAEE_VERSION_10 = "10.0.0";
    /**
     * The version string for Jakarta EE 11.
     */
    public static final String JAKARTAEE_VERSION_11 = "11.0.0";

    // --- Maven Scopes ---
    /**
     * The "provided" Maven dependency scope.
     */
    public static final String PROVIDED_SCOPE = "provided";

    // --- JSON and Model Keys ---
    /**
     * A generic key for a name identifier.
     */
    public static final String NAME = "name";
    /**
     * A key for a type identifier.
     */
    public static final String TYPE = "type";
    /**
     * A key for a database table name.
     */
    public static final String TABLE_NAME = "table";
    /**
     * A key to indicate if a field is a primary identifier.
     */
    public static final String IS_ID = "isId";
    /**
     * A key for the value generation strategy of a primary key.
     */
    public static final String GENERATED_VALUE = "generatedValue";
    /**
     * A key for a Java package name.
     */
    public static final String PACKAGE_NAME = "packageName";
    /**
     * A key for a Java class name.
     */
    public static final String CLASS_NAME = "className";
    /**
     * A key for a list of fields in a model definition.
     */
    public static final String FIELDS = "fields";
    /**
     * A key for a list of import statements.
     */
    public static final String IMPORTS_LIST = "importsList";
    /**
     * A key for a model name, used in templates.
     */
    public static final String MODEL_NAME = "modelName";
    /**
     * A key for a description property.
     */
    public static final String DESCRIPTION = "description";
    /**
     * A key for a fully qualified class name.
     */
    public static final String FULL_NAME = "fullName";

    // --- Remote Configuration URLs ---
    /**
     * The base URL for development configuration files.
     */
    public static final String DEV_BASE_URL = "https://raw.githubusercontent.com/jakarta-coffee-builder/configuration/refs/heads/develop";
    /**
     * The base URL for production configuration files.
     */
    public static final String PRD_BASE_URL = "https://jakarta-coffee-builder.github.io/configuration";
    /**
     * The path for the remote dependencies configuration file.
     */
    public static final String DEPENDENCIES_URL = "/dependencies.json";
    /**
     * The path for the remote servers configuration file.
     */
    public static final String SERVERS_URL = "/servers.json";
    /**
     * The path for the remote specifications configuration file.
     */
    public static final String SPECIFICATIONS_URL = "/specifications.json";
    /**
     * The path for the remote class definitions file.
     */
    public static final String CLASSES_DEFINITIONS = "/classes-definitions.json";
    /**
     * The path for the remote Hibernate dialect configuration file.
     */
    public static final String DIALECT_URL = "/hibernate-dialect.json";
    /**
     * The path for the remote properties configuration file.
     */
    public static final String PROPERTIES_URL = "/properties.json";
    /**
     * The path for the remote XML schemas definition file.
     */
    public static final String SCHEMAS_URL = "/schemas.json";
    /**
     * The path for the remote OpenAPI Generator configuration file.
     */
    public static final String OPEN_API_GENERATOR_CONFIGURATION = "/openapi-generator-config.json";

    // --- Persistence and Web Configuration ---
    /**
     * The class name for the Hibernate JPA persistence provider.
     */
    public static final String HIBERNATE_PROVIDER = "org.hibernate.jpa.HibernatePersistenceProvider";
    /**
     * The class name for the Jakarta Faces Servlet.
     */
    public static final String JAKARTA_FACES_WEBAPP_FACES_SERVLET = "jakarta.faces.webapp.FacesServlet";
    /**
     * The default servlet name for the Jakarta Faces Servlet.
     */
    public static final String JAKARTA_FACES_SERVLET = "JakartaServlet";
    /**
     * The default description for the Jakarta Faces Servlet definition.
     */
    public static final String JAKARTA_FACES_SERVLET_DEFINITION = "Jakarta Faces Servlet Definition";
    /**
     * The XML namespace for Jakarta Faces Core.
     */
    public static final String FACES_NS_CORE = "jakarta.faces.core";
    /**
     * The XML namespace for Jakarta Faces HTML components.
     */
    public static final String FACES_NS_HTML = "jakarta.faces.html";
    /**
     * The XML namespace for Jakarta Faces Facelets.
     */
    public static final String FACES_NS_UI = "jakarta.faces.facelets";
    /**
     * A forward slash character, used for constructing paths.
     */
    public static final String SLASH = "/";

    // --- Datasource Declaration Strategies ---
    /**
     * Indicates that the datasource should be declared in {@code web.xml}.
     */
    public static final String DATASOURCE_DECLARE_WEB = "web.xml";
    /**
     * Indicates that the datasource should be declared programmatically in a class.
     */
    public static final String DATASOURCE_DECLARE_CLASS = "class";
    /**
     * Indicates a Payara-specific datasource declaration.
     */
    public static final String DATASOURCE_PAYARA = "payara";

    // --- Miscellaneous Keys and Coordinates ---
    /**
     * A generic key for a value.
     */
    public static final String VALUE = "value";
    /**
     * The key for a Maven project's group ID.
     */
    public static final String GROUP_ID = "groupId";
    /**
     * The key for a Maven project's artifact ID.
     */
    public static final String ARTIFACT_ID = "artifactId";
    /**
     * The group ID for the PrimeFaces library.
     */
    public static final String ORG_PRIMEFACES = "org.primefaces";
    /**
     * The artifact ID for the PrimeFaces library.
     */
    public static final String PRIMEFACES = "primefaces";
    /**
     * The key for a plugin configuration section in a POM file.
     */
    public static final String CONFIGURATION = "configuration";
    /**
     * The key for a list of Maven plugin goals.
     */
    public static final String GOALS = "goals";
    /**
     * The key for a single Maven plugin goal.
     */
    public static final String GOAL = "goal";
    /**
     * The group ID for standard Apache Maven plugins.
     */
    public static final String ORG_APACHE_MAVEN_PLUGINS = "org.apache.maven.plugins";
    /**
     * The artifact ID for the Maven Compiler Plugin.
     */
    public static final String MAVEN_COMPILER_PLUGIN = "maven-compiler-plugin";
    /**
     * The group ID for the MapStruct library.
     */
    public static final String ORG_MAPSTRUCT = "org.mapstruct";
    /**
     * The artifact ID for the MapStruct library.
     */
    public static final String MAPSTRUCT = "mapstruct";
    /**
     * A key representing an entity.
     */
    public static final String ENTITY = "entity";

    /**
     * Represents the "infrastructure" layer or component.
     */
    public static final String INFRASTRUCTURE = "infrastructure";

    /**
     * Represents the "domain" layer or component.
     */
    public static final String DOMAIN = "domain";

    /**
     * Represents the "application" layer or component.
     */
    public static final String APP = "app";

    /**
     * A set of annotation names that are searched for within field definitions.
     */
    public static final Set<String> SEARCH_ANNOTATIONS_FIELD_KEYS = Set.of("Column", "JoinColumn", "ManyToOne",
            "ElementCollection");
    /**
     * A set of supported strategies for the {@code @GeneratedValue} annotation.
     */
    public static final Set<String> GENERATION_TYPES = Set.of("AUTO", "IDENTITY", "SEQUENCE", "TABLE", "UUID");
    /**
     * The group ID for the OpenAPI Tools organization.
     */
    public static final String ORG_OPENAPITOOLS = "org.openapitools";
    /**
     * The artifact ID for the OpenAPI Generator Maven Plugin.
     */
    public static final String OPENAPI_GENERATOR_MAVEN_PLUGIN = "openapi-generator-maven-plugin";

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private Constants() {
    }
}
