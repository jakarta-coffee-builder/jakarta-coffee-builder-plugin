package ${packageName};

<#if (importsList??) && (importsList?size > 0)>
    <#list importsList as importItem>
import ${importItem};
    </#list>
</#if>

public interface ${modelName}Repository extends AbstractModelRepository<${modelName},${idClass}>{


}