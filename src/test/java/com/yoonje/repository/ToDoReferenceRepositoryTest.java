package com.yoonje.repository;

import com.yoonje.MockToDoFactory;
import com.yoonje.TestConfig;
import com.yoonje.model.ToDo;
import com.yoonje.model.ToDoReference;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class ToDoReferenceRepositoryTest extends TestConfig {

    @Autowired
    private ToDoReferenceRepository referenceRepository;
    @Autowired
    private ToDoRepository toDoRepository;

    @Test
    public void 참조된id로_검색해서_참조관계를_삭제할수있다() {
        // given
        ToDoReference reference = new ToDoReference();
        reference.setReferredId(100l);
        ToDo toDo = MockToDoFactory.getMockToDo();
        toDo.getReferences().add(reference);
        reference.setToDo(toDo);
        reference = this.referenceRepository.save(reference);

        // when
        this.referenceRepository.deleteAllByReferredId(reference.getReferredId());

        // then
        assertThat(this.referenceRepository.findById(reference.getId()).isPresent(), is(false));
    }

    @Test
    public void 참조된아이디로_조회할수있다() {
        // given
        ToDoReference reference = new ToDoReference();
        reference.setReferredId(100l);
        ToDo toDo = MockToDoFactory.getMockToDo();
        toDo.getReferences().add(reference);
        reference.setToDo(toDo);
        this.referenceRepository.save(reference);

        // when
        List<ToDoReference> references = this.referenceRepository.findAllByReferredId(100l);

        // then
        assertThat(
                references.stream().anyMatch(ref -> ref.getToDo().getId().equals(toDo.getId())),
                is(true));
    }

    @Test
    public void 참조관계삭제는_힐일을_함께_삭제하지않는다() {
        // given
        ToDoReference reference = new ToDoReference();
        reference.setReferredId(100l);
        ToDo mockToDo = MockToDoFactory.getMockToDo();
        mockToDo.getReferences().add(reference);
        reference.setToDo(mockToDo);
        mockToDo = this.toDoRepository.save(mockToDo);

        // when
        this.referenceRepository.deleteAllByReferredId(100l);

        // then
        assertThat(this.toDoRepository.findById(mockToDo.getId()).isPresent(), is(true));
    }

}
