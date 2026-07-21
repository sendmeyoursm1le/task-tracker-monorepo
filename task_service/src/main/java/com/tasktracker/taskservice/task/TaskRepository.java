package com.tasktracker.taskservice.task;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByCreatorIdOrAssigneeIdOrderByIdAsc(Long creatorId, Long assigneeId);
}
