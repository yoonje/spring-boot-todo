package com.yoonje.controller;

import com.yoonje.ResourceFileReader;
import com.yoonje.TestMvcConfig;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ToDoListIntegrationTest extends TestMvcConfig {

    @Test
    public void _1_create() throws Exception {
        String json = ResourceFileReader.readFile("todo.json");

        URI uri = UriComponentsBuilder.fromPath("/todos")
                .build().toUri();

        mockMvc.perform(MockMvcRequestBuilders.post(uri)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(json))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andExpect(jsonPath("$.id", is(notNullValue())));
    }

    @Test
    public void _2_read() throws Exception {
        URI uri = UriComponentsBuilder.fromPath("/todos/1")
                .build().toUri();

        mockMvc.perform(MockMvcRequestBuilders.get(uri))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(notNullValue())))
                .andExpect(jsonPath("$.status", is(notNullValue())));
    }

    @Test
    public void _3_update() throws Exception {
        String json = ResourceFileReader.readFile("todo.json");

        URI uri = UriComponentsBuilder.fromPath("/todos/1")
                .build().toUri();

        mockMvc.perform(MockMvcRequestBuilders.put(uri)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(json))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    public void _4_patch() throws Exception {
        URI uri = UriComponentsBuilder.fromPath("/todos/1")
                .build().toUri();

        mockMvc.perform(MockMvcRequestBuilders.patch(uri)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content("{\"id\":\"1\", \"status\":\"open\"}"))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    public void _5_listAll() throws Exception {
        URI uri = UriComponentsBuilder.fromPath("/todos")
                .build().toUri();

        mockMvc.perform(MockMvcRequestBuilders.get(uri))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(notNullValue())))
                .andExpect(jsonPath("$[0].description", is(notNullValue())));
    }

    @Test
    public void _6_delete() throws Exception {
        URI uri = UriComponentsBuilder.fromPath("/todos/1")
                .build().toUri();

        mockMvc.perform(MockMvcRequestBuilders.delete(uri))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

}
