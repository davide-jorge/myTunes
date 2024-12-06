package dk.easv.mytunes.bll;

import dk.easv.mytunes.be.Playlist;
import dk.easv.mytunes.dal.PlaylistDAO;

import java.util.List;

public class PlaylistManager {
    private final PlaylistDAO playlistDAO = new PlaylistDAO();

    public List<Playlist> getAllPlaylists(){
        List<Playlist> playlists = playlistDAO.getAllPlaylists();
        return playlists;
    }
}
