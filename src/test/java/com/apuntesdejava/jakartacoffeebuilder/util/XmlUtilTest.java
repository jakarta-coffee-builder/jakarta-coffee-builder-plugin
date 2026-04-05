package com.apuntesdejava.jakartacoffeebuilder.util;

import org.apache.maven.plugin.logging.Log;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

class XmlUtilTest {

    private XmlUtil xmlUtil;
    private Log mockLog;

    @BeforeEach
    void setUp() {
        xmlUtil = XmlUtil.getInstance();
        mockLog = mock(Log.class);
    }

    @Test
    @DisplayName("getInstance: should return the same singleton instance")
    void instanceIsSingleton() {
        assertSame(XmlUtil.getInstance(), XmlUtil.getInstance());
    }

    @Nested
    @DisplayName("addElement(parent, tagName, content)")
    class AddElementWithContent {

        @Test
        @DisplayName("should add child element with text content")
        void addsChildWithText() {
            Document doc = DocumentHelper.createDocument();
            Element root = doc.addElement("root");

            xmlUtil.addElement(root, "child", "hello");

            Element child = root.element("child");
            assertNotNull(child);
            assertEquals("hello", child.getText());
        }
    }

    @Nested
    @DisplayName("addElement(parent, tagName)")
    class AddElementSimple {

        @Test
        @DisplayName("should add empty child element")
        void addsEmptyChild() {
            Document doc = DocumentHelper.createDocument();
            Element root = doc.addElement("root");

            Element result = xmlUtil.addElement(root, "child");

            assertNotNull(result);
            assertEquals("child", result.getName());
        }
    }

    @Nested
    @DisplayName("addElement(parent, tagName, attributes)")
    class AddElementWithAttributes {

        @Test
        @DisplayName("should add element with attributes")
        void addsElementWithAttributes() {
            Document doc = DocumentHelper.createDocument();
            Element root = doc.addElement("root");
            Map<String, String> attrs = new LinkedHashMap<>();
            attrs.put("name", "test");
            attrs.put("value", "123");

            Element result = xmlUtil.addElement(root, "param", attrs);

            // When element is new, result is the new element
            Element param = root.element("param");
            assertNotNull(param);
            assertEquals("test", param.attributeValue("name"));
            assertEquals("123", param.attributeValue("value"));
        }

        @Test
        @DisplayName("should not add duplicate element with same attributes")
        void skipsDuplicateElement() {
            Document doc = DocumentHelper.createDocument();
            Element root = doc.addElement("root");
            Map<String, String> attrs = new LinkedHashMap<>();
            attrs.put("name", "test");

            xmlUtil.addElement(root, "param", attrs);
            xmlUtil.addElement(root, "param", attrs); // duplicate

            assertEquals(1, root.elements("param").size());
        }
    }

    @Nested
    @DisplayName("addElement(parent, tagName, namespace)")
    class AddElementWithNamespace {

        @Test
        @DisplayName("should add element with namespace")
        void addsElementWithNamespace() {
            Document doc = DocumentHelper.createDocument();
            Element root = doc.addElement("root");
            Namespace ns = new Namespace("ns", "http://example.com/ns");

            Element result = xmlUtil.addElement(root, "child", ns);

            assertNotNull(result);
            assertEquals("child", result.getName());
            assertEquals("http://example.com/ns", result.getNamespaceURI());
        }
    }

    @Nested
    @DisplayName("findElements")
    class FindElements {

        @Test
        @DisplayName("should find elements by XPath expression")
        void findsElementsByXpath() {
            Document doc = DocumentHelper.createDocument();
            Element root = doc.addElement("root");
            root.addElement("item").setText("a");
            root.addElement("item").setText("b");

            Stream<Element> result = xmlUtil.findElements(doc, "//item");

            assertEquals(2, result.count());
        }

        @Test
        @DisplayName("should return empty stream when no elements match")
        void returnsEmptyForNoMatch() {
            Document doc = DocumentHelper.createDocument();
            doc.addElement("root");

            Stream<Element> result = xmlUtil.findElements(doc, "//nonexistent");

            assertEquals(0, result.count());
        }

        @Test
        @DisplayName("should throw NullPointerException for null document")
        void throwsForNullDocument() {
            assertThrows(NullPointerException.class,
                () -> xmlUtil.findElements(null, "//test", Map.of()));
        }
    }

    @Nested
    @DisplayName("getElement")
    class GetElement {

        @Test
        @DisplayName("should return existing child element")
        void returnsExistingChild() {
            Document doc = DocumentHelper.createDocument();
            Element root = doc.addElement("root");
            Element existing = root.addElement("child");
            existing.setText("value");

            var result = xmlUtil.getElement(root, "child");

            assertTrue(result.isPresent());
            assertEquals("value", result.get().getText());
        }

        @Test
        @DisplayName("should create and return new element when not found")
        void createsNewElement() {
            Document doc = DocumentHelper.createDocument();
            Element root = doc.addElement("root");

            var result = xmlUtil.getElement(root, "newChild");

            assertTrue(result.isPresent());
            assertEquals("newChild", result.get().getName());
        }
    }

    @Nested
    @DisplayName("getElement(parent, name, namespace)")
    class GetElementWithNamespace {

        @Test
        @DisplayName("should return existing element with namespace")
        void returnsExistingNamespacedElement() {
            Document doc = DocumentHelper.createDocument();
            Namespace ns = new Namespace("ns", "http://example.com");
            Element root = doc.addElement("root");
            root.addElement(org.dom4j.QName.get("child", ns)).setText("found");

            var result = xmlUtil.getElement(root, "child", ns);

            assertTrue(result.isPresent());
            assertEquals("found", result.get().getText());
        }

        @Test
        @DisplayName("should create new namespaced element when not found")
        void createsNewNamespacedElement() {
            Document doc = DocumentHelper.createDocument();
            Namespace ns = new Namespace("ns", "http://example.com");
            Element root = doc.addElement("root");

            var result = xmlUtil.getElement(root, "newChild", ns);

            assertTrue(result.isPresent());
            assertEquals("newChild", result.get().getName());
        }
    }

    @Nested
    @DisplayName("removeElement")
    class RemoveElement {

        @Test
        @DisplayName("should remove existing child element")
        void removesChild() {
            Document doc = DocumentHelper.createDocument();
            Element root = doc.addElement("root");
            root.addElement("toRemove");

            xmlUtil.removeElement(root, "toRemove");

            assertNull(root.element("toRemove"));
        }

        @Test
        @DisplayName("should do nothing when element to remove does not exist")
        void doesNothingWhenNotFound() {
            Document doc = DocumentHelper.createDocument();
            Element root = doc.addElement("root");

            assertDoesNotThrow(() -> xmlUtil.removeElement(root, "nonexistent"));
        }
    }

    @Nested
    @DisplayName("removeElement(element, tagName, namespace)")
    class RemoveElementWithNamespace {

        @Test
        @DisplayName("should remove namespaced child element")
        void removesNamespacedChild() {
            Document doc = DocumentHelper.createDocument();
            Element root = doc.addElement("root");
            Namespace ns = new Namespace("ns", "http://example.com");
            root.addElement(org.dom4j.QName.get("child", ns));

            xmlUtil.removeElement(root, "child", ns);

            assertNull(root.element(org.dom4j.QName.get("child", ns)));
        }
    }

    @Nested
    @DisplayName("addElementAsFirstChild")
    class AddElementAsFirstChild {

        @Test
        @DisplayName("should add element as first child")
        void addsAsFirstChild() {
            Document doc = DocumentHelper.createDocument();
            Element root = doc.addElement("root");
            root.addElement("existing");

            Element result = xmlUtil.addElementAsFirstChild(root, "first", "value");

            assertEquals("first", result.getName());
            assertEquals("value", result.getText());
            // Verify it's the first content element
            Element firstElement = (Element) root.content().getFirst();
            assertEquals("first", firstElement.getName());
        }
    }

    @Nested
    @DisplayName("saveDocument")
    class SaveDocument {

        @Test
        @DisplayName("should save document to file")
        void savesDocumentToFile(@TempDir Path tempDir) throws IOException {
            Document doc = DocumentHelper.createDocument();
            Element root = doc.addElement("root");
            root.addElement("child").setText("value");

            Path xmlFile = tempDir.resolve("test.xml");
            xmlUtil.saveDocument(doc, mockLog, xmlFile);

            assertTrue(Files.exists(xmlFile));
            String content = Files.readString(xmlFile);
            assertTrue(content.contains("<root>"));
            assertTrue(content.contains("<child>value</child>"));
        }

        @Test
        @DisplayName("should handle null document gracefully")
        void handlesNullDocument(@TempDir Path tempDir) {
            Path xmlFile = tempDir.resolve("test.xml");
            assertDoesNotThrow(() -> xmlUtil.saveDocument(null, mockLog, xmlFile));
            assertFalse(Files.exists(xmlFile));
        }
    }

    @Nested
    @DisplayName("getDocument")
    class GetDocument {

        @Test
        @DisplayName("should read existing XML file")
        void readsExistingFile(@TempDir Path tempDir) throws IOException {
            Path xmlFile = tempDir.resolve("existing.xml");
            Files.writeString(xmlFile, "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<root><child>test</child></root>");

            var result = xmlUtil.getDocument(mockLog, xmlFile);

            assertTrue(result.isPresent());
            assertNotNull(result.get().getRootElement());
            assertEquals("root", result.get().getRootElement().getName());
        }

        @Test
        @DisplayName("should return empty Optional for non-existing file")
        void returnsEmptyForMissingFile(@TempDir Path tempDir) {
            Path nonExistingFile = tempDir.resolve("missing.xml");

            var result = xmlUtil.getDocument(mockLog, nonExistingFile);

            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("should create new document with postCreate when file does not exist")
        void createsNewDocumentWithPostCreate(@TempDir Path tempDir) {
            Path newFile = tempDir.resolve("subdir/new.xml");

            var result = xmlUtil.getDocument(mockLog, newFile,
                doc -> doc.addElement("newRoot"));

            assertTrue(result.isPresent());
            assertEquals("newRoot", result.get().getRootElement().getName());
        }
    }

    @Nested
    @DisplayName("addElement(document, log, parentNode, nodeName, postCreate)")
    class AddElementToDocument {

        @Test
        @DisplayName("should add element to document node found by XPath")
        void addsElementToDocumentNode() {
            Document doc = DocumentHelper.createDocument();
            doc.addElement("root");

            Element result = xmlUtil.addElement(doc, mockLog, "//root", "child",
                elem -> elem.setText("created"));

            assertNotNull(result);
            assertEquals("child", result.getName());
            assertEquals("created", result.getText());
        }

        @Test
        @DisplayName("should return null when parent node not found")
        void returnsNullWhenParentNotFound() {
            Document doc = DocumentHelper.createDocument();
            doc.addElement("root");

            Element result = xmlUtil.addElement(doc, mockLog, "//nonexistent", "child", null);

            assertNull(result);
        }
    }
}
