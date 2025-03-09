package task_management.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import task_management.entity.Task;

import java.util.UUID;

@Repository
public interface TaskRepository extends JpaRepository<Task, UUID> {
}
