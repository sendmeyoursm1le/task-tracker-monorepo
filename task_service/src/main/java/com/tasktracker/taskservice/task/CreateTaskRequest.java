package com.tasktracker.taskservice.task;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record CreateTaskRequest(
        @NotBlank(message = "Title is required")
        @Size(max = 255, message = "Title must contain at most 255 characters")
        String title,

        @NotNull(message = "Creator id is required")
        @Positive(message = "Creator id must be positive")
        Long creatorId,

        @Positive(message = "Assignee id must be positive")
        Long assigneeId
) {
}
