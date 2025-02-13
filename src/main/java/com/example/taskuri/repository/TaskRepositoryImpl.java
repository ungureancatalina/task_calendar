package com.example.taskuri.repository;

import com.example.taskuri.domain.Taskss;
import com.example.taskuri.domain.TaskStatus;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TaskRepositoryImpl implements Repository<Taskss> {
    private final String url;
    private final String username;
    private final String password;

    public TaskRepositoryImpl(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    @Override
    public void add(Taskss task) {
        String sql = "INSERT INTO tasks (title, description, start_date, finish_date, status) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, task.getTitle());
            stmt.setString(2, task.getDescription());
            stmt.setTimestamp(3, Timestamp.valueOf(task.getStartDateTime()));
            stmt.setTimestamp(4, task.getFinishDateTime() != null ? Timestamp.valueOf(task.getFinishDateTime()) : null);
            stmt.setString(5, task.getStatus().name());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Taskss> getAll() {
        List<Taskss> tasks = new ArrayList<>();
        String sql = "SELECT * FROM tasks ORDER BY start_date ASC";
        try (Connection conn = DriverManager.getConnection(url, username, password);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Taskss task = new Taskss(
                        rs.getLong("id"),
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getTimestamp("start_date").toLocalDateTime(),
                        rs.getTimestamp("finish_date") != null ? rs.getTimestamp("finish_date").toLocalDateTime() : null,
                        TaskStatus.valueOf(rs.getString("status"))
                );
                tasks.add(task);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tasks;
    }

    @Override
    public void delete(Long id) {
        String sql = "DELETE FROM tasks WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Taskss> getTasksByDateRange(LocalDate startDate, LocalDate endDate) {
        List<Taskss> tasks = new ArrayList<>();
        String sql = "SELECT * FROM tasks WHERE DATE(start_date) BETWEEN ? AND ? ORDER BY start_date ASC";
        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(startDate));
            stmt.setDate(2, Date.valueOf(endDate));
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Taskss task = new Taskss(
                            rs.getLong("id"),
                            rs.getString("title"),
                            rs.getString("description"),
                            rs.getTimestamp("start_date").toLocalDateTime(),
                            rs.getTimestamp("finish_date") != null ? rs.getTimestamp("finish_date").toLocalDateTime() : null,
                            TaskStatus.valueOf(rs.getString("status"))
                    );
                    tasks.add(task);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tasks;
    }

    public void update(Taskss task) {
        String sql = "UPDATE tasks SET title = ?, description = ?, start_date = ?, finish_date = ?, status = ? WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, task.getTitle());
            stmt.setString(2, task.getDescription());
            stmt.setTimestamp(3, Timestamp.valueOf(task.getStartDateTime()));
            stmt.setTimestamp(4, task.getFinishDateTime() != null ? Timestamp.valueOf(task.getFinishDateTime()) : null);
            stmt.setString(5, task.getStatus().name());
            stmt.setLong(6, task.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
