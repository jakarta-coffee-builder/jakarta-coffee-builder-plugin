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
package com.apuntesdejava.jakartacoffeebuilder.mojo.faces;

import com.apuntesdejava.jakartacoffeebuilder.util.JakartaFacesUtil;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.IOException;
import java.util.List;

/**
 * Add a new template to the faces project.
 * @author Diego Silva <diego.silva at apuntesdejava.com>
 */
@Mojo(
    name = "add-face-template"
)
public class AddFaceTemplateMojo extends AbstractMojo {

    @Parameter(
        required = true,
        property = "name"
    )
    private String templateName;

    @Parameter(
        property = "inserts"
    )
    private List<String> inserts;

    @Parameter(defaultValue = "${project}", readonly = true)
    private MavenProject mavenProject;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        var log = getLog();
        try {
            log.info("Adding Template face page " + templateName);
            var jakartaFacesUtil = JakartaFacesUtil.getInstance();
            jakartaFacesUtil.addFaceTemplate(mavenProject,log,templateName,inserts);
        } catch (IOException e) {
            log.error(e.getMessage(),e);
            throw new MojoExecutionException(e);
        }
    }

}
