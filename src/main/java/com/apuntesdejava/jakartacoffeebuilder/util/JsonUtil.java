package com.apuntesdejava.jakartacoffeebuilder.util;

import jakarta.json.Json;
import jakarta.json.JsonNumber;
import jakarta.json.JsonObject;
import jakarta.json.JsonString;
import jakarta.json.JsonValue;
import org.codehaus.plexus.util.xml.Xpp3Dom;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Utility class for JSON operations.
 */
public class JsonUtil {

    private JsonUtil() {
    }

    /**
     * Reads a JSON value from the specified file path.
     *
     * @param jsonPath the path to the JSON file
     * @return the JSON value read from the file
     * @throws IOException if an I/O error occurs
     */
    public static JsonValue readJsonValue(Path jsonPath) throws IOException {
        return Json.createReader(Files.newBufferedReader(jsonPath)).readValue();
    }

    /**
     * Retrieves the value from a JsonValue object.
     *
     * @param jsonValue the JsonValue object to retrieve the value from
     * @return the value of the JsonValue object as an Object
     */
    public static Object getJsonValue(JsonValue jsonValue) {
        return switch (jsonValue.getValueType()) {
            case ARRAY -> jsonValue.asJsonArray();
            case OBJECT -> jsonValue.asJsonObject();
            case STRING -> ((JsonString) jsonValue).getString();
            case NUMBER -> ((JsonNumber) jsonValue).numberValue();
            case TRUE -> true;
            case FALSE -> false;
            default -> null;
        };
    }

    /**
     * Converts a JsonObject to an Xpp3Dom object.
     *
     * @param config     the Xpp3Dom object to which the JsonObject will be added
     * @param jsonObject the JsonObject to convert
     * @return the resulting Xpp3Dom object
     */
    public static Xpp3Dom jsonToXpp3Dom(Xpp3Dom config, JsonObject jsonObject, boolean allowRepeat) {
        jsonObject.forEach((key, value) -> {
            if (allowRepeat || config.getChild(key) == null) {

                switch (value.getValueType()) {
                    case ARRAY -> {
                        var values = value.asJsonArray();
                        System.out.println("values:" + values);
                        values.stream().map(JsonValue::asJsonObject).forEach(item -> {
                            config.addChild(jsonToXpp3Dom(new Xpp3Dom(key), item, true));
                        });
                    }
                    case OBJECT -> config.addChild(jsonToXpp3Dom(new Xpp3Dom(key), value.asJsonObject()));
                    default -> {
                        Xpp3Dom child = new Xpp3Dom(key);
                        child.setValue(((JsonString) value).getString());
                        config.addChild(child);
                    }
                }
            }
        });
        return config;
    }

    public static Xpp3Dom jsonToXpp3Dom(Xpp3Dom config, JsonObject jsonObject) {
        return jsonToXpp3Dom(config, jsonObject, false);
    }

    /**
     * Saves a JsonObject to the specified file path.
     *
     * @param jsonFile the path to the JSON file
     * @param json     the JsonObject to save
     * @throws IOException if an I/O error occurs
     */
    public static void saveJsonValue(Path jsonFile, JsonValue json) throws IOException {
        try (var writer = Files.newBufferedWriter(jsonFile)) {
            Json.createWriter(writer).write(json);
        }
    }
}
