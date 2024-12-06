package dk.easv.mytunes.dal;

import dk.easv.mytunes.be.Playlist;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PlaylistDAO {
    private DBConnection conn = new DBConnection();

    // Method to get all playlists from the database
    public List<Playlist> getAllPlaylists() {
        List<Playlist> Playlists = new ArrayList<>();
        try {
            Connection c = conn.getConnection();
            String sql = "SELECT * FROM Playlist";
            PreparedStatement stmnt = c.prepareStatement(sql);
            ResultSet rs = stmnt.executeQuery();
            while (rs.next()) { //While there are rows
                int id = rs.getInt("id");
                String name = rs.getString("name");
                int duration = rs.getInt("duration");
                Playlist playlist = new Playlist(id, name, duration);
                Playlists.add(playlist);
            }
        }catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Playlists;
    }
}
