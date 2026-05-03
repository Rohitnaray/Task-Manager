package com.taskmanager.repository;

import com.taskmanager.entity.Task;
import com.taskmanager.entity.User;
import com.taskmanager.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.time.LocalDate;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByProject(Project project);
    List<Task> findByAssignee(User assignee);

    @Query("SELECT t FROM Task t WHERE t.assignee = :user AND t.dueDate < :today AND t.status <> 'DONE'")
    List<Task> findOverdueByUser(User user, LocalDate today);

    @Query("SELECT t FROM Task t WHERE t.project.id = :projectId AND t.dueDate < :today AND t.status <> 'DONE'")
    List<Task> findOverdueByProject(Long projectId, LocalDate today);

    @Query("SELECT t FROM Task t WHERE t.assignee = :user OR t.project IN (SELECT p FROM Project p WHERE p.owner = :user OR :user MEMBER OF p.members)")
    List<Task> findAllVisibleToUser(User user);
}
