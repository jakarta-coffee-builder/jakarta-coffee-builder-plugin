package com.apuntesdejava.jakartacoffeebuilder.util;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.RegexValidator;

import static com.apuntesdejava.jakartacoffeebuilder.util.Constants.DATASOURCE_DECLARE_CLASS;
import static com.apuntesdejava.jakartacoffeebuilder.util.Constants.DATASOURCE_DECLARE_WEB;

/**
 * @author Diego Silva
 */
public class DataSourceUtil {
    /**
     * Returns the appropriate prefix for a data source name based on the declaration type. This prefix is used to construct the full JNDI name for the data source.
     *
     * @param declare The declaration type (e.g., DATASOURCE_DECLARE_WEB, DATASOURCE_DECLARE_CLASS).
     * @return The prefix for the data source name.
     */
    public static String getPrefix(String declare) {
        return switch (declare) {
            case DATASOURCE_DECLARE_WEB -> "java:global/";
            case DATASOURCE_DECLARE_CLASS -> "java:app/";
            default -> StringUtils.EMPTY;
        } + "jdbc/";
    }

    /**
     * Validates the given data source name against a regular expression and prepends the appropriate JNDI prefix based on the declaration type.
     *
     * @param declare        The declaration type (e.g., DATASOURCE_DECLARE_WEB, DATASOURCE_DECLARE_CLASS).
     * @param datasourceName The name of the data source to validate.
     * @return The fully qualified data source name with the prefix.
     * @throws IllegalArgumentException If the data source name is invalid.
     */
    public static String validateDataSourceName(String declare, String datasourceName) {
        RegexValidator validator = new RegexValidator("^[a-zA-Z][a-zA-Z0-9_]*$");
        if (!validator.isValid(datasourceName)) {
            throw new IllegalArgumentException("Invalid datasource name");
        }
        return getPrefix(declare) + datasourceName;
    }

}
