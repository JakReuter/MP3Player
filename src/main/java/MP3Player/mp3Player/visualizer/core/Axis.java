package MP3Player.mp3Player.visualizer.core;

import com.sun.org.apache.bcel.internal.generic.INSTANCEOF;
import javafx.beans.InvalidationListener;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.DoublePropertyBase;
import javafx.beans.property.Property;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.stage.Window;

/**
 * Axis for Visualizer class
 * @Authors
 */
public class Axis {
    private boolean isLog;
    private double ampLow;
    private double ampHigh;
    private double scalar;
    private double drawRatio;
    private DoubleProperty WindowSize;
    private double f_xOut;
    private double lowInput;
    private double highInput;
    private double lowOutput;
    private double highOutput;
    private double range;


    public Axis(double lowInput, double lowOutput, double highInput, double highOutput, boolean isLogarithmic){
        this.lowInput = lowInput;
        this.highInput = highInput;
        this.lowOutput = lowOutput;
        this.highOutput = highOutput;
        range=lowOutput-lowInput;
        isLog = isLogarithmic;
        ampLow = highInput-lowInput;

        f_xOut=highOutput;
        if(isLog){
            scalar = 1;//math.log here
        } else {
            scalar = highOutput/(range+highInput);
        }
        WindowSize = new DoublePropertyBase() {
            @Override
            public Object getBean() {
                return Axis.this;
            }

            @Override
            public String getName() {
                return " pixel max";
            }
        };

    }

    public void setDrawBound(int max){

    }

    public DoubleProperty getWindowSize(){
        return WindowSize;
    }

    public ChangeListener<Number> getWindowSizeListner(){
        return (observable, oldValue, newValue) -> {
            WindowSize.set((double) newValue);
            //System.out.println(newValue);
        };
    }

    public Number getDrawPoint(Number valueIn, Number windowBound){
        if(windowBound.doubleValue()==400){
            return valueIn.doubleValue()*-2;
        }
        return valueIn.doubleValue()*2;
    }
    //public double getDrawPoint(float valueIn, Number windowBound){

        //double out = highOutput/(double) valueIn;
        //return out;
    //}

}
