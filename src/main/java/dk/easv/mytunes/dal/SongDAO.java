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
    public void addSong(Song song) {
        try {
            Connection c = conn.getConnection();
            String sql = "INSERT INTO Song (title, artist_id, category, duration, file_path) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement stmnt = c.prepareStatement(sql);
            stmnt.setString(1, song.getTitle());
            stmnt.setInt(2, getArtistId(song.getArtist_name()));
            stmnt.setString(3, song.getCategory());
            stmnt.setInt(4, 0); // Duration is optional or calculated later
            stmnt.setString(5, song.getFile_path());
            stmnt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method to update an existing song in the database
    public void updateSong(Song song) {
        try {
            // Get the artist ID based on the artist name
            int artistId = getArtistId(song.getArtist_name());

            // If the artist does not exist in the database, handle the error or add the artist
            if (artistId == -1) {
                artistId = addArtist(song.getArtist_name());
            }

            // Proceed with the update if the artist exists
            Connection c = conn.getConnection();
            String sql = "UPDATE Song SET title = ?, artist_id = ?, category = ?, duration = ?, file_path = ? WHERE id = ?";
            PreparedStatement stmnt = c.prepareStatement(sql);
            stmnt.setString(1, song.getTitle());
            stmnt.setInt(2, artistId);
            stmnt.setString(3, song.getCategory());
            stmnt.setInt(4, song.getDuration());
            stmnt.setString(5, song.getFile_path());
            stmnt.setInt(6, song.getId());
            stmnt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int addArtist(String artistName) {
        try {
            String sql = "INSERT INTO Artist (name) VALUES (?)";
            Connection c = conn.getConnection();
            PreparedStatement stmnt = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmnt.setString(1, artistName);
            stmnt.executeUpdate();

            // Get the generated artist ID
            ResultSet rs = stmnt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1); // Return the generated artist ID
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; // Return -1 if adding the artist fails
    }

    public void deleteSong(Song song) throws SQLException {
        // Remove song from all playlists it is associated with
        removeSongFromPlaylists(song);

        // Then delete the song from the Songs table
        try (Connection conn = this.conn.getConnection()) {
            String sql = "DELETE FROM Song WHERE id = ?";
            try (PreparedStatement stmnt = conn.prepareStatement(sql)) {
                stmnt.setInt(1, song.getId());
                stmnt.executeUpdate(); // Execute the delete statement
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException("Error deleting song", e);
        }
    }

    private void removeSongFromPlaylists(Song song) throws SQLException {
        try (Connection conn = this.conn.getConnection()) {
            // Remove song from PlaylistSongs table (if necessary)
            String sql = "DELETE FROM PlaylistSongs WHERE song_id = ?";
            try (PreparedStatement stmnt = conn.prepareStatement(sql)) {
                stmnt.setInt(1, song.getId());
                stmnt.executeUpdate(); // Remove song from any playlist it's associated with
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException("Error removing song from playlists", e);
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
