package ${packageName};

<#if (importsList??) && (importsList?size > 0)>
    <#list importsList as importItem>
import ${importItem};
    </#list>
</#if>
public class ${className} {

<#if (fields??) && (fields?size > 0)>
<#list fields as field>
    private ${field.type} ${field.name};
</#list>

<#list fields as field>
    <#if (!getters??) || (getters==true)>
    public ${field.type} get${field.name?cap_first}() {
        return ${field.name};
    }
    </#if>

    <#if (!setters??) || (setters==true)>
    public void set${field.name?cap_first}(${field.type} ${field.name}) {
        this.${field.name} = ${field.name};
    }
    </#if>
</#list>
</#if>
}