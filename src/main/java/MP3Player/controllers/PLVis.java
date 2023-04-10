package MP3Player.controllers;

import MP3Player.database.Database;
import MP3Player.mp3Player.playlist.Playlist;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.ResultSet;


// TODO: Connect buttons to database
public class PLVis {

    @FXML private Button newButton;
    @FXML private Button removeButton;
    @FXML private Button renameButton;
    @FXML private TextField textField;
    @FXML private Button descriptionButton;
    @FXML private TableView<Playlist> tableView;
    @FXML private TableColumn<Playlist, String> nameColumn;
    @FXML private TableColumn<Playlist, Integer> songCountColumn;
    @FXML private TableColumn<Playlist, String> durationColumn;
    @FXML private TableColumn<Playlist, String> descriptionColumn;

    private ObservableList<Playlist> playlists;

    private PLSongs plSongs;

    public PLVis(PLSongs plSongs) {
        this.plSongs = plSongs;
    }

    public void initialize() {
        this.playlists = FXCollections.observableArrayList();
        // Get all playlists from database
        ResultSet rs = Database.selectAllPlaylists();
        // Loop through all playlists, adding them to the observableArrayList (getting the appropriate values from the database)
        try {
            int i = 0;
            while (rs.next()) {
                this.playlists.add(new Playlist(
                        (String) rs.getObject("name"),
                        (int) rs.getObject("num_songs"),
                        (String) rs.getObject("duration"),
                        (String) rs.getObject("description")
                ));
                i++;
            }
            if (i == 0) {
                System.out.println("No output");
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }

        tableView.setItems(playlists);
        tableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    }

    @FXML
    private void handleNewButton(ActionEvent event) {
        // code to handle new button click
        if (textField.isVisible()) {
            playlists.add(new Playlist(textField.getText(),0,"00:00:00",""));
            textField.setVisible(false);
            textField.clear();
        } else {
            textField.setVisible(true);
        }
    }

    @FXML
    private void handleRemoveButton(ActionEvent event) {
        // code to handle remove button click
        playlists.remove(tableView.getSelectionModel().getSelectedItem());
    }

    @FXML
    private void handleRenameButton(ActionEvent event) {
        // code to handle rename button click
        if(textField.isVisible()) {
            playlists.get(tableView.getSelectionModel().getSelectedIndex()).setName(textField.getText());
            tableView.refresh();
            textField.setVisible(false);
            textField.clear();
        }
        else {
            textField.setVisible(true);
        }
    }

    @FXML
    private void handleDescriptionButton(ActionEvent event) {
        // code to handle description button click
        if(textField.isVisible()) {
            playlists.get(tableView.getSelectionModel().getSelectedIndex()).setDescription(textField.getText());
            tableView.refresh();
            textField.setVisible(false);
            textField.clear();
        }
        else {
            textField.setVisible(true);
        }
    }

    @FXML
    private void handlePlaylistSelection(ActionEvent event) {
        // When a playlist is selected, tell the PLSongs, and give it the name of the playlist
        String plName = tableView.getSelectionModel().getSelectedItem().getName();
        this.plSongs.update(plName);
    }
}
