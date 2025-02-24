package task_management.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskDto {

    private UUID id;

    @NotBlank(message = "Заголовок задачи не может быть пустым")
    @Size(max = 64, message = "Заголовок задачи не может быть длиннее 64 символов")
    private String title;

    @Size(max = 1024, message = "Описание задачи не может быть длиннее 1024 символов")
    private String description;

    private Long userId;
}
