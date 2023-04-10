package MP3Player.mp3Player.visualizer.core;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;

/**
 * Axis for Visualizer class
 * @Authors
 */

//TODO: change current output method to versatile using axisFunction
//TODO: begin circular axis for circle chart
//TODO: take Data as parameter for axisfunction/update
public class Axis {
    private boolean isLog;
    private SimpleDoubleProperty WindowSize;
    private double f_xOut;
    private double lowInput;
    private double highInput;
    private double lowOutput;
    private double highOutput;
    private double range;
    private AxisFunction function;
    private double translate;
    private double scale;

    /**
     * Calculates the coordinate on given function
     * Scale: multiplier for size, assume scale is adjusted for window elsewhere
     * Translation: adjusts origin of current axis.
     *
     *   0,0   0,1   0,2    0,0  0,1  0,2
     *  -1,0  -1,1  -1,2 -> 1,0  1,1  1,2
     *  -2,0  -2,1  -2,2    2,0  2,1  2,2
     */
    public interface AxisFunction {
        double valueToCoord(Number in, double translate, double scale);
    }

    protected AxisFunction linearDefault = ((in, translate1, scale1) -> ((double)in+translate1)*scale1);


    public Axis(double lowInput, double lowOutput, double highInput, double highOutput, AxisFunction function){
        //might not need these variables

        this.lowInput = lowInput;
        this.highInput = highInput;
        this.lowOutput = lowOutput;
        this.highOutput = highOutput;

        range=lowOutput-lowInput;
        translate = lowOutput-lowInput;
        scale = /**highInput==0 ? (highOutput)/(highInput+.000001) :**/ highOutput/(highInput+translate);

        System.out.println("translate, scale -> " + translate + ", "+scale);

        if(function==null){
            this.function=linearDefault;
        } else {
            this.function=function;
        }

        WindowSize = new SimpleDoubleProperty();

    }
    public Axis(double lowInput, double lowOutput, double highInput, double highOutput){
        this( lowInput,  lowOutput,  highInput,  highOutput, null);
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
        //System.out.println("="+(windowBound.doubleValue()/highInput));
        return function.valueToCoord(valueIn.doubleValue(), translate, windowBound.doubleValue()/(highInput+translate));/**
        if(windowBound.doubleValue()==400){
            return valueIn.doubleValue()*-2;
        }
        return valueIn.doubleValue()*2; **/
    }
    //public double getDrawPoint(float valueIn, Number windowBound){

        //double out = highOutput/(double) valueIn;
        //return out;
    //}

}
