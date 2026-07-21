package com.tasktracker.taskservice.task;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "tasks")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TaskStatus status;

    @Column(name = "creator_id", nullable = false)
    private Long creatorId;

    @Column(name = "assignee_id")
    private Long assigneeId;

    protected Task() {
    }

    public Task(String title, TaskStatus status, Long creatorId, Long assigneeId) {
        this.title = title;
        this.status = status;
        this.creatorId = creatorId;
        this.assigneeId = assigneeId;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public Long getCreatorId() {
        return creatorId;
    }

    public Long getAssigneeId() {
        return assigneeId;
    }
}
