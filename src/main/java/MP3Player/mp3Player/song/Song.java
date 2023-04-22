package MP3Player.mp3Player.song;

import javafx.beans.property.SimpleStringProperty;

public class Song {

    private SimpleStringProperty name;



    private SimpleStringProperty path;
    private SimpleStringProperty artist;
    private SimpleStringProperty album;
    private SimpleStringProperty duration;

    public Song(String name, String path, String artist, String album, String duration) {
        this.name = new SimpleStringProperty(name);
        this.path = new SimpleStringProperty(path);
        this.artist = new SimpleStringProperty(artist);
        this.album = new SimpleStringProperty(album);
        this.duration = new SimpleStringProperty(duration);
    }

    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public String getPath() {
        return path.get();
    }

    public SimpleStringProperty pathProperty() {
        return path;
    }

    public void setPath(String path) {
        this.path.set(path);
    }

    public String getArtist() {
        return artist.get();
    }

    public void setArtist(String artist) {
        this.artist.set(artist);
    }

    public String getAlbum() {
        return album.get();
    }

    public void setAlbum(String album) {
        this.album.set(album);
    }

    public String getDuration() {
        return duration.get();
    }

    public void setDuration(String duration) {
        this.duration.set(duration);
    }

}
