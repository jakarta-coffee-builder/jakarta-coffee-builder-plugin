package com.apuntesdejava.jakartacoffeebuilder.util;

import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class CollectionsUtilTest {

    @Test
    void areEqualReturnsTrueForEqualMaps() {
        Map<String, String> first = Map.of("key1", "value1", "key2", "value2");
        Map<String, String> second = Map.of("key1", "value1", "key2", "value2");
        assertTrue(CollectionsUtil.areEqual(first, second));
    }

    @Test
    void areEqualReturnsFalseForMapsWithDifferentSizes() {
        Map<String, String> first = Map.of("key1", "value1");
        Map<String, String> second = Map.of("key1", "value1", "key2", "value2");
        assertFalse(CollectionsUtil.areEqual(first, second));
    }

    @Test
    void areEqualReturnsFalseForMapsWithDifferentKeys() {
        Map<String, String> first = Map.of("key1", "value1");
        Map<String, String> second = Map.of("key2", "value1");
        assertFalse(CollectionsUtil.areEqual(first, second));
    }

    @Test
    void areEqualReturnsFalseForMapsWithDifferentValues() {
        Map<String, String> first = Map.of("key1", "value1");
        Map<String, String> second = Map.of("key1", "value2");
        assertFalse(CollectionsUtil.areEqual(first, second));
    }

    @Test
    void areEqualReturnsTrueForEmptyMaps() {
        Map<String, String> first = Collections.emptyMap();
        Map<String, String> second = Collections.emptyMap();
        assertTrue(CollectionsUtil.areEqual(first, second));
    }

    @Test
    void areEqualReturnsFalseWhenFirstMapIsNull() {
        Map<String, String> second = Map.of("key1", "value1");
        assertThrows(NullPointerException.class, () -> CollectionsUtil.areEqual(null, second));
    }

    @Test
    void areEqualReturnsFalseWhenSecondMapIsNull() {
        Map<String, String> first = Map.of("key1", "value1");
        assertThrows(NullPointerException.class, () -> CollectionsUtil.areEqual(first, null));
    }
}
