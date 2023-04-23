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
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import org.w3c.dom.ls.LSOutput;

import java.awt.event.ActionListener;
import java.sql.ResultSet;

/*
 * Playlist song list controller. Upon initialization, create the list to display
 */
public class PLSongs {

    @FXML private VBox root;
    @FXML private Button removeButton;
    @FXML private Button upButton;
    @FXML private Button downButton;
    @FXML private TableView<Song> tableView;
    @FXML private TableColumn<Song, String> nameColumn;
    @FXML private TableColumn<Song, String> artistColumn;
    @FXML private TableColumn<Song, String> albumColumn;
    @FXML private TableColumn<Song, String> durationColumn;
    private ObservableList<Song> songs;
    private String currentPL;
    private ActionListener refreshListener;


    public PLSongs(){
    }

    public void initialize() {
        this.songs = FXCollections.observableArrayList();
        tableView.setItems(songs);
        tableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        //Bind table columns to the width of table
        nameColumn.prefWidthProperty().bind(root.widthProperty().multiply(.3));        //30% of table is names
        artistColumn.prefWidthProperty().bind(root.widthProperty().multiply(.3));   //20% of table is count
        albumColumn.prefWidthProperty().bind(root.widthProperty().multiply(.2));
        durationColumn.prefWidthProperty().bind(root.widthProperty().multiply(.2));
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
        currentPL=plName;
        songs.clear();
        ResultSet rs = Database.selectSongsFromPlaylist(currentPL);
        try {
            int i = 0;
            while (rs.next()) {
                songs.add(new Song(
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
        tableView.setItems(songs);
        tableView.refresh();
    }

    /**
     * Called to refresh the information in the UI
     * When database is updated
     */
    public void refreshInformation(){
        if(currentPL!=null){
            songs.clear();
            ResultSet rs = Database.selectSongsFromPlaylist(currentPL);
            try {
                int i = 0;
                while (rs.next()) {
                    songs.add(new Song(
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
        }

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
