import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class Playlist {

    private SimpleStringProperty name;
    private SimpleIntegerProperty numSongs;
    private SimpleStringProperty duration;
    private SimpleStringProperty description;

    public Playlist(String name, int numSongs, String duration, String description) {
        this.name = new SimpleStringProperty(name);
        this.numSongs = new SimpleIntegerProperty(numSongs);
        this.duration = new SimpleStringProperty(duration);
        this.description = new SimpleStringProperty(description);
    }

    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public int getNumSongs() {
        return numSongs.get();
    }

    public void setNumSongs(int numSongs) {
        this.numSongs.set(numSongs);
    }

    public String getDuration() {
        return duration.get();
    }

    public void setDuration(String duration) {
        this.duration.set(duration);
    }

    public String getDescription() {
        return description.get();
    }

    public void setDescription(String description) {
        this.description.set(description);
    }
}
