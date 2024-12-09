package dk.easv.mytunes.be;

// Class Playlist of the business entity package (be) defines a Playlist
public class Playlist {
    private int id;
    private String name;
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
