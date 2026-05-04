package com.taskmanager.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "projects")
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String name;

    private String description;

    @Enumerated(EnumType.STRING)
    private Status status;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "owner_id")
    private User owner;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "project_members",
        joinColumns = @JoinColumn(name = "project_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<User> members = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL)
    private List<Task> tasks;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public Project() {}

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public Status getStatus() { return status; }
    public User getOwner() { return owner; }
    public List<User> getMembers() { return members; }
    public List<Task> getTasks() { return tasks; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setId(Long id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setStatus(Status status) { this.status = status; }
    public void setOwner(User owner) { this.owner = owner; }
    public void setMembers(List<User> members) { this.members = members; }
    public void setTasks(List<Task> tasks) { this.tasks = tasks; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
        if (status == null) status = Status.ACTIVE;
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private final Project p = new Project();
        public Builder name(String name) { p.name = name; return this; }
        public Builder description(String desc) { p.description = desc; return this; }
        public Builder owner(User owner) { p.owner = owner; return this; }
        public Builder members(List<User> members) { p.members = members; return this; }
        public Project build() { return p; }
    }

    public enum Status { ACTIVE, COMPLETED, ON_HOLD }
}
