package com.apuntesdejava.jakartacoffeebuilder.util;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonValue;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class JsonUtilTest {


    @Test
    void readJsonValueReadsValidJsonFile() throws IOException {
        Path tempFile = Files.createTempFile("test", ".json");
        Files.writeString(tempFile, "{\"key\":\"value\"}");
        JsonValue jsonValue = JsonUtil.readJsonValue(tempFile);
        assertInstanceOf(JsonObject.class, jsonValue);
        assertEquals("value", ((JsonObject) jsonValue).getString("key"));
        Files.delete(tempFile);
    }

    @Test
    void readJsonValueThrowsIOExceptionForInvalidPath() {
        Path invalidPath = Path.of("nonexistent.json");
        assertThrows(IOException.class, () -> JsonUtil.readJsonValue(invalidPath));
    }

    @Test
    void getJsonValueReturnsCorrectValueForJsonString() {
        JsonValue jsonValue = Json.createValue("test");
        Object result = JsonUtil.getJsonValue(jsonValue);
        assertEquals("test", result);
    }

    @Test
    void getJsonValueReturnsCorrectValueForJsonNumber() {
        JsonValue jsonValue = Json.createValue(42);
        Object result = JsonUtil.getJsonValue(jsonValue);
        assertEquals(42, result);
    }

    @Test
    void getJsonValueReturnsNullForNullJsonValue() {
        Object result = JsonUtil.getJsonValue(JsonValue.NULL);
        assertNull(result);
    }

    @Test
    void jsonToXpp3DomConvertsJsonObjectToXpp3Dom() {
        JsonObject jsonObject = Json.createObjectBuilder()
                                    .add("key", "value")
                                    .build();
        Xpp3Dom config = new Xpp3Dom("root");
        Xpp3Dom result = JsonUtil.jsonToXpp3Dom(config, jsonObject);
        assertNotNull(result.getChild("key"));
        assertEquals("value", result.getChild("key").getValue());
    }

    @Test
    void saveJsonValueWritesJsonToFile() throws IOException {
        Path tempFile = Files.createTempFile("test", ".json");
        JsonObject jsonObject = Json.createObjectBuilder()
                                    .add("key", "value")
                                    .build();
        JsonUtil.saveJsonValue(tempFile, jsonObject);
        String content = Files.readString(tempFile);
        assertTrue(content.contains("\"key\":\"value\""));
        Files.delete(tempFile);
    }
}
