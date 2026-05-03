package com.taskmanager.controller;

import com.taskmanager.dto.Dtos.*;
import com.taskmanager.entity.User;
import com.taskmanager.service.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    @Autowired private ProjectService projectService;
    @Autowired private CurrentUserService currentUserService;

    @GetMapping
    public ResponseEntity<?> getAll() {
        User user = currentUserService.getCurrentUser();
        return ResponseEntity.ok(projectService.getProjectsForUser(user));
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody ProjectRequest req) {
        try {
            User user = currentUserService.getCurrentUser();
            return ResponseEntity.ok(projectService.createProject(req, user));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        try {
            User user = currentUserService.getCurrentUser();
            return ResponseEntity.ok(projectService.getProjectById(id, user));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @Valid @RequestBody ProjectRequest req) {
        try {
            User user = currentUserService.getCurrentUser();
            return ResponseEntity.ok(projectService.updateProject(id, req, user));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            User user = currentUserService.getCurrentUser();
            projectService.deleteProject(id, user);
            return ResponseEntity.ok("Project deleted");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{id}/members")
    public ResponseEntity<?> addMember(@PathVariable Long id, @RequestBody AddMemberRequest req) {
        try {
            User user = currentUserService.getCurrentUser();
            return ResponseEntity.ok(projectService.addMember(id, req.userId, user));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}/members/{userId}")
    public ResponseEntity<?> removeMember(@PathVariable Long id, @PathVariable Long userId) {
        try {
            User user = currentUserService.getCurrentUser();
            return ResponseEntity.ok(projectService.removeMember(id, userId, user));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
