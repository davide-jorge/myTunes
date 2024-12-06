package dk.easv.mytunes.gui.models;

import dk.easv.mytunes.be.Song;
import dk.easv.mytunes.bll.SongManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class SongModel {
    private final SongManager songManager = new SongManager();
    private ObservableList<Song> songObservableList = FXCollections.observableArrayList();

    public SongModel() {
        songObservableList.setAll(songManager.getAllSongs());
    }

    public ObservableList<Song> getObservableSongList() {
        return songObservableList;
    }
}
