import javafx.beans.InvalidationListener;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import static java.lang.Math.floor;

public class MP3Player implements Initializable {
    @FXML
    Button play_pause_btn;
    @FXML
    ImageView play_pause_btn_icon;

    boolean audioPlaying = false;

    @FXML
    Button prev_btn;

    @FXML
    Button next_btn;

    Media audio;
    MediaPlayer audioPlayer;

    @FXML
    Label timestamp;
    @FXML
    Slider time_slider;
    private ChangeListener<Number> progressListener;

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
            if (audioPlaying) {
                audioPlayer.pause();
                play_pause_btn_icon.setImage(new Image("resources/play_button.png"));
                audioPlaying = false;
            } else {
                audioPlayer.play();
                play_pause_btn_icon.setImage(new Image("resources/pause_button.png"));
                audioPlaying = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        prev_btn = new Button();
        next_btn = new Button();
        play_pause_btn = new Button();
        play_pause_btn_icon = new ImageView(new Image("resources/pause_button.png"));
        initalizeAudioPlayer();

    }


    private void initalizeAudioPlayer() {
        try {
            File file = new File("C:\\Users\\aidan\\IdeaProjects\\MP3Player\\src\\resources\\MeAndYourMama.mp3");
            audio = new Media(file.toURI().toString());
            audioPlayer = new MediaPlayer(audio);
            time_slider.setValue(0);
            timestamp.setText("0:0");


            //progress listener
            audioPlayer.currentTimeProperty().addListener((observableValue, oldDuration, newDuration) -> {
                double total = audioPlayer.getTotalDuration().toMillis();
                if (newDuration != oldDuration) {
                    //update timestamp
                    timestamp.setText(String.format("%.0f:%02.0f", floor(newDuration.toMinutes()), floor(newDuration.toSeconds() % 60)));

                    //move slider
                    time_slider.setValue((newDuration.toSeconds() /audioPlayer.getTotalDuration().toSeconds()) * 100);
                    System.out.println((newDuration.toSeconds() /audioPlayer.getTotalDuration().toSeconds()) * 100);
                }
            });

            //update audioPlayer if time_slider is updated
            time_slider.valueProperty().addListener((observableValue, oldDuration, newDuration) -> {
                if (time_slider.isPressed()) {
                    audioPlayer.seek(Duration.seconds((time_slider.getValue() / 100) * (long) audioPlayer.getTotalDuration().toSeconds()));
                }
            });

            audioPlayer.setOnEndOfMedia(() -> {
                //TODO: queue next song
            });

        } catch (Exception e) {
            System.out.println("Exception in initalizeAudioPlayer");
            e.printStackTrace();
        }

    }



}