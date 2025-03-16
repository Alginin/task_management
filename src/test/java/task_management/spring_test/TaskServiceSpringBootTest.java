package task_management.spring_test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import task_management.entity.Task;
import task_management.entity.TaskStatus;
import task_management.repository.TaskRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import task_management.service.TaskService;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class TaskServiceSpringBootTest extends PostgresContainer {

    @Autowired
    private TaskService taskService;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    private Task task;

    @BeforeEach
    public void setUp() {

        taskRepository.deleteAll();

        task = Task.builder()
                .title("Test Task")
                .description("Test Description")
                .status(TaskStatus.NEW)
                .userId(1L)
                .build();
    }

    @Test
    @DisplayName("Проверка соединения с Kafka")
    public void testKafkaConnection() {

        System.out.println("Kafka bootstrap servers: " + kafka.getBootstrapServers());
    }

    @Test
    @DisplayName("Создание задачи")
    void createTask() throws Exception {

        String taskJson = objectMapper.writeValueAsString(task);

        ResultActions response = mockMvc.perform(post("/api/v1/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(taskJson));

        response.andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value(task.getTitle()));
    }

    @Test
    @DisplayName("Попытка создания задачи с пустым обязательным полем")
    void createTaskWithEmptyTitle() throws Exception {

        task.setTitle("");
        String taskJson = objectMapper.writeValueAsString(task);

        mockMvc.perform(post("/api/v1/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(taskJson))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Получение задачи по ID")
    void getTaskById() throws Exception {

        Task savedTask = taskRepository.save(task);

        mockMvc.perform(get("/api/v1/tasks/" + savedTask.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedTask.getId().toString()));
    }

    @Test
    @DisplayName("Попытка получения задачи для несуществующего ID задачи")
    void getNonExistingTask() throws Exception {

        mockMvc.perform(get("/api/v1/tasks/" + UUID.randomUUID()))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Получение списка всех задач")
    void getAllTasks() throws Exception {

        taskRepository.save(task);

        mockMvc.perform(get("/api/v1/tasks/all-tasks"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Обновление задачи")
    void updateTask() throws Exception {

        Task savedTask = taskRepository.save(task);
        savedTask.setTitle("Updated Title");

        String updatedTaskJson = objectMapper.writeValueAsString(savedTask);

        mockMvc.perform(put("/api/v1/tasks/" + savedTask.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedTaskJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Title"));
    }

    @Test
    @DisplayName("Обновление несуществующей задачи")
    void updateNonExistingTask() throws Exception {

        mockMvc.perform(put("/api/v1/tasks/" + UUID.randomUUID()))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Удаление задачи")
    public void deleteTask() throws Exception {

        Task createdTask = taskService.createTask(task);

        mockMvc.perform(delete("/api/v1/tasks/{id}", createdTask.getId()))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/tasks/{id}", createdTask.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Удаление не существующей задачи")
    void deleteNonExistingTask() throws Exception {

        mockMvc.perform(delete("/api/v1/tasks/{id}", UUID.randomUUID()))
                .andExpect(status().isNotFound());
    }

}