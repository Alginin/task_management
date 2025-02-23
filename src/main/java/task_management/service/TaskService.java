package task_management.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import task_management.entity.Task;
import task_management.repository.TaskRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;

    @Transactional
    public Task createTask(Task task) {
        if (task.getTitle().isBlank()){
            throw new IllegalArgumentException("Название задачи не может быть пустым");
        }
        Task createdTaskService = taskRepository.save(task);

        return createdTaskService;
    }

    @Transactional(readOnly = true)
    public Task getTaskById(UUID id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Задача с ID " + id + " не найдена"));
    }

    @Transactional(readOnly = true)
    public List<Task> findAllTasksById() {
        List<Task> tasks = taskRepository.findAll();

        return tasks;
    }

    @Transactional
    public Task updateTask(Task task, UUID id) {
        Task existingTask = taskRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Задача с ID " + id + " не найдена"));

        existingTask.setTitle(task.getTitle());
        existingTask.setDescription(task.getDescription());
        existingTask.setUserId(task.getUserId());

        return taskRepository.save(existingTask);
    }

    @Transactional
    public Task deleteTask(UUID id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Задача с ID " + id + " не найдена"));
        taskRepository.delete(task);

        return task;
    }
}
