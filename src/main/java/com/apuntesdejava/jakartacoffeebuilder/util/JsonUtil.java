package com.apuntesdejava.jakartacoffeebuilder.util;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonValue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class JsonUtil {
    public static JsonArray readJsonArray(Path jsonPath) throws IOException {
        return readJsonValue(jsonPath).asJsonArray();
    }

    public static JsonObject readJsonObject(Path jsonPath) throws IOException {
        return readJsonValue(jsonPath).asJsonObject();
    }

    public static JsonValue readJsonValue(Path jsonPath) throws IOException {
        return Json.createReader(Files.newBufferedReader(jsonPath)).readValue();
    }
}
