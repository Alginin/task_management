package task_management.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import task_management.dto.TaskDto;
import task_management.entity.Task;

import task_management.mapper.TaskMapper;
import task_management.service.TaskService;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/tasks")
@RequiredArgsConstructor
public class TaskController {
    private final TaskMapper taskMapper;
    private final TaskService taskService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TaskDto createTask(@Valid @RequestBody TaskDto taskDto) {
        Task task = taskMapper.toTaskEntity(taskDto);
        Task createdTask = taskService.createTask(task);

        return taskMapper.toTaskDto(createdTask);
    }

    @GetMapping("/{id}")
    public TaskDto getTaskById(@PathVariable UUID id) {
        Task task = taskService.getTaskById(id);

        return taskMapper.toTaskDto(task);
    }

    @GetMapping("/all-tasks")
    public List<TaskDto> getAllTasksByUserId() {
        List<Task> tasks = taskService.findAllTasksById();

        return taskMapper.toTaskDtoList(tasks);
    }

    @PutMapping("/{id}")
    public TaskDto updateTask(@Valid @RequestBody TaskDto taskDto, @PathVariable UUID id) {
        Task task = taskMapper.toTaskEntity(taskDto);
        Task updateTask = taskService.updateTask(task, id);

        return taskMapper.toTaskDto(updateTask);
    }

    @DeleteMapping("/{id}")
    public TaskDto deleteTask(@PathVariable UUID id) {
        Task deleteTask = taskService.deleteTask(id);

        return taskMapper.toTaskDto(deleteTask);
    }
}
