package com.tasktracker.taskservice.task;

public class UserServiceUnavailableException extends RuntimeException {

    public UserServiceUnavailableException(Long assigneeId) {
        super("User service is unavailable while checking assignee with id " + assigneeId);
    }
}
