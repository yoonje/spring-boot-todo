package com.yoonje.repository;

import com.yoonje.MockToDoFactory;
import com.yoonje.TestConfig;
import com.yoonje.model.Status;
import com.yoonje.model.ToDo;
import com.yoonje.model.ToDoReference;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class ToDoRepositoryTest extends TestConfig {

    @Autowired
    private ToDoRepository toDoRepository;
    @Autowired
    private ToDoReferenceRepository referenceRepository;

    @Test
    public void 할일을_생성할수_있다() {
        // given
        ToDo toDo = new ToDo();
        toDo.setDescription("hi");
        toDo.open();

        // when
        ToDo result = this.toDoRepository.save(toDo);

        // then
        assertThat(result.getId(), is(notNullValue()));
    }

    @Test(expected = ConstraintViolationException.class)
    public void 할일설명은_최소_1자를_입력해야한다() {
        // given
        ToDo toDo = MockToDoFactory.getMockToDo();
        toDo.setId(null);
        toDo.setDescription("");

        // when
        this.toDoRepository.save(toDo);
    }

    @Test(expected = ConstraintViolationException.class)
    public void 할일설명은_최대_20자까지_입력할수있다() {
        // given
        ToDo toDo = MockToDoFactory.getMockToDo();
        toDo.setId(null);
        toDo.setDescription("12345678901234567890"/*20자*/ + "A");

        // when
        this.toDoRepository.save(toDo);
    }

    @Test
    public void 할일을_조회할수_있다() {
        // given
        ToDo toDo = MockToDoFactory.getMockToDo();
        Long id = this.toDoRepository.save(toDo).getId();

        // when
        Optional<ToDo> result = this.toDoRepository.findById(id);

        // then
        assertThat(result.isPresent(), is(true));
    }

    @Test
    public void 할일을_수정할수_있다() {
        // given
        ToDo toDo = MockToDoFactory.getMockToDo();
        toDo = this.toDoRepository.save(toDo);
        toDo.setDescription("updated");

        // when
        ToDo result = this.toDoRepository.save(toDo);

        // then
        assertThat(result.getDescription(), is("updated"));
    }

    @Test
    public void 할일을_완료처리할수_있다() {
        // given
        ToDo toDo = MockToDoFactory.getMockToDo();
        toDo.open();
        toDo = this.toDoRepository.save(toDo);

        // when
        toDo.complete();
        ToDo result = this.toDoRepository.save(toDo);

        // then
        assertThat(result.getStatus(), is(Status.closed));
    }

    @Test
    public void 할일을_열수_있다() {
        // given
        ToDo toDo = MockToDoFactory.getMockToDo();
        toDo.complete();
        toDo = this.toDoRepository.save(toDo);

        // when
        toDo.open();
        ToDo result = this.toDoRepository.save(toDo);

        // then
        assertThat(result.getStatus(), is(Status.open));
    }

    @Test
    public void 할일을_삭제할수_있다() {
        // given
        ToDo toDo = MockToDoFactory.getMockToDo();
        toDo = this.toDoRepository.save(toDo);

        // when
        this.toDoRepository.deleteById(toDo.getId());

        // then
        assertThat(this.toDoRepository.findById(toDo.getId()).isPresent(), is(false));
    }

    @Test
    public void 할일을삭제하면_할일의_참조관계도_삭제한다() {
        // given
        ToDo toDo = MockToDoFactory.getMockToDo();
        assertThat(toDo.getReferences(), is(not(empty())));
        toDo = this.toDoRepository.save(toDo);
        toDo = toDoRepository.findById(toDo.getId()).get();
        List<Long> referenceIds = toDo.getReferences().stream().map(ToDoReference::getId).collect(toList());

        // when
        this.toDoRepository.deleteById(toDo.getId());

        // then
        referenceIds.forEach(refId -> {
            assertThat(this.referenceRepository.findById(refId).isPresent(), is(false));
        });
    }

    @Test
    public void 목록을_조회할수_있다() {
        // given
        int currentPage = 0;
        int display = 10;
        PageRequest pageRequest = PageRequest.of(currentPage, display);

        // when
        Page<ToDo> toDos = this.toDoRepository.findAll(pageRequest);

        // then
        assertThat(toDos.getContent(), hasSize(greaterThan(0)));
    }

    @Test
    public void 목록을_정렬해서_구할수있다() {
        // given
        int currentPage = 0;
        int display = 2;
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        PageRequest pageRequest = PageRequest.of(currentPage, display, sort);

        // when
        Page<ToDo> toDos = this.toDoRepository.findAll(pageRequest);

        // then
        assertThat(toDos.getContent(), hasSize(display));
        assertThat(toDos.getContent().get(0).getId(), greaterThan(toDos.getContent().get(1).getId()));
    }

    @Test
    public void 할일을수정하면_참조도_함께수정된다() {
        // given
        ToDo mockToDo = MockToDoFactory.getMockToDo();
        mockToDo.getReferences().clear();

        ToDoReference reference1 = new ToDoReference();
        reference1.setReferredId(100l);
        reference1.setToDo(mockToDo);
        mockToDo.getReferences().add(reference1);

        ToDoReference reference2 = new ToDoReference();
        reference2.setReferredId(200l);
        reference2.setToDo(mockToDo);
        mockToDo.getReferences().add(reference2);

        mockToDo = this.toDoRepository.save(mockToDo);

        // when
        Set<ToDoReference> newReferences = mockToDo.getReferences().stream()
                .filter(ref -> ref.getReferredId() == reference1.getReferredId())
                .collect(toSet());
        ToDoReference reference3 = new ToDoReference();
        reference3.setReferredId(300l);
        reference3.setToDo(mockToDo);
        newReferences.add(reference3);

        mockToDo.setReferences(newReferences);
        this.toDoRepository.save(mockToDo);

        // then
        ToDo toDo = toDoRepository.findById(mockToDo.getId()).get();
        List<Long> references = toDo.getReferences().stream()
                .map(ToDoReference::getReferredId).collect(toList());
        assertThat(references, hasSize(2));
        assertThat(references, hasItems(reference1.getReferredId(), reference3.getReferredId()));
    }

}
