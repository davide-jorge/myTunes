package dk.easv.mytunes.be;

import javafx.collections.ObservableList;

// Class Playlist of the Business Entity package sets and defines the Playlist
public class Playlist {
    private int id;
    private String name;
    private ObservableList<Song> songs;
    private int duration;

    public Playlist(int id, String name, int duration) {
        this.id = id;
        this.name = name;
        this.duration = duration;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    @Override
    public String toString() {return id + ". " + name + " (" + duration + ") ";}
}
