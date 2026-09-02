package com.tasktracker.taskservice.task;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserServiceClient userServiceClient;

    public TaskService(TaskRepository taskRepository, UserServiceClient userServiceClient) {
        this.taskRepository = taskRepository;
        this.userServiceClient = userServiceClient;
    }

    @Transactional
    public TaskResponse create(CreateTaskRequest request) {
        Task task = new Task(
                request.title().trim(),
                TaskStatus.TODO,
                request.creatorId(),
                request.assigneeId()
        );

        return TaskResponse.from(taskRepository.save(task));
    }

    @Transactional(readOnly = true)
    public List<TaskResponse> getByUserId(Long userId) {
        return taskRepository.findByCreatorIdOrAssigneeIdOrderByIdAsc(userId, userId)
                .stream()
                .map(TaskResponse::from)
                .toList();
    }

    @Transactional
    public TaskResponse delegate(Long taskId, Long assigneeId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException(taskId));

        userServiceClient.verifyUserExists(assigneeId);
        task.delegateTo(assigneeId);

        return TaskResponse.from(task);
    }
}
