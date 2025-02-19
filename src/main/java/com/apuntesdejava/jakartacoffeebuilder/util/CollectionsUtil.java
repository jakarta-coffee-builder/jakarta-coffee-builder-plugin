package com.apuntesdejava.jakartacoffeebuilder.util;

import java.util.Map;

public class CollectionsUtil {
    public static boolean areEqual(Map<String, String> first, Map<String, String> second) {
        if (first.size() != second.size()) {
            return false;
        }

        return first.entrySet().stream()
                    .allMatch(e -> e.getValue().equals(second.get(e.getKey())));
    }
}
