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

    public void addPlaylist(Playlist playlist) {
        try {
            Connection c = conn.getConnection();
            String sql = "INSERT INTO Playlist (name, duration) VALUES (?, ?)";
            PreparedStatement stmnt = c.prepareStatement(sql);
            stmnt.setString(1, playlist.getName());
            stmnt.setInt(2, playlist.getDuration());
            stmnt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void updatePlaylist(Playlist playlist) {
        try {
            Connection c = conn.getConnection();
            String sql = "UPDATE Playlist SET name = ?, duration = ? WHERE id = ?";
            PreparedStatement stmnt = c.prepareStatement(sql);
            stmnt.setString(1, playlist.getName());
            stmnt.setInt(2, playlist.getDuration());
            stmnt.setInt(3, playlist.getId());
            stmnt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void deletePlaylist(Playlist playlist) {
        try {
            Connection c = conn.getConnection();
            String sql = "DELETE FROM Playlist WHERE id = ?";
            PreparedStatement stmnt = c.prepareStatement(sql);
            stmnt.setInt(1, playlist.getId());
            stmnt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
