package ${packageName};

<#if (importsList??) && (importsList?size > 0)>
    <#list importsList as importItem>
import ${importItem};
    </#list>
</#if>

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class ${modelName}Service {

    @Inject
    private ${modelName}Mapper mapper;

    @Inject
    private ${modelName}EntityRepository entityRepository;

    public List<${modelName}> findAll() {
     return entityRepository.findAll().map(mapper::entityToModel).toList();
    }

    @Transactional
    public ${modelName} save(${modelName} model) {
        var entity = mapper.modelToEntity(model);
        return mapper.entityToModel(entityRepository.save(entity));
    }

    public Optional<${modelName}> findById(${idClass} id) {
        return entityRepository.findById(id).map(mapper::entityToModel);
    }

    @Transactional
    public void deleteById(${idClass} id){
        entityRepository.deleteById(id);
    }

    @Transactional
    public void deleteBy(${modelName} model){
        entityRepository.delete(mapper.modelToEntity(model));
    }
}