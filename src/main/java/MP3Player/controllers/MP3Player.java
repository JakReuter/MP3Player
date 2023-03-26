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
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.*;
import javafx.stage.FileChooser;
import javafx.util.Duration;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import static java.lang.Math.floor;

public class MP3Player implements Initializable {

    private final String PATH_DEFAULT = System.getProperty("user.dir");
    private final String PATH_MVMT = "/song/4th Mvmt.mp3";
    private final String PATH_MAMA = "\\song\\meAndUrMama.mp3";


    boolean audioPlaying = false;

    @FXML
    Label isTesting;
    @FXML
    Label timestamp;
    @FXML
    ImageView play_pause_btn_icon;
    @FXML
    Slider time_slider;
    @FXML
    Button prev_btn;
    @FXML
    Button next_btn;
    @FXML
    Button play_pause_btn;
    @FXML
    private AnchorPane visDispPane;
    @FXML
    private AreaChart visualizerChart;

    public AnchorPane added;
    private RectangleVisualizer chart;
    private XYChartVisualizer areaChart;

    Media audio;
    MediaPlayer audioPlayer;


    /**
     * Listeners
     */
    private ChangeListener<Duration> progressListener;
    private AudioSpectrumListener VisualizeListener;
    private ChangeListener<Number> slideListener;


    private FileChooser fileChooser;
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

    /**
     * The following code is necessary for displaying accurate specturm
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

    //brendan window
    @FXML
    protected void LoadWindow(){
        audioPlayer.setAudioSpectrumThreshold(-100);
        Equalizer newEQ = new Equalizer(audioPlayer.getAudioEqualizer());
        chart = new RectangleVisualizer(32);
        areaChart = new XYChartVisualizer("chart", audioPlayer.getAudioSpectrumNumBands());
        newEQ.setListener(areaChart.getEQListener());
        TabHandler tabHandler = new TabHandler();
        tabHandler.addApp(newEQ, areaChart, chart);
        visDispPane.getChildren().add(tabHandler.getPane());
        audioPlayer.setAudioSpectrumListener((double timestamp, double duration, float[] magnitudes, float[] phases) -> {


            for(int i = 0; i< audioPlayer.getAudioSpectrumNumBands(); i++) {
                //magArray[i].setYValue((magnitudes[i]+120)/5); javaFX movde
                //visualizerRectangle.getChildren().get(i).setScaleY((magnitudes[i] + 120) / 60);

                    //System.out.println("her");
                    areaChart.update(i, magnitudes[i]);

            }
        });
    }

    protected void initializeAudioPlayer(){
        try {

            fileChooser = new FileChooser();
            timestamp.setText("0:0");
            //File file = fileChooser.showOpenDialog(prev_btn.getScene().getWindow()); null pointer here, i give up.
            File file = new File(getClass().getClassLoader().getResource("song/4th Mvmt.mp3").getPath());
            audio = new Media(file.toURI().toString());
            audioPlayer = new MediaPlayer(audio);
            time_slider.setValue(0);

            //progress listener
            /**All of intializeAudioPlayer will run whenever a new Media Player is needed
             * a new mediaplayer might be required with each different song we play
             * might want to create listeners as global variables? **/
            audioPlayer.currentTimeProperty().addListener(progressListener);
            time_slider.valueProperty().addListener(slideListener); //update audioPlayer if time_slider is updated

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
                            if(areaChart!=null) {
                                System.out.println("her");
                                areaChart.update(i, magnitudes[i]);
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

        @Override
        public void initialize(URL url, ResourceBundle resourceBundle) {

            prev_btn = new Button();
            next_btn = new Button();
            play_pause_btn = new Button();
            play_pause_btn_icon = new ImageView(new Image("image/pause_button.png"));
            initializeListeners();
            //Will not run given the Testing Condition
            if(isTesting==null) {
                initializeAudioPlayer();
            } else {

                System.out.println("Testing layout...");
            }
    }

    protected void initializeListeners(){
        slideListener = (observableValue, oldDuration, newDuration) -> {
            if (time_slider.isPressed()) {
                audioPlayer.seek(Duration.seconds((time_slider.getValue() / 100) * (long) audioPlayer.getTotalDuration().toSeconds()));
            }};

        progressListener = (observableValue, oldDuration, newDuration) -> {
            if (newDuration != oldDuration) { /**Would newDuration ever = oldDuration if we are listening for update?**/
                //update timestamp
                timestamp.setText(String.format("%.0f:%02.0f", floor(newDuration.toMinutes()), floor(newDuration.toSeconds() % 60)));

                //move slider
                /**Note: slider max value can be set to song duration, removing the math between slider and player.
                 * Total Slider Duration might also give wrong number if repeated? not sure what cycles means**/
                time_slider.setValue((newDuration.toSeconds() /audioPlayer.getTotalDuration().toSeconds()) * 100);
            }};

    }

    @FXML
    protected void debugSongLoader(){
        fileChooser = new FileChooser();
        //timestamp.setText("0:0");
        File file = fileChooser.showOpenDialog(timestamp.getScene().getWindow());
        audio = new Media(file.toURI().toString());
        audioPlayer = new MediaPlayer(audio);
        audioPlayer.currentTimeProperty().addListener(progressListener);
        time_slider.valueProperty().addListener(slideListener); //update audioPlayer if time_slider is updated

    }

}