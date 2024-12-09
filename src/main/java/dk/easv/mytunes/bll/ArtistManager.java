package dk.easv.mytunes.bll;

import dk.easv.mytunes.be.Artist;
import dk.easv.mytunes.dal.ArtistDAO;

import java.util.List;

public class ArtistManager {
    private final ArtistDAO artistDAO = new ArtistDAO();

    public List<Artist> getArtists(){
        List<Artist> artists = artistDAO.getArtists();
        return artists;
    }
}
