package com.tasktracker.taskservice.task;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TaskResponse create(@Valid @RequestBody CreateTaskRequest request) {
        return taskService.create(request);
    }

    @GetMapping
    public List<TaskResponse> getByUserId(
            @RequestParam @Positive(message = "User id must be positive") Long userId
    ) {
        return taskService.getByUserId(userId);
    }

    @PostMapping("/{taskId}/delegate")
    public TaskResponse delegate(
            @PathVariable @Positive(message = "Task id must be positive") Long taskId,
            @RequestParam @Positive(message = "Assignee id must be positive") Long assigneeId
    ) {
        return taskService.delegate(taskId, assigneeId);
    }
}
