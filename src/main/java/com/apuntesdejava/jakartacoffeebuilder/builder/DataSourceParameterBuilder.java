package com.apuntesdejava.jakartacoffeebuilder.builder;

import com.apuntesdejava.jakartacoffeebuilder.config.DataSourceConfigProvider;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;

import static com.apuntesdejava.jakartacoffeebuilder.util.Constants.NAME;
/**
 * Builder class for creating a JSON object with data source parameters.
 * Uses a {@link DataSourceConfigProvider} to retrieve configuration values.
 */
public class DataSourceParameterBuilder {

    private final DataSourceConfigProvider provider;

    /**
     * Constructs a new DataSourceParameterBuilder with the given configuration provider.
     * @param provider The {@link DataSourceConfigProvider} to retrieve data source configuration from.
     */
    public DataSourceParameterBuilder(DataSourceConfigProvider provider) {
        this.provider = provider;
    }


    /**
     * Builds a {@link JsonObject} containing data source parameters based on the
     * configuration provided by the {@link DataSourceConfigProvider}.
     * Only non-blank or non-null values from the provider are added to the JSON object.
     * Properties are split by comma and added as a JSON array.
     *
     * @return A {@link JsonObject} representing the data source parameters.
     */
    public JsonObject build() {
        var jsonBuilder = Json.createObjectBuilder()
                              .add(NAME, provider.getDatasourceName());

        if (StringUtils.isNotBlank(provider.getServerName())) {
            jsonBuilder.add("serverName", provider.getServerName());
        }
        if (provider.getPortNumber() != null) {
            jsonBuilder.add("portNumber", provider.getPortNumber());
        }
        if (StringUtils.isNotBlank(provider.getUrl())) {
            jsonBuilder.add("url", provider.getUrl());
        }
        if (StringUtils.isNotBlank(provider.getUser())) {
            jsonBuilder.add("user", provider.getUser());
        }
        if (StringUtils.isNotBlank(provider.getPassword())) {
            jsonBuilder.add("password", provider.getPassword());
        }
        if (StringUtils.isNotBlank(provider.getProperties())) {
            var propertiesBuilder = Json.createArrayBuilder();
            Arrays.stream(provider.getProperties().split(",")).forEach(propertiesBuilder::add);
            jsonBuilder.add("properties", propertiesBuilder);
        }
        return jsonBuilder.build();
    }
}
