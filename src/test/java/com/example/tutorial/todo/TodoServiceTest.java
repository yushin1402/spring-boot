package com.example.tutorial.todo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.times;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest(classes = TodoServiceImpl.class) // (1)
public class TodoServiceTest {

    private static final DateTimeFormatter DATETIME_FORMAT = DateTimeFormatter.ofPattern("uuuu/MM/dd HH:mm:ss");

    @Autowired
    private TodoService todoService; // (2)

    @MockBean
    private TodoRepository todoRepository; // (3)

    @Test
    @DisplayName("全Todoが取得できることを確認する(service)")
    void testFindAll() {
        // setup
        Todo expectTodo1 = new Todo(1L, "sample todo 1", false, LocalDateTime.parse("2019/09/19 01:01:01", DATETIME_FORMAT));
        Todo expectTodo2 = new Todo(2L, "sample todo 2", true, LocalDateTime.parse("2019/09/19 02:02:02", DATETIME_FORMAT));
        Todo expectTodo3 = new Todo(3L, "sample todo 3", false, LocalDateTime.parse("2019/09/19 03:03:03", DATETIME_FORMAT));

        // setup mocks
        given(todoRepository.findAll()).willReturn(Arrays.asList(expectTodo1, expectTodo2, expectTodo3));

        // run
        Collection<Todo> actualTodos = todoService.findAll();

        // check
        then(todoRepository).should(times(1)).findAll();
        assertThat(actualTodos).usingFieldByFieldElementComparator().containsExactly(expectTodo1, expectTodo2, expectTodo3);
    }

    @Test
    @DisplayName("todoIdに対応するTodoが取得できることを確認する(Service)")
    void testFindOne() {
        // setup
        Todo expectTodo = new Todo(1L, "sample todo 1", false, LocalDateTime.parse("2019/09/19 01:01:01", DATETIME_FORMAT));

        // setup mocks
        given(todoRepository.findById(1L)).willReturn(Optional.of(expectTodo));

        // run
        Todo actualTodo = todoService.findOne(1L);

        // check
        then(todoRepository).should(times(1)).findById(ArgumentMatchers.longThat(arg -> arg == actualTodo.getTodoId()));
        assertThat(actualTodo).isEqualToComparingFieldByField(expectTodo);
    }

    @Test
    @DisplayName("新たなTodoが作成できることを確認する(service)")
    void testCreate() {
        // setup
        Todo expectTodo = new Todo(null, "sample todo 4", false, null);

        // setup mocks
        willDoNothing().given(todoRepository).create(expectTodo);

        // run
        todoService.create(expectTodo);

        // check
        then(todoRepository).should(times(1)).create(
                ArgumentMatchers.<Todo>argThat(
                        arg -> expectTodo.getTodoTitle().equals(arg.getTodoTitle())
                        && !arg.isFinished()
                        && Objects.nonNull(arg.getCreatedAt())
                        )
                );
    }

    @Test
    @DisplayName("todoId=1のfinishedがtrueになることを確認する(service)")
    void testFinish() {
        // setup
        Todo expectTodo = new Todo(1L, "sample todo 1", false, LocalDateTime.parse("2019/09/19 01:01:01", DATETIME_FORMAT));

        // setup mocks
        given(todoRepository.findById(1L)).willReturn(Optional.of(expectTodo));
        given(todoRepository.updateById(1L)).willReturn(1L);

        // run
        todoService.finish(1L);

        // check
        then(todoRepository).should(times(1)).findById(ArgumentMatchers.longThat(arg -> arg == expectTodo.getTodoId()));
        then(todoRepository).should(times(1)).updateById(ArgumentMatchers.longThat(arg -> arg == 1L));
    }

    @Test
    @DisplayName("todoId=1がDeleteによって削除されることを確認する(service)")
    void testDelete() {
        // setup
        Todo expectTodo = new Todo(1L, "sample todo 1", false, LocalDateTime.parse("2019/09/19 01:01:01", DATETIME_FORMAT));

        // setup mocks
        given(todoRepository.findById(1L)).willReturn(Optional.of(expectTodo));
        given(todoRepository.deleteById(1L)).willReturn(1L);

        // run
        todoService.delete(1L);

        // check
        then(todoRepository).should(times(1)).findById(ArgumentMatchers.longThat(arg -> arg == expectTodo.getTodoId()));
        then(todoRepository).should(times(1)).deleteById(ArgumentMatchers.longThat(arg -> arg == 1L));
    }
}