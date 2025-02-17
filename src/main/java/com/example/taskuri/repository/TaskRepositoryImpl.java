package com.example.taskuri.repository;

import com.example.taskuri.domain.Taskss;
import com.example.taskuri.domain.TaskStatus;
import com.example.taskuri.domain.User;

import java.sql.*;
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
        String sql = "INSERT INTO tasks (title, description, start_date, finish_date, status, user_id) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, task.getTitle());
            stmt.setString(2, task.getDescription());
            stmt.setTimestamp(3, Timestamp.valueOf(task.getStartDateTime()));

            if (task.getFinishDateTime() != null) {
                stmt.setTimestamp(4, Timestamp.valueOf(task.getFinishDateTime()));
            } else {
                stmt.setNull(4, Types.TIMESTAMP);
            }

            stmt.setString(5, task.getStatus().name());
            stmt.setLong(6, task.getUserId());

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
                LocalDateTime finishDateTime = null;

                if (rs.getTimestamp("finish_date") != null) {
                    finishDateTime = rs.getTimestamp("finish_date").toLocalDateTime();
                }

                Taskss task = new Taskss(
                        rs.getLong("id"),
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getTimestamp("start_date").toLocalDateTime(),
                        finishDateTime,
                        TaskStatus.valueOf(rs.getString("status")),
                        rs.getLong("user_id")
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

    public void update(Taskss task) {
        String sql = "UPDATE tasks SET title = ?, description = ?, start_date = ?, finish_date = ?, status = ?, user_id = ? WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, task.getTitle());
            stmt.setString(2, task.getDescription());
            stmt.setTimestamp(3, Timestamp.valueOf(task.getStartDateTime()));
            if (task.getFinishDateTime() != null) {
                stmt.setTimestamp(4, Timestamp.valueOf(task.getFinishDateTime()));
            } else {
                stmt.setNull(4, Types.TIMESTAMP);
            }

            stmt.setString(5, task.getStatus().name());
            stmt.setLong(6, task.getUserId());
            stmt.setLong(7, task.getId());

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Taskss> getTasksByUserId(Long userId) {
        List<Taskss> tasks = new ArrayList<>();
        String sql = "SELECT * FROM tasks WHERE user_id = ?";

        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                tasks.add(new Taskss(
                        rs.getLong("id"),
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getTimestamp("start_date").toLocalDateTime(),
                        rs.getTimestamp("finish_date") != null ? rs.getTimestamp("finish_date").toLocalDateTime() : null,
                        TaskStatus.valueOf(rs.getString("status")),
                        rs.getLong("user_id")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tasks;
    }

    @Override
    public List<Taskss> getNotesByTaskId(Long taskId) {
        return List.of();
    }

    @Override
    public User getUserByEmail(String email) {
        return null;
    }
}
