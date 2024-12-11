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

import org.apache.commons.lang3.RegExUtils;
import org.apache.maven.project.MavenProject;

/**
 * @author Diego Silva <diego.silva at apuntesdejava.com>
 */
public class MavenProjectUtil {

    private MavenProjectUtil() {
    }

    public static MavenProjectUtil getInstance() {
        return MavenProjectUtilHolder.INSTANCE;
    }

    private static class MavenProjectUtilHolder {

        private static final MavenProjectUtil INSTANCE = new MavenProjectUtil();
    }

    /**
     * Constructs a package name based on the group ID and artifact ID of the given Maven project.
     * Non-alphanumeric characters in the group ID and artifact ID are replaced with dots.
     *
     * @param mavenProject the Maven project containing the group ID and artifact ID
     * @return the generated package name as a string
     */
    public String getProjectPackage(MavenProject mavenProject) {
        return "%s.%s".formatted(RegExUtils.replaceAll(mavenProject.getGroupId(), "[^a-zA-Z0-9]", "."),
            RegExUtils.replaceAll(mavenProject.getArtifactId(), "[^a-zA-Z0-9]", "."));
    }
}
