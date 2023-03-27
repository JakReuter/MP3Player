import MP3Player.controllers.MP3Player;
import MP3Player.mp3Player.equalizer.Equalizer;
import MP3Player.mp3Player.time.TimeControl;
import MP3Player.mp3Player.visualizer.RectangleVisualizer;
import MP3Player.mp3Player.visualizer.XYChartVisualizer;
import MP3Player.util.general.TabHandler;
import javafx.fxml.FXML;
import javafx.scene.chart.AreaChart;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.FileChooser;

import java.io.File;

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

    //brendan window
    @FXML
    protected void LoadWindow(){
        audioPlayer.setAudioSpectrumInterval(.05);
        audioPlayer.setAudioSpectrumThreshold(-100);
        audioPlayer.setAudioSpectrumNumBands(1024);
        Equalizer newEQ = new Equalizer(audioPlayer.getAudioEqualizer());
        chart = new RectangleVisualizer(32);
        areaChart = new XYChartVisualizer("chart", audioPlayer.getAudioSpectrumNumBands(), -100);
        newEQ.setListener(areaChart.getEQListener());
        TabHandler EQtabHandler = new TabHandler();
        TabHandler VIStabHandler = new TabHandler();
        EQtabHandler.addApp(newEQ, chart);
        VIStabHandler.addApp(areaChart);
        visDispPane.getChildren().add(VIStabHandler.getPane());
        eqDispPane.getChildren().add(EQtabHandler.getPane());
        audioPlayer.setAudioSpectrumListener((double timestamp, double duration, float[] magnitudes, float[] phases) -> {


            for(int i = 0; i< audioPlayer.getAudioSpectrumNumBands(); i++) {
                //magArray[i].setYValue((magnitudes[i]+120)/5); javaFX movde
                //visualizerRectangle.getChildren().get(i).setScaleY((magnitudes[i] + 120) / 60);

                //System.out.println("her");
                areaChart.update(i, magnitudes[i]);

            }
        });
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
