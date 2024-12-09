package dk.easv.mytunes.be;

// Class Song of the business entity package (be) defines a Song
public class Song {
    private int id;
    private String title;
    private String artist_name;
    private String category;
    private int duration;
    private String file_path;

    public Song(int id, String title, String artist_name, String category, int duration, String file_path) {
        this.id = id;
        this.title = title;
        this.artist_name = artist_name;
        this.category = category;
        this.duration = duration;
        this.file_path = file_path;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist_name() {
        return artist_name;
    }

    public void setArtist_name(String artist_name) {
        this.artist_name = artist_name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getFile_path() {
        return file_path;
    }

    public void setFile_path(String file_path) {
        this.file_path = file_path;
    }

    @Override
    public String toString() {return title + ", " + artist_name + ", " + category + " " + "(" + duration + ")";}
}
