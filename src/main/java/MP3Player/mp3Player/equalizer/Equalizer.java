package MP3Player.mp3Player.equalizer;

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
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.media.AudioEqualizer;
import javafx.scene.media.EqualizerBand;

import java.util.ArrayList;

//possibly a tabable element interface? one that has a concrete display, getTab and maybe getWindow kind of thing

/**
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
        Sliders.setAlignment(Pos.valueOf("CENTER"));

        sliderArrayList = new ArrayList<>();

        for(int i = 0; i<EQBANDS; i++){
            Slider newSlider = new Slider(-24, 24, 0);
            newSlider.valueProperty().bindBidirectional(EQ.getBands().get(i).gainProperty());
            newSlider.setOrientation(Orientation.VERTICAL);
            sliderArrayList.add(newSlider);
        }
        Sliders.getChildren().addAll(sliderArrayList);
        Buttons = new VBox(newToggleButton());
        Sliders.getChildren().add(Buttons);
        getRoot().getChildren().add(Sliders);

    }


    public void toggleEQ(){
        EQ.setEnabled(!EQ.isEnabled());
    }

    protected Button newToggleButton(){
        Button button = new Button("Toggle");
        button.setOnAction(event -> toggleEQ());
        return button;
    }

    public void setListener(ChangeListener<? super Number> ... newListener){
        for(int i = 0; i < EQBANDS; i++){
            sliderArrayList.get(i).valueProperty().addListener(newListener[i]);
        }

    }


}
