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

    // Method to add a new artist to the database
    public void addArtist(Artist artist) {
        try {
            Connection c = conn.getConnection();
            String sql = "INSERT INTO Artist (name) VALUES (?)";
            PreparedStatement stmnt = c.prepareStatement(sql);
            stmnt.setString(1, artist.getName());
            stmnt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
