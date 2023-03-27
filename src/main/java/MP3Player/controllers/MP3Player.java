package MP3Player.controllers;

import MP3Player.mp3Player.equalizer.Equalizer;
import MP3Player.mp3Player.visualizer.RectangleVisualizer;
import MP3Player.mp3Player.visualizer.XYChartVisualizer;
import MP3Player.util.general.TabHandler;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.chart.AreaChart;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import MP3Player.mp3Player.time.*;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import static java.lang.Math.floor;

public class MP3Player implements Initializable {

    private final String PATH_DEFAULT = System.getProperty("user.dir");
    private final String PATH_MVMT = PATH_DEFAULT+"/out/production/AnotherMp3Test/song/4th Mvmt.mp3";
    private final String PATH_MAMA = PATH_DEFAULT+"/out/production/AnotherMp3Test/song/MeAndYourMama.mp3";


    boolean audioPlaying = false;

    @FXML
    Label isTesting;
    @FXML
    protected Label timestamp;
    @FXML
    ImageView play_pause_btn_icon;
    @FXML
    protected
    Slider time_slider;
    @FXML
    Button prev_btn;
    @FXML
    Button next_btn;
    @FXML
    Button play_pause_btn;

    protected Media audio;
    protected MediaPlayer audioPlayer;
    protected TimeControl timeControl;

    /**
     * Listeners
     */
    protected ChangeListener<Duration> progressListener;
    protected AudioSpectrumListener VisualizeListener;
    protected ChangeListener<Number> slideListener;

    private final int BANDS = 1024;
    //private Duration songLength;


    @FXML
    protected void prev_audio_event() {
        System.out.println("prev_audio_event");
    }

    @FXML
    protected void next_audio_event() {
        System.out.println("next_audio_event");
    }

    @FXML
    public void play_pause_audio_event() {

        try {
            if (audioPlayer.getStatus().compareTo(MediaPlayer.Status.PLAYING)==0) {
                audioPlayer.pause();
                play_pause_btn_icon.setImage(new Image("image/play_button.png"));
                audioPlaying = false;
            } else {
                audioPlayer.play();
                play_pause_btn_icon.setImage(new Image("image/pause_button.png"));
                audioPlaying = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    protected void initializeAudioPlayer() {
        try {

            File file = new File(PATH_MAMA);
            audio = new Media(file.toURI().toString());
            audioPlayer = new MediaPlayer(audio);

            timestamp.setText("0:0");
            time_slider.setValue(0);

            timeControl = new TimeControl(audioPlayer);

            audioPlayer.currentTimeProperty().addListener(progressListener);
            time_slider.valueProperty().addListener(slideListener);

            audioPlayer.setOnEndOfMedia(() -> {
                //TODO: queue next song
            });
        } catch (Exception e) {
            System.out.println("Exception in initalizeAudioPlayer");
            e.printStackTrace();
        }
    }


    /**
     * helper function to determin if the player can recieve ui commands
     * @return
     */
    protected boolean isPlayerActive(){
        return (audioPlayer!=null&&audioPlayer.getStatus().compareTo(MediaPlayer.Status.UNKNOWN)!=0);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        prev_btn = new Button();
        next_btn = new Button();
        play_pause_btn = new Button();
        play_pause_btn_icon = new ImageView(new Image("image/pause_button.png"));
        initializeListeners();
    }

    protected void initializeListeners(){
        slideListener = (observableValue, oldDuration, newDuration) -> {
            if (time_slider.isPressed()) {
                audioPlayer.seek(Duration.seconds((time_slider.getValue() / 100) * (long) audioPlayer.getTotalDuration().toSeconds()));
            }};

        progressListener = (observableValue, oldDuration, newDuration) -> {
            if (newDuration != oldDuration) {
                //update timestamp
                timestamp.setText(String.format("%.0f:%02.0f", floor(newDuration.toMinutes()), floor(newDuration.toSeconds() % 60)));

                //move slider
                time_slider.setValue((newDuration.toSeconds() /audioPlayer.getTotalDuration().toSeconds()) * 100);
            }};

    }




}