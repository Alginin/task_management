package task_management.mapper;

import org.springframework.stereotype.Component;
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
                .build();
    }

    public Task toTaskEntity(TaskDto taskDto) {
        return Task.builder()
                .id(taskDto.getId())
                .title(taskDto.getTitle())
                .description(taskDto.getDescription())
                .userId(taskDto.getUserId())
                .build();
    }

    public List<TaskDto> toTaskDtoList(List<Task> tasks) {
        return tasks.stream()
                .map(this::toTaskDto)
                .toList();
    }
}
