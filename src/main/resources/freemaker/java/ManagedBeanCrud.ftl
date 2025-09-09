package ${packageName};

<#if (importsList??) && (importsList?size > 0)>
    <#list importsList as importItem>
import ${importItem};
    </#list>
</#if>

import jakarta.inject.Named;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import java.util.List;

@Named
@RequestScoped
public class ${className} {

    @Inject
    private ${modelName}Service ${instanceModelName}Service;

    private ${modelName} current${modelName};

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
}
