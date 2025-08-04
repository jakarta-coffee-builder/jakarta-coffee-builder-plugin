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
package com.apuntesdejava.jakartacoffeebuilder.helper;

import com.apuntesdejava.jakartacoffeebuilder.util.JsonUtil;
import com.apuntesdejava.jakartacoffeebuilder.util.PathsUtil;
import com.apuntesdejava.jakartacoffeebuilder.util.StringsUtil;
import com.apuntesdejava.jakartacoffeebuilder.util.XmlUtil;
import jakarta.json.JsonObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Namespace;

import java.io.IOException;
import java.nio.file.Path;

import static com.apuntesdejava.jakartacoffeebuilder.util.Constants.FIELDS;
import static com.apuntesdejava.jakartacoffeebuilder.util.Constants.NAME;
import static com.apuntesdejava.jakartacoffeebuilder.util.Constants.TYPE;

public class PrimeFacesHelper extends JakartaFacesHelper {

    protected static final Namespace PRIMEFACES_NS_P_NAMESPACE = new Namespace("p", "primefaces");

    private PrimeFacesHelper() {

    }

    public static PrimeFacesHelper getInstance() {
        return PrimeFacesUtilHolder.INSTANCE;
    }

    public void addFormsFromEntities(MavenProject mavenProject,
                                     Log log,
                                     Path formsPath,
                                     Path entitiesPth) throws IOException {
        var formsJson = JsonUtil.readJsonValue(formsPath).asJsonObject();
        var entitiesJson = JsonUtil.readJsonValue(entitiesPth).asJsonObject();
        var webAppPath = PathsUtil.getWebappPath(mavenProject);
        formsJson.forEach((key, value) -> {
            var formJson = value.asJsonObject();
            createForm(log, webAppPath, key, formJson, entitiesJson.getJsonObject(formJson.getString("entity")));
        });
    }

    private void createForm(Log log, Path webAppPath, String formName, JsonObject formDescription, JsonObject entity) {
        var base = formDescription.getString("base", "/");
        var pageName = StringsUtil.removeCharacterRoot(base + formName);
        var pagePath = webAppPath.resolve(pageName + ".xhtml");
        var title = formDescription.getString("title", formName);
        var templateDesc = formDescription.getJsonObject("template");
        var entityName = formDescription.getString("entity");
        var formIdName = StringUtils.uncapitalize(entityName) + "Form";
        var pageXhtml = templateDesc == null
            ? createFacePage(log, pagePath, entity, formIdName)
            : createFacePageWithTemplate(log, pagePath, templateDesc, entity, formIdName);
        XmlUtil.getInstance().saveDocument(pageXhtml, log, pagePath);
    }

    private Document createFacePage(Log log,
                                    Path xhtml,
                                    JsonObject entityDefinition, String formIdName) {
        return createFacePage(log, xhtml, (bodyElement) -> createForm(log, bodyElement, entityDefinition, formIdName));
    }

    private Document createFacePageWithTemplate(Log log,
                                                Path xhtml,
                                                JsonObject templateDesc,
                                                JsonObject entityDefinition, String formIdName) {
        String templateFacelet = templateDesc.getString("facelet");
        String define = templateDesc.getString("define");
        return createFacePageWithTemplate(log, xhtml, templateFacelet, (defineTag) -> {
            var name = defineTag.attributeValue(NAME);
            if (Strings.CS.equals(name, define)) {
                createForm(log, defineTag, entityDefinition, formIdName);
            }
        }, PRIMEFACES_NS_P_NAMESPACE);
    }

    private void createForm(Log log, Element defineTag, JsonObject entityDefinition, String formIdName) {
        log.debug("creating content form in element:" + defineTag);
        var xmlUtil = XmlUtil.getInstance();
        var formElement = xmlUtil.addElement(defineTag, "form", FACES_NS_HTML_NAMESPACE)
                                 .addAttribute("id", formIdName);
        var cardElement = xmlUtil.addElement(formElement, "card", PRIMEFACES_NS_P_NAMESPACE);
        entityDefinition.getJsonObject(FIELDS).forEach((fieldName, fieldDef) -> {
            var fieldDefinition = fieldDef.asJsonObject();

            var panelGroupElement = xmlUtil.addElement(cardElement, "panelGroup", FACES_NS_HTML_NAMESPACE)
                                           .addAttribute("layout", "block")
                                           .addAttribute("styleClass", "field");

            var textLabel = fieldDefinition.getString("label", fieldName);
            var outputLabel = xmlUtil.addElement(panelGroupElement, "outputLabel", PRIMEFACES_NS_P_NAMESPACE)
                                     .addAttribute("for", fieldName)
                                     .addAttribute("value", textLabel);

            var elementInput = getElementInputByType(fieldDefinition.getString(TYPE));

            var inputText = xmlUtil.addElement(panelGroupElement, elementInput, PRIMEFACES_NS_P_NAMESPACE)
                                   .addAttribute("id", fieldName);
        });
    }

    private String getElementInputByType(String fieldType) {
        return switch (fieldType) {
            case "String" -> "inputText";
            case "Integer", "Long" -> "inputNumber";
            case "LocalDate" -> "datePicker";
            default -> "inputText";
        };
    }

    private static class PrimeFacesUtilHolder {
        private static final PrimeFacesHelper INSTANCE = new PrimeFacesHelper();
    }
}
