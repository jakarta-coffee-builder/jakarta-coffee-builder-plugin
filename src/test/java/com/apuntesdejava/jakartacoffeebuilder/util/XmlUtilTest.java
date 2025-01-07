package com.apuntesdejava.jakartacoffeebuilder.util;

import org.apache.maven.plugin.logging.Log;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class XmlUtilTest {
    // Handling non-existent XML files and directories
    @Test
    public void test_get_document_with_nonexistent_file() {
        Log mockLog = mock(Log.class);
        Path nonExistentPath = Paths.get("non/existent/file.xml");
        XmlUtil xmlUtil = XmlUtil.getInstance();

        Optional<Document> result = xmlUtil.getDocument(mockLog, nonExistentPath);

        assertTrue(result.isEmpty());
        verify(mockLog, never()).error(anyString(), any(Exception.class));
    }

    // Adding elements with different parameters (tag name, content, namespace)
    @Test
    public void test_add_elements_with_various_parameters() {
        XmlUtil xmlUtil = XmlUtil.getInstance();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        try {
            builder = factory.newDocumentBuilder();
            Document document = builder.newDocument();
            Element root = document.createElement("root");
            document.appendChild(root);

            // Test adding element with tag name and content
            xmlUtil.addElement(root, "child1", "content1");
            NodeList children = root.getElementsByTagName("child1");
            assertEquals(1, children.getLength());
            assertEquals("content1", children.item(0).getTextContent());

            // Test adding element with only tag name
            Element child2 = xmlUtil.addElement(root, "child2");
            children = root.getElementsByTagName("child2");
            assertEquals(1, children.getLength());
            assertSame(child2, children.item(0));

            // Test adding element with namespace and tag name
            Element child3 = xmlUtil.addElementNS(root, "http://example.com/ns", "child3");
            children = root.getElementsByTagNameNS("http://example.com/ns", "child3");
            assertEquals(1, children.getLength());
            assertSame(child3, children.item(0));

        } catch (ParserConfigurationException e) {
            fail("ParserConfigurationException should not occur: " + e.getMessage());
        }
    }

    // Performance with complex XPath queries
    @Test
    public void test_find_elements_with_complex_xpath() throws ParserConfigurationException {
        // Arrange
        XmlUtil xmlUtil = XmlUtil.getInstance();
        Log mockLog = mock(Log.class);
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document document = dBuilder.newDocument();
        Element root = document.createElement("root");
        document.appendChild(root);
        Element child1 = document.createElement("child");
        child1.setAttribute("id", "1");
        root.appendChild(child1);
        Element child2 = document.createElement("child");
        child2.setAttribute("id", "2");
        root.appendChild(child2);

        String complexXPath = "/root/child[@id='2']";

        // Act
        NodeList result = xmlUtil.findElements(document, mockLog, complexXPath);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getLength());
        assertEquals("2", ((Element) result.item(0)).getAttribute("id"));
    }
}
