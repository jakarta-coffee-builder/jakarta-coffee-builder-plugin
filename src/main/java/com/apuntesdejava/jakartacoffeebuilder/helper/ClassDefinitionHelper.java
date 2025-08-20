/*
 * Copyright 2025 dsilva.
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
package com.apuntesdejava.jakartacoffeebuilder.helper;

import com.apuntesdejava.jakartacoffeebuilder.util.CoffeeBuilderUtil;
import com.apuntesdejava.jakartacoffeebuilder.util.Constants;
import com.apuntesdejava.jakartacoffeebuilder.util.JsonUtil;
import com.apuntesdejava.jakartacoffeebuilder.util.StringsUtil;
import jakarta.json.JsonObject;
import jakarta.json.JsonValue;
import org.apache.commons.lang3.function.TriFunction;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.apuntesdejava.jakartacoffeebuilder.util.Constants.IS_ID;
import static com.apuntesdejava.jakartacoffeebuilder.util.Constants.NAME;
import static com.apuntesdejava.jakartacoffeebuilder.util.Constants.TYPE;

/**
 *
 * @author dsilva
 */
public class ClassDefinitionHelper {

    private final JsonObject classesDefinitions;

    private ClassDefinitionHelper() {
        try {
            this.classesDefinitions = CoffeeBuilderUtil.getClassesDefinitions().orElseThrow();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    public static ClassDefinitionHelper getInstance() {
        return ClassDefinitionHelperHolder.INSTANCE;
    }
    
    private static class ClassDefinitionHelperHolder {

        private static final ClassDefinitionHelper INSTANCE = new ClassDefinitionHelper();
    }

    public List<Map<String, Object>> createFieldsDefinitions(JsonObject fieldsJson,
                                                             TriFunction<String, JsonObject, List<Map<String, Object>>, String> evaluateField) {
        return fieldsJson.entrySet().stream().map(fieldEntry -> {
            var field = fieldEntry.getValue().asJsonObject();
            Map<String, Object> item = new LinkedHashMap<>();
            item.put(NAME, fieldEntry.getKey());
            List<Map<String, Object>> annotations = new LinkedList<>();
            var type = evaluateField.apply(fieldEntry.getKey(), field, annotations);
            item.put(TYPE, type);
            var keys = field.keySet();
            if (StringsUtil.containsAnyIgnoreCase(Constants.SEARCH_ANNOTATIONS_FIELD_KEYS, keys)) {
                var annotationsList = StringsUtil.findIgnoreCase(Constants.SEARCH_ANNOTATIONS_FIELD_KEYS, keys);
                List<Map<String, Object>> annot = annotationsList.stream().map(annotationName -> {
                    Map<String, Object> annotationMap = new LinkedHashMap<>();
                    annotationMap.put(NAME, annotationName);
                    var aField = field.get(getKeyName(keys, annotationName));
                    if (aField.getValueType() == JsonValue.ValueType.OBJECT) {
                        annotationMap.put("description", getMapFromJsonObject(aField.asJsonObject()));
                    }
                    return annotationMap;
                }).toList();
                annotations.addAll(annot);
            }
            if (field.containsKey(IS_ID)) {
                item.put(IS_ID, field.getBoolean(IS_ID, false));
            }
            if (!annotations.isEmpty()) {
                item.put("annotations", annotations);
            }
            return item;
        }).toList();
    }

    public Collection<String> importsFromFieldsClassesType(JsonObject fields) {

        return fields.values().stream()
                     .map(JsonValue::asJsonObject)
                     .map(field -> field.getString(TYPE))
                     .filter(classesDefinitions::containsKey)
                     .map(type -> classesDefinitions.getJsonObject(type).getString("fullName"))
                     .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private static Map<String, Object> getMapFromJsonObject(JsonObject column) {
        return column.entrySet()
                     .stream()
                     .collect(LinkedHashMap::new,
                         (map, entry) -> map.put(entry.getKey(), JsonUtil.getJsonValue(entry.getValue())), Map::putAll);
    }

    private static String getKeyName(Set<String> keys, String otherName) {
        return keys.stream().filter(key -> key.equalsIgnoreCase(otherName)).findFirst().orElse(null);
    }
}
