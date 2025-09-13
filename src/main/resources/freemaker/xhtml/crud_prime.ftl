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
        <#assign currentBean="{${variableBean}ListBean.current${className}}"/>
        <#assign bundleConfirm="{bundle.confirm}"/>
        <#assign bundleYes="{bundle.yes}"/>
        <#assign bundleNo="{bundle.no}"/>
        <#assign bundleCancel="{bundle.cancel}"/>
        <#assign formId="${variableBean}Form" />
        <p:card>
            <h:form id="${formId}">
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

                        <#assign deleteButtonMessage="{${variableBean}ListBean.deleteButtonMessage}" />
                        <#assign deleteSelectedListener="{${variableBean}ListBean.deleteSelected${className}s}" />
                        <#assign buttonDisabled="{!${variableBean}ListBean.hasSelected${className}s()}" />
                        <#assign deleteBeansMessage="{bundle.delete_confirm_${variableBean}s}" />

                        <p:commandButton id="delete-${variableBean}s-button" 
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
                
                <#assign dataTableValue="{${variableBean}ListBean.${variableBean}sList}"/>
                <#assign selectionValue="{${variableBean}ListBean.selected${className}s}"/>
                <p:dataTable var="${variableBean}" 
                             value="#${dataTableValue}" 
                             selectionMode="multiple" 
                             selection="#${selectionValue}">
                    

                    <p:ajax event="rowSelect" update=":${formId}:delete-${variableBean}s-button" />
                    <p:ajax event="rowUnselect" update=":${formId}:delete-${variableBean}s-button" />
                    <p:ajax event="rowSelectCheckbox" update=":${formId}:delete-${variableBean}s-button" />
                    <p:ajax event="rowUnselectCheckbox" update=":${formId}:delete-${variableBean}s-button" />
                    <p:ajax event="toggleSelect" update=":${formId}:delete-${variableBean}s-button" />                    
                    
                    <#list fields as field>
                        <#assign headerText="{bundle.Project_${field}}"/>
                        <#assign columnValue="{${variableBean}.${field}}" />
                    <p:column headerText="#${headerText}" >
                        #${columnValue}
                    </p:column>
                    </#list>
                    
                    <p:column exportable="false" ariaHeaderText="Actions">
                        <#assign valueBean="{${variableBean}}"/>
                        <p:commandButton icon="pi pi-pencil" 
                                         update=":dialogs:manage-${variableBean}-content"
                                         oncomplete="PF('manage${className}Dialog').show()"
                                         styleClass="edit-button rounded-button ui-button-success" 
                                         process="@this">
                            <f:setPropertyActionListener value="#${valueBean}" 
                                                         target="#${currentBean}" />
                            <p:resetInput target=":dialogs:manage-${variableBean}-content" />
                        </p:commandButton>
                        <p:commandButton class="ui-button-warning rounded-button" 
                                         icon="pi pi-trash"
                                         process="@this"
                                         oncomplete="PF('delete${className}Dialog').show()">
                            <f:setPropertyActionListener value="#${valueBean}" 
                                                         target="#${currentBean}" />
                        </p:commandButton>
                    </p:column>

                </p:dataTable>
            </h:form>


            <h:form id="dialogs">
                <p:dialog header="${className} Details" 
                          showEffect="fade" 
                          modal="true" 
                          widgetVar="manage${className}Dialog"
                          responsive="true">
                    <p:outputPanel id="manage-${variableBean}-content" 
                                   class="ui-fluid">
                        <#assign selectedValue = "{not empty ${variableBean}ListBean.current${className}}" />
                        <p:outputPanel rendered="#${selectedValue}">
                            <#list fields as field>
                            <#assign headerText="{bundle.Project_${field}}"/>
                            <#assign columnValue="{${variableBean}.${field}}" />
                            <div class="field">
                                <p:outputLabel for="${field}">#${headerText}</p:outputLabel>
                                <p:inputText id="${field}" value="#${columnValue}"  />
                            </div>
                            </#list>
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

                <#assign deleteBeanMessage="{bundle.delete_confirm_${variableBean}}" />
                <#assign deleteMethod="{${variableBean}ListBean.delete${className}}" />

                <p:confirmDialog widgetVar="delete${className}Dialog" 
                                 showEffect="fade" 
                                 width="300"
                                 message="#${deleteBeanMessage}" 
                                 header="#${bundleConfirm}" 
                                 severity="warn">
                    <p:commandButton value="#${bundleYes}" 
                                     icon="pi pi-check" 
                                     actionListener="#${deleteMethod}"
                                     process="@this" 
                                     update=":${formId}:delete-${variableBean}s-button"
                                     oncomplete="PF('delete${className}Dialog').hide()" />
                    <p:commandButton value="#${bundleNo}" 
                                     type="button" 
                                     styleClass="ui-button-secondary" 
                                     icon="pi pi-times"
                                     onclick="PF('delete${className}Dialog').hide()" />
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

