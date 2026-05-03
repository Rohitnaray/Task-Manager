package com.taskmanager.dto;

import com.taskmanager.entity.Task;
import com.taskmanager.entity.User;
import jakarta.validation.constraints.*;
import java.time.LocalDate;

public class Dtos {

    // ── Auth ──────────────────────────────────────────────
    public static class SignupRequest {
        @NotBlank public String name;
        @Email @NotBlank public String email;
        @NotBlank @Size(min = 6) public String password;
        public User.Role role;
    }

    public static class LoginRequest {
        @NotBlank public String email;
        @NotBlank public String password;
    }

    public static class AuthResponse {
        public String token;
        public Long id;
        public String name;
        public String email;
        public String role;

        public AuthResponse() {}
        public AuthResponse(String token, Long id, String name, String email, String role) {
            this.token = token; this.id = id; this.name = name;
            this.email = email; this.role = role;
        }
    }

    // ── Project ───────────────────────────────────────────
    public static class ProjectRequest {
        @NotBlank public String name;
        public String description;
        public String status;
    }

    public static class AddMemberRequest {
        @NotNull public Long userId;
    }

    // ── Task ──────────────────────────────────────────────
    public static class TaskRequest {
        @NotBlank public String title;
        public String description;
        public Task.Priority priority;
        public LocalDate dueDate;
        public Long assigneeId;
    }

    public static class TaskStatusRequest {
        @NotNull public Task.Status status;
    }

    // ── Dashboard ─────────────────────────────────────────
    public static class DashboardStats {
        public long totalProjects;
        public long totalTasks;
        public long todoTasks;
        public long inProgressTasks;
        public long doneTasks;
        public long overdueTasks;

        public DashboardStats() {}
        public DashboardStats(long totalProjects, long totalTasks, long todoTasks,
                              long inProgressTasks, long doneTasks, long overdueTasks) {
            this.totalProjects = totalProjects;
            this.totalTasks = totalTasks;
            this.todoTasks = todoTasks;
            this.inProgressTasks = inProgressTasks;
            this.doneTasks = doneTasks;
            this.overdueTasks = overdueTasks;
        }
    }
}
