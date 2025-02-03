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
import java.util.Collections;
import java.util.Map;

import static org.apache.commons.lang3.StringUtils.EMPTY;

/**
 * A simple implementation of the NamespaceContext interface that maps a single
 * prefix to a namespace URI. This class provides methods to retrieve the
 * namespace URI for a given prefix, the prefix for a given namespace URI, and
 * an iterator over all prefixes.
 *
 * <p>This implementation uses a singleton map to store the prefix-URI mapping,
 * making it suitable for scenarios where only one prefix-URI pair is needed.
 *
 * <p>Note: This class returns an empty string for unknown prefixes and null
 * for unknown namespace URIs.
 *
 * @author Diego Silva
 */
public class NamespaceContextMap implements NamespaceContext {

    private final Map<String, String> prefMap;

    public NamespaceContextMap(String prefix, String uri) {
        this.prefMap = Collections.singletonMap(prefix, uri);
    }

    @Override
    public String getNamespaceURI(String prefix) {
        return prefMap.getOrDefault(prefix, EMPTY);
    }

    @Override
    public String getPrefix(String namespaceURI) {
        return prefMap.entrySet()
                       .stream()
                       .filter(entry -> entry.getValue().equals(namespaceURI))
                       .findFirst()
                       .map(Map.Entry::getKey)
                       .orElse(null);
    }

    @Override
    public Iterator<String> getPrefixes(String namespaceURI) {
        return prefMap.keySet().iterator();
    }
}
