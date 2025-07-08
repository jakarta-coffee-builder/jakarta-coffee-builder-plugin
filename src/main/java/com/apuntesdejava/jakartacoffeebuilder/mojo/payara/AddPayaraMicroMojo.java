/*
 * Copyright 2025 Diego Silva <diego.silva at apuntesdejava.com>.
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
package com.apuntesdejava.jakartacoffeebuilder.mojo.payara;

import com.apuntesdejava.jakartacoffeebuilder.helper.PayaraMicroHelper;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.IOException;

import static com.apuntesdejava.jakartacoffeebuilder.util.Constants.JAKARTAEE_VERSION_11;

/**
 * @author Diego Silva <diego.silva at apuntesdejava.com>
 */
@Mojo(
    name = "add-payaramicro"
)

public class AddPayaraMicroMojo extends AbstractMojo {
    @Parameter(
        property = "profile",
        required = true,
        defaultValue = "payaramicro"
    )
    private String profileId;

    /**
     * The Maven project.
     */
    @Parameter(defaultValue = "${project}", readonly = true)
    private MavenProject mavenProject;

    @Parameter(
        property = "jakartaee-version",
        required = true,
        defaultValue = JAKARTAEE_VERSION_11

    )
    private String jakartaEeVersion;


    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        try {
            PayaraMicroHelper.getInstance().addPlugin(mavenProject, getLog(), profileId, jakartaEeVersion);
        } catch (IOException e) {
            getLog().error(e.getMessage(), e);
        }
    }

}
