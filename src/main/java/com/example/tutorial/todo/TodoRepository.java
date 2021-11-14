package com.example.tutorial.todo;

import java.util.Collection;
import java.util.Optional;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper // (1)
public interface TodoRepository {

    @Select("SELECT todo_id, todo_title, finished, created_at FROM todo WHERE todo_id = #{todoId}") // (2)
    Optional<Todo> findById(Long todoId);

    @Select("SELECT todo_id, todo_title, finished, created_at FROM todo")
    Collection<Todo> findAll();

    @Insert("INSERT INTO todo(todo_title, finished, created_at) VALUES(#{todoTitle}, #{finished}, #{createdAt})")
    @Options(useGeneratedKeys = true, keyProperty = "todoId")
    void create(Todo todo);

    @Update("UPDATE todo SET finished = true WHERE todo_id = #{todoId}")
    long updateById(Long todoId);

    @Delete("DELETE FROM todo WHERE todo_id = #{todoId}")
    long deleteById(Long todoId);

    @Select("SELECT COUNT(*) FROM todo WHERE finished = #{finished}")
    long countByFinished(boolean finished);
}