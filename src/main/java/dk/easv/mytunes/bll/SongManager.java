package dk.easv.mytunes.bll;

import dk.easv.mytunes.be.Song;
import dk.easv.mytunes.dal.SongDAO;

import java.util.List;

public class SongManager {
    private final SongDAO songDAO = new SongDAO();

    public List<Song> getSongs() {
        List<Song> songs = songDAO.getSongs();
        return songs;
    }
}
