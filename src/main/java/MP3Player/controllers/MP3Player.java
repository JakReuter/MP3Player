package MP3Player.controllers;

import MP3Player.mp3Player.equalizer.Equalizer;
import MP3Player.mp3Player.song.Song;
import MP3Player.mp3Player.visualizer.ChartVisualizer;
import MP3Player.mp3Player.visualizer.CircleChart;
import MP3Player.mp3Player.visualizer.ConeChart;
import MP3Player.mp3Player.visualizer.core.Series;
import MP3Player.mp3Player.visualizer.core.Visualizer;
import MP3Player.util.general.TabHandler;
import MP3Player.util.general.Tabable;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;

import MP3Player.mp3Player.time.*;
import org.farng.mp3.MP3File;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;

import static java.lang.Math.floor;

//TODO: make nodes fill size of split panes
//TODO: add advertisements in random positions of playlist $$$
public class MP3Player implements Initializable {

    protected final String PATH_DEFAULT = System.getProperty("user.dir");
    private final String PATH_MVMT = PATH_DEFAULT+"/out/production/AnotherMp3Test/song/4th Mvmt.mp3";
    private final String PATH_MAMA = PATH_DEFAULT+"/out/production/AnotherMp3Test/song/MeAndYourMama.mp3";


    Image playBtn = new Image("/img/play_button.png");
    Image pauseBtn = new Image("/img/pause_button.png");

    MSongs masterSongUI;
    PLSongs playlistWithSongsUI;
    PLVis playlistsUI;
    FXMLLoader masterSongsLoader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/masterSongs.fxml"));
    FXMLLoader playListSongsLoader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/playlistSongs.fxml"));
    FXMLLoader playListsLoader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/playlists.fxml"));

    //Thomas's test thing, please ignore
    //private final String PATH_MAMA = PATH_DEFAULT+"/out/production/AnotherMp3Test/TestFiles/NotLegit.mp3";


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
    @FXML
    AnchorPane mainLeftSplit;
    TabHandler leftTabPane;
    @FXML AnchorPane mainCenterSplit;
    TabHandler centerTabPane;
    @FXML AnchorPane mainRightSplit;
    TabHandler rightTabPane;

    //Instantiated in Controller and passed to TabHandler which passes to tabs
    //Holds the content of a tab when draggin between tabPanes
    ObjectProperty<Tab> tabToDrag;

    @FXML TableView tableView;

    protected Media audio;
    protected MediaPlayer audioPlayer;
    protected TimeControl timeControl;
    protected Timeline animater;

    protected Visualizer mainVisualizer;
    protected Series visualizerSeries;
    protected ArrayList<Series> seriesArray;
    protected final double SPEC_INTERVAL = .04;
    protected final int SPEC_THRESH = -100;
    protected final int SPEC_BANDS = 128;

    //used to pass audioPlayer to main application which uses it for unit tests
    public MediaPlayer getAudioPlayer()
    {
        return  audioPlayer;
    }

    public void setAudioPlayer(String filePath)
    {
        audioPlayer = new MediaPlayer(new Media(filePath));
    }

    //TODO: make default queue a playlist or have playlist hold arraylist?
    public ArrayList<Song> playlistQueue;
    public Queue<Song> audioQueue = new LinkedList<>();
    PLSongs plSongs;



    int playlistNum = 0;


    /**
     * Listeners
     */
    protected ChangeListener<Duration> progressListener;
    protected AudioSpectrumListener VisualizeListener;
    protected ChangeListener<Number> slideListener;


    @FXML
    protected void prev_audio_event() {
        try{
            //go to beginning of song if more than 5 seconds in
            if(audioPlayer.getCurrentTime().toSeconds() > 5 || 0 == playlistNum){
                audioPlayer.seek(Duration.millis(0));
            }else if(playlistWithSongsUI.getSongs().size() == 0){
                //todo: make into error or something
                System.out.println("No previous song");
            } else if (audioPlayer.getCurrentTime().toSeconds() <= 5 && playlistNum > 0){
                playlistNum--;
                audioPlayer.stop();
                if(playlistNum == 0)
                    playlistNum = playlistWithSongsUI.getSongs().size() - 1;
                else playlistNum -= 1;
                initializeAudioPlayer();
                endOfMedia();


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
        if(playlistWithSongsUI.getSongs().size() > 1 || masterSongUI.getQueue().size() > 0){
            audioPlayer.stop();
            initializeAudioPlayer();
            endOfMedia();
            //keep playing status
            if(audioPlaying)
                audioPlayer.play();
        }else {
            //todo: make into error or something
            System.out.println("No next song");
        }

    }

    @FXML
    public void play_pause_audio_event() {

        try {
            if(audioPlayer == null){
                initializeAudioPlayer();
            }
            if (audioPlayer.getStatus().compareTo(MediaPlayer.Status.PLAYING)==0) {
                audioPlayer.pause();

                play_pause_btn_icon.setImage(playBtn);
                audioPlaying = false;
            } else {

                audioPlayer.play();
                play_pause_btn_icon.setImage(pauseBtn);
                System.out.println(play_pause_btn_icon.getImage().toString());
                audioPlaying = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    protected File getNewFile(){
//        try {
        if(masterSongUI == null ){
//            throw new RuntimeException("Master Song UI is not open");
            System.out.println("Master Song UI is not open");
        }else{
            if (masterSongUI.getQueue().size() > 0) {
//                audioQueue.size() > 0){
                return new File(masterSongUI.getQueue().remove(0).getPath());
            }
        }

        if(playlistWithSongsUI == null){
            System.out.println("Open a playlist first");
        }else {
            if(playlistWithSongsUI.getSongs().size() == 0)
                return null;
            if (playlistNum > playlistWithSongsUI.getSongs().size() - 1)
                playlistNum = 0;
            System.out.println(playlistWithSongsUI.getSongs().get(playlistNum).getPath());
            System.out.printf("%d | %d\n", playlistNum, playlistWithSongsUI.getSongs().size() - 1);
            return new File(playlistWithSongsUI.getSongs().get(playlistNum++).getPath());
        }
        return null;

//        }catch (NullPointerException ex){
//            System.out.println("Please open the All songs tab");
//            ex.printStackTrace();
//        }
    }

    protected void endOfMedia(){

    }
    protected void initializeAudioPlayer() {
        try {
            File file = getNewFile();
            MP3File mp3File = new MP3File(file);
            mp3File.seekMP3Frame();
            System.out.println(file.getName() + ": "+mp3File.getFrequency()+" kHz");
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
                System.out.println("END OF SONGNGNGNGNGNGNGNNNNNNN");
                initializeAudioPlayer();
                audioPlayer.play();
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
            System.out.println("Exception in initializeAudioPlayer");
            e.printStackTrace();
        }
    }

    //may be useless
    protected void initializeQueue(){
        System.out.println("Playlist SET.");

        playlistQueue = (ArrayList<Song>) playlistWithSongsUI.getSongs();

    }
    /**
     * helper function to determine if the player can receive ui commands
     * @return
     */
    protected boolean isPlayerActive(){
        return (audioPlayer!=null&&audioPlayer.getStatus().compareTo(MediaPlayer.Status.UNKNOWN)!=0);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        audioQueue.add(new Song(
                "Iko Iko",
                "C:\\Users\\aidan\\IdeaProjects\\MP3Player\\src\\main\\resources\\song\\Iko_Iko_From__Alvin_And_The_Chipmunks__The_Road_Chip__Soundtrack.mp3",
                "Alvin and the Chipmunks",
                "Iko Iko Album",
                "1"

        ));
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
        endOfMedia();
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
        // Song|Playlist management UIS


        ActionListener refresher =(ActionEvent e) ->
            {
                masterSongUI.refreshInformation();
                playlistWithSongsUI.refreshInformation();
                playlistsUI.refreshInformation();
            };



        try{
            masterSongsLoader.load();
            playListsLoader.load();
            playListSongsLoader.load();

            masterSongUI = masterSongsLoader.getController();
            playlistWithSongsUI = playListSongsLoader.getController();
            playlistsUI = playListsLoader.getController();

            masterSongUI.setOnRefresh(refresher);
            masterSongUI.refreshInformation();
            playlistWithSongsUI.setOnRefresh(refresher);
            playlistsUI.setOnRefresh(refresher);
            playlistsUI.setPLSongs(playlistWithSongsUI);
            masterSongUI.setPLVis(playlistsUI);
        } catch (Exception e){
            e.printStackTrace();
        }




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

    protected MenuItem[] getWindowsMenu(TabHandler targetTab) {
        MenuItem[] windows = new MenuItem[4];
        windows[0] = new MenuItem("Playlist");
        windows[1] = new MenuItem("PlaylistSongs");


        windows[0].setOnAction(event -> {
            refreshTabs(playListsLoader.getRoot().toString());
            targetTab.addApp(new Tabable("Playlist", playListsLoader.getRoot()) {});
            targetTab.refresh();
        });

        windows[1].setOnAction(event -> {
            refreshTabs(playListSongsLoader.getRoot().toString());
            targetTab.addApp(new Tabable("Songs in Playlists", playListSongsLoader.getRoot()) {});
            targetTab.refresh();
        });
        windows[2] = new MenuItem("AllSongs");
        windows[2].setOnAction(event -> {
            refreshTabs(masterSongsLoader.getRoot().toString());
            targetTab.addApp(new Tabable("Songs", masterSongsLoader.getRoot()) {});
            targetTab.refresh();
        });
        windows[3] = new Menu("Visualizers",null,getVisualizers(targetTab));
        return windows;
    }


    protected MenuItem[] getVisualizers(TabHandler targetTab){
        MenuItem[] windows = new MenuItem[4];
        windows[0] = new MenuItem("Basic Chart");
        windows[1] = new MenuItem("Basic Chart w/ eq");
        windows[2] = new MenuItem("Cone Chart");
        windows[3] = new MenuItem("Circle Chart");
        windows[0].setOnAction(event -> {
            ChartVisualizer newChart = new ChartVisualizer(SPEC_BANDS, new Stage());
            seriesArray.add(newChart.getSeries());

            targetTab.addApp(newChart);
            targetTab.refresh();
        });
        windows[1].setOnAction(event -> {
            ChartVisualizer newChart = new ChartVisualizer(SPEC_BANDS, new Stage());
            seriesArray.add(newChart.getSeries());
            newChart.getRoot().setMinHeight(500);
            VBox t = new VBox(newChart.getRoot(), (new Equalizer(audioPlayer.getAudioEqualizer())).getRoot());
            targetTab.addApp(new Tabable("", t) {});
            targetTab.refresh();
        });

        windows[2].setOnAction(event -> {
            ConeChart newChart = new ConeChart(SPEC_BANDS, new Stage());
            seriesArray.add(newChart.getSeries());

            targetTab.addApp(newChart);
            targetTab.refresh();
        });

        windows[3].setOnAction(event -> {
            CircleChart newChart = new CircleChart(SPEC_BANDS, "circle");
            seriesArray.add(newChart.getSeries());

            targetTab.addApp(newChart);
            targetTab.refresh();
        });
        return windows;
    }

    public void refreshTabs(String nodeId){
        leftTabPane.refresh(nodeId);
        leftTabPane.refresh();
        rightTabPane.refresh(nodeId);
        rightTabPane.refresh();
        centerTabPane.refresh(nodeId);
        centerTabPane.refresh();
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