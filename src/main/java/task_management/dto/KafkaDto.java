package task_management.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import task_management.entity.TaskStatus;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class KafkaDto {

    private UUID id;

    @NotNull(message = "Статус задачи не может быть null")
    private TaskStatus status;
}
