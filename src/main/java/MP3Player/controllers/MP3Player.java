package MP3Player.controllers;

import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.*;
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
    @FXML ImageView play_pause_btn_icon;



    @FXML
    protected
    Slider time_slider;
    @FXML Button prev_btn;
    @FXML Button next_btn;
    @FXML Button play_pause_btn;
    Image playBtn = new Image("\\img\\play_button.png");
//    Image playBtn = new Image(getClass().getResourceAsStream("play_button/png"));
    Image pauseBtn = new Image("\\img\\pause_button.png");
//Image pauseBtn = new Image(getClass().getResourceAsStream("pause_button/png"));


    protected Media audio;
    protected MediaPlayer audioPlayer;
    protected TimeControl timeControl;

    ArrayList<File> queue = new ArrayList<File>(){
        {
            add(new File(PATH_MAMA));
            add(new File(PATH_MVMT));
        }
    };

    int queueNumber = 1;


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
        //TODO: slider dragger dissapears (bug)
        System.out.println("prev_audio_event");
        try{
            //go to beginning of song if more than 5 seconds in
            if(audioPlayer.getCurrentTime().toSeconds() > 5 || 0 == queueNumber){
                audioPlayer.seek(Duration.millis(0));
            }else if (audioPlayer.getCurrentTime().toSeconds() <= 5 && queueNumber > 0){
                queueNumber--;
                audioPlayer.stop();
                initializeAudioPlayer();


                //keep playing status
                if(audioPlaying)
                    audioPlayer.play();
            }
        }catch (NullPointerException e){
            e.printStackTrace();
        }
    }

    @FXML
    protected void next_audio_event() {
        //TODO: slider dragger dissapears (bug)

        //Go to next song
        if(queueNumber < queue.size() - 1){
            queueNumber++;
            //go to first song if at the end of the queue
        }else{
            queueNumber = 0;
        }

        audioPlayer.stop();
        initializeAudioPlayer();
//        audio = new Media(queue.get(queueNumber).toURI().toString());
//        audioPlayer = new MediaPlayer(audio);
//        initializeListeners();

        //keep playing status
        if(audioPlaying)
            audioPlayer.play();
        System.out.println("next_audio_event");


    }


    @FXML
    public void play_pause_audio_event() {

        try {
            if (audioPlayer.getStatus().compareTo(MediaPlayer.Status.PLAYING)==0) {
                audioPlayer.pause();

                System.out.println(PATH_DEFAULT + "\\src\\main\\resources\\image\\pause_button.png");
//                Image playBtn = new Image(getClass().getResourceAsStream(PATH_DEFAULT + "\\src\\main\\resources\\image\\pause_button.png"));
                play_pause_btn_icon.setImage(pauseBtn);
                audioPlaying = false;
            } else {
                audioPlayer.play();
//                System.out.println(PATH_DEFAULT + "\\src\\main\\resources\\image\\play_button.png");

                play_pause_btn_icon.setImage(playBtn);
                System.out.println(play_pause_btn_icon.getImage().toString());
                audioPlaying = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    protected void initializeAudioPlayer() {
        try {

            File file = queue.get(queueNumber);
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
//        play_pause_btn = new Button();
//        play_pause_btn_icon = new ImageView(new Image("img/pause_button.png"));
        //THIS STUPID LINE OF CODE COST ME 2 HOURS OF HAIR PULLING
        //I COULDN'T FIGURE OUT WHY THE IMAGE WOULDN'T CHANGE
        initializeListeners();
        initializeAudioPlayer();
    }

    protected void initializeListeners(){


//        audioPlayerValue.bind(Duration.seconds((timeSliderValue.doubleValue() / 100) * (long) audioPlayer.getTotalDuration().toSeconds());

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