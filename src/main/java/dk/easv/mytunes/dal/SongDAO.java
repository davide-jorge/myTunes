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

    // Method to get all songs from the database
    public List<Song> getAllSongs() {
        List<Song> Songs = new ArrayList<>();
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
                Songs.add(song);
            }
        }catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Songs;
    }
}
