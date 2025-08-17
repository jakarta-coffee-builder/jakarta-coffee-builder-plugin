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
import ${packageEntity}.${entityName};
import jakarta.data.repository.Repository;
import jakarta.data.repository.${classRepository}Repository;

@Repository
public interface ${className} extends ${classRepository}Repository<${entityName}, ${idType}> {
}