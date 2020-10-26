package com.yoonje.controller;

import com.yoonje.MockToDoFactory;
import com.yoonje.ResourceFileReader;
import com.yoonje.controller.advice.GlobalExceptionHandler;
import com.yoonje.controller.model.ErrorCause;
import com.yoonje.exception.InvalidReferenceException;
import com.yoonje.exception.NotClosableException;
import com.yoonje.exception.NotFoundException;
import com.yoonje.model.Status;
import com.yoonje.model.ToDo;
import com.yoonje.service.ToDoDetailViewUrlFactory;
import com.yoonje.service.ToDoListService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.ConstraintViolationException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(MockitoJUnitRunner.class)
public class ToDoListControllerTest {

    @InjectMocks
    private ToDoListController toDoListController;
    @Mock
    private ToDoListService toDoListService;
    @Spy
    private ToDoDetailViewUrlFactory toDoDetailViewUrlFactory;
    private MockMvc mockMvc;

    @Before
    public void initMockMvc() {
        mockMvc = MockMvcBuilders.standaloneSetup(toDoListController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    public void create() throws Exception {
        when(toDoListService.create(any(ToDo.class))).thenReturn(123l);

        String json = ResourceFileReader.readFile("todo.json");
        URI uri = UriComponentsBuilder.fromPath("/todos")
                .build().toUri();

        mockMvc.perform(MockMvcRequestBuilders.post(uri)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(json))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andExpect(jsonPath("$.id", is(123)));
    }

    @Test
    public void create_바디가_누락된경우_400() throws Exception {
        URI uri = UriComponentsBuilder.fromPath("/todos")
                .build().toUri();

        mockMvc.perform(MockMvcRequestBuilders.post(uri))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void create_제약조건을_어긴경우_400() throws Exception {
        when(toDoListService.create(any(ToDo.class))).thenThrow(new ConstraintViolationException(new HashSet<>()));
        String json = ResourceFileReader.readFile("todo.json");

        URI uri = UriComponentsBuilder.fromPath("/todos")
                .build().toUri();

        mockMvc.perform(MockMvcRequestBuilders.post(uri)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(json))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void create_생성에_실패한경우_500() throws Exception {
        when(toDoListService.create(any(ToDo.class))).thenThrow(new RuntimeException("something unexpected happened"));

        String json = ResourceFileReader.readFile("todo.json");
        URI uri = UriComponentsBuilder.fromPath("/todos")
                .build().toUri();

        mockMvc.perform(MockMvcRequestBuilders.post(uri)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(json))
                .andDo(print())
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message", is(notNullValue())));
    }

    @Test
    public void read() throws Exception {
        when(toDoListService.read(anyLong())).thenReturn(MockToDoFactory.getMockToDo());

        URI uri = UriComponentsBuilder.fromPath("/todos/1")
                .build().toUri();

        mockMvc.perform(MockMvcRequestBuilders.get(uri))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(notNullValue())))
                .andExpect(jsonPath("$.status", is(notNullValue())));
    }

    @Test
    public void read_id가_잘못된경우_400() throws Exception {
        URI uri = UriComponentsBuilder.fromPath("/todos/a")
                .build().toUri();

        mockMvc.perform(MockMvcRequestBuilders.get(uri))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void read_존재하지않는_할일일경우_404() throws Exception {
        when(toDoListService.read(anyLong())).thenThrow(new NotFoundException(1l));

        URI uri = UriComponentsBuilder.fromPath("/todos/1")
                .build().toUri();

        mockMvc.perform(MockMvcRequestBuilders.get(uri))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is(notNullValue())));
    }

    @Test
    public void read_조회에_실패한경우_500() throws Exception {
        when(toDoListService.read(anyLong())).thenThrow(new RuntimeException("something unexpected happened"));

        URI uri = UriComponentsBuilder.fromPath("/todos/1")
                .build().toUri();

        mockMvc.perform(MockMvcRequestBuilders.get(uri))
                .andDo(print())
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message", is(notNullValue())));
    }

    @Test
    public void update() throws Exception {
        when(toDoListService.update(any(ToDo.class))).thenReturn(1);

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
    public void update_id가_잘못된경우_400() throws Exception {
        String json = ResourceFileReader.readFile("todo.json");
        URI uri = UriComponentsBuilder.fromPath("/todos/a")
                .build().toUri();

        mockMvc.perform(MockMvcRequestBuilders.put(uri)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(json))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void update_바디가_누락된경우_400() throws Exception {
        URI uri = UriComponentsBuilder.fromPath("/todos/1")
                .build().toUri();

        mockMvc.perform(MockMvcRequestBuilders.put(uri))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void update_제약조건을_어긴경우_400() throws Exception {
        when(toDoListService.update(any(ToDo.class))).thenThrow(new ConstraintViolationException(new HashSet<>()));
        String json = ResourceFileReader.readFile("todo.json");

        URI uri = UriComponentsBuilder.fromPath("/todos/1")
                .build().toUri();

        mockMvc.perform(MockMvcRequestBuilders.put(uri)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(json))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void update_닫을수없는_할일인경우_403() throws Exception {
        when(toDoListService.update(any(ToDo.class))).thenThrow(new NotClosableException());
        String json = ResourceFileReader.readFile("todo.json");
        URI uri = UriComponentsBuilder.fromPath("/todos/1")
                .build().toUri();

        mockMvc.perform(MockMvcRequestBuilders.put(uri)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(json))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    public void update_잘못된_참조가걸린경우_403() throws Exception {
        when(toDoListService.update(any(ToDo.class))).thenThrow(new InvalidReferenceException(ErrorCause.REFERENCE_NOT_FOUND));

        String json = ResourceFileReader.readFile("todo.json");
        URI uri = UriComponentsBuilder.fromPath("/todos/1")
                .build().toUri();

        mockMvc.perform(MockMvcRequestBuilders.put(uri)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(json))
                .andDo(print())
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message", is(notNullValue())));
    }

    @Test
    public void update_존재하지않는경우_404() throws Exception {
        when(toDoListService.update(any(ToDo.class))).thenThrow(new NotFoundException(1l));

        String json = ResourceFileReader.readFile("todo.json");
        URI uri = UriComponentsBuilder.fromPath("/todos/1")
                .build().toUri();

        mockMvc.perform(MockMvcRequestBuilders.put(uri)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(json))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is(notNullValue())));
    }

    @Test
    public void update_수정에_실패한경우_500() throws Exception {
        when(toDoListService.update(any(ToDo.class))).thenThrow(new RuntimeException("something unexpected happened"));

        String json = ResourceFileReader.readFile("todo.json");
        URI uri = UriComponentsBuilder.fromPath("/todos/1")
                .build().toUri();

        mockMvc.perform(MockMvcRequestBuilders.put(uri)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(json))
                .andDo(print())
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message", is(notNullValue())));
    }

    @Test
    public void patch() throws Exception {
        when(toDoListService.updateStatus(anyLong(), any(Status.class))).thenReturn(1);

        String json = ResourceFileReader.readFile("todo.json");
        URI uri = UriComponentsBuilder.fromPath("/todos/1")
                .build().toUri();

        mockMvc.perform(MockMvcRequestBuilders.patch(uri)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(json))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    public void patch_id가_잘못된경우_400() throws Exception {
        String json = ResourceFileReader.readFile("todo.json");
        URI uri = UriComponentsBuilder.fromPath("/todos/a")
                .build().toUri();

        mockMvc.perform(MockMvcRequestBuilders.patch(uri)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(json))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void patch_바디가_누락된경우_400() throws Exception {
        URI uri = UriComponentsBuilder.fromPath("/todos/1")
                .build().toUri();

        mockMvc.perform(MockMvcRequestBuilders.patch(uri))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void patch_제약조건을_어긴경우_400() throws Exception {
        when(toDoListService.updateStatus(anyLong(), any(Status.class))).thenThrow(new ConstraintViolationException(new HashSet<>()));
        String json = ResourceFileReader.readFile("todo.json");

        URI uri = UriComponentsBuilder.fromPath("/todos/1")
                .build().toUri();

        mockMvc.perform(MockMvcRequestBuilders.patch(uri)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(json))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void patch_닫을수없는_할일인경우_403() throws Exception {
        when(toDoListService.updateStatus(anyLong(), any(Status.class))).thenThrow(new NotClosableException());
        String json = ResourceFileReader.readFile("todo.json");
        URI uri = UriComponentsBuilder.fromPath("/todos/1")
                .build().toUri();

        mockMvc.perform(MockMvcRequestBuilders.patch(uri)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(json))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    public void patch_존재하지않는경우_405() throws Exception {
        when(toDoListService.updateStatus(anyLong(), any(Status.class))).thenThrow(new NotFoundException(1l));
        String json = ResourceFileReader.readFile("todo.json");
        URI uri = UriComponentsBuilder.fromPath("/todos/1")
                .build().toUri();

        mockMvc.perform(MockMvcRequestBuilders.patch(uri)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(json))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void patch_수정에_실패한경우_500() throws Exception {
        when(toDoListService.updateStatus(anyLong(), any(Status.class))).thenThrow(new RuntimeException("something unexpected happened"));

        String json = ResourceFileReader.readFile("todo.json");
        URI uri = UriComponentsBuilder.fromPath("/todos/1")
                .build().toUri();

        mockMvc.perform(MockMvcRequestBuilders.patch(uri)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(json))
                .andDo(print())
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message", is(notNullValue())));
    }

    @Test
    public void delete() throws Exception {
        when(toDoListService.delete(anyLong())).thenReturn(1);
        URI uri = UriComponentsBuilder.fromPath("/todos/1")
                .build().toUri();

        mockMvc.perform(MockMvcRequestBuilders.delete(uri))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    public void delete_id가_잘못된경우_400() throws Exception {
        URI uri = UriComponentsBuilder.fromPath("/todos/a")
                .build().toUri();

        mockMvc.perform(MockMvcRequestBuilders.delete(uri))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void delete_존재하지않는_할일일경우_404() throws Exception {
        when(toDoListService.delete(anyLong())).thenThrow(new NotFoundException(1l));
        URI uri = UriComponentsBuilder.fromPath("/todos/1")
                .build().toUri();

        mockMvc.perform(MockMvcRequestBuilders.delete(uri))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is(notNullValue())));
    }

    @Test
    public void delete_삭제에_실패한경우_500() throws Exception {
        when(toDoListService.delete(anyLong())).thenThrow(new RuntimeException("something unexpected happened"));

        URI uri = UriComponentsBuilder.fromPath("/todos/1")
                .build().toUri();

        mockMvc.perform(MockMvcRequestBuilders.delete(uri))
                .andDo(print())
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message", is(notNullValue())));
    }

    @Test
    public void listAll() throws Exception {
        List<ToDo> mockToDoList = MockToDoFactory.getMockToDoList();
        when(toDoListService.list(anyInt(), anyInt())).thenReturn(new PageImpl<>(mockToDoList));
        URI uri = UriComponentsBuilder.fromPath("/todos")
                .build().toUri();

        mockMvc.perform(MockMvcRequestBuilders.get(uri))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(notNullValue())))
                .andExpect(jsonPath("$[0].description", is(notNullValue())));
    }

    @Test
    public void list_데이터가_없는경우() throws Exception {
        ArrayList<ToDo> emptyList = new ArrayList<>();
        when(toDoListService.list(anyInt(), anyInt())).thenReturn(new PageImpl<>(emptyList));
        URI uri = UriComponentsBuilder.fromPath("/todos")
                .build().toUri();

        mockMvc.perform(MockMvcRequestBuilders.get(uri))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    public void list_파라미터가_잘못된경우_400() throws Exception {
        URI uri = UriComponentsBuilder.fromPath("/todos")
                .queryParam("currentPage", "a")
                .build().toUri();

        mockMvc.perform(MockMvcRequestBuilders.get(uri))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void list_조회에_실패한경우_500() throws Exception {
        when(toDoListService.list(anyInt(), anyInt())).thenThrow(new RuntimeException("something unexpected happened"));

        URI uri = UriComponentsBuilder.fromPath("/todos")
                .build().toUri();

        mockMvc.perform(MockMvcRequestBuilders.get(uri))
                .andDo(print())
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message", is(notNullValue())));
    }

}
