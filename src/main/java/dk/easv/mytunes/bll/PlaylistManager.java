package dk.easv.mytunes.bll;

import dk.easv.mytunes.be.Playlist;
import dk.easv.mytunes.dal.PlaylistDAO;

import java.util.List;

public class PlaylistManager {
    private final PlaylistDAO playlistDAO = new PlaylistDAO();

    public List<Playlist> getPlaylists(){
        List<Playlist> playlists = playlistDAO.getPlaylists();
        return playlists;
    }
}
