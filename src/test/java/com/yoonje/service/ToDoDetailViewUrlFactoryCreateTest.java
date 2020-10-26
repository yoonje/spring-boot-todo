package com.yoonje.service;

import org.junit.Test;
import org.springframework.beans.factory.UnsatisfiedDependencyException;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.mock.env.MockEnvironment;

public class ToDoDetailViewUrlFactoryCreateTest {

    @Test(expected = UnsatisfiedDependencyException.class)
    public void base_url이_invalid한경우_빈을_생성할수없다() {
        // given
        MockEnvironment environment = new MockEnvironment();
        environment.setProperty("todo.detail-view.base-url", "\"");

        AnnotationConfigApplicationContext appContext = new AnnotationConfigApplicationContext();
        appContext.setEnvironment(environment);
        appContext.scan("com.dain");
        appContext.refresh();

        // when
        appContext.getBean(ToDoDetailViewUrlFactory.class);
    }


}
