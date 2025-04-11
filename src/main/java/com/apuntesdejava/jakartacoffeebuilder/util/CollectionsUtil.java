package com.apuntesdejava.jakartacoffeebuilder.util;

import java.util.Map;

/**
 * Utility class for handling collections operations.
 * <p>
 * This class provides methods to compare two maps for equality.
 * </p>
 */
public class CollectionsUtil {

    private CollectionsUtil(){

    }

    /**
     * Compares two maps for equality.
     *
     * @param first  the first map to compare
     * @param second the second map to compare
     * @return true if the maps are equal, false otherwise
     */
    public static boolean areEqual(Map<String, String> first, Map<String, String> second) {
        if (first.size() != second.size()) {
            return false;
        }

        return first.entrySet().stream()
                    .allMatch(e -> e.getValue().equals(second.get(e.getKey())));
    }
}
