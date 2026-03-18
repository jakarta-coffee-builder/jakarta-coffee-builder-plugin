package com.apuntesdejava.jakartacoffeebuilder.util;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.function.Function;

import static com.apuntesdejava.jakartacoffeebuilder.util.Constants.DEV_BASE_URL;
import static com.apuntesdejava.jakartacoffeebuilder.util.Constants.PRD_BASE_URL;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class HttpUtilTest {

    @Test
    public void testGetContent() throws IOException {
        // Mock the converter function
        Function<String, String> converter = mock(Function.class);
        when(converter.apply(any(String.class))).thenReturn("mocked response");

        // Mock the HttpUtil.Parameter
        HttpUtil.Parameter param = new HttpUtil.Parameter("key", "value");

        // Call the method
        String result = HttpUtil.getContent("http://example.com", converter, param);

        // Verify the result
        assertEquals("mocked response", result);
    }

    @Nested
    @DisplayName("getUrl")
    class GetUrl {

        @AfterEach
        void cleanUpSystemProperty() {
            System.clearProperty("devel");
        }

        @Test
        @DisplayName("should use PRD base URL when devel is false")
        void usesPrdBaseUrlByDefault() {
            System.setProperty("devel", "false");
            String result = HttpUtil.getUrl("/dependencies.json");
            assertTrue(result.startsWith(PRD_BASE_URL));
            assertEquals(PRD_BASE_URL + "/dependencies.json", result);
        }

        @Test
        @DisplayName("should use DEV base URL when devel is true")
        void usesDevBaseUrlWhenDevelTrue() {
            System.setProperty("devel", "true");
            String result = HttpUtil.getUrl("/dependencies.json");
            assertTrue(result.startsWith(DEV_BASE_URL));
            assertEquals(DEV_BASE_URL + "/dependencies.json", result);
        }

        @Test
        @DisplayName("should use PRD base URL when devel property is not set")
        void usesPrdBaseUrlWhenPropertyNotSet() {
            System.clearProperty("devel");
            String result = HttpUtil.getUrl("/servers.json");
            assertEquals(PRD_BASE_URL + "/servers.json", result);
        }
    }

    @Nested
    @DisplayName("Parameter record")
    class ParameterRecord {

        @Test
        @DisplayName("should store name and value")
        void storesNameAndValue() {
            HttpUtil.Parameter param = new HttpUtil.Parameter("key", "value");
            assertEquals("key", param.name());
            assertEquals("value", param.value());
        }
    }
}
