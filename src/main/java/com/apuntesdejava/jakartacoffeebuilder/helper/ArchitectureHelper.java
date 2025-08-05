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

import com.apuntesdejava.jakartacoffeebuilder.util.PomUtil;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;

public class ArchitectureHelper {

    private ArchitectureHelper() {
    }

    public static ArchitectureHelper getInstance() {
        return ArchitectureHelperHolder.INSTANCE;
    }

    public void checkDependency(MavenProject mavenProject, Log log) throws MojoExecutionException {
        var jakartaEeUtil = JakartaEeHelper.getInstance();
        log.debug("Checking org.mapstruct depending");
        if (!PomUtil.existsDependency(mavenProject, log, "org.mapstruct", "mapstruct")){
            PomUtil.addDependency(mavenProject, log, "org.mapstruct:mapstruct");
            PomUtil.saveMavenProject(mavenProject, log);
        }
    }

    private static class ArchitectureHelperHolder {

        private static final ArchitectureHelper INSTANCE = new ArchitectureHelper();
    }
}
