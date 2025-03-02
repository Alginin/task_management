package task_management.service;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import task_management.dto.KafkaDto;
import task_management.entity.Task;
import task_management.mapper.TaskMapper;
import task_management.producer.KafkaTaskProducer;
import task_management.repository.TaskRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Builder
public class TaskService {

    private final TaskRepository taskRepository;
    private final TaskMapper mapper;
    private final TaskMapper taskMapper;

    @Qualifier("kafkaTaskProducer")
    private final KafkaTaskProducer kafkaProducer;

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
        Optional<Task> existingTask = Optional.ofNullable(taskRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Задача с ID " + id + " не найдена")));

        Task updatedTask = existingTask.get();
        updatedTask.setTitle(task.getTitle());
        updatedTask.setDescription(task.getDescription());
        updatedTask.setUserId(task.getUserId());
        updatedTask.setStatus(task.getStatus());

        KafkaDto forKafka = taskMapper.toKafkaDto(existingTask.get());
        kafkaProducer.sendTo(forKafka);

        return taskRepository.save(updatedTask);
    }

    @Transactional
    public Task deleteTask(UUID id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Задача с ID " + id + " не найдена"));
        taskRepository.delete(task);

        return task;
    }

}
