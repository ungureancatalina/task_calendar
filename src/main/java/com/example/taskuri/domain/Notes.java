package com.example.taskuri.domain;

import java.time.LocalDateTime;

public class Notes {
    private Long id;
    private Long taskId;
    private String content;
    private LocalDateTime createdAt;

    public Notes(Long id, Long taskId, String content, LocalDateTime createdAt) {
        this.id = id;
        this.taskId = taskId;
        this.content = content;
        this.createdAt = createdAt;
    }

    public Notes(Long taskId, String content) {
        this.taskId = taskId;
        this.content = content;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return content;
    }
}
