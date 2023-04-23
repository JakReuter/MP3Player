package MP3Player.application;

import MP3Player.controllers.MP3Player;
import MP3Player.database.Database;
import MP3Player.util.general.FileErrorHandler;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

public class MP3Application extends Application {
    //some local variables to be passed into get methods
    private Stage s = null;
    private MediaPlayer p = null;
    MP3Player mp3Player = null;
    Alert alert = null;

    @Override
    public void start(Stage stage) throws IOException {
        s = stage;
        alert = new Alert(Alert.AlertType.ERROR);

        Database.connect();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/MainView.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1440, 1028);

        //get audiop player from controller class
        mp3Player =  (MP3Player) fxmlLoader.getController();
        p = mp3Player.getAudioPlayer();

        //Font.loadFont(getClass().getResourceAsStream("fonts/RobotoMono-Regular.ttf"), 16);
        //scene.getStylesheets().add(getClass().getResource("MP3.css").toExternalForm());
        stage.setTitle("Mp3 Player");
        stage.setScene(scene);
        stage.show();
        Database.verifyAllSongs();
        stage.setOnCloseRequest((event) -> {
            Database.close();
        });
    }

    public Stage getStage()
    {
        return s;
    }

    public MediaPlayer getMediaPlayer()
    {
        return p;
    }

    public void setMediaPlayer(String filePath)
    {
        mp3Player.setAudioPlayer(filePath);
    }

    public Alert getAlert()
    {
        return alert;
    }

    public static void main(String[] args) {
        launch();
    }
}