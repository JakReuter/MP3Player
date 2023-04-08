package MP3Player.mp3Player.song;

import javafx.beans.property.SimpleStringProperty;

public class Song {

    private SimpleStringProperty name;
    private SimpleStringProperty artist;
    private SimpleStringProperty album;
    private SimpleStringProperty duration;

    public Song(String name, String artist, String album, String duration) {
        this.name = new SimpleStringProperty(name);
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
