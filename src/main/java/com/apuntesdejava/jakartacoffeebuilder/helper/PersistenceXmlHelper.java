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
import jakarta.json.JsonValue;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.dom4j.Document;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;

import static com.apuntesdejava.jakartacoffeebuilder.util.Constants.HIBERNATE_PROVIDER;
import static com.apuntesdejava.jakartacoffeebuilder.util.Constants.NAME;
import static com.apuntesdejava.jakartacoffeebuilder.util.Constants.VALUE;

/**
 * Helper class for managing `persistence.xml` files.
 * Provides methods to create and save `persistence.xml` documents.
 *
 * @author Diego Silva diego.silva at apuntesdejava.com
 */
public class PersistenceXmlHelper {

    private PersistenceXmlHelper() {
    }

    /**
     * Returns the singleton instance of the `PersistenceXmlHelper` class.
     * This method ensures that only one instance of the class is created
     * (Singleton pattern).
     *
     * @return the singleton instance of `PersistenceXmlHelper`
     */
    public static PersistenceXmlHelper getInstance() {
        return PersistenceXmlUtilHolder.INSTANCE;
    }

    /**
     * Creates or loads a {@code persistence.xml} document.
     * <p>
     * If the file does not exist, this method initializes it with a standard {@code <persistence>}
     * root element, including the appropriate XML namespaces and schema locations based on the
     * project's detected Jakarta EE version. It also adds a new {@code <persistence-unit>}
     * with the specified name. If the file already exists, it will be loaded.
     *
     * @param mavenProject        the current Maven project, used to resolve paths and the Jakarta EE version.
     * @param log                 the Maven logger for outputting messages.
     * @param persistenceUnitName the name to be assigned to the new persistence unit.
     * @return an {@link Optional} containing the created or loaded {@link Document}, or an empty
     * {@code Optional} if an error occurs during file access.
     * @throws RuntimeException if an {@link IOException} occurs while fetching schema information.
     */
    public Optional<Document> createPersistenceXml(MavenProject mavenProject, Log log, String persistenceUnitName) {
        var currentPath = mavenProject.getBasedir().toPath();
        var xmlUtil = XmlUtil.getInstance();
        var xmlPath = getPersistencePath(currentPath);
        return xmlUtil.getDocument(log, xmlPath, document -> {
            try {
                var jakartaEeVersion = PomUtil.getJakartaEeCurrentVersion(mavenProject, log).orElseThrow();
                JsonObject schemaDescription = CoffeeBuilderUtil.getSchema(jakartaEeVersion,
                    "persistence").orElseThrow();

                var persistenceElem = document.addElement("persistence");
                persistenceElem.addAttribute("xmlns", "https://jakarta.ee/xml/ns/persistence");
                persistenceElem.addAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
                persistenceElem.addAttribute("xsi:schemaLocation",
                    "https://jakarta.ee/xml/ns/persistence " + schemaDescription.getString("url"));
                persistenceElem.addAttribute("version", schemaDescription.getString("version"));

                var persistenceUnitElem = xmlUtil.addElement(persistenceElem, "persistence-unit");
                persistenceUnitElem.addAttribute(NAME, persistenceUnitName);
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
     * Resolves the path to the `persistence.xml` file.
     * This method constructs the path by appending the standard directory structure
     * (`src/main/resources/META-INF`) to the provided base path.
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
     * Adds a data source to the specified persistence unit within the `persistence.xml` file.
     * If the `persistence.xml` file does not exist, it will be created.
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
                           "//persistence-unit[@name='%s' and not(jta-data-source/text()='%s')]"
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

    /**
     * Adds a provider to the `persistence.xml` file.
     *
     * @param currentPath  the current path where the `persistence.xml` is located
     * @param log          the logger to use for logging messages
     * @param dialectClass the class name of the dialect to be added
     */
    public void addProviderToPersistenceXml(Path currentPath, Log log, String dialectClass) {
        var persistencePath = getPersistencePath(currentPath);
        var xmlUtil = XmlUtil.getInstance();
        xmlUtil.getDocument(log, persistencePath).ifPresent(persistenceXml -> {
            xmlUtil.findElementsStream(persistenceXml, "//persistence-unit")
                   .findFirst()
                   .ifPresent(elem -> {
                       if (xmlUtil.findElementsStream(persistenceXml,
                                      "//persistence-unit/provider[text()='%s']".formatted(HIBERNATE_PROVIDER))
                                  .findFirst().isEmpty())
                           xmlUtil.addElementAtStart(elem, log, "provider", HIBERNATE_PROVIDER);
                       xmlUtil.getElement(elem, "properties").ifPresent(properties -> {
                           if (StringUtils.isNotBlank(dialectClass))
                               xmlUtil.addElement(properties, "property",
                                   Map.of(NAME, "hibernate.dialect", VALUE, dialectClass));
                           try {
                               CoffeeBuilderUtil.getPropertiesConfiguration("jpa-hibernate")
                                                .ifPresent(propertiesConfig -> propertiesConfig
                                                    .stream()
                                                    .map(JsonValue::asJsonObject)
                                                    .forEach(property -> {
                                                        var name = property.getString(NAME);
                                                        var value = property.getString(VALUE);
                                                        xmlUtil.addElement(
                                                            properties, "property",
                                                            Map.of(NAME, name, VALUE, value)
                                                        );
                                                    }));
                           } catch (IOException e) {
                               log.error(e.getMessage(), e);
                           }
                       });

                   });
            xmlUtil.saveDocument(persistenceXml, log, persistencePath);
        });

    }

    private static class PersistenceXmlUtilHolder {

        private static final PersistenceXmlHelper INSTANCE = new PersistenceXmlHelper();
    }
}
