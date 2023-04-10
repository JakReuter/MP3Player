package MP3Player.controllers;

import MP3Player.mp3Player.equalizer.Equalizer;
import MP3Player.mp3Player.visualizer.ChartVisualizer;
import MP3Player.mp3Player.visualizer.ConeChart;
import MP3Player.mp3Player.visualizer.RectangleVisualizer;
import MP3Player.mp3Player.visualizer.XYChartVisualizer;
import MP3Player.mp3Player.visualizer.core.Series;
import MP3Player.mp3Player.visualizer.core.Visualizer;
import MP3Player.util.general.TabHandler;
import MP3Player.util.general.Tabable;
import javafx.animation.*;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.chart.AreaChart;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.*;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import MP3Player.mp3Player.time.*;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import static java.lang.Math.floor;

//TODO: make nodes fill size of split panes
public class MP3Player implements Initializable {

    protected final String PATH_DEFAULT = System.getProperty("user.dir");
    private final String PATH_MVMT = PATH_DEFAULT+"/out/production/AnotherMp3Test/song/4th Mvmt.mp3";
    private final String PATH_MAMA = PATH_DEFAULT+"/out/production/AnotherMp3Test/song/MeAndYourMama.mp3";


    boolean audioPlaying = false;

    @FXML Label isTesting;
    @FXML protected Label timestamp;
    @FXML protected ImageView play_pause_btn_icon;
    @FXML protected Slider time_slider;
    @FXML Button prev_btn;
    @FXML Button next_btn;
    @FXML Button play_pause_btn;
    @FXML StackPane root;

    @FXML MenuItem viewToggle;
    @FXML BorderPane playView;
    @FXML MenuBar menuBar;
    @FXML Label playViewLabel;

    //Split Plane panes
    @FXML SplitPane mainSplit;
    @FXML AnchorPane mainLeftSplit;
    TabHandler leftTabPane;
    @FXML AnchorPane mainCenterSplit;
    TabHandler centerTabPane;
    @FXML AnchorPane mainRightSplit;
    TabHandler rightTabPane;

    //Instantiated in Controller and passed to TabHandler which passes to tabs
    //Holds the content of a tab when draggin between tabPanes
    ObjectProperty<Tab> tabToDrag;

    protected Media audio;
    protected MediaPlayer audioPlayer;
    protected TimeControl timeControl;
    protected Timeline animater;

    protected Visualizer mainVisualizer;
    protected Series visualizerSeries;
    protected ArrayList<Series> seriesArray;
    protected final double SPEC_INTERVAL = .04;
    protected final int SPEC_THRESH = -100;
    protected final int SPEC_BANDS = 256;

    /**
     * Listeners
     */
    protected ChangeListener<Duration> progressListener;
    protected AudioSpectrumListener VisualizeListener;
    protected ChangeListener<Number> slideListener;

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


    protected void initializeAudioPlayer() {
        try {

            File file = new File(PATH_MAMA);
            audio = new Media(file.toURI().toString());
            audioPlayer = new MediaPlayer(audio);

            timestamp.setText("0:0");
            time_slider.setValue(0);

            timeControl = new TimeControl(audioPlayer);

            audioPlayer.currentTimeProperty().addListener(progressListener);
            time_slider.valueProperty().addListener(slideListener);

            audioPlayer.setAudioSpectrumInterval(SPEC_INTERVAL);
            audioPlayer.setAudioSpectrumThreshold(SPEC_THRESH);
            audioPlayer.setAudioSpectrumNumBands(SPEC_BANDS);

            audioPlayer.setOnEndOfMedia(() -> {
                //TODO: queue next song
            });

            audioPlayer.setAudioSpectrumListener((double timestamp, double duration, float[] magnitudes, float[] phases) ->{
                if(seriesArray!=null) {
                    for(Series s : seriesArray) {
                        for (int i = 0; i < audioPlayer.getAudioSpectrumNumBands(); i++) {
                            s.change(i, magnitudes[i]);
                        }
                    }
                }
            });
        } catch (Exception e) {
            System.out.println("Exception in initalizeAudioPlayer");
            e.printStackTrace();
        }
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
//        play_pause_btn = new Button();
//        play_pause_btn_icon = new ImageView(new Image("image/pause_button.png"));
//        playViewLabel.setText("Childish Gambino: Me and Your Mama");
        animater = new Timeline();
        tabToDrag = new SimpleObjectProperty<>();
        initializeWindow();
        initializeListeners();
        initializeAudioPlayer();
    }

    protected void initializeListeners(){
        slideListener = (observableValue, oldDuration, newDuration) -> {
            if (time_slider.isPressed()) {
                audioPlayer.seek(Duration.seconds((time_slider.getValue() / 100) * (long) audioPlayer.getTotalDuration().toSeconds()));
            }};

        progressListener = (observableValue, oldDuration, newDuration) -> {
            if (newDuration != oldDuration) {
                //update timestamp
                timestamp.setText(String.format("%.0f:%02.0f", floor(newDuration.toMinutes()), floor(newDuration.toSeconds() % 60)));

                //move slider
                time_slider.setValue((newDuration.toSeconds() /audioPlayer.getTotalDuration().toSeconds()) * 100);
            }};

        viewToggle.setOnAction( event -> {
            toggleMasterView();
        });

    }

    /**
     * Initializes the split and tab panes.
     */
    //TODO: make slider change color based on position here
    protected void initializeWindow(){
        leftTabPane = new TabHandler(tabToDrag);
        centerTabPane = new TabHandler(tabToDrag);
        rightTabPane = new TabHandler(tabToDrag);

        seriesArray = new ArrayList<>();
        Label testLabel = new Label("Play View Here");
        testLabel.setFont(Font.font("Comic Sans MS", 40));


        mainVisualizer = new ChartVisualizer(SPEC_BANDS, new Stage());
        //Load each tabpane with right click context menu
        leftTabPane.getPane().setContextMenu(new ContextMenu(getWindowsMenu(leftTabPane)));
        centerTabPane.getPane().setContextMenu(new ContextMenu(getWindowsMenu(centerTabPane)));
        rightTabPane.getPane().setContextMenu(new ContextMenu(getWindowsMenu(rightTabPane)));

        //TODO: Bind width of anchor pane with corresponding tabPane

        mainLeftSplit.getChildren().add(leftTabPane.getPane());
        mainCenterSplit.getChildren().add(centerTabPane.getPane());
        mainRightSplit.getChildren().add(rightTabPane.getPane());


    }

    protected MenuItem[] getWindowsMenu(TabHandler targetTab){
        MenuItem[] windows = new MenuItem[3];
        windows[0] = new MenuItem("Playlist");
        windows[0].setOnAction(event -> {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/playlists.fxml"));
            try {
                fxmlLoader.load();
                targetTab.addApp(new Tabable("Playlist", fxmlLoader.getRoot()) {
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
            targetTab.refresh();
        });
        windows[1] = new MenuItem("Other stuff");
        windows[2] = new Menu("Visualizers",null,getVisualizers(targetTab));
        return windows;
    }

    //TODO: stop updating a chart after sending it to the ether
    protected MenuItem[] getVisualizers(TabHandler targetTab){
        MenuItem[] windows = new MenuItem[2];
        windows[0] = new MenuItem("Basic Chart");
        windows[1] = new MenuItem("Cone Chart");
        windows[0].setOnAction(event -> {
            ChartVisualizer newChart = new ChartVisualizer(SPEC_BANDS, new Stage());
            seriesArray.add(newChart.getSeries());

            targetTab.addApp(newChart);
            targetTab.refresh();
        });

        windows[1].setOnAction(event -> {
            ConeChart newChart = new ConeChart(SPEC_BANDS, new Stage());
            seriesArray.add(newChart.getSeries());

            targetTab.addApp(newChart);
            targetTab.refresh();
        });
        return windows;
    }

    public void toggleMasterView(){

        animater.getKeyFrames().clear();
        if(playView.isVisible()){
            animater.getKeyFrames().addAll(
                    new KeyFrame(Duration.ZERO, new KeyValue(playView.scaleYProperty(), 1), new KeyValue(playView.translateYProperty(), 0)),
                    new KeyFrame(Duration.millis(1000), new KeyValue(playView.scaleYProperty(), 0.01), new KeyValue(playView.translateYProperty(), root.getHeight() / 2))
            );
            animater.play();
            animater.setOnFinished(event -> {
                playView.setVisible(false);
            });
        } else {
            playView.setVisible(true);
            animater.getKeyFrames().addAll(
                    new KeyFrame(Duration.ZERO, new KeyValue(playView.scaleYProperty(), 0.01), new KeyValue(playView.translateYProperty(), root.getHeight() / 2)),
                    new KeyFrame(Duration.millis(1000), new KeyValue(playView.scaleYProperty(), 1), new KeyValue(playView.translateYProperty(), 0))
            );
            animater.play();
            animater.setOnFinished(event -> {

            });
        }
    }




}