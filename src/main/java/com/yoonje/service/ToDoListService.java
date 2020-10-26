package com.yoonje.service;

import com.yoonje.controller.model.ErrorCause;
import com.yoonje.exception.InvalidReferenceException;
import com.yoonje.exception.NotClosableException;
import com.yoonje.exception.NotFoundException;
import com.yoonje.model.Status;
import com.yoonje.model.ToDo;
import com.yoonje.model.ToDoReference;
import com.yoonje.repository.ToDoReferenceRepository;
import com.yoonje.repository.ToDoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

@Service
public class ToDoListService {

    private static final Sort SORT = new Sort(Sort.Direction.DESC, "id");

    private ToDoRepository toDoRepository;
    private ToDoReferenceRepository referenceRepository;

    public ToDoListService(@Autowired ToDoRepository toDoRepository,
                           @Autowired ToDoReferenceRepository referenceRepository) {
        this.toDoRepository = toDoRepository;
        this.referenceRepository = referenceRepository;
    }

    public Long create(ToDo todo) {
        todo.getReferences().forEach(ref -> ref.setToDo(todo));
        checkReferable(todo.getReferences());

        ToDo toDo = this.toDoRepository.save(todo);
        return toDo.getId();
    }

    public ToDo read(Long id) {
        Optional<ToDo> toDo = this.toDoRepository.findById(id);
        if (!toDo.isPresent()) {
            throw new NotFoundException(id);
        }
        return toDo.get();
    }

    public int update(ToDo todo) {
        Assert.notNull(todo.getId(), "todo is can not be null in update request");
        Optional<ToDo> find = this.toDoRepository.findById(todo.getId());
        if (!find.isPresent()) {
            throw new NotFoundException(todo.getId());
        }

        Set<Long> newReferences = todo.getReferences().stream()
                .map(ToDoReference::getReferredId)
                .filter(ref -> find.get().getReferences().stream().noneMatch(originalRef -> ref == originalRef.getReferredId()))
                .collect(toSet());
        checkReferable(todo.getId(), newReferences);

        switch (todo.getStatus()) {
            case open:
                openAllReferredToDoList(todo.getId());
                break;
            case closed:
                checkClosable(todo);
                break;
        }

        todo.getReferences().forEach(ref -> ref.setToDo(todo));
        this.toDoRepository.save(todo);
        return 1;
    }

    public int updateStatus(Long id, Status status) {
        Assert.notNull(id, "todo is can not be null in update request");
        Optional<ToDo> find = this.toDoRepository.findById(id);
        if (!find.isPresent()) {
            throw new NotFoundException(id);
        }

        ToDo toDo = find.get();
        switch (status) {
            case open:
                openAllReferredToDoList(id);
                toDo.open();
                break;
            case closed:
                checkClosable(toDo);
                toDo.complete();
                break;
        }
        this.toDoRepository.save(toDo);
        return 1;
    }

    public int delete(Long id) {
        try {
            this.referenceRepository.deleteAllByReferredId(id);
            this.toDoRepository.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException(id);
        }
        return 1;
    }

    public Page<ToDo> list(int currentPage, int display) {
        Pageable pageable = PageRequest.of(currentPage - 1, display, SORT);
        return this.toDoRepository.findAll(pageable);
    }

    private void checkReferable(Set<ToDoReference> references) {
        if (references.stream()
                .map(ToDoReference::getReferredId)
                .anyMatch(ref -> !this.toDoRepository.findById(ref).isPresent())) {
            throw new InvalidReferenceException(ErrorCause.REFERENCE_NOT_FOUND);
        }
    }

    private void checkReferable(Long id, Set<Long> newReferences) {
        if (newReferences.contains(id)) {
            throw new InvalidReferenceException(ErrorCause.SELF_REFERENCE);
        }

        List<Optional<ToDo>> newReferredToDoList = newReferences.stream()
                .map(this.toDoRepository::findById)
                .collect(toList());

        if (newReferredToDoList.stream().anyMatch(toDo -> !toDo.isPresent())) {
            throw new InvalidReferenceException(ErrorCause.REFERENCE_NOT_FOUND);
        }
        if (newReferredToDoList.stream().map(Optional::get)
                .anyMatch(newRefs -> newRefs.getReferences().stream().anyMatch(ref -> id.equals(ref.getReferredId())))) {
            throw new InvalidReferenceException(ErrorCause.CROSS_REFERENCE);
        }
    }

    private void openAllReferredToDoList(Long id) {
        this.referenceRepository.findAllByReferredId(id).stream()
                .map(ToDoReference::getToDo)
                .filter(referredToDo -> referredToDo.getStatus() == Status.closed)
                .forEach(referredToDo -> {
                    referredToDo.open();
                    this.toDoRepository.save(referredToDo);
                });
    }

    private void checkClosable(ToDo toDo) {
        if (toDo.getReferences().stream()
                .map(ref -> this.toDoRepository.findById(ref.getReferredId()))
                .filter(Optional::isPresent).map(Optional::get)
                .anyMatch(ref -> ref.getStatus() == Status.open)) {
            throw new NotClosableException();
        }
    }

}
