package com.tasktracker.taskservice.task;

public record TaskResponse(
        Long id,
        String title,
        TaskStatus status,
        Long creatorId,
        Long assigneeId
) {
    public static TaskResponse from(Task task) {
        return new TaskResponse(
                task.getId(),
                task.getTitle(),
                task.getStatus(),
                task.getCreatorId(),
                task.getAssigneeId()
        );
    }
}
