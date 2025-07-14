/*
 * Copyright 2024 Diego Silva diego.silva at apuntesdejava.com.
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
package com.apuntesdejava.jakartacoffeebuilder.helper.datasource;

import com.apuntesdejava.jakartacoffeebuilder.util.WebXmlUtil;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;

import java.io.IOException;

/**
 * Clase responsable de crear el DataSource en un contexto web.
 * <p>
 * Esta clase extiende {@link DataSourceCreator} y proporciona la implementación
 * para agregar la configuración del DataSource al archivo `web.xml` de un proyecto Jakarta EE.
 * Utiliza la utilidad {@link WebXmlUtil} para manejar las operaciones relacionadas con el archivo `web.xml`.
 * </p>
 */
public class DataSourceWebCreator extends DataSourceCreator {


    /**
     * Constructor de la clase DataSourceWebCreator.
     *
     * @param mavenProject el proyecto Maven actual
     * @param log          el logger para registrar mensajes
     */
    public DataSourceWebCreator(MavenProject mavenProject, Log log) {
        super(mavenProject, log);
    }

    /**
     * Construye y configura el DataSource en el archivo `web.xml`.
     * <p>
     * Este método verifica la existencia del archivo `web.xml` en el proyecto actual,
     * obtiene los parámetros del DataSource y los agrega al archivo. Finalmente, guarda
     * los cambios realizados en el archivo `web.xml`.
     * </p>
     */
    @Override
    public void build() throws IOException {
        var webXmlUtil = WebXmlUtil.getInstance();

        webXmlUtil.checkExistsFile(mavenProject, log)
                  .ifPresent(document -> {
                      var properties = getDataSourceParameters();
                      var currentPath = mavenProject.getBasedir().toPath();
                      webXmlUtil.addDataSource(document, log, properties);
                      webXmlUtil.saveDocument(mavenProject, document, log);
                  });
    }
}
