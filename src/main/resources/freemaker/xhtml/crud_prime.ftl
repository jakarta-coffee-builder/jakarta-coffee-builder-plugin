<?xml version='1.0' encoding='UTF-8' ?>
<#assign formId="${instanceModelName}Form" />
<#assign serviceClassName="${modelName}Service" />
<#assign serviceInstanceName="${instanceModelName}Service" />
<#assign currentModel="current${modelName}" />
<#assign selectedModels="selected${modelName}s" />
<#assign idNameCap=idName?cap_first />
<#assign managedBeanName="${instanceModelName}ListBean" />


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
        <#assign currentBean="{${managedBeanName}.${currentModel}}"/>
        <#assign bundleConfirm="{bundle.confirm}"/>
        <#assign bundleYes="{bundle.yes}"/>
        <#assign bundleNo="{bundle.no}"/>
        <#assign bundleCancel="{bundle.cancel}"/>
        <#assign formId="${instanceModelName}Form" />
        <p:card>
            <h:form id="${formId}">
                <p:toolbar>
                    <p:toolbarGroup>
                        <#assign actionListenerValue="{${managedBeanName}.openNew}"/>
                        <#assign commandButtonValue="{bundle.app_new}" />
                        <p:commandButton value="#${commandButtonValue}" 
                                         icon="pi pi-plus" 
                                         actionListener="#${actionListenerValue}"
                                         update=":dialogs:manage-${instanceModelName}-content"
                                         oncomplete="PF('manage${modelName}Dialog').show()"
                                         styleClass="ui-button-success" 
                                         style="margin-right: .5rem">
                            <p:resetInput target=":dialogs:manage-${instanceModelName}-content" />
                        </p:commandButton>

                        <#assign deleteButtonMessage="{${managedBeanName}.deleteButtonMessage}" />
                        <#assign deleteSelectedListener="{${managedBeanName}.deleteSelected${modelName}s}" />
                        <#assign buttonDisabled="{!${managedBeanName}.hasSelected${modelName}s()}" />
                        <#assign deleteBeansMessage="{bundle.delete_confirm_${instanceModelName}s}" />

                        <p:commandButton id="delete-${instanceModelName}s-button"
                                         value="#${deleteButtonMessage}"
                                         icon="pi pi-trash" 
                                         actionListener="#${deleteSelectedListener}"
                                         styleClass="ui-button-danger" 
                                         disabled="#${buttonDisabled}" 
                                         update="@this">
                            <p:confirm header="#${bundleConfirm}" 
                                       message="#${deleteBeansMessage}"
                                       icon="pi pi-exclamation-triangle" />
                        </p:commandButton>
                    </p:toolbarGroup>
                </p:toolbar>
                
                <#assign dataTableValue="{${managedBeanName}.${instanceModelName}sList}"/>
                <#assign selectionValue="{${managedBeanName}.${selectedModels}}"/>
                <#assign rowKeyValue="{${instanceModelName}.${idName}}"/>
                <p:dataTable var="${instanceModelName}"
                             value="#${dataTableValue}" 
                             selectionMode="multiple"
                             rowKey="#${rowKeyValue}"
                             paginator="true"
                             rows="10"
                             id="dt-${instanceModelName}s"
                             selection="#${selectionValue}">

                    <p:ajax event="rowSelect" update=":${formId}:delete-${instanceModelName}s-button" />
                    <p:ajax event="rowUnselect" update=":${formId}:delete-${instanceModelName}s-button" />
                    <p:ajax event="rowSelectCheckbox" update=":${formId}:delete-${instanceModelName}s-button" />
                    <p:ajax event="rowUnselectCheckbox" update=":${formId}:delete-${instanceModelName}s-button" />
                    <p:ajax event="toggleSelect" update=":${formId}:delete-${instanceModelName}s-button" />
                    
                    <#list fields as field>
                        <#assign headerText="{bundle.Project_${field.name}}"/>
                        <#assign columnValue="{${instanceModelName}.${field.name}}" />
                    <p:column headerText="#${headerText}" >
                        #${columnValue}
                    </p:column>
                    </#list>
                    
                    <p:column exportable="false" ariaHeaderText="Actions">
                        <#assign valueBean="{${instanceModelName}}"/>
                        <p:commandButton icon="pi pi-pencil" 
                                         update=":dialogs:manage-${instanceModelName}-content"
                                         oncomplete="PF('manage${modelName}Dialog').show()"
                                         styleClass="edit-button rounded-button ui-button-success" 
                                         process="@this">
                            <f:setPropertyActionListener value="#${valueBean}" 
                                                         target="#${currentBean}" />
                            <p:resetInput target=":dialogs:manage-${instanceModelName}-content" />
                        </p:commandButton>
                        <p:commandButton class="ui-button-warning rounded-button" 
                                         icon="pi pi-trash"
                                         process="@this"
                                         oncomplete="PF('delete${modelName}Dialog').show()">
                            <f:setPropertyActionListener value="#${valueBean}" 
                                                         target="#${currentBean}" />
                        </p:commandButton>
                    </p:column>

                </p:dataTable>
            </h:form>


            <h:form id="dialogs">
                <p:dialog header="${modelName} Details"
                          showEffect="fade" 
                          modal="true" 
                          widgetVar="manage${modelName}Dialog"
                          responsive="true">
                    <p:outputPanel id="manage-${instanceModelName}-content"
                                   class="ui-fluid">
                        <#assign selectedValue = "{not empty ${managedBeanName}.${currentModel}}" />
                        <p:outputPanel rendered="#${selectedValue}">
                            <#list fields as field>
                                <#assign readOnlyValue = "" />
                                <#if field.name == '${idName}'>
                                    <#assign readOnlyValue = " readonly='true' " />  
                                </#if>
                            <#assign headerText="{bundle.Project_${field.name}}"/>
                            <#assign columnValue="{${managedBeanName}.${currentModel}.${field.name}}" />
                            <div class="field">
                                <p:outputLabel for="${field.name}">#${headerText}</p:outputLabel>

                                <#switch field.type >
                                    <#on "LocalDate">
                                <p:datePicker  id="${field.name}" value="#${columnValue}" pattern="yyyy-MM-dd" showIcon="true" />
                                    <#default>
                                <p:inputText id="${field.name}" value="#${columnValue}" ${readOnlyValue} />
                                </#switch>
                            </div>
                            </#list>
                        </p:outputPanel>
                    </p:outputPanel>
                    
                    <f:facet name="footer">
                         <#assign saveCommandButtonValue="{bundle.app_save}" />
                         <#assign cancelCommandButtonValue="{bundle.app_cancel}" />
                         <#assign saveButtonActionListener="{${managedBeanName}.save${modelName}}" />
                        <p:commandButton value="#${saveCommandButtonValue}" 
                                         icon="pi pi-check"
                                         actionListener="#${saveButtonActionListener}"
                                         update="manage-${instanceModelName}-content"
                                         process="manage-${instanceModelName}-content @this" />
                        <p:commandButton value="#${cancelCommandButtonValue}" 
                                         icon="pi pi-times"
                                         onclick="PF('manage${modelName}Dialog').hide()"
                                         class="ui-button-secondary"
                                         type="button" />
                    </f:facet>
                </p:dialog>

                <#assign deleteBeanMessage="{bundle.delete_confirm_${instanceModelName}}" />
                <#assign deleteMethod="{${managedBeanName}.delete${modelName}}" />

                <p:confirmDialog widgetVar="delete${modelName}Dialog"
                                 showEffect="fade" 
                                 width="300"
                                 message="#${deleteBeanMessage}" 
                                 header="#${bundleConfirm}" 
                                 severity="warn">
                    <p:commandButton value="#${bundleYes}" 
                                     icon="pi pi-check" 
                                     actionListener="#${deleteMethod}"
                                     process="@this" 
                                     update=":${formId}:delete-${instanceModelName}s-button"
                                     oncomplete="PF('delete${modelName}Dialog').hide()" />
                    <p:commandButton value="#${bundleNo}" 
                                     type="button" 
                                     styleClass="ui-button-secondary" 
                                     icon="pi pi-times"
                                     onclick="PF('delete${modelName}Dialog').hide()" />
                </p:confirmDialog>

                <p:confirmDialog global="true" 
                                 showEffect="fade"
                                 width="300">
                    <p:commandButton value="#${bundleYes}" 
                                     type="button" 
                                     styleClass="ui-confirmdialog-yes" 
                                     icon="pi pi-check" />
                    <p:commandButton value="#${bundleNo}" 
                                     type="button" 
                                     styleClass="ui-confirmdialog-no ui-button-secondary"
                                     icon="pi pi-times" />
                </p:confirmDialog>
            </h:form>
        </p:card>
    </ui:define>

</ui:composition>

