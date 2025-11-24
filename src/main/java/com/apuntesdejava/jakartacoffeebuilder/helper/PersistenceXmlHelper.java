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

import com.apuntesdejava.jakartacoffeebuilder.util.CoffeeBuilderUtil;
import com.apuntesdejava.jakartacoffeebuilder.util.PomUtil;
import com.apuntesdejava.jakartacoffeebuilder.util.XmlUtil;
import jakarta.json.JsonObject;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.dom4j.Document;
import org.dom4j.Namespace;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;

import static com.apuntesdejava.jakartacoffeebuilder.util.Constants.NAME;

/**
 * Helper class for managing `persistence.xml` files. Provides methods to create and save `persistence.xml` documents.
 *
 * @author Diego Silva diego.silva at apuntesdejava.com
 */
public class PersistenceXmlHelper {

    public static final String NS_PERSISTENCE = "https://jakarta.ee/xml/ns/persistence";

    private PersistenceXmlHelper() {
    }

    /**
     * Returns the singleton instance of the `PersistenceXmlHelper` class. This method ensures that only one instance of
     * the class is created (Singleton pattern).
     *
     * @return the singleton instance of `PersistenceXmlHelper`
     */
    public static PersistenceXmlHelper getInstance() {
        return PersistenceXmlUtilHolder.INSTANCE;
    }

    /**
     * Creates or loads a {@code persistence.xml} document.
     * <p>
     * If the file does not exist, this method initializes it with a standard {@code <persistence>} root element,
     * including the appropriate XML namespaces and schema locations based on the project's detected Jakarta EE version.
     * It also adds a new {@code <persistence-unit>} with the specified name. If the file already exists, it will be
     * loaded.
     *
     * @param mavenProject        the current Maven project, used to resolve paths and the Jakarta EE version.
     * @param log                 the Maven logger for outputting messages.
     * @param persistenceUnitName the name to be assigned to the new persistence unit.
     * @return an {@link Optional} containing the created or loaded {@link Document}, or an empty {@code Optional} if an
     * error occurs during file access.
     * @throws RuntimeException if an {@link IOException} occurs while fetching schema information.
     */
    public Optional<Document> createPersistenceXml(MavenProject mavenProject, Log log, String persistenceUnitName) {
        var currentPath = mavenProject.getBasedir().toPath();
        var xmlUtil = XmlUtil.getInstance();
        var xmlPath = getPersistencePath(currentPath);
        return xmlUtil.getDocument(log, xmlPath, document -> {
            try {
                var jakartaEeVersion = PomUtil.getJakartaEeCurrentVersion(mavenProject, log)
                    .orElseThrow();
                JsonObject schemaDescription = CoffeeBuilderUtil.getSchema(jakartaEeVersion,
                        "persistence")
                    .orElseThrow();

                var persistenceElem = document.addElement("persistence",
                    NS_PERSISTENCE);
                var xsiNS = new Namespace("xsi", "http://www.w3.org/2001/XMLSchema-instance");
                persistenceElem.add(xsiNS);
                persistenceElem.addAttribute("xsi:schemaLocation",
                    "%s %s".formatted(NS_PERSISTENCE, schemaDescription.getString("url")));
                persistenceElem.addAttribute("version", schemaDescription.getString("version"));

                var persistenceUnitElem = xmlUtil.addElement(persistenceElem,
                    "persistence-unit",
                    Map.of(NAME, persistenceUnitName));
                var propertiesElement = xmlUtil.addElement(persistenceUnitElem, "properties");
                xmlUtil.addElement(propertiesElement, "property", Map.of(
                    "name", "jakarta.persistence.schema-generation.database.action",
                    "value", "drop-and-create"
                ));

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * Saves the `persistence.xml` document to the specified path.
     *
     * @param currentPath the current path where the `persistence.xml` will be saved
     * @param log         the logger to use for logging messages
     * @param document    the `Document` to be saved
     */
    public void savePersistenceXml(Path currentPath, Log log, Document document) {
        XmlUtil.getInstance().saveDocument(document, log, getPersistencePath(currentPath));
    }

    /**
     * Resolves the path to the `persistence.xml` file. This method constructs the path by appending the standard
     * directory structure (`src/main/resources/META-INF`) to the provided base path.
     *
     * @param currentPath the base path from which the `persistence.xml` path will be resolved
     * @return the resolved `Path` to the `persistence.xml` file
     */
    protected Path getPersistencePath(Path currentPath) {
        return currentPath.resolve("src")
            .resolve("main")
            .resolve("resources")
            .resolve("META-INF")
            .resolve("persistence.xml");
    }

    /**
     * Adds a data source to the specified persistence unit within the `persistence.xml` file. If the `persistence.xml`
     * file does not exist, it will be created.
     *
     * @param mavenProject    the current Maven project, used to resolve paths.
     * @param log             the Maven logger for outputting messages.
     * @param persistenceUnit the name of the persistence unit to modify.
     * @param name            the JTA data source name to be added or updated.
     */
    public void addDataSourceToPersistenceXml(MavenProject mavenProject, Log log, String persistenceUnit, String name) {
        createPersistenceXml(mavenProject, log, persistenceUnit)
            .ifPresent(document -> {
                var xmlUtil = XmlUtil.getInstance();
                xmlUtil.findElementsStream(document,
                        "//*[local-name()='persistence-unit'][@name='%s'][not(*[local-name()='jta-data-source' and text()='%s'])]"
                            .formatted(persistenceUnit, name))
                    .findFirst()
                    .ifPresent(element -> {
                        xmlUtil.removeElement(element, "jta-data-source");
                        xmlUtil.addElement(element, "jta-data-source").setText(name);
                        var currentPath = mavenProject.getBasedir().toPath();
                        savePersistenceXml(currentPath, log, document);
                    });
            });
    }

    private static class PersistenceXmlUtilHolder {

        private static final PersistenceXmlHelper INSTANCE = new PersistenceXmlHelper();
    }
}
