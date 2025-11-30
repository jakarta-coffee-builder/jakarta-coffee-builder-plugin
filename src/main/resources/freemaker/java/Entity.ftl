package ${packageName};

import jakarta.persistence.Entity;
<#if (tableName??)>
import jakarta.persistence.Table;
</#if>
<#list fields as field>
    <#if field.isId?? && field.isId?string('y','n') == 'y'>
import jakarta.persistence.Id;
    </#if>
</#list>
<#if (importsList??) && (importsList?size > 0)>
    <#list importsList as importItem>
import ${importItem};
    </#list>
</#if>

@Entity
<#if (tableName??)>
@Table(name = "${tableName}")
</#if>
public class ${className} {

<#if (fields??) && (fields?size > 0)>
    <#list fields as field>
        <#if field.isId?? && field.isId?string('y','n') == 'y'>
    @Id
        </#if>
        <#if field.annotations??>
            <#list field.annotations as annotation>
    @${annotation.name}<#if annotation.description??> (
                <#list annotation.description as key,value>
                    <#if value?is_string>
        <#if value?starts_with("+")>
        ${key} = ${value?substring(1)}<#if key_has_next>,</#if>
        <#else>
        ${key} = "${value}"<#if key_has_next>,</#if>
        </#if>
                    <#elseif value?is_number>
        ${key} = ${value?string("0")}<#if key_has_next>,</#if>
                    <#elseif value?is_boolean>
        ${key} = ${value?string("true","false")}<#if key_has_next>,</#if>
            </#if>
                </#list>
    )</#if>
            </#list>
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