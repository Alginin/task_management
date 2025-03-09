package task_management.mapper;

import org.springframework.stereotype.Component;
import task_management.dto.KafkaDto;
import task_management.dto.TaskDto;
import task_management.entity.Task;

import java.util.List;

@Component
public class TaskMapper {

    public TaskDto toTaskDto(Task task) {
        return TaskDto.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .userId(task.getUserId())
                .status(task.getStatus())
                .build();
    }

    public Task toTaskEntity(TaskDto taskDto) {
        return Task.builder()
                .id(taskDto.getId())
                .title(taskDto.getTitle())
                .description(taskDto.getDescription())
                .userId(taskDto.getUserId())
                .status(taskDto.getStatus())
                .build();
    }

    public List<TaskDto> toTaskDtoList(List<Task> tasks) {
        return tasks.stream()
                .map(this::toTaskDto)
                .toList();
    }

    public KafkaDto toKafkaDto(Task updatedTask) {
        return KafkaDto.builder()
                .id(updatedTask.getId())
                .status(updatedTask.getStatus())
                .build();
    }
}
