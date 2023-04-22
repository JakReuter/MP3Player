package MP3Player.controllers;


/*
 * Master song table controller. Upon initialization, load all songs from database and display them.
 * Add to playlist button appends the selected song to the selected playlist
 */

import MP3Player.database.Database;
import MP3Player.mp3Player.song.Song;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.FileChooser;
import org.farng.mp3.MP3File;
import org.farng.mp3.id3.AbstractID3v2;

import java.awt.event.ActionListener;
import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;

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

    public ObservableList<Song> getQueue() {
        return queue;
    }

    public void setQueue(ObservableList<Song> queue) {
        this.queue = queue;
    }

    private ObservableList<Song> queue;


    //add event  variable here
    ActionListener refreshListener;

    public MSongs(){

    }

    //What if we made table view dragdetectable in fxml?
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
                        (String) rs.getObject("path"),
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
    }

    @FXML
    private void handleUpButton(ActionEvent event) {

    }

    @FXML
    private void handleRemoveButton(ActionEvent event) {
        Song toRemove = null;
        if((toRemove = tableView.getSelectionModel().getSelectedItem())!=null){
            Database.removeSong(toRemove.getName());
            System.out.println("removing " + toRemove.getName());
            refreshSendEvent();
        } else {
            System.out.println("no item selected");
        }
    }

    @FXML
    private void handleDownButton(ActionEvent event) {

    }

    // Adds the selected song to the selected playlist. Just add it in database, Maybe update the other two UIs
    @FXML
    private void handleAddButton(ActionEvent event)  {
    }

    @FXML
    private void handleNewSongButton(ActionEvent event) throws SQLException {
        FileChooser fileChooser = new FileChooser();
        File inFile;
        AbstractID3v2 tags = null;
        try {
            inFile = fileChooser.showOpenDialog(upButton.getScene().getWindow());
            //For reading  from folders
            //if inFile.isDirectory { for(File f : inFile.subFiles ) {
            MP3File mp3File = new MP3File(inFile);
            mp3File.seekMP3Frame();
            tags= mp3File.getID3v2Tag();

            //Calculating duration
            int dur = (int)inFile.length()*8 / (mp3File.getBitRate()*1000);

            Database.addNewSong(tags.getSongTitle(), inFile.getPath(), tags.getLeadArtist(), tags.getAlbumTitle(), dur);

        } catch (Exception e) {
            e.printStackTrace();
        }
        //Test
        ResultSet rs = Database.getSongInfo(tags.getSongTitle());

        System.out.println(rs.getObject(1));
        refreshSendEvent();
    }

    @FXML
    public void addToQueue(){
        queue.add(songs.get(tableView.getSelectionModel().getSelectedIndex()));
    }

    /**
     * Called to refresh the information in the UI
     * When database is updated
     */
    public void refreshInformation(){

        ResultSet rs = Database.selectAllSongs();
        this.songs.clear();
        // Loop through all playlists, adding them to the observableArrayList (getting the appropriate values from the database)
        try {
            int i = 0;
            while (rs.next()) {
                this.songs.add(i,new Song(
                        (String) rs.getObject("name"),
                        (String) rs.getObject("path"),
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
