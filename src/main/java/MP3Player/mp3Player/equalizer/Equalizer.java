package MP3Player.mp3Player.equalizer;

import MP3Player.util.general.ColorSlider;
import MP3Player.util.general.Tabable;
import javafx.beans.property.Property;
import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.control.Tab;
import javafx.scene.layout.*;
import javafx.scene.media.AudioEqualizer;
import javafx.scene.media.EqualizerBand;

import java.util.ArrayList;


/**
 * Provides UI control over the sound/frequency response of music
 * as vertical sliders.
 *
 * @Authors Brendan Reuter
 */
public class Equalizer extends Tabable {
    private String tabName = "h";
    private HBox Sliders;
    private VBox Buttons;
    private final int EQBANDS = 10;
    private AudioEqualizer EQ;
    private int maxGain;
    private ArrayList<Slider> sliderArrayList;

    public Equalizer(AudioEqualizer mediaEQ) {
        super("Equalizer");
        EQ = mediaEQ;
        Sliders = new HBox();
        Sliders.setPrefSize(500,200);
        Sliders.setAlignment(Pos.CENTER);
        Sliders.widthProperty().addListener((observable, oldValue, newValue) -> System.out.println(newValue));

        sliderArrayList = new ArrayList<>();

        for(int i = 0; i<EQBANDS; i++){
            ColorSlider newSlider = new ColorSlider(-24, 24, 0);
            newSlider.valueProperty().bindBidirectional(EQ.getBands().get(i).gainProperty());
            newSlider.setOrientation(Orientation.VERTICAL);
            newSlider.setPrefWidth(1000);
            HBox.setHgrow(newSlider, Priority.ALWAYS);
            newSlider.applyColor("#FF0000");
            newSlider.enableColorChangeOnValue();
            sliderArrayList.add(newSlider);
        }
        Sliders.getChildren().addAll(sliderArrayList);
        Buttons = new VBox(newToggleButton("on"));
        Sliders.getChildren().add(Buttons);
        AnchorPane.setLeftAnchor(Sliders,0.0);
        AnchorPane.setRightAnchor(Sliders,0.0);
        getRoot().getChildren().add(Sliders);
        getRoot().widthProperty().addListener((observable, oldValue, newValue) -> System.out.println("root:"+newValue));
        this.childId = Sliders.toString();

    }


    public void toggleEQ(){
        EQ.setEnabled(!EQ.isEnabled());
    }

    protected Button newToggleButton(String name){
        Button button = new Button(name);
        button.setOnAction(event -> toggleEQ());
        return button;
    }

    public void setListener(ChangeListener<? super Number> ... newListener){
        for(int i = 0; i < EQBANDS; i++){
            sliderArrayList.get(i).valueProperty().addListener(newListener[i]);
        }

    }



}
