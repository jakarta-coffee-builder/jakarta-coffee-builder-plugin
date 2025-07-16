package ${packageName};

public enum ${className} {
    <#list values as value>
        ${value}<#if value_has_next>,</#if>
    </#list>

}