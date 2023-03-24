import javafx.fxml.FXML;
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
import java.util.ArrayList;
import java.util.ResourceBundle;

import static java.lang.Math.floor;


public class MP3Player implements Initializable {
    //Initalizer

    //Media control
    @FXML
    Button play_pause_btn;
    @FXML
    ImageView play_pause_btn_icon;

    boolean audioPlaying = false;
    ArrayList<File> queue = new ArrayList<File>(){
        {
            add(new File("C:\\Users\\aidan\\IdeaProjects\\MP3Player\\src\\resources\\MeAndYourMama.mp3"));
            add(new File("C:\\Users\\aidan\\IdeaProjects\\MP3Player\\src\\resources\\Soul smile.mp3"));
        }
    };

    int queueNumber = 1;

    @FXML
    Button prev_btn;

    @FXML
    Button next_btn;


    //Media Player
    Media audio;
    MediaPlayer audioPlayer;

    @FXML
    Label timestamp;
    @FXML
    Slider time_slider;


    //Metadata




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
                audio = new Media(queue.get(queueNumber).toURI().toString());
                audioPlayer = new MediaPlayer(audio);

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
        audio = new Media(queue.get(queueNumber).toURI().toString());
        audioPlayer = new MediaPlayer(audio);

        //keep playing status
        if(audioPlaying)
            audioPlayer.play();
        System.out.println("next_audio_event");


    }

    @FXML
    public void play_pause_audio_event() {
        try {
            if (audioPlaying) {
                audioPlayer.pause();
                //TODO: switch icon to pause when playing audio
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
            File file = queue.get(queueNumber);
            audio = new Media(file.toURI().toString());
            audioPlayer = new MediaPlayer(audio);
            time_slider.setValue(0);
            timestamp.setText("0:0");

            setMetaData(file);
//            updateMetadata(audio);
            System.out.println(audio.getMetadata().toString());
            audioPlayer.setOnReady(() ->{

            });

            //progress listener
            audioPlayer.currentTimeProperty().addListener((observableValue, oldDuration, newDuration) -> {
                double total = audioPlayer.getTotalDuration().toMillis();
                if (newDuration != oldDuration) {
                    //update timestamp
                    timestamp.setText(String.format("%.0f:%02.0f", floor(newDuration.toMinutes()), floor(newDuration.toSeconds() % 60)));

                    //move slider
                    time_slider.setValue((newDuration.toSeconds() /audioPlayer.getTotalDuration().toSeconds()) * 100);
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



    private void setMetaData(File file){
        //TODO: get metadata from files or media
        //get file extension
        String extension = "";

        int i = file.getName().lastIndexOf('.');
        if(i > 0)
            extension = file.getName().substring(i + 1);

        //
        switch (extension){
            case "mp3":

                break;

            case "flack":
                break;
        }
    }



}