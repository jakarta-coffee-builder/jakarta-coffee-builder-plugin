package com.apuntesdejava.jakartacoffeebuilder.util;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.EntityUtils;

import java.io.IOException;
import java.io.StringReader;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.function.Function;

import static com.apuntesdejava.jakartacoffeebuilder.util.Constants.DEV_BASE_URL;
import static com.apuntesdejava.jakartacoffeebuilder.util.Constants.PRD_BASE_URL;
import static org.apache.commons.lang3.StringUtils.EMPTY;


/**
 * A utility class for handling HTTP requests.
 * <p>
 * This class provides static methods to perform HTTP GET requests and process the
 * responses. It is designed to fetch remote configuration files and other resources.
 */
public final class HttpUtil {

    private static final System.Logger LOG = System.getLogger(HttpUtil.class.getName());

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private HttpUtil() {
    }

    /**
     * Executes an HTTP GET request to a specified URL with optional query parameters and converts
     * the response body using a provided converter function.
     *
     * @param <T>        The target type of the response after conversion.
     * @param address    The URL for the GET request.
     * @param converter  A {@link Function} that transforms the raw response string into the target type {@code T}.
     * @param parameters An optional varargs array of {@link Parameter} objects to be sent as URL query parameters.
     * @return The converted response of type {@code T}.
     * @throws IOException if an I/O error occurs during the HTTP request.
     */
    public static <T> T getContent(String address,
                                   Function<String, T> converter,
                                   Parameter... parameters) throws IOException {
        try (final var httpClient = HttpClients.createDefault()) {
            var queryParams = Arrays.stream(parameters)
                    .map(p -> p.name() + "=" + URLEncoder.encode(p.value(), StandardCharsets.UTF_8))
                    .reduce((p1, p2) -> p1 + "&" + p2)
                    .orElse(EMPTY);
            var requestUrl = address + (parameters.length == 0 ? EMPTY : ("?" + queryParams));
            var httpGet = new HttpGet(requestUrl);
            LOG.log(System.Logger.Level.DEBUG, () -> "Request URL:" + requestUrl);
            return httpClient.execute(httpGet, response -> {
                var responseString = EntityUtils.toString(response.getEntity());
                return converter.apply(responseString);
            });
        }
    }

    /**
     * A reusable {@link Function} that converts a JSON string into a {@link JsonObject}.
     */
    public static final Function<String, JsonObject> STRING_TO_JSON_OBJECT_RESPONSE_CONVERTER = (response) -> {

        try (var reader = Json.createReader(new StringReader(response))) {
            return reader.readObject();
        }

    };

    /**
     * A record representing a key-value pair for an HTTP query parameter.
     *
     * @param name  The name of the parameter.
     * @param value The value of the parameter.
     */
    public record Parameter(String name, String value) {

    }

    /**
     * Constructs a full URL by prepending the appropriate base URL (development or production)
     * to a given service path. The selection is based on the "devel" system property.
     *
     * @param serviceUrl The relative path of the service or resource (e.g., "/dependencies.json").
     * @return The complete URL as a string.
     */
    public static String getUrl(String serviceUrl) {
        return (BooleanUtils.toBoolean(System.getProperty("devel", "false"))
                ? DEV_BASE_URL : PRD_BASE_URL) + serviceUrl;
    }

}
