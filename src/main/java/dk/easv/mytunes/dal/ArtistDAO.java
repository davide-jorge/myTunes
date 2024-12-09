package dk.easv.mytunes.dal;

import dk.easv.mytunes.be.Artist;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ArtistDAO {
    private DBConnection conn = new DBConnection();

    public Artist getArtistById(int artistId) throws SQLException {
        String sql = "SELECT id, name FROM Artist WHERE id = ?";
        try {
            Connection c = conn.getConnection();
            PreparedStatement stmnt = c.prepareStatement(sql);
            stmnt.setInt(1, artistId);
            ResultSet rs = stmnt.executeQuery();
            if (rs.next()) {
                return new Artist(rs.getInt("id"), rs.getString("name"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null; // Return null if no artist is found
    }

    // Method to collect artists from the database
    public List<Artist> getArtists() {
        List<Artist> artists = new ArrayList<>();
        try {
            Connection c = conn.getConnection();
            String sql = "SELECT * FROM Artist";
            PreparedStatement stmnt = c.prepareStatement(sql);
            ResultSet rs = stmnt.executeQuery();
            while (rs.next()) { //While there are rows
                int id = rs.getInt("id");
                String name = rs.getString("name");
                Artist artist = new Artist(id, name);
                artists.add(artist);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return artists;
    }
}
