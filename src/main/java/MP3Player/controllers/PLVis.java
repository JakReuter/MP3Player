package MP3Player.controllers;

import MP3Player.database.Database;
import MP3Player.mp3Player.playlist.Playlist;
import MP3Player.mp3Player.song.Song;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;

import java.awt.event.ActionListener;
import java.net.URL;
import java.sql.ResultSet;
import java.util.ResourceBundle;


// TODO: Connect buttons to database
public class PLVis {

    @FXML private Button newButton;
    @FXML private Button removeButton;
    @FXML private Button renameButton;
    @FXML private TextField textField;
    @FXML private Button descriptionButton;
    @FXML public TableView<Playlist> tableView;
    @FXML private TableColumn<Playlist, String> nameColumn;
    @FXML private TableColumn<Playlist, Integer> songCountColumn;
    @FXML private TableColumn<Playlist, String> durationColumn;
    @FXML private TableColumn<Playlist, String> descriptionColumn;

    private ObservableList<Playlist> playlists;

    private PLSongs plSongs;
    private ActionListener refreshListener;

    public PLVis(){ this(null);}

    public PLVis(PLSongs plSongs) {
        this.plSongs = plSongs;
    }

    public void setPLSongs(PLSongs plSongs) {
        this.plSongs = plSongs;
    }

    public void initialize(){
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
                        rs.getObject("duration").toString(),
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
            Database.createNewPlaylist(textField.getText(), "");
            refreshInformation();
            textField.setVisible(false);
            textField.clear();
        } else {
            textField.setVisible(true);
        }
    }

    @FXML
    private void handleRemoveButton(ActionEvent event) {
        // code to handle remove button click
        Database.removePlaylist(tableView.getSelectionModel().getSelectedItem().getName());
        refreshInformation();
    }

    @FXML
    private void handleRenameButton(ActionEvent event) {
        // code to handle rename button click
        if(textField.isVisible()) {
            //Database.editPlaylistName(tableView.getSelectionModel().getSelectedItem().getName(), textField.getText());
            refreshInformation();
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
            Database.editPlaylist(tableView.getSelectionModel().getSelectedItem().getName(), textField.getText());
            refreshInformation();
            tableView.refresh();
            textField.setVisible(false);
            textField.clear();
        }
        else {
            textField.setVisible(true);
        }
    }

    @FXML
    private void handlePlaylistSelection(KeyEvent event) {
        // When a playlist is selected, tell the PLSongs, and give it the name of the playlist
        String plName = tableView.getSelectionModel().getSelectedItem().getName();
        this.plSongs.update(plName);
    }
    /**
     * Called to refresh the information in the UI
     * When database is updated
     */
    public void refreshInformation(){

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
                        rs.getObject("duration").toString(),
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
        tableView.setItems(this.playlists);
        tableView.refresh();

    }

    /**
     * Sets the event triggered when database is updated
     * @param l event that refreshes all other UIs
     */
    public void setOnRefresh(ActionListener l){
        this.refreshListener = l;
    }

    /**
     * Triggers
     */
    public void refreshSendEvent(){
        this.refreshListener.actionPerformed(new java.awt.event.ActionEvent(this, 1001, "Refresh"));
    }


}
