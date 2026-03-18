package com.apuntesdejava.jakartacoffeebuilder.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NamespaceContextMapTest {

    private static final String PREFIX = "ns";
    private static final String URI = "http://example.com/ns";

    private NamespaceContextMap context;

    @BeforeEach
    void setUp() {
        context = new NamespaceContextMap(PREFIX, URI);
    }

    @Test
    @DisplayName("getNamespaceURI: should return URI for known prefix")
    void getNamespaceURI_knownPrefix_returnsUri() {
        assertEquals(URI, context.getNamespaceURI(PREFIX));
    }

    @Test
    @DisplayName("getNamespaceURI: should return empty string for unknown prefix")
    void getNamespaceURI_unknownPrefix_returnsEmpty() {
        assertEquals("", context.getNamespaceURI("unknown"));
    }

    @Test
    @DisplayName("getPrefix: should return prefix for known URI")
    void getPrefix_knownUri_returnsPrefix() {
        assertEquals(PREFIX, context.getPrefix(URI));
    }

    @Test
    @DisplayName("getPrefix: should return null for unknown URI")
    void getPrefix_unknownUri_returnsNull() {
        assertNull(context.getPrefix("http://unknown.com"));
    }

    @Test
    @DisplayName("getPrefixes: should return iterator containing the known prefix")
    void getPrefixes_returnsIteratorWithPrefix() {
        Iterator<String> prefixes = context.getPrefixes(URI);
        assertTrue(prefixes.hasNext());
        assertEquals(PREFIX, prefixes.next());
        assertFalse(prefixes.hasNext());
    }
}
