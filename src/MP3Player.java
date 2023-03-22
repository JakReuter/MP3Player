

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.media.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import ext.*;

import java.io.File;
import java.io.FileWriter;

public class MP3Player {
    public AnchorPane added;
    private ChartVisualizer chart;
    @FXML
    private Label testLabel;
    @FXML
    private Label welcomeText;
    @FXML
    private Slider progress;
    @FXML
    private HBox visualizerRectangle;
    @FXML
    private AreaChart visualizerChart;
    @FXML
    private LogAxis X_AXIS;

    private String path = "C:\\Users\\breut\\Music\\sPOOKY.mp3";
    private Media song;
    private MediaPlayer player;
    private FileChooser fileChooser;
    private AudioSpectrumListener VisualizeListener;
    private ChangeListener<Number> slideListener;
    private final int BANDS = 1024;
    private final String X_UNIT = "";
    private boolean testBool;
    private Duration songLength;


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

            songLength=song.getDuration();

            progress.setMin(0);
            progress.setMax(song.getDuration().toSeconds());

            player.setAudioSpectrumNumBands(BANDS);
            player.setAudioSpectrumThreshold(-120);
            player.setAudioSpectrumInterval(.05);
            System.out.println(player.getAudioSpectrumThreshold());
            player.setAudioSpectrumListener(VisualizeListener);
            player.play();
            welcomeText.setText("Playing!");
        }
    }

    @FXML
    protected void pausePlaySong(){
        if(isPlayerActive()){
            if(player.getStatus().compareTo(MediaPlayer.Status.PLAYING)==0){
                player.pause();
            } else {
                player.play();
            }


        }
    }

    @FXML
    protected void LoadWindow(){
        Equalizer newEQ = new Equalizer(player.getAudioEqualizer());
        chart = new ChartVisualizer(32);
        TabHandler tabHandler = new TabHandler();
        tabHandler.addApp(newEQ, chart);
        tabHandler.Display();
    }

    protected void loadSong(){
        if(player==null) {
            try {
                fileChooser = new FileChooser();
                File file = fileChooser.showOpenDialog(welcomeText.getScene().getWindow());

                song = new Media(file.toURI().toString());
                player = new MediaPlayer(song);
                player.currentTimeProperty().addListener(progressListener);
                testLabel.setText("testText");
                progress.valueProperty().addListener(slideListener);


                //player = createPlayer(song);
            } catch (Exception e) {
                System.out.println("didnt work!");
                e.printStackTrace();
            }

        } else {

            System.out.println("Is enabled: " +player.getAudioEqualizer().isEnabled());
            System.out.println("0: " +player.getAudioEqualizer().getBands().get(0).getGain());
        }
    }

    protected MediaPlayer createPlayer(Media song){
        MediaPlayer newPlayer = new MediaPlayer(song);
        newPlayer.currentTimeProperty().addListener(progressListener);

        //add media player to mediaviewer?
        return newPlayer;
    }

    /**
     * helper function to determin if the player can recieve ui commands
     * @return
     */
    protected boolean isPlayerActive(){
        return (player!=null&&player.getStatus().compareTo(MediaPlayer.Status.UNKNOWN)!=0);
    }




    @FXML
    public void initialize() {

        /**
         * Adjusts mediaplayer position with user-slider input
         */
        slideListener =
                (ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
                    if(player!=null&&player.getStatus()!=MediaPlayer.Status.UNKNOWN)
                    if (progress.isPressed()) {
                        System.out.println("Old value: "+oldValue+" new value: "+newValue);
                        long dur = newValue.intValue() * 1000;

                        player.seek(new Duration(dur));
                    }
                };
        /**
         * Adjusts Progress bar value with mediaplayer position
         */
        progressListener =
                (ObservableValue<? extends Duration> observable, Duration oldValue, Duration newValue) -> {

                    progress.setValue(newValue.toSeconds());
                    welcomeText.setText(String.format("%2.0f:%02.0f / %2.0f:%02.0f",newValue.toMinutes(),newValue.toSeconds()%60, songLength.toMinutes(), songLength.toSeconds()%60));
                };

        /**
         * JavaFX
         */
        XYChart.Series<Number, Number> amplitudes = new XYChart.Series<>();
        XYChart.Data[] magArray = new XYChart.Data[BANDS];
        for(int i = 0; i< magArray.length; i++){
            magArray[i] = new XYChart.Data<>(Math.log10(i+1), 0f);
            amplitudes.getData().add(magArray[i]);
        }
        //visualizerChart = new AreaChart<Number, Number>(new LogAxis(1, BANDS), visualizerChart.getYAxis());
        visualizerChart.setAnimated(false);
        visualizerChart.getData().add(amplitudes);
        visualizerChart.setCreateSymbols(false);

        //chart.setRectangles1(added);
        VisualizeListener =
                    (double timestamp, double duration, float[] magnitudes, float[] phases) -> {


                            for(int i = 0; i< player.getAudioSpectrumNumBands(); i++) {
                                magArray[i].setYValue((magnitudes[i]+120)/5);
                                //visualizerRectangle.getChildren().get(i).setScaleY((magnitudes[i] + 120) / 60);
                                if(chart!=null) {
                                    chart.update(i, magnitudes[i]);
                                }
                            }
                        };

                }
    }

