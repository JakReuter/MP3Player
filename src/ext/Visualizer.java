package ext;

import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.Observable;

public abstract class Visualizer {
    private double[] realData;
    private int bands;
    private final double MAXFREQ = 16000.0;
    private double currentScale;

    public interface IndexAxis{
        double get(int index, double coordinate, double scale);
    }

    private IndexAxis logAxis = (int index, double coordinate, double scale)->{
        return currentScale;
    };
    private IndexAxis linearAxis = (int index, double coordinate, double scale)->{
        return 0;
    };

    public Visualizer(int bands){

        this.bands=bands;
        realData = new double[bands];

    }

    public int linearScalar(double maxIndex, double maxValue){
return 0;
    }

    public void update(int xIndex, double yValue){

    }

    protected boolean validate(int index){
        if(index>=0&&index<realData.length){
            return true;
        }
        adjustRange(index);
        return false;
    }

    abstract protected void adjustRange(int outLier);

}
