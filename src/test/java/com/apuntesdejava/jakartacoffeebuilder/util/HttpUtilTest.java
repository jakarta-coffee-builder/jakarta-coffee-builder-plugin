package com.apuntesdejava.jakartacoffeebuilder.util;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
}
