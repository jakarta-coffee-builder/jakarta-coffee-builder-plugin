package com.apuntesdejava.jakartacoffeebuilder.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StringsUtilTest {

    @Nested
    @DisplayName("removeCharacterRoot")
    class RemoveCharacterRoot {

        @Test
        @DisplayName("should remove leading slash")
        void removesLeadingSlash() {
            assertEquals("path/to/file", StringsUtil.removeCharacterRoot("/path/to/file"));
        }

        @Test
        @DisplayName("should return path unchanged when no leading slash")
        void returnsUnchangedWhenNoLeadingSlash() {
            assertEquals("path/to/file", StringsUtil.removeCharacterRoot("path/to/file"));
        }

        @Test
        @DisplayName("should handle single slash")
        void handlesSingleSlash() {
            assertEquals("", StringsUtil.removeCharacterRoot("/"));
        }

        @Test
        @DisplayName("should handle empty string")
        void handlesEmptyString() {
            assertEquals("", StringsUtil.removeCharacterRoot(""));
        }
    }

    @Nested
    @DisplayName("toPascalCase")
    class ToPascalCase {

        @ParameterizedTest
        @CsvSource({
            "hello-world, HelloWorld",
            "my_class_name, MyClassName",
            "simple, Simple",
            "already-Pascal, AlreadyPascal",
            "one.two.three, OneTwoThree"
        })
        @DisplayName("should convert delimited strings to PascalCase")
        void convertsToPascalCase(String input, String expected) {
            assertEquals(expected, StringsUtil.toPascalCase(input));
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"   ", "  \t "})
        @DisplayName("should return empty for null, blank, or empty input")
        void returnsEmptyForBlankInput(String input) {
            assertEquals("", StringsUtil.toPascalCase(input));
        }

        @Test
        @DisplayName("should handle string with only special characters")
        void handlesOnlySpecialChars() {
            assertEquals("", StringsUtil.toPascalCase("---"));
        }
    }

    @Nested
    @DisplayName("camelCaseToParamCase")
    class CamelCaseToParamCase {

        @ParameterizedTest
        @CsvSource({
            "camelCase, camel-case",
            "myVariableName, my-variable-name",
            "simple, simple",
            "HTMLParser, html-parser"
        })
        @DisplayName("should convert camelCase to param-case")
        void convertsCorrectly(String input, String expected) {
            assertEquals(expected, StringsUtil.camelCaseToParamCase(input));
        }
    }

    @Nested
    @DisplayName("containsIgnoreCase")
    class ContainsIgnoreCase {

        @Test
        @DisplayName("should return true when set contains value (same case)")
        void returnsTrueForSameCase() {
            assertTrue(StringsUtil.containsIgnoreCase(Set.of("Hello", "World"), "Hello"));
        }

        @Test
        @DisplayName("should return true when set contains value (different case)")
        void returnsTrueForDifferentCase() {
            assertTrue(StringsUtil.containsIgnoreCase(Set.of("Hello", "World"), "hello"));
        }

        @Test
        @DisplayName("should return false when set does not contain value")
        void returnsFalseWhenNotFound() {
            assertFalse(StringsUtil.containsIgnoreCase(Set.of("Hello", "World"), "Foo"));
        }
    }

    @Nested
    @DisplayName("containsAnyIgnoreCase")
    class ContainsAnyIgnoreCase {

        @Test
        @DisplayName("should return true when any element matches")
        void returnsTrueWhenAnyMatches() {
            assertTrue(StringsUtil.containsAnyIgnoreCase(
                Set.of("Hello", "World"),
                Set.of("world", "foo")));
        }

        @Test
        @DisplayName("should return false when no element matches")
        void returnsFalseWhenNoneMatches() {
            assertFalse(StringsUtil.containsAnyIgnoreCase(
                Set.of("Hello", "World"),
                Set.of("foo", "bar")));
        }
    }

    @Nested
    @DisplayName("findIgnoreCase(Set, Set)")
    class FindIgnoreCaseSet {

        @Test
        @DisplayName("should find matching entries ignoring case")
        void findsMatchingEntries() {
            Collection<String> result = StringsUtil.findIgnoreCase(
                Set.of("Column", "JoinColumn", "ManyToOne"),
                Set.of("column", "manytoone"));
            assertEquals(2, result.size());
            assertTrue(result.contains("Column"));
            assertTrue(result.contains("ManyToOne"));
        }

        @Test
        @DisplayName("should return empty when no match found")
        void returnsEmptyWhenNoMatch() {
            Collection<String> result = StringsUtil.findIgnoreCase(
                Set.of("Column", "JoinColumn"),
                Set.of("foo", "bar"));
            assertTrue(result.isEmpty());
        }
    }

    @Nested
    @DisplayName("findIgnoreCase(Set, String)")
    class FindIgnoreCaseString {

        @Test
        @DisplayName("should return matching string ignoring case")
        void returnsMatchingString() {
            String result = StringsUtil.findIgnoreCase(Set.of("Column", "JoinColumn"), "column");
            assertEquals("Column", result);
        }

        @Test
        @DisplayName("should return null when no match found")
        void returnsNullWhenNotFound() {
            assertNull(StringsUtil.findIgnoreCase(Set.of("Column", "JoinColumn"), "foo"));
        }
    }

    @Nested
    @DisplayName("findIgnoreCaseOptional")
    class FindIgnoreCaseOptional {

        @Test
        @DisplayName("should return Optional with value when found")
        void returnsOptionalWithValue() {
            Optional<String> result = StringsUtil.findIgnoreCaseOptional(
                Set.of("Column", "JoinColumn"), "joincolumn");
            assertTrue(result.isPresent());
            assertEquals("JoinColumn", result.get());
        }

        @Test
        @DisplayName("should return empty Optional when not found")
        void returnsEmptyOptional() {
            Optional<String> result = StringsUtil.findIgnoreCaseOptional(
                Set.of("Column", "JoinColumn"), "foo");
            assertTrue(result.isEmpty());
        }
    }
}
