package com.example.taskuri.repository;

import com.example.taskuri.domain.Notes;
import com.example.taskuri.domain.Taskss;
import com.example.taskuri.domain.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class NoteRepositoryImpl implements Repository<Notes> {
    private final String url;
    private final String username;
    private final String password;

    public NoteRepositoryImpl(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    @Override
    public void add(Notes note) {
        String sql = "INSERT INTO notes (task_id, content, created_at) VALUES (?, ?, ?) RETURNING id";
        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, note.getTaskId());
            stmt.setString(2, note.getContent());
            stmt.setTimestamp(3, Timestamp.valueOf(note.getCreatedAt()));

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                note.setId(rs.getLong("id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    @Override
    public List<Notes> getAll() {
        return List.of();
    }

    @Override
    public void delete(Long id) {
        String sql = "DELETE FROM notes WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(Notes note) {
        String sql = "UPDATE notes SET content = ? WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, note.getContent());
            stmt.setLong(2, note.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public List<Notes> getNotesByTaskId(Long taskId) {
        List<Notes> notes = new ArrayList<>();
        String sql = "SELECT id, content, created_at FROM notes WHERE task_id = ?";

        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, taskId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Notes note = new Notes(
                        rs.getLong("id"),
                        taskId,
                        rs.getString("content"),
                        rs.getTimestamp("created_at").toLocalDateTime()
                );
                notes.add(note);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return notes;
    }

    @Override
    public User getUserByEmail(String email) {
        return null;
    }

    @Override
    public List<Taskss> getTasksByUserId(Long userId) {
        return null;
    }

}