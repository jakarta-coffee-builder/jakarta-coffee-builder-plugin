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

import com.apuntesdejava.jakartacoffeebuilder.model.NamespaceContextMap;
import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.logging.Log;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
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
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static com.apuntesdejava.jakartacoffeebuilder.util.Constants.XML_XSLT;

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


    public static XmlUtil getInstance() {
        return XmlUtilHolder.INSTANCE;
    }

    private final DocumentBuilder dBuilder;

    private XmlUtil() {
        try {
            var dbFactory = DocumentBuilderFactory.newInstance();
            this.dBuilder = dbFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    private File createXsltTemp(String xsltName) throws IOException {
        var classLoader = XmlUtil.class.getClassLoader();
        var resourceUrl = classLoader.getResource(xsltName);
        if (resourceUrl == null) throw new RuntimeException("Resource not found: " + xsltName);
        var tempFile = File.createTempFile("resource-temp", ".xslt");
        tempFile.deleteOnExit();
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
    public void addElement(Element parent, String tagName, String content) {
        var element = parent.getOwnerDocument().createElement(tagName);
        element.setTextContent(content);
        parent.appendChild(element);
    }

    public void addElement(Element parent, String tagName, Map<String,String> properties) {
        var element = parent.getOwnerDocument().createElement(tagName);
        properties.forEach(element::setAttribute);
        parent.appendChild(element);
    }

    /**
     * Adds a new element with the specified tag name to the given parent element.
     *
     * @param parent  the parent element to which the new element will be added
     * @param tagName the tag name of the new element
     * @return the newly created element
     */
    public Element addElement(Element parent, String tagName) {
        var element = parent.getOwnerDocument().createElement(tagName);
        parent.appendChild(element);
        return element;
    }

    /**
     * Adds a new element with the specified tag name to the given parent element.
     *
     * @param parent  the parent element to which the new element will be added
     * @param tagName the tag name of the new element
     * @return the newly created element
     */
    public Element addElementAtStart(Element parent, Log log, String tagName, String textContent) {
        if (findElementsStream(parent.getOwnerDocument(), log, tagName).findFirst().isPresent()) return null;
        var element = parent.getOwnerDocument().createElement(tagName);
        element.setTextContent(textContent);
        if (parent.getFirstChild() != null) {
            parent.insertBefore(element, parent.getFirstChild());
        } else {
            parent.appendChild(element);
        }
        return element;
    }

    /**
     * Adds a new element with the specified namespace and tag name to the given parent element.
     *
     * @param parent    the parent element to which the new element will be added
     * @param namespace the namespace URI of the new element
     * @param tagName   the tag name of the new element
     * @return the newly created element
     */
    public Element addElementNS(Element parent, String namespace, String tagName) {
        var element = parent.getOwnerDocument().createElementNS(namespace, tagName);
        parent.appendChild(element);
        return element;
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
    public Element addElement(Document parent, Log log, String parentNode,
                              String nodeName, Consumer<Element> postCreate) {
        var nodeList = findElements(parent, log, parentNode);
        if (nodeList.getLength() == 0) {
            log.error("Parent node not found: " + parentNode);
            return null;
        }
        var node = nodeList.item(0);
        var element = parent.createElement(nodeName);
        if (postCreate != null) {
            postCreate.accept(element);
        }
        node.appendChild(element);
        return element;
    }

    /**
     * Adds a new element to the specified parent node in the given XML document.
     *
     * @param parent     the XML document to which the new element will be added
     * @param log        the logger to use for logging messages
     * @param parentNode the XPath expression to locate the parent node
     * @param nodeName   the name of the new element to be added
     * @return the newly created element
     */
    public Element addElement(Document parent, Log log, String parentNode,
                              String nodeName) {
        return addElement(parent, log, parentNode, nodeName, null);
    }

    /**
     * Retrieves an XML document from the specified path. If the file does not exist, it creates a new document.
     *
     * @param log                the logger to use for logging messages
     * @param path               the path to the XML file
     * @param createDocumentType a function to create a new document type if the file does not exist
     * @param postCreate         a consumer to perform additional operations on the document after creation
     * @return an Optional containing the XML Document if the file exists or was created successfully, otherwise an empty Optional
     */
    public Optional<Document> getDocument(Log log,
                                          Path path,
                                          Function<DocumentBuilder, Document> createDocumentType,
                                          Consumer<Document> postCreate) {
        try {

            if (Files.exists(path)) {
                var doc = dBuilder.parse(path.toFile());
                doc.getDocumentElement().normalize();
                return Optional.of(doc);
            }
            Files.createDirectories(path.getParent());
            var doc = Optional.ofNullable(createDocumentType)
                              .map(p -> p.apply(dBuilder))
                              .orElseGet(dBuilder::newDocument);
            Optional.ofNullable(postCreate).ifPresent(p -> p.accept(doc));
            return Optional.of(doc);
        } catch (IOException | SAXException e) {
            log.error(e.getMessage(), e);
            return Optional.empty();
        }
    }

    /**
     * Retrieves an XML document from the specified file path if it exists.
     * Parses and normalizes the document before returning it.
     * Logs an error message if the document cannot be parsed or read.
     *
     * @param log  the logger to use for logging error messages
     * @param path the file path to the XML document
     * @return an Optional containing the XML Document if it exists and is successfully parsed,
     * otherwise an empty Optional
     */
    public Optional<Document> getDocument(Log log, Path path) {
        if (!Files.exists(path)) return Optional.empty();
        try {
            var doc = dBuilder.parse(path.toFile());
            doc.getDocumentElement().normalize();
            return Optional.of(doc);
        } catch (SAXException | IOException e) {
            log.error(e.getMessage(), e);
        }
        return Optional.empty();
    }

    /**
     * Retrieves an XML document from the specified path. If the file does not exist, it creates a new document.
     *
     * @param log        the logger to use for logging messages
     * @param path       the path to the XML file
     * @param postCreate a consumer to perform additional operations on the document after creation
     * @return an Optional containing the XML Document if the file exists or was created successfully, otherwise an empty Optional
     */
    public Optional<Document> getDocument(Log log, Path path, Consumer<Document> postCreate) {
        return getDocument(log, path, null, postCreate);
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
        return findElements(doc, log, expression, Map.of());
    }

    /**
     * Finds elements in the given XML document based on the provided XPath expression and
     * returns them as a stream of {@code Element} objects.
     *
     * @param doc        the XML document to search
     * @param log        the logger to use for logging messages
     * @param expression the XPath expression used to evaluate and find matching elements
     * @return a {@code Stream} containing the matching {@code Element} objects
     */
    public Stream<Element> findElementsStream(Document doc, Log log, String expression) {
        var nodeList = findElements(doc, log, expression, Map.of());
        return IntStream.range(0, nodeList.getLength())
                        .mapToObj(nodeList::item)
                        .map(node -> (Element) node);
    }

    /**
     * Finds elements in the given XML document using the specified XPath expression and optional namespace mappings.
     *
     * @param doc        the XML document to search
     * @param log        the logger to use for logging messages
     * @param expression the XPath expression to evaluate
     * @param namespaces a map of namespace prefixes to namespace URIs for resolving namespaces in the XPath expression
     * @return a NodeList containing the elements matching the XPath expression
     * @throws RuntimeException if an error occurs while evaluating the XPath expression
     */
    public NodeList findElements(Node doc, Log log, String expression, Map<String, String> namespaces) {
        try {
            var xPathFactory = XPathFactory.newInstance();
            var xPath = xPathFactory.newXPath();
            if (namespaces != null && !namespaces.isEmpty())
                namespaces.forEach((key, value) -> xPath.setNamespaceContext(new NamespaceContextMap(key, value)));
            var xPathExpression = xPath.compile(expression);
            return (NodeList) xPathExpression.evaluate(doc, XPathConstants.NODESET);
        } catch (XPathExpressionException ex) {
            log.error(ex.getMessage(), ex);
            throw new RuntimeException(ex);
        }
    }

    /**
     * Finds elements in the given XML document based on the provided XPath expression and
     * returns them as a stream of {@code Element} objects.
     *
     * @param doc        the XML document to search
     * @param log        the logger to use for logging messages
     * @param expression the XPath expression used to evaluate and find matching elements
     * @return a {@code Stream} containing the matching {@code Element} objects
     */
    public Stream<Element> findElementsStream(Node doc,
                                              Log log,
                                              String expression,
                                              Map<String, String> namespaces) {
        var nodeList = findElements(doc, log, expression, namespaces);
        return IntStream.range(0, nodeList.getLength())
                        .mapToObj(nodeList::item)
                        .map(node -> (Element) node);
    }

    /**
     * Saves the given XML document to the specified path.
     *
     * @param document the XML document to save
     * @param log      the logger to use for logging messages
     * @param xmlPath  the path to save the XML document
     */
    public void saveDocument(Document document, Log log, Path xmlPath) {
        saveDocument(document, log, xmlPath, XML_XSLT);
    }

    /**
     * Saves the specified XML document to the given path, applying an XSLT transformation.
     *
     * @param document the XML document to save
     * @param log      the logger to use for logging messages
     * @param xmlPath  the path to save the transformed XML document
     * @param xsltName the name of the XSLT file to apply for the transformation
     */
    public void saveDocument(Document document, Log log, Path xmlPath, String xsltName) {
        try {
            var xlstFile = createXsltTemp(xsltName);
            var transformerFactory = TransformerFactory.newInstance();
            var styleSource = new StreamSource(xlstFile);
            var transformer = transformerFactory.newTransformer(styleSource);
            var source = new DOMSource(document);
            var result = new StreamResult(xmlPath.toFile());
            transformer.transform(source, result);
            log.debug("Saved document to: " + xmlPath);
        } catch (TransformerException | IOException ex) {
            log.error(ex.getMessage(), ex);
        }
    }

    /**
     * Retrieves the first child element with the specified tag name from the given parent element.
     *
     * @param parentElement the parent element to search within
     * @param elementName   the tag name of the child element to retrieve
     * @return the first child element with the specified tag name, or a new element if not found
     */
    public Optional<Element> getElement(Element parentElement, String elementName) {
        NodeList nodeList = parentElement.getElementsByTagName(elementName);
        if (nodeList.getLength() > 0) {
            return Optional.of((Element) nodeList.item(0));
        }
        return Optional.of(addElement(parentElement, elementName));
    }

    private static class XmlUtilHolder {

        private static final XmlUtil INSTANCE = new XmlUtil();
    }
}
