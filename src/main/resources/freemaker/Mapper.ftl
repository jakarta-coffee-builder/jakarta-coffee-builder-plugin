package ${packageName};

<#if (importsList??) && (importsList?size > 0)>
    <#list importsList as importItem>
import ${importItem};
    </#list>
</#if>
import org.mapstruct.Mapper;

@Mapper(componentModel = "jakarta")
public interface ${className} {
    ${modelName}Entity modelToEntity(${modelName} model);

    ${modelName} entityToModel(${modelName}Entity entity);
}