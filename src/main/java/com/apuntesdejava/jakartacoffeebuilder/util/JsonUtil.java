package com.apuntesdejava.jakartacoffeebuilder.util;

import jakarta.json.Json;
import jakarta.json.JsonNumber;
import jakarta.json.JsonObject;
import jakarta.json.JsonString;
import jakarta.json.JsonValue;
import org.apache.maven.plugin.logging.Log;
import org.codehaus.plexus.util.xml.Xpp3Dom;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Set;

import static java.util.stream.Collectors.toSet;

/**
 * A utility class for JSON-related operations, including reading and writing JSON files
 * and converting between JSON structures and Maven's {@link Xpp3Dom} model.
 */
public final class JsonUtil {

    private static final String ARTIFACT_TEMPLATE = "%s:%s";

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private JsonUtil() {
    }

    /**
     * Reads a JSON file from the specified path and parses it into a {@link JsonValue}.
     *
     * @param jsonPath The {@link Path} to the JSON file.
     * @return The parsed {@link JsonValue} from the file.
     * @throws IOException if an I/O error occurs while reading the file.
     */
    public static JsonValue readJsonValue(Path jsonPath) throws IOException {
        return Json.createReader(Files.newBufferedReader(jsonPath)).readValue();
    }

    /**
     * Extracts the underlying standard Java object from a {@link JsonValue}.
     *
     * @param jsonValue The {@link JsonValue} to process.
     * @return The corresponding Java object (e.g., {@link JsonObject}, {@link String}, {@link java.math.BigDecimal}, {@link Boolean}).
     * Returns {@code null} for {@code JsonValue.ValueType.NULL}.
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

    private static void evaluateObjectElement(Xpp3Dom config,
                                              Log log,
                                              String key,
                                              JsonValue value,
                                              boolean allowRepeat) {
        log.debug("---- in object");
        boolean noExists = config.getChildren(key).length == 0;
        var node = jsonToXpp3Dom(log,
            noExists ? new Xpp3Dom(key) : config.getChildren(key)[0],
            value.asJsonObject(), allowRepeat);
        if (noExists) {
            config.addChild(node);
        }
    }

    private static void evaluateArrayElement(Xpp3Dom config, Log log, String key, JsonValue value) {
        var childrenIds = getPluginsKeys(config.getChildren());
        log.debug("---- in array");
        var values = value.asJsonArray();
        values.stream()
              .map(JsonValue::asJsonObject)
              .filter(item -> {
                  var groupId = item.getString("groupId");
                  var artifactId = item.getString("artifactId");
                  return !childrenIds.contains(ARTIFACT_TEMPLATE.formatted(groupId, artifactId));
              })
              .forEach(item -> config.addChild(jsonToXpp3Dom(log, new Xpp3Dom(key), item, true)));
    }

    private static Set<String> getPluginsKeys(Xpp3Dom[] children) {
        return Arrays.stream(children)
                     .map(item -> ARTIFACT_TEMPLATE.formatted(item.getChild("groupId").getValue(),
                         item.getChild("artifactId").getValue())).collect(toSet());
    }

    /**
     * Converts a {@link JsonObject} into an {@link Xpp3Dom} model, recursively building the DOM tree.
     * This is typically used to create or update {@code <configuration>} sections in a Maven POM.
     *
     * @param log         The Maven logger for debug output.
     * @param config      The parent {@link Xpp3Dom} node to which the new elements will be appended.
     * @param jsonObject  The {@link JsonObject} to convert.
     * @param allowRepeat If {@code true}, allows multiple elements with the same name to be added.
     *                    If {@code false}, existing elements with the same name will be reused/merged.
     * @return The updated parent {@link Xpp3Dom} node.
     */
    public static Xpp3Dom jsonToXpp3Dom(Log log, Xpp3Dom config, JsonObject jsonObject, boolean allowRepeat) {
        jsonObject.forEach((key, value) -> {
            if (allowRepeat || config.getChild(key) == null) {

                switch (value.getValueType()) {
                    case ARRAY -> evaluateArrayElement(config, log, key, value);
                    case OBJECT -> evaluateObjectElement(config, log, key, value, allowRepeat);
                    default -> {
                        log.debug("---- in value");
                        Xpp3Dom child = new Xpp3Dom(key);
                        child.setValue(((JsonString) value).getString());
                        config.addChild(child);
                    }

                }
            }
        });
        return config;
    }

    /**
     * Converts a {@link JsonObject} into an {@link Xpp3Dom} model, preventing repeated elements.
     * This is a convenience method that calls {@link #jsonToXpp3Dom(Log, Xpp3Dom, JsonObject, boolean)}
     * with {@code allowRepeat} set to {@code false}.
     *
     * @param log        The Maven logger for debug output.
     * @param config     The parent {@link Xpp3Dom} node to which the new elements will be appended.
     * @param jsonObject The {@link JsonObject} to convert.
     * @return The updated parent {@link Xpp3Dom} node.
     */
    public static Xpp3Dom jsonToXpp3Dom(Log log, Xpp3Dom config, JsonObject jsonObject) {
        return jsonToXpp3Dom(log, config, jsonObject, false);
    }

    /**
     * Writes a {@link JsonValue} to the specified file path.
     *
     * @param jsonFile The {@link Path} of the file to be written.
     * @param json     The {@link JsonValue} to write to the file.
     * @throws IOException if an I/O error occurs during writing.
     */
    public static void saveJsonValue(Path jsonFile, JsonValue json) throws IOException {
        try (var writer = Files.newBufferedWriter(jsonFile)) {
            Json.createWriter(writer).write(json);
        }
    }
}
