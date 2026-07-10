package ${packageName};

<#if (importsList??) && (importsList?size > 0)>
    <#list importsList as importItem>
import ${importItem};
    </#list>
</#if>
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants.ComponentModel;

@Mapper(componentModel = ComponentModel.JAKARTA_CDI)
public interface ${className} {
    ${modelName}Entity modelToEntity(${modelName} model);

    ${modelName} entityToModel(${modelName}Entity entity);
}