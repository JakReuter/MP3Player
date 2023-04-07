import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.media.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import ext.*;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import static java.lang.Math.floor;


public class MP3Player implements Initializable {

    private final String PATH_DEFAULT = System.getProperty("user.dir");
//    private final String PATH_MVMT = PATH_DEFAULT+"/src/resources\\4th Mvmt.mp3";
    private final String PATH_MAMA = PATH_DEFAULT+"/src/resources/song/MeAndYourMama.mp3";

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

    public AnchorPane added;
    private ChartVisualizer chart;
    @FXML
    Button next_btn;


    //Media Player
    Media audio;
    MediaPlayer audioPlayer;

    @FXML
    Label timestamp;
    @FXML
    Slider time_slider;

    @FXML
    private HBox visualizerRectangle;
    @FXML
    private AreaChart visualizerChart;
    @FXML
    private LogAxis X_AXIS;

    //private Media song;
    //private MediaPlayer player;
    private FileChooser fileChooser;
    private AudioSpectrumListener VisualizeListener;
    //private ChangeListener<Number> slideListener;
    private final int BANDS = 1024;
    //private final String X_UNIT = "";
    //private boolean testBool;
    //private Duration songLength;


    //private javafx.beans.value.ChangeListener<Duration> progressListener;
    TimeControl timeControl;

    @FXML
    protected void prev_audio_event() {
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
        System.out.println("next_audio_event");


    }

    /**
     *  @FXML
     *     protected void onHelloPlayClick() {
     *
     *         if(player==null){
     *             welcomeText.setText("Not loaded");
     *         } else {
     *
     *             songLength=song.getDuration();
     *
     *             progress.setMin(0);
     *             progress.setMax(song.getDuration().toSeconds());
     *
     *             player.setAudioSpectrumNumBands(BANDS);
     *             player.setAudioSpectrumThreshold(-120);
     *             player.setAudioSpectrumInterval(.05);
     *             System.out.println(player.getAudioSpectrumThreshold());
     *             player.setAudioSpectrumListener(VisualizeListener);
     *             player.play();
     *             welcomeText.setText("Playing!");
     *         }
     *     }
     */
    @FXML
    public void play_pause_audio_event() {
        try {
            if (audioPlayer.getStatus().compareTo(MediaPlayer.Status.PLAYING)==0) {
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

    //brendan window
    @FXML
    protected void LoadWindow(){
        Equalizer newEQ = new Equalizer(audioPlayer.getAudioEqualizer());
        chart = new ChartVisualizer(32);
        TabHandler tabHandler = new TabHandler();
        tabHandler.addApp(newEQ, chart);
        tabHandler.Display();
    }

    protected void initializeAudioPlayer(){
        try {

    private void initalizeAudioPlayer() {
        try {
            File file = new File("C:\\Users\\aidan\\IdeaProjects\\MP3Player\\src\\resources\\MeAndYourMama.mp3");
            audio = new Media(file.toURI().toString());
            audioPlayer = new MediaPlayer(audio);
            time_slider.setValue(0);
            timeControl = new TimeControl(audioPlayer);


            //progress listener
            /**All of intializeAudioPlayer will run whenever a new Media Player is needed
             * a new mediaplayer might be required with each different song we play
             * might want to create listeners as global variables? **/
            audioPlayer.currentTimeProperty().addListener((observableValue, oldDuration, newDuration) -> {
                double total = audioPlayer.getTotalDuration().toMillis();
                if (newDuration != oldDuration) { /**Would newDuration ever = oldDuration if we are listening for update?**/
                    //update timestamp
                    timestamp.setText(String.format("%.0f:%02.0f", floor(newDuration.toMinutes()), floor(newDuration.toSeconds() % 60)));

                    //move slider
                    /**Note: slider max value can be set to song duration, removing the math between slider and player.
                     * Total Slider Duration might also give wrong number if repeated? not sure what cycles means**/
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

            /**
             * Not so epic javafx AREA CHART segment n rectangle
             */
            //XYChart.Series<Number, Number> amplitudes = new XYChart.Series<>();
            //XYChart.Data[] magArray = new XYChart.Data[BANDS];
            //for(int i = 0; i< magArray.length; i++){
            //    magArray[i] = new XYChart.Data<>(Math.log10(i+1), 0f);
            //    amplitudes.getData().add(magArray[i]);
            //}
            //visualizerChart = new AreaChart<Number, Number>(new LogAxis(1, BANDS), visualizerChart.getYAxis());
            //visualizerChart.setAnimated(false);
            //visualizerChart.getData().add(amplitudes);
            //visualizerChart.setCreateSymbols(false);

            //chart.setRectangles1(added);
            VisualizeListener =
                    (double timestamp, double duration, float[] magnitudes, float[] phases) -> {


                        for(int i = 0; i< audioPlayer.getAudioSpectrumNumBands(); i++) {
                            //magArray[i].setYValue((magnitudes[i]+120)/5); javaFX movde
                            //visualizerRectangle.getChildren().get(i).setScaleY((magnitudes[i] + 120) / 60);
                            if(chart!=null) {
                                chart.update(i, magnitudes[i]);
                            }
                        }
                    };

        }



    protected MediaPlayer createPlayer(Media song){
        MediaPlayer newPlayer = new MediaPlayer(song);
        //newPlayer.currentTimeProperty().addListener(progressListener);

        //add media player to mediaviewer?
        return newPlayer;
    }

    /**
     * helper function to determin if the player can recieve ui commands
     * @return
     */
    protected boolean isPlayerActive(){
        return (audioPlayer!=null&&audioPlayer.getStatus().compareTo(MediaPlayer.Status.UNKNOWN)!=0);
    }


}