import MP3Player.controllers.MP3Player;
import MP3Player.mp3Player.equalizer.Equalizer;
import MP3Player.mp3Player.time.TimeControl;
import MP3Player.mp3Player.visualizer.ChartVisualizer;
import MP3Player.mp3Player.visualizer.RectangleVisualizer;
import MP3Player.mp3Player.visualizer.XYChartVisualizer;
import MP3Player.mp3Player.visualizer.core.Series;
import MP3Player.util.general.TabHandler;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.chart.AreaChart;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import visualizer.core.Visualizer3D;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class TestMP3Controller extends MP3Player {

    @FXML private Slider xSlider;
    @FXML private Slider ySlider;
    @FXML private Slider zSlider;
    @FXML private Slider fSlide;
    //Containers for EQ/Visualization setup
    @FXML
    private AnchorPane visDispPane;
    ChartVisualizer chartVisualizer;
    private Series series;
    private final int BANDS = 256;

    private Visualizer3D test3d;

    public boolean stamp;

    //brendan window
    @FXML
    protected void LoadWindow(){
        /**stamp = false;
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

        }); **/

    }

    @FXML
    public void loadButton(){
        if(test3d!=null) visDispPane.getChildren().remove(0);
        test3d = new Visualizer3D(6, "3d");
        test3d.populatePointArray(11, xSlider.getValue(),ySlider.getValue(),zSlider.getValue(), fSlide.getValue());
        test3d.finalizeWindow();
        visDispPane.getChildren().add(test3d.getRoot());
    }

    @Override
    @FXML
    public void play_pause_audio_event() {

        try {
            if (audioPlayer.getStatus().compareTo(MediaPlayer.Status.PLAYING)==0) {
                audioPlayer.pause();

//                Image playBtn = new Image(getClass().getResourceAsStream(PATH_DEFAULT + "\\src\\main\\resources\\image\\pause_button.png"));
            } else {
                audioPlayer.play();
//                System.out.println(PATH_DEFAULT + "\\src\\main\\resources\\image\\play_button.png");

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }



}
