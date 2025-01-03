package com.apuntesdejava.jakartacoffeebuilder.util;

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.EntityUtils;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.function.Function;

import static org.apache.commons.lang3.StringUtils.EMPTY;

public class HttpUtil {

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
            return httpClient.execute(httpGet, response -> {
                var responseString = EntityUtils.toString(response.getEntity());
                return converter.apply(responseString);
            });
        }
    }

    public static record Parameter(String name, String value) {

    }

    //make function  get content from url
}
