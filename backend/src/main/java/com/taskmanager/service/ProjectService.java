package com.taskmanager.service;

import com.taskmanager.dto.Dtos.*;
import com.taskmanager.entity.*;
import com.taskmanager.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProjectService {

    @Autowired private ProjectRepository projectRepository;
    @Autowired private UserRepository userRepository;

    public Project createProject(ProjectRequest req, User owner) {
        Project project = Project.builder()
                .name(req.name)
                .description(req.description)
                .owner(owner)
                .members(new ArrayList<>())
                .build();
        return projectRepository.save(project);
    }

    public List<Project> getProjectsForUser(User user) {
        if (user.getRole() == User.Role.ADMIN) {
            return projectRepository.findAll();
        }
        return projectRepository.findByMemberOrOwner(user);
    }

    public Project getProjectById(Long id, User user) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Project not found"));
        if (user.getRole() == User.Role.ADMIN) return project;
        boolean isOwner = project.getOwner().getId().equals(user.getId());
        boolean isMember = project.getMembers().stream().anyMatch(m -> m.getId().equals(user.getId()));
        if (!isOwner && !isMember) throw new RuntimeException("Access denied");
        return project;
    }

    public Project updateProject(Long id, ProjectRequest req, User user) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Project not found"));
        if (!project.getOwner().getId().equals(user.getId()) && user.getRole() != User.Role.ADMIN) {
            throw new RuntimeException("Only project owner or admin can update");
        }
        project.setName(req.name);
        project.setDescription(req.description);
        if (req.status != null) project.setStatus(Project.Status.valueOf(req.status));
        return projectRepository.save(project);
    }

    public void deleteProject(Long id, User user) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Project not found"));
        if (!project.getOwner().getId().equals(user.getId()) && user.getRole() != User.Role.ADMIN) {
            throw new RuntimeException("Only project owner or admin can delete");
        }
        projectRepository.delete(project);
    }

    public Project addMember(Long projectId, Long userId, User requester) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));
        if (!project.getOwner().getId().equals(requester.getId()) && requester.getRole() != User.Role.ADMIN) {
            throw new RuntimeException("Only project owner or admin can add members");
        }
        User newMember = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (project.getMembers().stream().anyMatch(m -> m.getId().equals(userId))) {
            throw new RuntimeException("User is already a member");
        }
        project.getMembers().add(newMember);
        return projectRepository.save(project);
    }

    public Project removeMember(Long projectId, Long userId, User requester) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));
        if (!project.getOwner().getId().equals(requester.getId()) && requester.getRole() != User.Role.ADMIN) {
            throw new RuntimeException("Only project owner or admin can remove members");
        }
        project.getMembers().removeIf(m -> m.getId().equals(userId));
        return projectRepository.save(project);
    }
}
