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
package com.apuntesdejava.jakartacoffeebuilder.model;

import javax.xml.namespace.NamespaceContext;
import java.util.Iterator;
import java.util.Map;

import static org.apache.commons.lang3.StringUtils.EMPTY;

/**
 * @author Diego Silva <diego.silva at apuntesdejava.com>
 */
public class NamespaceContextMap implements NamespaceContext {

    private final Map<String, String> PREF_MAP;

    public NamespaceContextMap(String prefix, String uri) {
        this.PREF_MAP = Map.of(prefix, uri);
    }

    @Override
    public String getNamespaceURI(String prefix) {
        return PREF_MAP.getOrDefault(prefix, EMPTY);
    }

    @Override
    public String getPrefix(String namespaceURI) {
        return PREF_MAP.entrySet()
                       .stream()
                       .filter(entry -> entry.getValue().equals(namespaceURI))
                       .findFirst()
                       .map(Map.Entry::getKey)
                       .orElse(null);
    }

    @Override
    public Iterator<String> getPrefixes(String namespaceURI) {
        return PREF_MAP.keySet().iterator();
    }
}
