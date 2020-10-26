package com.yoonje.repository;

import com.yoonje.model.ToDoReference;
import org.springframework.data.repository.CrudRepository;

import javax.transaction.Transactional;
import java.util.List;

public interface ToDoReferenceRepository extends CrudRepository<ToDoReference, Long> {

    List<ToDoReference> findAllByReferredId(Long referredId);

    @Transactional
    void deleteAllByReferredId(Long referredId);

}
