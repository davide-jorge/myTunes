package dk.easv.mytunes.be;

// Class Song of the Business Entity (be) package determines the Song characteristics
public class Song {
    private int id;
    private String title;
    private int artist_id;
    private String category;
    private int duration;
    private String file_path;

    public Song(int id, String title, int artist_id, String category, int duration, String file_path) {
        this.id = id;
        this.title = title;
        this.artist_id = artist_id;
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

    public int getArtist_id() {
        return artist_id;
    }

    public void setArtist_id(int artist_id) {
        this.artist_id = artist_id;
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
    public String toString() {return id + "\t" + title + "\t" + artist_id + "\t" + category + "\t" + "(" + duration + ")" + "\t" + file_path;}
}
