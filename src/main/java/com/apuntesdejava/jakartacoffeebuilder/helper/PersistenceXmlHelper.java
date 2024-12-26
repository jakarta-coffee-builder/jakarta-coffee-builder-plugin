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
package com.apuntesdejava.jakartacoffeebuilder.helper;

import com.apuntesdejava.jakartacoffeebuilder.util.XmlUtil;
import org.apache.maven.plugin.logging.Log;
import org.w3c.dom.Document;

import java.nio.file.Path;
import java.util.Optional;

/**
 * Helper class for managing `persistence.xml` files.
 * Provides methods to create and save `persistence.xml` documents.
 *
 * @author Diego Silva <diego.silva at apuntesdejava.com>
 */
public class PersistenceXmlHelper {

    private PersistenceXmlHelper() {
    }

    public static PersistenceXmlHelper getInstance() {
        return PersistenceXmlUtilHolder.INSTANCE;
    }

    /**
     * Creates a new `persistence.xml` document.
     *
     * @param currentPath         the current path where the `persistence.xml` will be created
     * @param log                 the logger to use for logging messages
     * @param jakartaEeVersion    the Jakarta EE version to use
     * @param persistenceUnitName the name of the persistence unit
     * @return an `Optional` containing the created `Document`, or an empty `Optional` if the document could not be created
     */
    public Optional<Document> createPersistenceXml(Path currentPath, Log log, String jakartaEeVersion,
                                                   String persistenceUnitName) {
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

    private Path getPersistencePath(Path currentPath) {
        return currentPath.resolve("src")
                          .resolve("main")
                          .resolve("resources")
                          .resolve("META-INF")
                          .resolve("persistence.xml");
    }

    private static class PersistenceXmlUtilHolder {

        private static final PersistenceXmlHelper INSTANCE = new PersistenceXmlHelper();
    }
}
