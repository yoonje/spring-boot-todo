package com.yoonje;

import com.yoonje.model.ToDo;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MockToDoFactory {

    private static ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @SneakyThrows
    public static ToDo getMockToDo() {
        String json = ResourceFileReader.readFile("todo.json");
        ToDo toDo = OBJECT_MAPPER.readValue(json, ToDo.class);
        toDo.getReferences().forEach(ref -> ref.setToDo(toDo));
        return toDo;
    }

    @SneakyThrows
    public static List<ToDo> getMockToDoList() {
        String json = ResourceFileReader.readFile("todo-list.json");
        List<ToDo> toDos = OBJECT_MAPPER.readValue(json, new TypeReference<List<ToDo>>() {
        });
        toDos.forEach(toDo -> {
            toDo.getReferences().forEach(ref -> ref.setToDo(toDo));
        });
        return toDos;
    }

}
