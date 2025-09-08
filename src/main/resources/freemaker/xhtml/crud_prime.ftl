<?xml version='1.0' encoding='UTF-8' ?>
<!DOCTYPE composition PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<ui:composition xmlns="http://www.w3.org/1999/xhtml"
                xmlns:ui="jakarta.faces.facelets"
                template="${template_name}"
                xmlns:h="jakarta.faces.html"
                xmlns:p="primefaces"
                xmlns:f="jakarta.faces.core">
    <f:loadBundle var="bundle" basename="messages" />
    <ui:define name="title">${title}</ui:define>
    <ui:define name="${define}">
        <p:card>
            <h:form id="${variableBean}Form">
                <#assign dataTableValue="{${variableBean}ListBean.${variableBean}sList}"/>
                <p:dataTable var="${variableBean}" value="#${dataTableValue}">
                    <#list fields as field>
                        <#assign headerText="{bundle.Project_${field}}"/>
                        <#assign columnValue="{${variableBean}.${field}}" />
                    <p:column headerText="#${headerText}" >
                        #${columnValue}
                    </p:column>
                    </#list>

                </p:dataTable>
            </h:form>
        </p:card>
    </ui:define>

</ui:composition>

