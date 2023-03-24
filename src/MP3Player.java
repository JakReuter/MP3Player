import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

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
    protected void prev_audio_event(){
        System.out.println("prev_audio_event");
    }

    @FXML
    protected void next_audio_event(){
        System.out.println("next_audio_event");
    }

    @FXML
    public void play_pause_audio_event(){
        try {
            if(audioPlaying){
                audioPlayer.pause();
                play_pause_btn_icon.setImage(new Image("resources/pause_button.png"));
                audioPlaying = false;
            }else{
                audioPlayer.play();
                play_pause_btn_icon.setImage(new Image("resources/play_button.png"));
                audioPlaying = true;
            }
        }catch (Exception e){
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


    private void initalizeAudioPlayer(){
        try{
            File file = new File("C:\\Users\\aidan\\IdeaProjects\\MP3Player\\src\\resources\\MeAndYourMama.mp3");
            audio = new Media(file.toURI().toString());
            audioPlayer = new MediaPlayer(audio);
        }catch (Exception e){
            System.out.println("Exception in initalizeAudioPlayer");
            e.printStackTrace();
        }
    }
}