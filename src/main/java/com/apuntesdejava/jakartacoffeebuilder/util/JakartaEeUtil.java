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

import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;

import static com.apuntesdejava.jakartacoffeebuilder.util.Constants.JAKARTA_FACES;
import static com.apuntesdejava.jakartacoffeebuilder.util.Constants.JAKARTA_FACES_API;

/**
 *
 * @author Diego Silva <diego.silva at apuntesdejava.com>
 */
public class JakartaEeUtil {

    private JakartaEeUtil() {
    }

    public static JakartaEeUtil getInstance() {
        return JakartaEeUtilHolder.INSTANCE;
    }

    private static class JakartaEeUtilHolder {

        private static final JakartaEeUtil INSTANCE = new JakartaEeUtil();
    }

    public boolean hasJakartaFacesDependency(MavenProject mavenProject, Log log) {
        return PomUtil.getInstance().
                existsDependency(mavenProject, log, JAKARTA_FACES,
                                 JAKARTA_FACES_API);
    }
}
