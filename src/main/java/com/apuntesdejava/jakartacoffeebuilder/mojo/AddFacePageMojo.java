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
package com.apuntesdejava.jakartacoffeebuilder.mojo;

import com.apuntesdejava.jakartacoffeebuilder.util.JakartaFacesUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.IOException;

/**
 * @author Diego Silva <diego.silva at apuntesdejava.com>
 */
@Mojo(
    name = "add-face-page"
)
public class AddFacePageMojo extends AbstractMojo {

    @Parameter(
        required = true,
        property = "name"
    )
    private String pageName;

    @Parameter(
        required = false,
        property = "managed-bean",
        defaultValue = "true"
    )
    private boolean createManagedBean;

    @Parameter(
        required = false,
        property = "template"
    )
    private String templateFacelet;

    @Parameter(defaultValue = "${project}", readonly = true)
    private MavenProject mavenProject;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        var log = getLog();
        log.info("Adding face page " + pageName);
        try {
            var jakartaFacesUtil = JakartaFacesUtil.getInstance();
            if (StringUtils.isBlank(templateFacelet))
                jakartaFacesUtil.addFacePage(mavenProject, log, pageName, createManagedBean);
            else
                jakartaFacesUtil.addFacePageWithFaceletTemplate(mavenProject, log, pageName, templateFacelet,
                    createManagedBean);
            if (createManagedBean)
                jakartaFacesUtil.createManagedBean(mavenProject, log, pageName);

        } catch (IOException e) {
            throw new MojoExecutionException(e.getMessage(), e);
        }
    }

}
