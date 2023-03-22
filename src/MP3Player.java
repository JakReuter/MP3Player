import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.media.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;

public class MP3Player {
    @FXML
    private Label test;
    @FXML
    private Label welcomeText;
    @FXML
    private Slider progress = createProgress();
    private String path = "C:\\Users\\breut\\Music\\sPOOKY.mp3";
    private Media song;
    private MediaPlayer player;
    private FileChooser fileChooser;

    private javafx.beans.value.ChangeListener<Duration> progressListener;
    private Stage stage;

    public void passStage(Stage passed){
        stage = passed;
    }

    @FXML
    protected void onHelloButtonClick() {
        loadSong();
        welcomeText.setText("Song loaded!");
    }
    @FXML
    protected void onHelloPlayClick() {
        if(player==null){
            welcomeText.setText("Not loaded");
        } else {
            player.play();
            welcomeText.setText("Playing!");
        }
    }

    protected void loadSong(){
        try {
            fileChooser = new FileChooser();
            File file = fileChooser.showOpenDialog(welcomeText.getScene().getWindow());

            song = new Media(file.toURI().toString());
            player = new MediaPlayer(song);
            player.currentTimeProperty().addListener(progressListener);
            //player = createPlayer(song);
        } catch (Exception e){
            System.out.println("didnt work!");
            e.printStackTrace();
        }
    }

    protected MediaPlayer createPlayer(Media song){
        MediaPlayer newPlayer = new MediaPlayer(song);
        newPlayer.currentTimeProperty().addListener(progressListener);
        //add media player to mediaviewer?
        return newPlayer;
    }

    protected Slider createProgress(){

        Slider newSlide = new Slider();
        ChangeListener<Number> slideListener =
                (ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
                    if (newSlide.isPressed()) {
                        long dur = newValue.intValue() * 1000;

                        player.seek(new Duration(dur));
                    }
                };

        newSlide.setMin(0);
        newSlide.setMax(100);
        newSlide.setValue(50);

        newSlide.valueProperty().addListener(slideListener);

        return newSlide;
    }

    @FXML
    public void initialize() {
        progressListener =
                (ObservableValue<? extends Duration> observable, Duration oldValue, Duration newValue) -> {
                    progress.setValue(newValue.toSeconds());
                    welcomeText.setText(newValue.toSeconds()+"s");
                };
    }

}