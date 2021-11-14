package com.example.tutorial.todo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;
import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collection;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import com.github.dozermapper.core.Mapper;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT) // (1)
public class TodoControllerTest {

    private static final DateTimeFormatter DATETIME_FORMAT = DateTimeFormatter.ofPattern("uuuu/MM/dd HH:mm:ss");

    @Autowired // (2)
    private TestRestTemplate testRestTemplate;

    @Autowired
    private Mapper beanMapper;

    @MockBean // (3)
    private TodoService todoService;

    @Test
    @DisplayName("GET Todosが正常に動作することを確認する(Controller)")
    void testGetTodos() {
        // setup
        Todo expectTodo1 = new Todo(1L, "sample todo 1", false, LocalDateTime.parse("2019/09/19 01:01:01", DATETIME_FORMAT));
        Todo expectTodo2 = new Todo(2L, "sample todo 2", true, LocalDateTime.parse("2019/09/19 02:02:02", DATETIME_FORMAT));
        Collection<Todo> expectTodos = Arrays.asList(expectTodo1, expectTodo2);
        TodoResource[] expectTodoResources = expectTodos.stream()
                .map(todo -> beanMapper.map(todo, TodoResource.class)).toArray(TodoResource[]::new);

        // setup mocks
        given(todoService.findAll()).willReturn(expectTodos);

        // run
        ResponseEntity<TodoResource[]> actualResponseEntity =
                testRestTemplate.getForEntity("/todos", TodoResource[].class);

        // check
        then(todoService).should(times(1)).findAll();
        assertThat(actualResponseEntity.getBody()).usingFieldByFieldElementComparator().containsExactly(expectTodoResources[0], expectTodoResources[1]);
        assertThat(actualResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("GET Todoが正常に動作することを確認する(Controller)")
    void testGetTodo(){
        // setup
        Todo expectTodo = new Todo(1L, "sample todo 1", false, LocalDateTime.parse("2019/09/19 01:01:01", DATETIME_FORMAT));
        TodoResource expectTodoResource = beanMapper.map(expectTodo, TodoResource.class);

        // setup mocks
        given(this.todoService.findOne(1L)).willReturn(expectTodo);

        // run
        ResponseEntity<TodoResource> actualResponseEntity =
                testRestTemplate.getForEntity("/todos/1", TodoResource.class);

        // check
        then(todoService).should(times(1)).findOne(ArgumentMatchers.longThat(arg -> arg == expectTodo.getTodoId()));
        assertThat(actualResponseEntity.getBody()).isEqualToComparingFieldByField(expectTodoResource);
        assertThat(actualResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("POST Todoが正常に動作することを確認する(Controller)")
    void testPostTodo() {
        // setup
        TodoResource inputTodoResource = new TodoResource();
        inputTodoResource.setTodoTitle("sample todo 4");
        Todo inputTodo = beanMapper.map(inputTodoResource, Todo.class);
        Todo expectTodo = new Todo(4L, "sample todo 4", false, LocalDateTime.parse("2019/09/19 04:04:04", DATETIME_FORMAT));
        TodoResource expectTodoResource = beanMapper.map(expectTodo, TodoResource.class);

        // setup mocks
        given(this.todoService.create(any(Todo.class))).willReturn(expectTodo);

        // run
        ResponseEntity<TodoResource> actualResponseEntity =
                testRestTemplate.postForEntity("/todos", inputTodoResource, TodoResource.class);

        // check
        then(todoService).should(times(1)).create(ArgumentMatchers.<Todo>argThat(arg -> inputTodo.getTodoTitle().equals(arg.getTodoTitle())));
        assertThat(actualResponseEntity.getBody()).isEqualToComparingFieldByField(expectTodoResource);
        assertThat(actualResponseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    @DisplayName("PUT Todoが正常に動作することを確認する(Controller)")
    void testPutTodo() {
        // setup
        Todo expectTodo = new Todo(1L, "sample todo 1", true, LocalDateTime.parse("2019/09/19 01:01:01", DATETIME_FORMAT));
        TodoResource expectTodoResource = beanMapper.map(expectTodo, TodoResource.class);

        // setup mocks
        given(this.todoService.finish(1L)).willReturn(expectTodo);

        // run
        RequestEntity<String> actualRequestEntity =
                RequestEntity.put(URI.create("/todos/1")).body("");
        ResponseEntity<TodoResource> actualResponseEntity =
                testRestTemplate.exchange(actualRequestEntity, TodoResource.class);

        // check
        then(todoService).should(times(1)).finish(ArgumentMatchers.longThat(arg -> arg == expectTodo.getTodoId()));
        assertThat(actualResponseEntity.getBody()).isEqualToComparingFieldByField(expectTodoResource);
        assertThat(actualResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("DELETE Todoが正常に動作することを確認する(Controller)")
    void testDeleteTodo() {
        // run
        ResponseEntity<String> actualResponseEntity =
                testRestTemplate.exchange("/todos/1", HttpMethod.DELETE, HttpEntity.EMPTY, String.class);

        // check
        then(todoService).should(times(1)).delete(ArgumentMatchers.longThat(arg -> arg == 1L));
        assertThat(actualResponseEntity.getBody()).isNull();
        assertThat(actualResponseEntity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

}