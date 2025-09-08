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

import com.apuntesdejava.jakartacoffeebuilder.util.*;
import jakarta.json.JsonObject;
import jakarta.json.JsonValue;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Namespace;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

import static com.apuntesdejava.jakartacoffeebuilder.util.Constants.*;
import java.util.List;

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
        var jakartaEeHelper = JakartaEeHelper.getInstance();

        formsJson.entrySet()
                .stream()
                .filter(entry -> entry.getValue().getValueType() == JsonValue.ValueType.OBJECT
                && entry.getValue().asJsonObject().containsKey("entity")
                )
                .forEach(entry -> {
                    var formName = entry.getKey();
                    var formDescription = entry.getValue().asJsonObject();
                    createMessagesBundle(mavenProject, log, formDescription);
                    try {
                        var base = formDescription.getString("base", "/");
                        var pageName = StringsUtil.removeCharacterRoot(base + formName);
                        var entityName = formDescription.getString("entity");
                        var entityDescription = entitiesJson.getJsonObject(entityName);
                        jakartaEeHelper.createDomain(mavenProject, entityName, entityDescription);
                        createManagedBean(mavenProject, log, pageName, entityName);
                        createForm(log, webAppPath, formName, pageName, formDescription, entityDescription);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    public void createManagedBean(MavenProject mavenProject, Log log, String pageName, String entityName) throws IOException {
        var packageDefinition = MavenProjectUtil.getFacesPackage(mavenProject);
        var className = StringsUtil.toPascalCase(pageName) + "Bean";
        var managedBeanPath = PathsUtil.getJavaPath(mavenProject, packageDefinition, className);
        List<String> importsList = List.of(
                "%s.%sService".formatted(MavenProjectUtil.getServicePackage(mavenProject), entityName),
                "%s.%s".formatted(MavenProjectUtil.getModelPackage(mavenProject), entityName)
        );
        Map<String, Object> fieldsMap = Map.ofEntries(
                Map.entry(PACKAGE_NAME, packageDefinition),
                Map.entry("modelName", entityName),
                Map.entry("className", className),
                Map.entry("instanceModelName", StringUtils.uncapitalize(entityName)),
                Map.entry("importsList", importsList)
        );

        TemplateUtil.getInstance().createManagedBeanCrudFile(log, fieldsMap, managedBeanPath);
    }

    private void createMessagesBundle(MavenProject mavenProject, Log log, JsonObject formsJson) {
        try {
            var formEntityName = formsJson.getString("entity");
            log.debug("Creating messages bundle for " + formEntityName);
            var bundleMessages = formsJson.getJsonObject(FIELDS)
                    .entrySet().stream().map(entry -> Map.entry(formEntityName + "_" + entry.getKey(), entry.getValue().asJsonObject().getString("label", entry.getKey())))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
            var messageProperties = PathsUtil.getResourcePath(mavenProject).resolve("messages.properties");
            Properties properties = new Properties();
            if (Files.exists(messageProperties))
                try (FileReader reader = new FileReader(messageProperties.toFile())) {
                properties.load(reader);
            }
            bundleMessages.forEach(properties::setProperty);
            log.debug("Saving messages bundle");
            try (FileWriter writer = new FileWriter(messageProperties.toFile())) {
                properties.store(writer, null);
            }
        } catch (IOException ex) {
            log.error(ex.getMessage(), ex);
        }
    }

    private void createForm(Log log,
            Path webAppPath,
            String formName,
            String pageName, JsonObject formDescription,
            JsonObject entity) throws IOException {

        var pagePath = webAppPath.resolve(pageName + ".xhtml");
        var title = formDescription.getString("title", formName);
        var templateDesc = formDescription.getJsonObject("template");
        var entityName = formDescription.getString("entity");
        var formIdName = StringUtils.uncapitalize(entityName) + "Form";

        var pageXhtml = templateDesc == null
                ? createFacePage(log, pagePath, entity, formIdName)
                : createFacePageWithTemplate(log, pagePath, templateDesc, entity, entityName, title);
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
            JsonObject entityDefinition, String entityName, String title) throws IOException {
        String templateFacelet = templateDesc.getString("facelet");
        String define = templateDesc.getString("define");
        var fields = entityDefinition.getJsonObject(FIELDS).keySet();
        Map<String, Object> fieldsMap = Map.of(
                "define", define,
                "template_name", templateFacelet,
                "variableBean", StringUtils.uncapitalize(entityName),
                "fields", fields,
                "title", title
        );
        TemplateUtil.getInstance().createFacesCrudFile(log, fieldsMap, xhtml);

        return null;
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
            case "String" ->
                "inputText";
            case "Integer", "Long" ->
                "inputNumber";
            case "LocalDate" ->
                "datePicker";
            default ->
                "inputText";
        };
    }

    private static class PrimeFacesUtilHolder {

        private static final PrimeFacesHelper INSTANCE = new PrimeFacesHelper();
    }
}
