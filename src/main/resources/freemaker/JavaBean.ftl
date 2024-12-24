package ${packageName};

<#if (importsList??) && (importsList?size > 0)>
<#list importsList as importItem>
import ${importItem};
</#list>
</#if>
<#if (annotations??) && (annotations?size > 0)>
    <#list annotations?keys as importItem>
import ${importItem};
    </#list>
</#if>

<#if (annotations??) && (annotations?size > 0)>
<#list annotations as annotation,properties>
@${annotation?keep_after_last(".")}<#if (properties??) && (properties?size > 0)>(
  <#list properties as property,value>
    ${property} = ${value}<#if property_has_next>, </#if>
  </#list>
)</#if>
</#list>
</#if>
public class ${className} {

<#if (fields??) && (fields?size > 0)>
<#list fields as field>
    private ${field.type} ${field.name};
</#list>

<#list fields as field>
    public ${field.type} get${field.name?cap_first}() {
        return ${field.name};
    }

    public void set${field.name?cap_first}(${field.type} ${field.name}) {
        this.${field.name} = ${field.name};
    }
</#list>
</#if>
}