package dk.easv.mytunes.gui.models;

import dk.easv.mytunes.be.Artist;
import dk.easv.mytunes.bll.ArtistManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ArtistModel {
    private final ArtistManager artistManager = new ArtistManager();

    private ObservableList<Artist> artists = FXCollections.observableArrayList();

    public ArtistModel() {
        artists.setAll(artistManager.getArtists());
    }

    public ObservableList<Artist> getArtists() {
        return artists;
    }
}
