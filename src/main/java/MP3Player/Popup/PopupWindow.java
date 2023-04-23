package MP3Player.Popup;

import MP3Player.controllers.MP3Player;
import MP3Player.database.Database;
import MP3Player.mp3Player.playlist.Playlist;
import MP3Player.mp3Player.song.Song;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.BorderPane;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextBoundsType;
import javafx.stage.Stage;
import javafx.stage.Popup;
import MP3Player.database.Database;
import javafx.application.Application;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PopupWindow extends Application {

    private Stage s = null;
    //private Popup popup = new Popup();
    private final Dialog<String> dialog;// = new Dialog<String>();
    private final ObservableList<Song> songs;// = FXCollections.observableArrayList();
    private final TableColumn<Song, String> nameColumn;// = new TableColumn<>("Song Name");
    private final TableColumn<Song, String> artistColumn;// = new TableColumn<>("Song Artist");
    private final TableColumn<Song, String> albumColumn;// = new TableColumn<>("Song Album");
    private final TableColumn<Song, String> durationColumn;// = new TableColumn<>("Song Duration");

    public PopupWindow() {
        dialog = new Dialog<String>();
        songs = FXCollections.observableArrayList();
        nameColumn = new TableColumn<>("Song Name");
        artistColumn = new TableColumn<>("Song Artist");
        albumColumn = new TableColumn<>("Song Album");
        durationColumn = new TableColumn<>("Song Duration");
    }

    public void SQLError(String text) {
        //Dialog<String> dialog = new Dialog<String>();
        //Setting the title
        dialog.setTitle("Dialog");
        Button button = new Button();
        ButtonType type = new ButtonType("Ok", ButtonBar.ButtonData.OK_DONE);
        //Setting the content of the dialog
        dialog.setContentText(text);
        //Adding buttons to the dialog pane
        dialog.getDialogPane().getButtonTypes().add(type);
        dialog.showAndWait();
    }

    public void editSong(Song song) {
        Stage stage = new Stage();
        stage.setTitle("Edit Song");

        //popup.setHeight(1000);
        //popup.setWidth(1000);
        BorderPane borderPane = new BorderPane();
        nameColumn.setCellValueFactory(new PropertyValueFactory<Song, String>("name"));
        nameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        nameColumn.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Song, String>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<Song, String> event) {
                event.getTableView().getItems().get(event.getTablePosition().getRow()).setName(event.getNewValue());
                event.getTableView().refresh();
                song.setName(event.getNewValue());
            }
        });
        artistColumn.setCellValueFactory(new PropertyValueFactory<Song, String>("artist"));
        artistColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        artistColumn.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Song, String>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<Song, String> event) {
                event.getTableView().getItems().get(event.getTablePosition().getRow()).setArtist(event.getNewValue());
                event.getTableView().refresh();
                song.setArtist(event.getNewValue());
            }
        });
        albumColumn.setCellValueFactory(new PropertyValueFactory<Song, String>("album"));
        albumColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        albumColumn.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Song, String>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<Song, String> event) {
                event.getTableView().getItems().get(event.getTablePosition().getRow()).setAlbum(event.getNewValue());
                event.getTableView().refresh();
                song.setAlbum(event.getNewValue());
            }
        });
        durationColumn.setCellValueFactory(new PropertyValueFactory<Song, String>("duration"));
        durationColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        durationColumn.editableProperty().setValue(false);
        /*durationColumn.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Song, String>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<Song, String> event) {
                ((Song) event.getTableView().getItems().get(event.getTablePosition().getRow())).setDuration(event.getNewValue());
                event.getTableView().refresh();
            }
        });*/


        TableView<Song> table = new TableView<>();
        table.getColumns().addAll(nameColumn,artistColumn,albumColumn,durationColumn);

        this.songs.add(new Song(song.getName(), song.getArtist(), song.getAlbum(), song.getDuration(), song.filePath));

        table.setItems(songs);
        table.setEditable(true);
        table.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        borderPane.setCenter(table);
        Button confirm = new Button("Confirm");
        confirm.setOnAction(event -> {
            Database.editSong(song.getName(),"placeholder path",song.getArtist(),song.getAlbum());
            this.songs.remove(0);
            //table.
            stage.hide();
        });
        stage.setOnCloseRequest(event -> {
            this.songs.remove(0);
        });
        borderPane.setBottom(confirm);
        Text text = new Text("Please edit any values you wish, then select confirm to save your changes.\nClick the X to cancel.");
        text.setBoundsType(TextBoundsType.LOGICAL);
        borderPane.setTop(text);
        //FXCollections.observableArrayList();
        /*Label name = new Label("Song Name");
        Label author = new Label("Song Artist");
        Label album = new Label("Song Album");
        Label duration = new Label("Song Duration");

        TextField nameText = new TextField();
        TextField authorText = new TextField();
        TextField albumText = new TextField();
        TextField durationText = new TextField();
*/
        //popup.getContent().add(0,name);
        /*popup.getContent().add(1,nameText);
        popup.getContent().add(2,author);
        popup.getContent().add(3,authorText);
        popup.getContent().add(4,album);
        popup.getContent().add(5,albumText);
        popup.getContent().add(6,duration);
        popup.getContent().add(7,durationText);
        */
        /*popup.getContent().add(table);
        popup.getContent().add(confirm);
        popup.show(getStage());
        */Scene scene = new Scene(borderPane, 500, 250);
        stage.setScene(scene);
        stage.show();

    }

    public void start(Stage stage) throws IOException {
        s = stage;
        Database.connect();
        BorderPane borderPane = new BorderPane();
        Song song = new Song("testsong", "testauthor", "testalbum", "15", "testPath");
        Button button = new Button("Test popup");
        button.setOnAction(event -> {
            try {
                ResultSet rs = Database.getSongInfo(song.getName());
                Song songa = new Song(rs.getString("name"), rs.getString("author"), rs.getString("album"), rs.getString("duration"), rs.getString("filepath"));
                editSong(songa);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
        Button button1 = new Button("Test dialog");
        button1.setOnAction(event -> {
            SQLError("[SQLITE_CONSTRAINT_FOREIGNKEY] A foreign key constraint failed (FOREIGN KEY constraint failed)");
        });
        borderPane.setTop(button1);
        borderPane.setCenter(button);
        Scene scene = new Scene(borderPane, 1440, 1028);

        //Font.loadFont(getClass().getResourceAsStream("fonts/RobotoMono-Regular.ttf"), 16);
        //scene.getStylesheets().add(getClass().getResource("MP3.css").toExternalForm());
        stage.setTitle("Test Parent Popup Window");
        stage.setScene(scene);
        stage.show();
        //Database.verifyAllSongs();
        stage.setOnCloseRequest((event) -> {
            Database.close();
        });

        Database.addNewSong("test_song_name", "", "", "", 0);
        Database.editSong("test_song_name", null, "", "");
        Database.removeSong("test_song_name");
    }

    public Stage getStage() {
        return s;
    }

    public static void main(String[] args) {
        launch();
    }
}
