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
import com.apuntesdejava.jakartacoffeebuilder.util.XmlUtil;
import jakarta.json.JsonValue;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.logging.Log;
import org.w3c.dom.Document;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;

import static com.apuntesdejava.jakartacoffeebuilder.util.Constants.HIBERNATE_PROVIDER;

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
     * Creates a new `persistence.xml` document.
     *
     * @param currentPath         the current path where the `persistence.xml` will be created
     * @param log                 the logger to use for logging messages
     * @param persistenceUnitName the name of the persistence unit
     * @return an `Optional` containing the created `Document`, or an empty `Optional` if the document could not be created
     */
    public Optional<Document> createPersistenceXml(Path currentPath, Log log, String persistenceUnitName) {
        var xmlUtil = XmlUtil.getInstance();
        var xmlPath = getPersistencePath(currentPath);
        return xmlUtil.getDocument(log, xmlPath, document -> {

            var persistenceElem = document.createElement("persistence");
            persistenceElem.setAttribute("xmlns", "https://jakarta.ee/xml/ns/persistence");
            persistenceElem.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
            persistenceElem.setAttribute("xsi:schemaLocation",
                "https://jakarta.ee/xml/ns/persistence https://jakarta.ee/xml/ns/persistence/persistence_3_0.xsd");
            persistenceElem.setAttribute("version", "3.0");

            var persistenceUnitElem = xmlUtil.addElement(persistenceElem, "persistence-unit");
            persistenceUnitElem.setAttribute("name", persistenceUnitName);
            document.appendChild(persistenceElem);
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
     * Adds a data source to the `persistence.xml` file.
     *
     * @param currentPath     the current path where the `persistence.xml` is located
     * @param log             the logger to use for logging messages
     * @param persistenceUnit the name of the persistence unit to which the data source will be added
     * @param name            the name of the data source to be added
     */
    public void addDataSourceToPersistenceXml(Path currentPath, Log log, String persistenceUnit, String name) {
        createPersistenceXml(currentPath, log, persistenceUnit)
            .ifPresent(document -> {
                var xmlUtil = XmlUtil.getInstance();
                xmlUtil.findElementsStream(document, log,
                           "//persistence-unit[@name='%s' and not(jta-data-source/text()='%s')]"
                               .formatted(persistenceUnit, name))
                       .findFirst()
                       .ifPresent(element -> {
                           xmlUtil.removeElement(element,"jta-data-source");
                           xmlUtil.addElement(element, "jta-data-source").setTextContent(name);
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
            xmlUtil.findElementsStream(persistenceXml, log, "//persistence-unit")
                   .findFirst()
                   .ifPresent(elem -> {
                       if (xmlUtil.findElementsStream(persistenceXml, log,
                                      "//persistence-unit/provider[text()='%s']".formatted(HIBERNATE_PROVIDER))
                                  .findFirst().isEmpty())
                           xmlUtil.addElementAtStart(elem, log, "provider", HIBERNATE_PROVIDER);
                       xmlUtil.getElement(elem, "properties").ifPresent(properties -> {
                           if (StringUtils.isNotBlank(dialectClass))
                               xmlUtil.addElement(properties, "property",
                                   Map.of("name", "hibernate.dialect", "value", dialectClass));
                           try {
                               CoffeeBuilderUtil.getPropertiesConfiguration("hibernate")
                                                .ifPresent(propertiesConfig -> propertiesConfig
                                                    .stream()
                                                    .map(JsonValue::asJsonObject)
                                                    .forEach(property -> {
                                                        var name = property.getString("name");
                                                        var value = property.getString("value");
                                                        xmlUtil.addElement(
                                                            properties, "property",
                                                            Map.of("name", name, "value", value)
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
