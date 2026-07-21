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
}
