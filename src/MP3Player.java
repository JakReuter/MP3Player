

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.scene.media.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;

public class MP3Player {
    @FXML
    private Label testLabel;
    @FXML
    private Label welcomeText;
    @FXML
    private Slider progress = createProgress();
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
    private final int BANDS = 128;
    private final String X_UNIT = "";


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
            player.setAudioSpectrumNumBands(BANDS);
            player.setAudioSpectrumThreshold(-100);
            player.setAudioSpectrumInterval(0.05);
            System.out.println(player.getAudioSpectrumThreshold());
            player.setAudioSpectrumListener(VisualizeListener);
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
            testLabel.setText("Test Value");
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
        XYChart.Series<Number, Number> amplitudes = new XYChart.Series<>();
        XYChart.Data[] magArray = new XYChart.Data[BANDS];
        for(int i = 0; i< magArray.length; i++){
            magArray[i] = new XYChart.Data<>((i+1), 0f);
            amplitudes.getData().add(magArray[i]);
        }
        //visualizerChart = new AreaChart<Number, Number>(new LogAxis(1, BANDS), visualizerChart.getYAxis());
        //visualizerChart.setAnimated(false);
        visualizerChart.getData().add(amplitudes);
        VisualizeListener =
                (double timestamp, double duration, float[] magnitudes, float[] phases) -> {
                    //System.out.println(phases[1]);
                    //System.out.println("Timestamp: "+timestamp+"\nDuration: "+duration+"\nMagnitudes length: "+magnitudes.length+"\nphases length: "+phases.length);
                    for(int i = 0; i< player.getAudioSpectrumNumBands(); i++) {
                        //visualizerRectangle.getChildren().get(i).setScaleY((magnitudes[i] + 120) / 60);

                        magArray[i].setYValue(magnitudes[i]);

                    }
                        };

                }
    }

