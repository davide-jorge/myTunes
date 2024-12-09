package dk.easv.mytunes.be;

// Class PlaylistSongs of the business entity package (be) defines the PlaylistSongs collection
public class PlaylistSongs {
    private int index; // Number of the song in the user selected playlist
    private String title;

    public PlaylistSongs(int index, String title) {
        this.index = index;
        this.title = title;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return index + ". " + title; // Format: 1. Song title
    }
}
