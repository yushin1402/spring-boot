package com.example.tutorial.todo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.EmptySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

@MybatisTest // (1)
public class TodoRepositoryTest {

    private static final DateTimeFormatter DATETIME_FORMAT = DateTimeFormatter.ofPattern("uuuu/MM/dd HH:mm:ss");

    @Autowired
    private TodoRepository todoRepository; // (2)

    @Autowired
    NamedParameterJdbcOperations jdbcOperations; // (3)

    @Test
    @DisplayName("全Todoが取得できることを確認する(Repository)")
    void testFindAll() {
        // run
        Collection<Todo> actualTodos = todoRepository.findAll();

        // check
        assertThat(actualTodos)
            .extracting(Todo::getTodoId, Todo::getTodoTitle, Todo::isFinished, Todo::getCreatedAt)
            .contains(tuple(1L, "sample todo 1", false, LocalDateTime.parse("2019/09/19 01:01:01", DATETIME_FORMAT)),
                    tuple(2L, "sample todo 2", true, LocalDateTime.parse("2019/09/19 02:02:02", DATETIME_FORMAT)),
                    tuple(3L, "sample todo 3", false, LocalDateTime.parse("2019/09/19 03:03:03", DATETIME_FORMAT)));
    }

    @Test
    @DisplayName("todoIdに対応するTodoが取得できることを確認する(Repository)")
    void testFindById() {
        // run
        Todo actualTodo = todoRepository.findById(1L).get();

        // check
        assertThat(actualTodo)
            .extracting(Todo::getTodoId, Todo::getTodoTitle, Todo::isFinished, Todo::getCreatedAt)
            .contains(1L, "sample todo 1", false, LocalDateTime.parse("2019/09/19 01:01:01", DATETIME_FORMAT));
    }

    @Test
    @DisplayName("新たなTodoが作成できることを確認する(Repository)")
    void testCreate() {
        // setup
        Todo actualTodo = new Todo(null, "sample todo 4", false, LocalDateTime.parse("2019/09/19 04:04:04", DATETIME_FORMAT));

        // run
        todoRepository.create(actualTodo);

        // check
        Todo todo = getLastTodo();
        assertThat(actualTodo)
            .isEqualToIgnoringGivenFields(todo, "todoId")
            .hasNoNullFieldsOrProperties();
    }

    @Test
    @DisplayName("finishedをfalseからtrueに変更できることを確認する(Repository)")
    void testUpdateById() {
        // setup
        Todo todo = getTodo(1L);

        // run
        long count = todoRepository.updateById(1L);
        Todo updated = getTodo(1L);

        // check
        assertThat(count).isEqualTo(1L);
        assertThat(updated)
            .isEqualToIgnoringGivenFields(todo, "finished")
            .hasFieldOrPropertyWithValue("finished", true);
    }

    @Test
    @DisplayName("todoId=1が削除できていることを確認する(Repository)")
    void testDeleteById() {
        // run
        long count = todoRepository.deleteById(1L);

        // check
        assertThat(count).isEqualTo(1);
    }

    @Test
    @DisplayName("未完了 or 完了済のTodoの件数を取得できることを確認する(Repository)")
    void testCountByFinished() {
        // run
        long unfinishedCount = todoRepository.countByFinished(false);
        long finishedCount = todoRepository.countByFinished(true);

        // check
        assertThat(unfinishedCount).isEqualTo(2);
        assertThat(finishedCount).isEqualTo(1);
    }

    private Todo getLastTodo() {
        String sql = "SELECT * FROM todo ORDER BY todo_id DESC LIMIT 1";
        SqlParameterSource paramSource = new EmptySqlParameterSource();
        RowMapper<Todo> rowMapper = new BeanPropertyRowMapper<>(Todo.class);
        return jdbcOperations.queryForObject(sql, paramSource, rowMapper);
    }

    private Todo getTodo(Long todoId) {
        String sql = "SELECT * FROM todo WHERE todo_id=:todoId";
        SqlParameterSource paramSource = new MapSqlParameterSource().addValue("todoId", todoId);
        RowMapper<Todo> rowMapper = new BeanPropertyRowMapper<>(Todo.class);
        return jdbcOperations.queryForObject(sql, paramSource, rowMapper);
    }

}