package dk.easv.mytunes.dal;

import dk.easv.mytunes.be.PlaylistSongs;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PlaylistSongsDAO {
    private DBConnection conn = new DBConnection();

    // This method retrieves all songs belonging to a specific playlist
    public List<PlaylistSongs> getSongsByPlaylistId(int playlistId) throws SQLException {
        List<PlaylistSongs> playlistSongs = new ArrayList<>();
        String sql = "SELECT s.id, s.title " +
                "FROM Song s " +
                "JOIN PlaylistSongs ps ON s.id = ps.song_id " +
                "WHERE ps.playlist_id = ?";
        try {
            Connection c = conn.getConnection();
            PreparedStatement stmnt = c.prepareStatement(sql);
            stmnt.setInt(1, playlistId);
            ResultSet rs = stmnt.executeQuery();
            int index = 1; // Number the songs starting from 1
            while (rs.next()) {
                String title = rs.getString("title");
                playlistSongs.add(new PlaylistSongs(index++, title));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return playlistSongs;
    }
}
