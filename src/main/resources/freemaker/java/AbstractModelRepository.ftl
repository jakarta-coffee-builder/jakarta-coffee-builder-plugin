package ${packageName};

import java.util.List;
import java.util.Optional;

public interface AbstractModelRepository
<M,I> {

List
<M> findAll();

    M save(M model);

    Optional
    <M> findById(I id);

        void deleteById(I id);

        void delete(M model);

        void deleteAll(List
        <M> models);
            }
