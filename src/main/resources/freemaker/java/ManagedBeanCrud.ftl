package ${packageName};

<#if (importsList??) && (importsList?size > 0)>
    <#list importsList as importItem>
import ${importItem};
    </#list>
</#if>
<#assign formId="${instanceModelName}Form" />
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import org.primefaces.PrimeFaces;

import jakarta.annotation.PostConstruct;
import jakarta.inject.Named;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import java.util.List;
import java.util.ArrayList;

@Named
@RequestScoped
public class ${className} {

    @Inject
    private ${modelName}Service ${instanceModelName}Service;

    private ${modelName} current${modelName};

    private List<${modelName}> selected${modelName}s;

    @PostConstruct
    public void init() {
        this.selected${modelName}s = new ArrayList<>();
    }

    public ${modelName} getCurrent${modelName}() {
        return current${modelName};
    }

    public void setCurrent${modelName}(${modelName} current${modelName}) {
        this.current${modelName} = current${modelName};
    }

    public List<${modelName}> get${modelName}sList() {
        return ${instanceModelName}Service.findAll();
    }

    public void openNew(){
        this.current${modelName} = new ${modelName}();
    }

    public void save${modelName}(){
    }

    public List<${modelName}> getSelected${modelName}s() {
        return selected${modelName}s;
    }

    public void setSelected${modelName}s(List<${modelName}> selected${modelName}s) {
        this.selected${modelName}s = selected${modelName}s;
    }

    public String getDeleteButtonMessage() {
        if (hasSelected${modelName}s()) {
            int size = this.selected${modelName}s.size();
            return size > 1 ? size + " ${instanceModelName}s selected" : "1 ${instanceModelName} selected";
        }
        return "Delete";
    }

    public void delete${modelName}() {
        ${instanceModelName}Service.delete(this.current${modelName});
        this.selected${modelName}s.remove(this.current${modelName});
        this.current${modelName} = null;
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("${modelName} Removed"));
        PrimeFaces.current().ajax().update("${formId}:messages", "${formId}:dt-${instanceModelName}s");
    }

    public boolean hasSelected${modelName}s() {
        return this.selected${modelName}s != null && !this.selected${modelName}s.isEmpty();
    }

    public void deleteSelected${modelName}s() {
        ${instanceModelName}Service.deleteAll(selected${modelName}s);
        this.selected${modelName}s = null;
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("${modelName}s Removed"));
        PrimeFaces.current().ajax().update("${formId}:messages", "${formId}:dt-${instanceModelName}s");
        PrimeFaces.current().executeScript("PF('dt${modelName}s').clearFilters()");
    }
}
