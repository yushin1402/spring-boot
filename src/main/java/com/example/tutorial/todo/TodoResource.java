package com.example.tutorial.todo;

import java.io.Serializable;
import java.time.LocalDateTime;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TodoResource implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long todoId;

    @NotEmpty
    @Size(max = 30)
    private String todoTitle;

    private boolean finished;

    @JsonFormat(pattern = "uuuu/MM/dd HH:mm:ss")
    private LocalDateTime createdAt;

}