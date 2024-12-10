package dk.easv.mytunes.be;

// Class PlaylistSongs of the business entity package (be) defines the PlaylistSongs collection
public class PlaylistSongs {
    private int index; // Number of the song in the user selected playlist
    private String title;
    private Song song;

    public PlaylistSongs(int index, String title) {
        this.index = index;
        this.title = title;
    }

    public PlaylistSongs(Song song) {
        this.song = song;
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

    public Song getSong() {
        return song;
    }

    public void setSong(Song song) {
        this.song = song;
    }

    @Override
    public String toString() {
        return index + ". " + title; // Format: 1. Song title
    }
}
