package com.tasktracker.taskservice.task;

import java.util.List;

import com.tasktracker.taskservice.error.GlobalExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TaskController.class)
@Import(GlobalExceptionHandler.class)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TaskService taskService;

    @Test
    void createReturnsCreatedTask() throws Exception {
        TaskResponse response = new TaskResponse(1L, "Prepare report", TaskStatus.TODO, 10L, null);
        when(taskService.create(any(CreateTaskRequest.class))).thenReturn(response);

        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "Prepare report",
                                  "creatorId": 10
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Prepare report"))
                .andExpect(jsonPath("$.status").value("TODO"))
                .andExpect(jsonPath("$.creatorId").value(10))
                .andExpect(jsonPath("$.assigneeId").doesNotExist());
    }

    @Test
    void getByUserIdReturnsRelatedTasks() throws Exception {
        List<TaskResponse> response = List.of(
                new TaskResponse(1L, "Created task", TaskStatus.TODO, 10L, null),
                new TaskResponse(2L, "Assigned task", TaskStatus.IN_PROGRESS, 20L, 10L)
        );
        when(taskService.getByUserId(10L)).thenReturn(response);

        mockMvc.perform(get("/tasks").param("userId", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].title").value("Created task"))
                .andExpect(jsonPath("$[1].title").value("Assigned task"));
    }

    @Test
    void delegateReturnsUpdatedTask() throws Exception {
        TaskResponse response = new TaskResponse(1L, "Prepare report", TaskStatus.TODO, 10L, 20L);
        when(taskService.delegate(1L, 20L)).thenReturn(response);

        mockMvc.perform(post("/tasks/1/delegate").param("assigneeId", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Prepare report"))
                .andExpect(jsonPath("$.assigneeId").value(20));
    }

    @Test
    void delegateReturnsNotFoundWhenTaskDoesNotExist() throws Exception {
        when(taskService.delegate(99L, 20L)).thenThrow(new TaskNotFoundException(99L));

        mockMvc.perform(post("/tasks/99/delegate").param("assigneeId", "20"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Task with id 99 was not found"));
    }

    @Test
    void delegateReturnsNotFoundWhenAssigneeDoesNotExist() throws Exception {
        when(taskService.delegate(1L, 404L)).thenThrow(new AssigneeNotFoundException(404L));

        mockMvc.perform(post("/tasks/1/delegate").param("assigneeId", "404"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Assignee with id 404 was not found"));
    }

    @Test
    void delegateReturnsServiceUnavailableWhenUserServiceFails() throws Exception {
        when(taskService.delegate(1L, 20L)).thenThrow(new UserServiceUnavailableException(20L));

        mockMvc.perform(post("/tasks/1/delegate").param("assigneeId", "20"))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.status").value(503))
                .andExpect(jsonPath("$.message")
                        .value("User service is unavailable while checking assignee with id 20"));
    }

    @Test
    void createReturnsBadRequestForInvalidInput() throws Exception {
        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "",
                                  "creatorId": 0,
                                  "assigneeId": -1
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.fieldErrors.title").value("Title is required"))
                .andExpect(jsonPath("$.fieldErrors.creatorId").value("Creator id must be positive"))
                .andExpect(jsonPath("$.fieldErrors.assigneeId").value("Assignee id must be positive"));
    }

    @Test
    void getByUserIdReturnsBadRequestForNonPositiveId() throws Exception {
        mockMvc.perform(get("/tasks").param("userId", "0"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Request validation failed"));
    }

    @Test
    void delegateReturnsBadRequestForNonPositiveAssigneeId() throws Exception {
        mockMvc.perform(post("/tasks/1/delegate").param("assigneeId", "0"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Request validation failed"));
    }
}
