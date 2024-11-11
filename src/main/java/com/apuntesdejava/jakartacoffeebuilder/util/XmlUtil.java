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

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.logging.Log;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * Utility class for XML operations.
 * <p>
 * This class provides methods to manipulate XML documents, including adding elements,
 * finding elements using XPath expressions, and saving documents to files.
 * </p>
 * <p>
 * This class follows the Singleton design pattern to ensure only one instance is created.
 * </p>
 * <p>
 * Usage example:
 * <pre>
 *     XmlUtil xmlUtil = XmlUtil.getInstance();
 *     Document document = xmlUtil.getDocument(log, path, postCreate).orElseThrow();
 *     xmlUtil.addElement(document, log, parentNode, nodeName, postCreate);
 *     xmlUtil.saveDocument(document, log, xmlPath);
 * </pre>
 * </p>
 * <p>
 * Note: This class is thread-safe.
 * </p>
 * <p>
 * Author: Diego Silva &lt;diego.silva at apuntesdejava.com&gt;
 * </p>
 */
public class XmlUtil {

    private final File xlstFile;

    public static XmlUtil getInstance() {
        return XmlUtilHolder.INSTANCE;
    }

    private final DocumentBuilder dBuilder;

    private XmlUtil() {
        try {
            var dbFactory = DocumentBuilderFactory.newInstance();
            this.dBuilder = dbFactory.newDocumentBuilder();
            this.xlstFile = createXsltTemp();
        } catch (ParserConfigurationException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private File createXsltTemp() throws IOException {
        var classLoader = XmlUtil.class.getClassLoader();
        var resourceUrl = classLoader.getResource("format-xml.xslt");
        if (resourceUrl == null) throw new RuntimeException("Resource not found: format-xml.xslt");
        var tempFile = File.createTempFile("resource-temp", ".xslt");
        FileUtils.copyURLToFile(resourceUrl, tempFile);
        return tempFile;
    }

    /**
     * Adds a new element with the specified tag name and content to the given parent element.
     *
     * @param parent  the parent element to which the new element will be added
     * @param tagName the tag name of the new element
     * @param content the text content of the new element
     */
    public void addElement(Element parent, String tagName,
                           String content) {
        var element = parent.getOwnerDocument().createElement(tagName);
        element.setTextContent(content);
        parent.appendChild(element);
    }

    /**
     * Adds a new element to the specified parent node in the given XML document.
     *
     * @param parent     the XML document to which the new element will be added
     * @param log        the logger to use for logging messages
     * @param parentNode the XPath expression to locate the parent node
     * @param nodeName   the name of the new element to be added
     * @param postCreate a consumer to perform additional operations on the new element after creation
     */
    public void addElement(Document parent, Log log, String parentNode,
                           String nodeName, Consumer<Element> postCreate) {
        var nodeList = findElements(parent, log, parentNode);
        if (nodeList.getLength() == 0) {
            log.error("Parent node not found: " + parentNode);
            return;
        }
        var node = nodeList.item(0);
        var element = parent.createElement(nodeName);
        if (postCreate != null) {
            postCreate.accept(element);
        }
        node.appendChild(element);
    }

    /**
     * Retrieves an XML document from the specified path. If the file does not exist, it creates a new document.
     *
     * @param log        the logger to use for logging messages
     * @param path       the path to the XML file
     * @param postCreate a consumer to perform additional operations on the document after creation
     * @return an Optional containing the XML Document if the file exists or was created successfully, otherwise an empty Optional
     * @throws IOException if an error occurs while accessing or creating the XML file
     */
    public Optional<Document> getDocument(Log log, Path path, Consumer<Document> postCreate) throws IOException {
        try {

            if (Files.exists(path)) {
                var doc = dBuilder.parse(path.toFile());
                doc.getDocumentElement().normalize();
                return Optional.of(doc);
            }
            Files.createDirectories(path.getParent());
            var doc = dBuilder.newDocument();
            if (postCreate != null) {
                postCreate.accept(doc);
            }
            return Optional.of(doc);
        } catch (SAXException e) {
            log.error(e.getMessage(), e);
            return Optional.empty();

        }
    }

    /**
     * Finds elements in the given XML document that match the specified XPath expression.
     *
     * @param doc        the XML document to search
     * @param log        the logger to use for logging messages
     * @param expression the XPath expression to evaluate
     * @return a NodeList containing the matching elements
     * @throws RuntimeException if an error occurs while evaluating the XPath expression
     */
    public NodeList findElements(Document doc, Log log, String expression) {
        try {
            var xPathFactory = XPathFactory.newInstance();
            var xPath = xPathFactory.newXPath();
            var xPathExpression = xPath.compile(expression);
            return (NodeList) xPathExpression.evaluate(doc, XPathConstants.NODESET);
        } catch (XPathExpressionException ex) {
            log.error(ex.getMessage(), ex);
            throw new RuntimeException(ex);
        }
    }

    /**
     * Saves the given XML document to the specified path.
     *
     * @param document the XML document to save
     * @param log      the logger to use for logging messages
     * @param xmlPath  the path to save the XML document
     */
    public void saveDocument(Document document, Log log, Path xmlPath) {
        try {
            var transformerFactory = TransformerFactory.newInstance();
            var styleSource = new StreamSource(xlstFile);
            var transformer = transformerFactory.newTransformer(styleSource);
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
            var source = new DOMSource(document);
            var result = new StreamResult(xmlPath.toFile());
            transformer.transform(source, result);
            log.debug("Saved document to: " + xmlPath);
        } catch (TransformerException ex) {
            log.error(ex.getMessage(), ex);
        }
    }

    private static class XmlUtilHolder {

        private static final XmlUtil INSTANCE = new XmlUtil();
    }
}
