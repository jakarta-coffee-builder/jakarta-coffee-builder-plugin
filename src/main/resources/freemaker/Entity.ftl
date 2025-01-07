package ${packageName};

<#list fields as field>
    <#if field.isId?? && field.isId?string('y','n') == 'y'>
import jakarta.persistence.Id;
    </#if>
</#list>

public class ${className} {

<#if (fields??) && (fields?size > 0)>
    <#list fields as field>
        <#if field.isId?? && field.isId?string('y','n') == 'y'>
    @Id
        </#if>
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