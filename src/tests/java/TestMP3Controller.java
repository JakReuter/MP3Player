import MP3Player.controllers.MP3Player;
import MP3Player.mp3Player.equalizer.Equalizer;
import MP3Player.mp3Player.time.TimeControl;
import MP3Player.mp3Player.visualizer.ChartVisualizer;
import MP3Player.mp3Player.visualizer.RectangleVisualizer;
import MP3Player.mp3Player.visualizer.XYChartVisualizer;
import MP3Player.mp3Player.visualizer.core.Series;
import MP3Player.util.general.TabHandler;
import javafx.fxml.FXML;
import javafx.scene.chart.AreaChart;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class TestMP3Controller extends MP3Player {


    //Containers for EQ/Visualization setup
    @FXML
    private AnchorPane visDispPane;
    @FXML
    private AnchorPane eqDispPane;
    @FXML
    private AreaChart visualizerChart;
    public AnchorPane added;
    private RectangleVisualizer chart;
    private XYChartVisualizer areaChart;
    ChartVisualizer chartVisualizer;
    private Series series;
    private final int BANDS = 256;

    public boolean stamp;

    //brendan window
    @FXML
    protected void LoadWindow(){
        stamp = false;
        audioPlayer.setAudioSpectrumInterval(.1);
        audioPlayer.setAudioSpectrumThreshold(-100);
        audioPlayer.setAudioSpectrumNumBands(BANDS);
        Equalizer newEQ = new Equalizer(audioPlayer.getAudioEqualizer());
        chart = new RectangleVisualizer(32);
        areaChart = new XYChartVisualizer("chart", audioPlayer.getAudioSpectrumNumBands(), -100);
        newEQ.setListener(areaChart.getEQListener());
        TabHandler EQtabHandler = new TabHandler();
        TabHandler VIStabHandler = new TabHandler();
        chartVisualizer = new ChartVisualizer(BANDS, timestamp.getScene().getWindow());
        EQtabHandler.addApp(newEQ, chart);
        series = chartVisualizer.getSeries();
        VIStabHandler.addApp(chartVisualizer, areaChart);
        Window thisWindow = eqDispPane.getScene().getWindow();
        visDispPane.getChildren().add(VIStabHandler.getPane());
        eqDispPane.getChildren().add(EQtabHandler.getPane());
        audioPlayer.setAudioSpectrumListener((double timestamp, double duration, float[] magnitudes, float[] phases) -> {




                for (int i = 0; i < audioPlayer.getAudioSpectrumNumBands(); i++) {
                    //magArray[i].setYValue((magnitudes[i]+120)/5); javaFX movde
                    //visualizerRectangle.getChildren().get(i).setScaleY((magnitudes[i] + 120) / 60);

                    //System.out.println("her");
                    //if (areaChart.isFocused()) {
                        //areaChart.update(i, magnitudes[i]);
                        series.change(i, magnitudes[i]);
                    //}
                    //writer.append(i+","+(magnitudes[i]+100)+"\n");
                    //if (i % 8 == 0) {
                        //chartVisualizer.update(i, magnitudes[i]);
                   // }
                }

        });

    }
    @Override
    @FXML
    public void play_pause_audio_event() {

        try {
            if (audioPlayer.getStatus().compareTo(MediaPlayer.Status.PLAYING)==0) {
                audioPlayer.pause();
                play_pause_btn_icon.setImage(new Image("image/play_button.png"));
                System.out.println("trued");
                stamp = true;
            } else {
                audioPlayer.play();
                play_pause_btn_icon.setImage(new Image("image/pause_button.png"));
                stamp = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @FXML
    protected void debugSongLoader(){
        FileChooser fileChooser = new FileChooser();
        //timestamp.setText("0:0");
        File file = fileChooser.showOpenDialog(timestamp.getScene().getWindow());
        System.out.println(file.toURI().toString());
        audio = new Media(file.toURI().toString());
        audioPlayer = new MediaPlayer(audio);
        audioPlayer.currentTimeProperty().addListener(progressListener);
        time_slider.valueProperty().addListener(slideListener); //update audioPlayer if time_slider is updated

    }

}
