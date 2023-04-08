package MP3Player.controllers;


/*
 * Master song table controller. Upon initialization, load all songs from database and display them.
 * Add to playlist button appends the selected song to the selected playlist
 */

import MP3Player.database.Database;
import MP3Player.mp3Player.playlist.Playlist;
import MP3Player.mp3Player.song.Song;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.sql.ResultSet;

public class MSongs {

    @FXML
    private Button removeButton;
    @FXML private Button upButton;
    @FXML private Button downButton;
    @FXML private Button addButton;
    @FXML private TableView<Song> tableView;
    @FXML private TableColumn<Song, String> nameColumn;
    @FXML private TableColumn<Song, String> artistColumn;
    @FXML private TableColumn<Song, String> albumColumn;
    @FXML private TableColumn<Song, String> durationColumn;
    private ObservableList<Song> songs;


    public void initialize() {
        this.songs = FXCollections.observableArrayList();
        // Get all playlists from database
        ResultSet rs = Database.selectAllSongs();
        // Loop through all playlists, adding them to the observableArrayList (getting the appropriate values from the database)
        try {
            int i = 0;
            while (rs.next()) {
                this.songs.add(new Song(
                        (String) rs.getObject("name"),
                        (String) rs.getObject("author"),
                        (String) rs.getObject("album"),
                        (String) rs.getObject("duration")
                ));
                i++;
            }
            if (i == 0) {
                System.out.println("No output");
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }

        tableView.setItems(songs);
        tableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    }

    @FXML
    private void handleUpButton(ActionEvent event) {

    }

    @FXML
    private void handleRemoveButton(ActionEvent event) {

    }

    @FXML
    private void handleDownButton(ActionEvent event) {

    }

    // Adds the selected song to the selected playlist. Just add it in database, Maybe update the other two UIs
    @FXML
    private void handleAddButton(ActionEvent event) {

    }

}
