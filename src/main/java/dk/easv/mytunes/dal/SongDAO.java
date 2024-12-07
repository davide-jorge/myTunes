package dk.easv.mytunes.dal;

import dk.easv.mytunes.be.Song;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SongDAO {
    private DBConnection conn = new DBConnection();

    // Method to collect songs from the database
    public List<Song> getSongs() {
        List<Song> songs = new ArrayList<>();
        try {
            Connection c = conn.getConnection();
            String sql = "SELECT * FROM Song";
            PreparedStatement stmnt = c.prepareStatement(sql);
            ResultSet rs = stmnt.executeQuery();
            while (rs.next()) { //While there are rows
                int id = rs.getInt("id");
                String title = rs.getString("title");
                int artist_id = rs.getInt("artist_id");
                String category = rs.getString("category");
                int duration = rs.getInt("duration");
                String file_path = rs.getString("file_path");
                Song song = new Song(id, title, artist_id, category, duration, file_path);
                songs.add(song);
            }
        }catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return songs;
    }

    public Song add(Song song) {
        try {
            Connection c = conn.getConnection();
            String sql = "INSERT INTO Song (id, title) VALUES (?,?)";
            PreparedStatement stmnt = c.prepareStatement(sql);
            stmnt.setInt(1, song.getId());
            stmnt.setString(2, song.getTitle());
            stmnt.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return song;
    }

    public void delete(Song song) {
        try {
            Connection c = conn.getConnection();
            String sql = "DELETE FROM Song WHERE id=?";
            PreparedStatement stmnt = c.prepareStatement(sql);
            stmnt.setInt(1, song.getId());
            stmnt.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
