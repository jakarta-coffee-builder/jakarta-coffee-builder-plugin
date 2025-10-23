package ${packageName};

<#if (importsList??) && (importsList?size > 0)>
    <#list importsList as importItem>
import ${importItem};
    </#list>
</#if>
<#assign formId="${instanceModelName}Form" />
<#assign serviceClassName="${modelName}Repository" />
<#assign serviceInstanceName="${instanceModelName}Repository" />
<#assign currentModel="current${modelName}" />
<#assign selectedModels="selected${modelName}s" />
<#assign idNameCap=idName?cap_first />

import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import org.primefaces.PrimeFaces;

import jakarta.annotation.PostConstruct;
import jakarta.inject.Named;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;

@Named
@ViewScoped
public class ${className} implements Serializable{

    @Inject
    private ${serviceClassName} ${serviceInstanceName};

    private ${modelName} ${currentModel};

    private List<${modelName}> ${selectedModels};

    @PostConstruct
    public void init() {
        this.${selectedModels} = new ArrayList<>();
    }

    public ${modelName} getCurrent${modelName}() {
        return ${currentModel};
    }

    public void setCurrent${modelName}(${modelName} ${currentModel}) {
        this.${currentModel} = ${currentModel};
    }

    public List<${modelName}> get${modelName}sList() {
        return ${serviceInstanceName}.findAll();
    }

    public void openNew(){
        this.${currentModel} = new ${modelName}();
    }

    public void save${modelName}(){
        if (${currentModel}.get${idNameCap}() == null){
            ${serviceInstanceName}.save(${currentModel});
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("${modelName} Added"));
        }else{
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("${modelName} Updated"));
        }
        PrimeFaces.current().executeScript("PF('manage${modelName}Dialog').hide()");
        PrimeFaces.current().ajax().update("${formId}:messages", "${formId}:dt-${instanceModelName}s");
    }

    public List<${modelName}> getSelected${modelName}s() {
        return ${selectedModels};
    }

    public void setSelected${modelName}s(List<${modelName}> ${selectedModels}) {
        this.${selectedModels} = ${selectedModels};
    }

    public String getDeleteButtonMessage() {
        if (hasSelected${modelName}s()) {
            int size = this.${selectedModels}.size();
            return size > 1 ? size + " ${instanceModelName}s selected" : "1 ${instanceModelName} selected";
        }
        return "Delete";
    }

    public void delete${modelName}() {
        ${serviceInstanceName}.delete(this.${currentModel});
        this.${selectedModels}.remove(this.${currentModel});
        this.${currentModel} = null;
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("${modelName} Removed"));
        PrimeFaces.current().ajax().update("${formId}:messages", "${formId}:dt-${instanceModelName}s");
    }

    public boolean hasSelected${modelName}s() {
        return this.${selectedModels} != null && !this.${selectedModels}.isEmpty();
    }

    public void deleteSelected${modelName}s() {
        ${serviceInstanceName}.deleteAll(${selectedModels});
        this.${selectedModels} = null;
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("${modelName}s Removed"));
        PrimeFaces.current().ajax().update("${formId}:messages", "${formId}:dt-${instanceModelName}s");
        PrimeFaces.current().executeScript("PF('dt${modelName}s').clearFilters()");
    }
}
