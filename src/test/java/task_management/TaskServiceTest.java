package task_management;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import task_management.dto.KafkaDto;
import task_management.entity.Task;
import task_management.mapper.TaskMapper;
import task_management.producer.KafkaTaskProducer;
import task_management.repository.TaskRepository;
import task_management.service.TaskService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private TaskMapper taskMapper;

    @Mock
    private KafkaTaskProducer kafkaProducer;

    @InjectMocks
    private TaskService taskService;

    private Task task;
    private UUID taskId;

    @BeforeEach
    void setUp() {
        taskId = UUID.randomUUID();
        task = new Task();
        task.setId(taskId);
        task.setTitle("Test Task");
        task.setDescription("Test Description");
    }

    @Test
    @DisplayName("Создание задачи")
    void createTask_ValidTask_ReturnsSavedTask() {
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        Task result = taskService.createTask(task);

        ArgumentCaptor<Task> taskCaptor = ArgumentCaptor.forClass(Task.class);
        verify(taskRepository, times(1)).save(taskCaptor.capture());

        assertNotNull(result);
        assertEquals(task.getTitle(), result.getTitle());
        assertEquals(task.getDescription(), result.getDescription());
        assertEquals(task.getId(), result.getId());
        assertEquals(task, taskCaptor.getValue());
    }

    @Test
    @DisplayName("Создание задачи с пустым названием")
    void createTask_BlankTitle_ThrowsException() {

        task.setTitle("");

        Exception exception = assertThrows(IllegalArgumentException.class, () -> taskService.createTask(task));
        assertEquals("Название задачи не может быть пустым", exception.getMessage());
    }

    @Test
    @DisplayName("Получение задачи по ID")
    void getTaskById_ExistingId_ReturnsTask() {

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));

        Task result = taskService.getTaskById(taskId);

        assertNotNull(result);
        assertEquals(taskId, result.getId());
    }

    @Test
    @DisplayName("Получение задачи по несуществующему ID")
    void getTaskById_NonExistingId_ThrowsException() {

        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> taskService.getTaskById(taskId));
    }

    @Test
    @DisplayName("Получение всех задач")
    void findAllTasks_ReturnsTaskList() {

        when(taskRepository.findAll()).thenReturn(List.of(task));

        List<Task> result = taskService.findAllTasksById();

        assertEquals(1, result.size());
        verify(taskRepository).findAll();
    }

    @Test
    @DisplayName("Обновление задачи")
    void updateTask_ExistingTask_UpdatesAndSendsKafkaMessage() {

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(taskRepository.save(any())).thenReturn(task);
        when(taskMapper.toKafkaDto(any())).thenReturn(new KafkaDto());

        Task updatedTask = new Task();
        updatedTask.setTitle("Updated Title");
        updatedTask.setDescription("Updated Description");

        Task result = taskService.updateTask(updatedTask, taskId);

        assertEquals("Updated Title", result.getTitle());

        ArgumentCaptor<KafkaDto> kafkaDtoCaptor = ArgumentCaptor.forClass(KafkaDto.class);
        verify(kafkaProducer).sendTo(kafkaDtoCaptor.capture());

        KafkaDto sentKafkaDto = kafkaDtoCaptor.getValue();
        assertNotNull(sentKafkaDto);
    }

    @Test
    @DisplayName("Обновление задачи с несуществующим ID")
    void updateTask_NonExistingId_ThrowsException() {

        task.setTitle("Updated Title");
        task.setDescription("Updated Description");

        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> taskService.updateTask(task, taskId));

        assertEquals("Задача с ID " + taskId + " не найдена", exception.getMessage());

        verify(taskRepository).findById(taskId);

        verify(taskRepository, never()).save(any(Task.class));

        verify(kafkaProducer, never()).sendTo(any(KafkaDto.class));
    }

    @Test
    @DisplayName("Удаление задачи")
    void deleteTask_ExistingId_DeletesTask() {

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));

        Task deletedTask = taskService.deleteTask(taskId);

        assertEquals(taskId, deletedTask.getId());
        verify(taskRepository).delete(task);
    }

    @Test
    @DisplayName("Удаление задачи с несуществующим ID")
    void deleteTask_NonExistingId_ThrowsException() {

        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> taskService.deleteTask(taskId));

        assertEquals("Задача с ID " + taskId + " не найдена", exception.getMessage());

        verify(taskRepository).findById(taskId);
    }

}