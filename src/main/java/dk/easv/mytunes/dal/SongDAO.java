package dk.easv.mytunes.dal;

import dk.easv.mytunes.be.Song;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SongDAO {
    private DBConnection conn = new DBConnection();

    // Method to collect songs from the database
    public List<Song> getSongs() {
        List<Song> songs = new ArrayList<>();
        try {
            Connection c = conn.getConnection();
            String sql = "SELECT s.id, s.title, a.name AS artist_name, s.category, s.duration, s.file_path " +
                    "FROM Song s " +
                    "JOIN Artist a ON s.artist_id = a.id";
            PreparedStatement stmnt = c.prepareStatement(sql);
            ResultSet rs = stmnt.executeQuery();
            while (rs.next()) { //While there are rows
                int id = rs.getInt("id");
                String title = rs.getString("title");
                String artist_name = rs.getString("artist_name");
                String category = rs.getString("category");
                int duration = rs.getInt("duration");
                String file_path = rs.getString("file_path");
                Song song = new Song(id, title, artist_name, category, duration, file_path);
                songs.add(song);
            }
        }catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return songs;
    }

    // Method to add a new song to the database
    public void addSong(Song song) throws SQLException {
        String sql = "INSERT INTO Song (title, artist_id, category, duration, file_path) VALUES (?, ?, ?, ?, ?)";
        try (Connection c = conn.getConnection(); PreparedStatement stmnt = c.prepareStatement(sql)) {
            int artistId = getArtistId(song.getArtist_name()); // Get the artist id based on the artist name
            if (artistId == -1) { // Assuming -1 means artist doesn't exist
                // Insert the artist first
                insertArtist(song.getArtist_name());
                artistId = getArtistId(song.getArtist_name()); // Retrieve the newly inserted artist's id
            }
            stmnt.setString(1, song.getTitle());
            stmnt.setInt(2, artistId);
            stmnt.setString(3, song.getCategory());
            stmnt.setInt(4, song.getDuration());
            stmnt.setString(5, song.getFile_path());
            stmnt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException("Error adding song to database", e);
        }
    }

    private void insertArtist(String artistName) throws SQLException {
        String sql = "INSERT INTO Artist (name) VALUES (?)";
        try (Connection c = conn.getConnection(); PreparedStatement stmnt = c.prepareStatement(sql)) {
            stmnt.setString(1, artistName);
            stmnt.executeUpdate();
        }
    }

    // Method to update an existing song in the database
    public void updateSong(Song song) throws SQLException {
        String sql = "UPDATE Song SET title = ?, artist_id = ?, category = ?, duration = ?, file_path = ? WHERE id = ?";
        try (Connection c = conn.getConnection(); PreparedStatement stmnt = c.prepareStatement(sql)) {
            int artistId = getArtistId(song.getArtist_name());
            stmnt.setString(1, song.getTitle());
            stmnt.setInt(2, artistId);
            stmnt.setString(3, song.getCategory());
            stmnt.setInt(4, song.getDuration());
            stmnt.setString(5, song.getFile_path());
            stmnt.setInt(6, song.getId());
            stmnt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException("Error updating song in database", e);
        }
    }

    // Method to delete an existing song in the database
    public void deleteSong(Song song) throws SQLException {
        String sql = "DELETE FROM Song WHERE id = ?";
        try (Connection c = conn.getConnection(); PreparedStatement stmnt = c.prepareStatement(sql)) {
            stmnt.setInt(1, song.getId());
            stmnt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException("Error deleting song from database", e);
        }
    }

    // Fetch artist id based on artist name
    private int getArtistId(String artistName) {
        try {
            Connection c = conn.getConnection();
            String sql = "SELECT id FROM Artist WHERE name = ?";
            PreparedStatement stmnt = c.prepareStatement(sql);
            stmnt.setString(1, artistName);
            ResultSet rs = stmnt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            } else {
                // If artist doesn't exist, return -1 to indicate an error
                return -1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; // Return -1 if no artist is found
    }
}
