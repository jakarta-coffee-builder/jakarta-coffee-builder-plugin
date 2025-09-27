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
 * Utility class for performing HTTP requests.
 * <p>
 * Provides methods to execute HTTP GET requests and convert the responses into different formats using conversion
 * functions.
 * </p>
 */
public class HttpUtil {

    private static final System.Logger LOG = System.getLogger(HttpUtil.class.getName());

    private HttpUtil() {
    }

    /**
     * Executes an HTTP GET request to the specified URL with optional query parameters and converts the response
     * content using the provided converter function.
     *
     * @param <T>        the type of the result produced by the converter
     * @param address    the URL to send the GET request to
     * @param converter  a function to convert the response content from String to type T
     * @param parameters optional query parameters to include in the request
     * @return the converted response content
     * @throws IOException if an I/O error occurs during the request
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
     * A converter function that converts a JSON response string to a JsonObject.
     */
    public static final Function<String, JsonObject> STRING_TO_JSON_OBJECT_RESPONSE_CONVERTER = (response) -> {

        try (var reader = Json.createReader(new StringReader(response))) {
            return reader.readObject();
        }

    };

    /**
     * Record class representing a query parameter for HTTP requests.
     *
     * @param name  the name of the parameter
     * @param value the value of the parameter
     */
    public record Parameter(String name, String value) {

    }

    public static String getUrl(String serviceUrl) {
        return (BooleanUtils.toBoolean(System.getProperty("devel", "false"))
                ? DEV_BASE_URL : PRD_BASE_URL) + serviceUrl;
    }

    //make function  get content from url
}
