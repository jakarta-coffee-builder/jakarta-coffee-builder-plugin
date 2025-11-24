package ${packageName};

<#if (importsList??) && (importsList?size > 0)>
    <#list importsList as importItem>
import ${importItem};
    </#list>
</#if>

import java.util.List;
import java.util.Optional;

public interface ${modelName}Repository {

  List<${modelName}> findAll();

  ${modelName} save(${modelName} model) ;

  Optional<${modelName}> findById(${idClass} id);

  void deleteById(${idClass} id);

  void delete(${modelName} model);

  void deleteAll(List<${modelName}> models);
}