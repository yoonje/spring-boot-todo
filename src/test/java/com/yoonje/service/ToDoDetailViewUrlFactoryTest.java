package com.yoonje.service;

import org.junit.Test;

import java.net.URI;
import java.net.URISyntaxException;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class ToDoDetailViewUrlFactoryTest {

    @Test
    public void todo상세뷰페이지_링크를_생성한다() {
        // given
        String baseUrl = "http://test.com/todo/";
        ToDoDetailViewUrlFactory toDoDetailViewUrlFactory = new ToDoDetailViewUrlFactory(baseUrl);
        Long id = 1l;

        // when
        URI uri = toDoDetailViewUrlFactory.get(id);

        // then
        assertThat(uri.toString(), is("http://test.com/todo/1"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void todo_id가_null이면_링크를_생성할수없다() {
        // given
        String baseUrl = "http://test.com/todo/";
        ToDoDetailViewUrlFactory toDoDetailViewUrlFactory = new ToDoDetailViewUrlFactory(baseUrl);
        Long id = null;

        // when
        toDoDetailViewUrlFactory.get(id);
    }

    @Test(expected = URISyntaxException.class)
    public void uri생성에_실패하는경우_예외발생() {
        // given
        String baseUrl = "\"";
        ToDoDetailViewUrlFactory toDoDetailViewUrlFactory = new ToDoDetailViewUrlFactory(baseUrl);
        Long id = 1l;

        // when
        toDoDetailViewUrlFactory.get(id);
    }

}
