package ${packageName};

<#if (importsList??) && (importsList?size > 0)>
    <#list importsList as importItem>
import ${importItem};
    </#list>
</#if>

public record ${className}(
<#if (fields??) && (fields?size > 0)>
<#list fields as field>
    ${field.type} ${field.name}<#if field_has_next>,</#if>
</#list>
</#if>
){
}