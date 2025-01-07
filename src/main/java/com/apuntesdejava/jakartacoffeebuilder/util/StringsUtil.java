/*
 * Copyright 2024 Diego Silva <diego.silva at apuntesdejava.com>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.apuntesdejava.jakartacoffeebuilder.util;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.apuntesdejava.jakartacoffeebuilder.util.Constants.SLASH;
import static org.apache.commons.lang3.StringUtils.EMPTY;

/**
 * @author Diego Silva <diego.silva at apuntesdejava.com>
 */
public class StringsUtil {


    /**
     * Removes the root character (e.g., a leading forward slash) from the beginning of the
     * given path string if it exists.
     *
     * @param path the input string path to be processed; it may or may not start with a leading slash
     * @return the path string without the leading slash if it was present; otherwise, the original string
     */
    public static String removeCharacterRoot(String path) {
        if (StringUtils.startsWith(path, SLASH))
            return StringUtils.removeStart(path, SLASH);
        return path;
    }

    /**
     * Converts a given string to PascalCase, where the first letter of each word is capitalized
     * and all words are joined together without spaces or delimiters.
     *
     * @param string the input string to be converted; it may contain non-alphanumeric characters
     *               as delimiters, which will be removed in the resulting PascalCase string
     * @return the string converted to PascalCase, or an empty string if the input is null, blank,
     * or contains no alphanumeric characters
     */
    public static String toPascalCase(String string) {
        if (StringUtils.isBlank(string))
            return EMPTY;

        String[] parts = string.split("[^a-zA-Z0-9]+");
        return Arrays.stream(parts)
                     .filter(part -> !part.isEmpty())
                     .map(StringUtils::capitalize)
                     .collect(Collectors.joining());
    }

    /**
     * Converts a camelCase string to param-case (kebab-case).
     *
     * @param camelCase the input string in camelCase format
     * @return the string converted to param-case format
     */
    public static String camelCaseToParamCase(String camelCase) {
        return StringUtils.join(
            StringUtils.splitByCharacterTypeCamelCase(camelCase), '-'
        ).toLowerCase();
    }

    public static boolean containsIgnoreCase(Set<String> set, String value) {
        return set.stream().anyMatch(str -> str.equalsIgnoreCase(value));
    }

    public static boolean containsAnyIgnoreCase(Set<String> set, Set<String> search) {
        return set.stream().anyMatch(str -> containsIgnoreCase(search, str));
    }

    public static List<String> findIgnoreCase(Set<String> searchAnnotations, Set<String> strings) {
        return searchAnnotations.stream().filter(str -> containsIgnoreCase(strings, str)).toList();
    }

    public static String findIgnoreCase(Set<String> searchAnnotations, String search) {
        return searchAnnotations.stream()
                                .filter(item -> StringUtils.equalsIgnoreCase(item, search))
                                .findFirst()
                                .orElse(null);
    }
}
