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

import java.util.Map;

/**
 *
 * @author Diego Silva <diego.silva at apuntesdejava.com>
 */
public class Constants {

    public static final String JAKARTA_PLATFORM = "jakarta.platform";
    public static final String JAKARTA_JAKARTAEE_WEB_API = "jakarta.jakartaee-web-api";
    public static final String JAKARTA_JAKARTAEE_API = "jakarta.jakartaee-api";

    public static final String JAKARTA_FACES = "jakarta.faces";
    public static final String JAKARTA_FACES_API = "jakarta.faces-api";

    public static final String JAKARTAEE_VERSION_10 = "10.0.0";
    public static final String JAKARTAEE_VERSION_11 = "11.0.0";

    public static final String PROVIDED_SCOPE = "provided";

    public static final Map<String, Map<String, String>> SPECS_VERSIONS = Map.
            of(
                    JAKARTAEE_VERSION_10, Map.of(
                            JAKARTA_FACES_API, "4.0.1"
                    )
            );
}
