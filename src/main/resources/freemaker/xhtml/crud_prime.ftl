<?xml version='1.0' encoding='UTF-8' ?>
<!DOCTYPE composition PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<ui:composition xmlns="http://www.w3.org/1999/xhtml"
                xmlns:ui="jakarta.faces.facelets"
                template="${template_name}"
                xmlns:h="jakarta.faces.html"
                xmlns:p="primefaces"
                xmlns:f="jakarta.faces.core">
    <ui:define name="title">${title}</ui:define>
    <ui:define name="${define}">
        <f:loadBundle var="bundle" basename="messages" />
        <p:card>
            <h:form id="${variableBean}Form">
                <p:toolbar>
                    <p:toolbarGroup>
                        <#assign actionListenerValue="{${variableBean}ListBean.openNew}"/>
                        <#assign commandButtonValue="{bundle.app_new}" />
                        <p:commandButton value="#${commandButtonValue}" 
                                         icon="pi pi-plus" 
                                         actionListener="#${actionListenerValue}"
                                         update=":dialogs:manage-${variableBean}-content" 
                                         oncomplete="PF('manage${className}Dialog').show()"
                                         styleClass="ui-button-success" 
                                         style="margin-right: .5rem">
                            <p:resetInput target=":dialogs:manage-${variableBean}-content" />
                        </p:commandButton>
                    </p:toolbarGroup>
                </p:toolbar>
                
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
            
            <h:form id="dialogs">
                <p:dialog header="${className} Details" 
                          showEffect="fade" 
                          modal="true" 
                          widgetVar="manage${className}Dialog"
                          responsive="true">
                    <p:outputPanel id="manage-${variableBean}-content" class="ui-fluid">
                        <#assign selectedValue = "{not empty ${variableBean}ListBean.current${className}}" />
                        <p:outputPanel rendered="#${selectedValue}">

                        </p:outputPanel>
                    </p:outputPanel>
                    
                    <f:facet name="footer">
                         <#assign saveCommandButtonValue="{bundle.app_save}" />
                         <#assign cancelCommandButtonValue="{bundle.app_cancel}" />
                         <#assign saveButtonActionListener="{${variableBean}ListBean.save${className}}" />
                        <p:commandButton value="#${saveCommandButtonValue}" 
                                         icon="pi pi-check"
                                         actionListener="#@{saveButtonActionListener=}"
                                         update="manage-${variableBean}-content"
                                         process="manage-${variableBean}-content @this" />
                        <p:commandButton value="#${cancelCommandButtonValue}" 
                                         icon="pi pi-times"
                                         onclick="PF('manage${className}Dialog').hide()"
                                         class="ui-button-secondary"
                                         type="button" />
                    </f:facet>
                </p:dialog>
            </h:form>
        </p:card>
    </ui:define>

</ui:composition>

