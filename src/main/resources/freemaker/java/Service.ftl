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
public class ${modelName}RepositoryImpl implements ${modelName}Repository {

    @Inject
    private ${modelName}Mapper mapper;

    @Inject
    private ${modelName}EntityRepository entityRepository;

    @Override public List<${modelName}> findAll() {
     return entityRepository.findAll().map(mapper::entityToModel).toList();
    }

    @Transactional
    @Override public ${modelName} save(${modelName} model) {
        var entity = mapper.modelToEntity(model);
        return mapper.entityToModel(entityRepository.save(entity));
    }

    @Override public Optional<${modelName}> findById(${idClass} id) {
        return entityRepository.findById(id).map(mapper::entityToModel);
    }

    @Transactional
    @Override public void deleteById(${idClass} id){
        entityRepository.deleteById(id);
    }

    @Transactional
    @Override public void delete(${modelName} model){
        entityRepository.delete(mapper.modelToEntity(model));
    }

    @Transactional
    @Override public void deleteAll(List<${modelName}> models){
        entityRepository.deleteAll(models.stream().map(mapper::modelToEntity).toList());
    }
}