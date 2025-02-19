package com.apuntesdejava.jakartacoffeebuilder.util;

import jakarta.json.*;
import org.codehaus.plexus.util.xml.Xpp3Dom;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class JsonUtil {

    public static JsonValue readJsonValue(Path jsonPath) throws IOException {
        return Json.createReader(Files.newBufferedReader(jsonPath)).readValue();
    }

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

    public static Xpp3Dom jsonToXpp3Dom(String newKey, JsonObject jsonObject) {
        Xpp3Dom config = new Xpp3Dom(newKey);
        jsonObject.forEach((key, value) -> {
            if (value.getValueType() == JsonValue.ValueType.OBJECT) {
                config.addChild(jsonToXpp3Dom(key, value.asJsonObject()));
            } else {
                Xpp3Dom child = new Xpp3Dom(key);
                child.setValue(((JsonString)value).getString());
                config.addChild(child);
            }
        });
        return config;
    }
}
