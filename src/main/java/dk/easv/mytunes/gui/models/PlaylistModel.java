package dk.easv.mytunes.gui.models;

import dk.easv.mytunes.be.Playlist;
import dk.easv.mytunes.bll.PlaylistManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class PlaylistModel {
    private final PlaylistManager playlistManager = new PlaylistManager();

    private ObservableList<Playlist> playlists = FXCollections.observableArrayList();

    public PlaylistModel() {
        playlists.setAll(playlistManager.getPlaylists());
    }

    public ObservableList<Playlist> getPlaylists() {
        return playlists;
    }
}
