package com.tasktracker.taskservice.task;

public class AssigneeNotFoundException extends RuntimeException {

    public AssigneeNotFoundException(Long assigneeId) {
        super("Assignee with id " + assigneeId + " was not found");
    }
}
