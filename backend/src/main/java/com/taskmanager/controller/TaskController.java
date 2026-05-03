package com.taskmanager.controller;

import com.taskmanager.dto.Dtos.*;
import com.taskmanager.entity.Task;
import com.taskmanager.service.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class TaskController {

    @Autowired private TaskService taskService;
    @Autowired private CurrentUserService currentUserService;

    // Dashboard stats
    @GetMapping("/dashboard")
    public ResponseEntity<?> dashboard() {
        return ResponseEntity.ok(taskService.getDashboardStats(currentUserService.getCurrentUser()));
    }

    // My tasks
    @GetMapping("/tasks/mine")
    public ResponseEntity<?> myTasks() {
        return ResponseEntity.ok(taskService.getMyTasks(currentUserService.getCurrentUser()));
    }

    // Tasks by project
    @GetMapping("/projects/{projectId}/tasks")
    public ResponseEntity<?> byProject(@PathVariable Long projectId) {
        try {
            return ResponseEntity.ok(taskService.getTasksByProject(projectId, currentUserService.getCurrentUser()));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Create task
    @PostMapping("/projects/{projectId}/tasks")
    public ResponseEntity<?> create(@PathVariable Long projectId, @Valid @RequestBody TaskRequest req) {
        try {
            return ResponseEntity.ok(taskService.createTask(projectId, req, currentUserService.getCurrentUser()));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Update task
    @PutMapping("/tasks/{taskId}")
    public ResponseEntity<?> update(@PathVariable Long taskId, @Valid @RequestBody TaskRequest req) {
        try {
            return ResponseEntity.ok(taskService.updateTask(taskId, req, currentUserService.getCurrentUser()));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Update status only
    @PatchMapping("/tasks/{taskId}/status")
    public ResponseEntity<?> updateStatus(@PathVariable Long taskId, @RequestBody TaskStatusRequest req) {
        try {
            return ResponseEntity.ok(taskService.updateStatus(taskId, req.status, currentUserService.getCurrentUser()));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Delete task
    @DeleteMapping("/tasks/{taskId}")
    public ResponseEntity<?> delete(@PathVariable Long taskId) {
        try {
            taskService.deleteTask(taskId, currentUserService.getCurrentUser());
            return ResponseEntity.ok("Task deleted");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
