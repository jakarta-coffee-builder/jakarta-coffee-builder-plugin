package ${packageName};

import jakarta.persistence.Entity;
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
public class ${className} {

<#if (fields??) && (fields?size > 0)>
    <#list fields as field>
        <#if field.isId?? && field.isId?string('y','n') == 'y'>
    @Id
        </#if>
        <#if field.Column??>
    @Column(
            <#list field.Column as key,value>
                <#if value?is_string>
        ${key} = "${value}"<#if key_has_next>,</#if>
                <#elseif value?is_number>
        ${key} = ${value?string("0")}<#if key_has_next>,</#if>
                <#elseif value?is_boolean>
        ${key} = ${value?string("true","false")}<#if key_has_next>,</#if>
        </#if>
            </#list>
    )
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