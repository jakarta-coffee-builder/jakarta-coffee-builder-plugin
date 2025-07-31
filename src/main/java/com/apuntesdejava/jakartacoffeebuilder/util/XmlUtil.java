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

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.logging.Log;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.Node;
import org.dom4j.QName;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;

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
 * <p>
 * Note: This class is thread-safe.
 * </p>
 * <p>
 * Author: Diego Silva &lt;diego.silva at apuntesdejava.com&gt;
 * </p>
 */
public class XmlUtil {

    /**
     * Retrieves the singleton instance of the `XmlUtil` class.
     * <p>
     * This method ensures that only one instance of the `XmlUtil` class
     * is created and provides a global point of access to it.
     * </p>
     *
     * @return the singleton instance of `XmlUtil`
     */
    public static XmlUtil getInstance() {
        return XmlUtilHolder.INSTANCE;
    }


    private XmlUtil() {
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
        var element = parent.addElement(tagName);
        element.setText(content);
    }

    private boolean existsChildElement(Element parent, String tagName, Map<String, String> attributes) {
        List<Element> childElements = parent.elements(tagName);
        if (childElements.isEmpty()) {
            return false;
        }

        for (Element child : childElements) {
            Map<String, String> childAttributes = new LinkedHashMap<>();
            child.attributes().forEach(attr -> childAttributes.put(attr.getName(), attr.getValue()));

            if (CollectionsUtil.areEqual(childAttributes, attributes)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Adds a new element with the specified tag name and attributes to the given parent element.
     * <p>
     * If a child element with the same tag name and attributes already exists, this method does nothing.
     * </p>
     *
     * @param parent     the parent element to which the new element will be added
     * @param tagName    the tag name of the new element
     * @param attributes a map of attributes to set on the new element
     */
    public void addElement(Element parent, String tagName, Map<String, String> attributes) {
        if (existsChildElement(parent, tagName, attributes)) return;

        var element = parent.addElement(tagName);
        attributes.forEach(element::addAttribute);
    }

    /**
     * Adds a new element with the specified tag name to the given parent element.
     *
     * @param parent  the parent element to which the new element will be added
     * @param tagName the tag name of the new element
     * @return the newly created element
     */
    public Element addElement(Element parent, String tagName) {
        return parent.addElement(tagName);
    }

    /**
     * Adds a new element with the specified tag name and text content at the start of the given parent element.
     * <p>
     * If an element with the same tag name already exists in the document, this method does nothing.
     * </p>
     *
     * @param parent      the parent element to which the new element will be added
     * @param log         the logger to use for logging messages
     * @param tagName     the tag name of the new element
     * @param textContent the text content of the new element
     */
    public void addElementAtStart(Element parent, Log log, String tagName, String textContent) {
        if (findElementsStream(parent.getDocument(), tagName).findFirst().isPresent()) return;
        var element = DocumentHelper.createElement(tagName);
        element.setText(textContent);
        parent.content().addFirst(element);
    }

    /**
     * Adds a new element with the specified tag name and namespace to the given parent element.
     *
     * @param element   the parent element to which the new element will be added
     * @param tagName   the tag name of the new element
     * @param namespace the namespace of the new element
     * @return the newly created element
     */
    public Element addElement(Element element, String tagName, Namespace namespace) {
        return element.addElement(new QName(tagName, namespace));
    }

    /**
     * Adds a new element to the specified document node in the given XML document.
     *
     * @param document   the XML document to which the new element will be added
     * @param log        the logger to use for logging messages
     * @param parentNode the XPath expression to locate the document node
     * @param nodeName   the name of the new element to be added
     * @param postCreate a consumer to perform additional operations on the new element after creation
     * @return the newly created element
     */
    public Element addElement(Document document, Log log, String parentNode,
                              String nodeName, Consumer<Element> postCreate) {
        var nodeList = findElements(document, parentNode).toList();
        if (nodeList.isEmpty()) {
            log.error("Parent node not found: " + parentNode);
            return null;
        }
        var element = nodeList.getFirst().addElement(nodeName);
        if (postCreate != null) {
            postCreate.accept(element);
        }
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
                                          Supplier<Document> createDocumentType,
                                          Consumer<Document> postCreate) {
        try {
            if (Files.exists(path)) {
                return Optional.of(getDocument(path));
            }
            Files.createDirectories(path.getParent());
            var doc = createDocumentType == null
                ? DocumentHelper.createDocument()
                : createDocumentType.get();
            Optional.ofNullable(postCreate).ifPresent(p -> p.accept(doc));
            return Optional.of(doc);
        } catch (IOException | DocumentException e) {
            log.error(e.getMessage(), e);
            return Optional.empty();
        }
    }

    private Document getDocument(Path path) throws IOException, DocumentException {
        return DocumentHelper.parseText(Files.readString(path));
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
            return Optional.of(getDocument(path));
        } catch (IOException | DocumentException e) {
            log.error(e.getMessage(), e);
        }
        return getDocument(log, path, null, null);
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
     * @param expression the XPath expression to evaluate
     * @return a NodeList containing the matching elements
     * @throws RuntimeException if an error occurs while evaluating the XPath expression
     */
    public Stream<Element> findElements(Document doc, String expression) {
        return findElements(doc, expression, Map.of());
    }

    /**
     * Finds elements in the given XML document based on the provided XPath expression and
     * returns them as a stream of {@code Element} objects.
     *
     * @param doc        the XML document to search
     * @param expression the XPath expression used to evaluate and find matching elements
     * @return a {@code Stream} containing the matching {@code Element} objects
     */
    public Stream<Element> findElementsStream(Document doc, String expression) {
        return findElements(doc, expression, Map.of());
    }

    /**
     * Finds elements in the given XML document using the specified XPath expression and optional namespace mappings.
     *
     * @param doc        the XML document to search
     * @param expression the XPath expression to evaluate
     * @param namespaces a map of namespace prefixes to namespace URIs for resolving namespaces in the XPath expression
     * @return a NodeList containing the elements matching the XPath expression
     * @throws RuntimeException if an error occurs while evaluating the XPath expression
     */
    public Stream<Element> findElements(Node doc,
                                        String expression,
                                        Map<String, String> namespaces) {
        Objects.requireNonNull(doc, "Document cannot be null");
        var xPath = doc.createXPath(expression);
        if (namespaces != null && !namespaces.isEmpty())
            xPath.setNamespaceURIs(namespaces);
        return xPath.selectNodes(doc).stream().map(Element.class::cast);
    }

    /**
     * Finds elements in the given XML document based on the provided XPath expression and
     * returns them as a stream of {@code Element} objects.
     *
     * @param doc        the XML document to search
     * @param expression the XPath expression used to evaluate and find matching elements
     * @param namespaces a map of namespace prefixes to namespace URIs for resolving namespaces in the XPath expression
     * @return a {@code Stream} containing the matching {@code Element} objects
     */
    public Stream<Element> findElementsStream(Node doc,
                                              String expression,
                                              Map<String, String> namespaces) {
        return findElements(doc, expression, namespaces);
    }


    /**
     * Saves the specified XML document to the given path, applying an XSLT transformation.
     *
     * @param document the XML document to save
     * @param log      the logger to use for logging messages
     * @param xmlPath  the path to save the transformed XML document
     */
    public void saveDocument(Document document, Log log, Path xmlPath) {
        var format = OutputFormat.createPrettyPrint();
        format.setIndentSize(4);
        format.setSuppressDeclaration(false);
        format.setEncoding("UTF-8");

        try (StringWriter sw = new StringWriter(); XMLWriter writer = new XMLWriter(sw, format)) {
            writer.write(document);
            var contents = sw.toString();
            Files.writeString(xmlPath, contents);
            log.debug("Saved document to: " + xmlPath);
        } catch (IOException ex) {
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
        var element = Optional.ofNullable((Element) parentElement.selectSingleNode(elementName));
        if (element.isEmpty())
            return Optional.of(addElement(parentElement, elementName));
        return element;
    }

    /**
     * Removes the first child element with the specified tag name from the given parent element.
     *
     * @param element         the parent element from which the child element will be removed
     * @param tagNameToRemove the tag name of the child element to be removed
     */
    public void removeElement(Element element, String tagNameToRemove) {
        Optional.ofNullable((Element) element.selectSingleNode(tagNameToRemove)).ifPresent(element::remove);
    }

    private static class XmlUtilHolder {

        private static final XmlUtil INSTANCE = new XmlUtil();
    }
}
