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
    public List<Playlist> getPlaylists() {
        List<Playlist> playlists = new ArrayList<>();
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
                playlists.add(playlist);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return playlists;
    }

    public Playlist getPlaylistById(int playlistId) throws SQLException {
        String sql = "SELECT id, name, duration FROM Playlist WHERE id = ?";
        try {
            Connection c = conn.getConnection();
            PreparedStatement stmnt = c.prepareStatement(sql);
            stmnt.setInt(1, playlistId);
            ResultSet rs = stmnt.executeQuery();
            if (rs.next()) {
                return new Playlist(rs.getInt("id"), rs.getString("name"), rs.getInt("duration"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null; // Return null if no playlist is found
    }
}
