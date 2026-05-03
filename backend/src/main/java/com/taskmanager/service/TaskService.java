package com.taskmanager.service;

import com.taskmanager.dto.Dtos.*;
import com.taskmanager.entity.*;
import com.taskmanager.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;

@Service
public class TaskService {

    @Autowired private TaskRepository taskRepository;
    @Autowired private ProjectRepository projectRepository;
    @Autowired private UserRepository userRepository;

    public Task createTask(Long projectId, TaskRequest req, User creator) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        boolean isOwner = project.getOwner().getId().equals(creator.getId());
        boolean isMember = project.getMembers().stream().anyMatch(m -> m.getId().equals(creator.getId()));
        if (!isOwner && !isMember && creator.getRole() != User.Role.ADMIN) {
            throw new RuntimeException("Not a project member");
        }

        User assignee = null;
        if (req.assigneeId != null) {
            assignee = userRepository.findById(req.assigneeId)
                    .orElseThrow(() -> new RuntimeException("Assignee not found"));
        }

        Task task = Task.builder()
                .title(req.title)
                .description(req.description)
                .priority(req.priority != null ? req.priority : Task.Priority.MEDIUM)
                .dueDate(req.dueDate)
                .project(project)
                .assignee(assignee)
                .createdBy(creator)
                .build();
        return taskRepository.save(task);
    }

    public List<Task> getTasksByProject(Long projectId, User user) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));
        return taskRepository.findByProject(project);
    }

    public Task updateTask(Long taskId, TaskRequest req, User user) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        boolean isOwner = task.getProject().getOwner().getId().equals(user.getId());
        boolean isAssignee = task.getAssignee() != null && task.getAssignee().getId().equals(user.getId());
        if (!isOwner && !isAssignee && user.getRole() != User.Role.ADMIN) {
            throw new RuntimeException("Access denied");
        }

        task.setTitle(req.title);
        task.setDescription(req.description);
        if (req.priority != null) task.setPriority(req.priority);
        if (req.dueDate != null) task.setDueDate(req.dueDate);
        if (req.assigneeId != null) {
            User assignee = userRepository.findById(req.assigneeId)
                    .orElseThrow(() -> new RuntimeException("Assignee not found"));
            task.setAssignee(assignee);
        }
        return taskRepository.save(task);
    }

    public Task updateStatus(Long taskId, Task.Status status, User user) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        task.setStatus(status);
        return taskRepository.save(task);
    }

    public void deleteTask(Long taskId, User user) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        boolean isOwner = task.getProject().getOwner().getId().equals(user.getId());
        if (!isOwner && user.getRole() != User.Role.ADMIN) {
            throw new RuntimeException("Only project owner or admin can delete tasks");
        }
        taskRepository.delete(task);
    }

    public List<Task> getMyTasks(User user) {
        return taskRepository.findByAssignee(user);
    }

    public DashboardStats getDashboardStats(User user) {
        List<Task> tasks = user.getRole() == User.Role.ADMIN
                ? taskRepository.findAll()
                : taskRepository.findAllVisibleToUser(user);
        List<Project> projects = user.getRole() == User.Role.ADMIN
                ? projectRepository.findAll()
                : projectRepository.findByMemberOrOwner(user);

        long todo = tasks.stream().filter(t -> t.getStatus() == Task.Status.TODO).count();
        long inProgress = tasks.stream().filter(t -> t.getStatus() == Task.Status.IN_PROGRESS).count();
        long done = tasks.stream().filter(t -> t.getStatus() == Task.Status.DONE).count();
        long overdue = tasks.stream().filter(t ->
                t.getDueDate() != null &&
                t.getDueDate().isBefore(LocalDate.now()) &&
                t.getStatus() != Task.Status.DONE).count();

        return new DashboardStats(projects.size(), tasks.size(), todo, inProgress, done, overdue);
    }
}
