package MP3Player.util.general;

import javafx.geometry.Orientation;
import javafx.scene.control.Slider;
import javafx.scene.layout.StackPane;

import javafx.beans.value.ChangeListener;

public class ColorSlider extends Slider {

    String gradientDirection;
    String color;

    public ColorSlider(double a, double b, double c){
        super(a,b,c);
        applyColor("#000000");
    }

    public ColorSlider(){
        super();
        applyColor("#000000");
    }


    /**
     * set the color of slider progrress
     * @param colorInHex    hex value of rgb with # included. i.e #ffffff
     */
    public void applyColor(String colorInHex){
        this.setId("color-slider");
        //getStylesheets().add(System.getProperty("user.dir")+"\\out\\production\\AnotherMp3Test\\css\\ColorSlider.css");

        //StackPane trackPane = (StackPane) this.lookup(".track");
        color = colorInHex;
        if(this.getOrientation()== Orientation.VERTICAL){
            gradientDirection = "top";
        } else {
            gradientDirection = "right";
        }
    }

    public void enableColorChangeOnValue(){

        this.valueProperty().addListener((observable, oldValue, newValue) -> {
            double percentage = (((newValue.doubleValue()-this.getMin())/(this.getMax()-this.getMin()))*100);
            String style = String.format("-fx-background-color: linear-gradient(to %s, %s %4.2f%%, #FFFFFF %4.2f%%);", gradientDirection, color, percentage, percentage);
            ((StackPane) this.lookup(".track")).setStyle(style);
        });

        //((StackPane) this.lookup(".track")).setStyle(String.format("-fx-background-color: linear-gradient(to %s, %s %d%%, #FFFFFF %d%%);", gradientDirection, color, 50,50));
    }

    public ChangeListener getColorListener(double minimum, double maximum){
        ChangeListener<Number> out = (observable, oldValue, newValue) -> {
            int percentage = (int)(((newValue.doubleValue()-minimum)/(maximum-minimum))*100);
            String style = String.format("-fx-background-color: linear-gradient(to %s, %s %d%%, #FFFFFF %d%%);", gradientDirection, color, percentage, percentage);
            ((StackPane) this.lookup(".track")).setStyle(style);
        };
        return  out;

    }
}
