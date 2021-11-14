package com.example.tutorial.todo;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class Todo implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long todoId;

    private String todoTitle;

    private boolean finished;

    private LocalDateTime createdAt;
}
