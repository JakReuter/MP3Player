package MP3Player.controllers;

import MP3Player.database.Database;
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

/*
 * Playlist song list controller. Upon initialization, create the list to display
 */
public class PLSongs {

    @FXML private Button removeButton;
    @FXML private Button upButton;
    @FXML private Button downButton;
    @FXML private TableView<Song> tableView;
    @FXML private TableColumn<Song, String> nameColumn;
    @FXML private TableColumn<Song, String> artistColumn;
    @FXML private TableColumn<Song, String> albumColumn;
    @FXML private TableColumn<Song, String> durationColumn;
    private ObservableList<Song> songs;


    public void initialize() {
        this.songs = FXCollections.observableArrayList();
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

    // Called by PLVis, when a new playlist is selected, to populate the list here.
    public void update(String plName) {
        ResultSet rs = Database.selectSongsFromPlaylist(plName);
        tableView.getItems().clear();
        try {
            int i = 0;
            while (rs.next()) {
                this.songs.add(new Song(
                        (String) rs.getObject("name"),
                        (String) rs.getObject("author"),
                        (String) rs.getObject("album"),
                        rs.getObject("duration").toString()
                ));
                i++;
            }
            if (i == 0) {
                System.out.println("No output");
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }

        tableView.refresh();
    }


}
