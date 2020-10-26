package com.yoonje.controller;

import com.yoonje.controller.model.CreateResponse;
import com.yoonje.controller.model.ErrorResponse;
import com.yoonje.model.ToDo;
import com.yoonje.service.ToDoDetailViewUrlFactory;
import com.yoonje.service.ToDoListService;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class ToDoListController {

    @Autowired
    private ToDoDetailViewUrlFactory toDoDetailViewUrlFactory;
    @Autowired
    private ToDoListService toDoListService;

    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Created", response = CreateResponse.class),
            @ApiResponse(code = 400, message = "Bad Request", response = ErrorResponse.class),
            @ApiResponse(code = 500, message = "Internal Server Error", response = ErrorResponse.class)
    })
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/todos")
    public ResponseEntity<CreateResponse> create(@RequestBody ToDo todo) {
        Long id = this.toDoListService.create(todo);
        URI uri = this.toDoDetailViewUrlFactory.get(id);
        return ResponseEntity.created(uri).body(new CreateResponse(id));
    }

    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Ok", response = ToDo.class),
            @ApiResponse(code = 400, message = "Bad Request", response = ErrorResponse.class),
            @ApiResponse(code = 404, message = "Not Found", response = ErrorResponse.class),
            @ApiResponse(code = 500, message = "Internal Server Error", response = ErrorResponse.class)
    })
    @GetMapping("/todos/{id}")
    public ToDo read(@PathVariable Long id) {
        return this.toDoListService.read(id);
    }

    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Ok", response = ToDo.class),
            @ApiResponse(code = 400, message = "Bad Request", response = ErrorResponse.class),
            @ApiResponse(code = 403, message = "Forbidden", response = ErrorResponse.class),
            @ApiResponse(code = 404, message = "Not Found", response = ErrorResponse.class),
            @ApiResponse(code = 500, message = "Internal Server Error", response = ErrorResponse.class)
    })
    @PutMapping("/todos/{id}")
    public ResponseEntity<Void> update(@PathVariable Long id, @RequestBody ToDo todo) {
        todo.setId(id);
        this.toDoListService.update(todo);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Ok", response = ToDo.class),
            @ApiResponse(code = 400, message = "Bad Request", response = ErrorResponse.class),
            @ApiResponse(code = 403, message = "Forbidden", response = ErrorResponse.class),
            @ApiResponse(code = 404, message = "Not Found", response = ErrorResponse.class),
            @ApiResponse(code = 500, message = "Internal Server Error", response = ErrorResponse.class)
    })
    @PatchMapping("/todos/{id}")
    public ResponseEntity<Void> patch(@PathVariable Long id, @RequestBody ToDo todo) {
        this.toDoListService.updateStatus(id, todo.getStatus());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Ok", response = ToDo.class),
            @ApiResponse(code = 400, message = "Bad Request", response = ErrorResponse.class),
            @ApiResponse(code = 404, message = "Not Found", response = ErrorResponse.class),
            @ApiResponse(code = 500, message = "Internal Server Error", response = ErrorResponse.class)
    })
    @DeleteMapping("/todos/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        this.toDoListService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Ok", response = ToDo.class),
            @ApiResponse(code = 400, message = "Bad Request", response = ErrorResponse.class),
            @ApiResponse(code = 500, message = "Internal Server Error", response = ErrorResponse.class)
    })
    @GetMapping(value = "/todos")
    public List<ToDo> list(@RequestParam(defaultValue = "1") int currentPage,
                           @RequestParam(defaultValue = "10") int display) {
        return this.toDoListService.list(currentPage, display).getContent();
    }

}
