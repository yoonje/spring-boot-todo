package com.yoonje.repository;

import com.yoonje.model.ToDo;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ToDoRepository extends PagingAndSortingRepository<ToDo, Long> {

}
