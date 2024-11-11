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
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;

/**
 * @author Diego Silva <diego.silva at apuntesdejava.com>
 */
public class PomUtil {

    private PomUtil() {
    }

    public static PomUtil getInstance() {
        return PomUtilHolder.INSTANCE;
    }

    private static class PomUtilHolder {

        private static final PomUtil INSTANCE = new PomUtil();
    }

    public boolean existsDependency(MavenProject mavenProject, Log log,
                                    String groupId, String artifactId) {
        log.debug("groupId:%s | artifactId:%s".formatted(groupId, artifactId));
        return mavenProject.getArtifacts().stream().
                anyMatch(artifact -> StringUtils.
                equals(artifact.getGroupId(), groupId) && StringUtils.
                equals(artifact.getArtifactId(), artifactId));

    }
}
