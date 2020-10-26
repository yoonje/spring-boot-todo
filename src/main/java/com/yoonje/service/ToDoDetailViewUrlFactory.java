package com.yoonje.service;

import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;
import java.net.URI;

@NoArgsConstructor
@Service
public class ToDoDetailViewUrlFactory {

    private String baseUrl;

    @Autowired
    public ToDoDetailViewUrlFactory(@Value("${todo.detail-view.base-url}") String baseUrl) {
        this.baseUrl = baseUrl;
    }

    @SneakyThrows
    @PostConstruct
    public void verify() {
        new URI(this.baseUrl);
    }

    @SneakyThrows
    public URI get(Long id) {
        Assert.notNull(id, "todo id can not be null");
        return new URI(this.baseUrl + id);
    }

}
