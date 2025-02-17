package com.example.taskuri.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Taskss {
    private Long id;
    private String title;
    private String description;
    private LocalDateTime startDateTime;
    private LocalDateTime finishDateTime;
    private TaskStatus status;
    private List<Notes> notes;
    private Long userId;

    public Taskss(Long id, String title, String description, LocalDateTime startDateTime, LocalDateTime finishDateTime, TaskStatus status, Long userId) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.startDateTime = startDateTime;
        this.finishDateTime = finishDateTime;
        this.status = status;
        this.notes = new ArrayList<>();
        this.userId = userId;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getStartDateTime() {
        return startDateTime;
    }

    public LocalDateTime getFinishDateTime() {
        return finishDateTime;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public List<Notes> getNotes() {
        return notes;
    }
    public Long getUserId() {
        return userId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStartDateTime(LocalDateTime startDateTime) {
        this.startDateTime = startDateTime;
    }

    public void setFinishDateTime(LocalDateTime finishDateTime) {
        this.finishDateTime = finishDateTime;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public void addNote(Notes note) {
        notes.add(note);
    }
}
