package com.example.taskuri.repository;

import com.example.taskuri.domain.Notes;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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
        String sql = "INSERT INTO notes (content, created_at) VALUES (?, ?) RETURNING id";
        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, note.getContent());
            stmt.setTimestamp(2, Timestamp.valueOf(note.getCreatedAt()));

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
        List<Notes> notes = new ArrayList<>();
        String sql = "SELECT id, content, created_at FROM notes";
        try (Connection conn = DriverManager.getConnection(url, username, password);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Notes note = new Notes(
                        rs.getLong("id"),
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
    public void update(Notes entity) {

    }
}