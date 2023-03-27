package MP3Player.mp3Player.visualizer;

import MP3Player.mp3Player.visualizer.core.Axis;
import MP3Player.util.general.Tabable;
import javafx.animation.Animation;
import javafx.animation.Timeline;
import javafx.scene.media.AudioSpectrumListener;

/**
 * Abstract visualizer class that serves to
 *  handle the visuals of more complex visualizers
 *  in the future.
 * @Authors Brendan Reuter
 */
public abstract class Visualizer extends Tabable {
    public Timeline timeline;
    private double[] realData;
    private int bands;
    private final double MAXFREQ = 22050;
    private double currentScale;

    private Axis XAxis;
    private Axis YAxis;

    public Visualizer(int bands, String name){
        super(name);
        this.bands=bands;
        realData = new double[bands];

    }

    protected void logVisualizer(){

    }

    public int linearScalar(double maxIndex, double maxValue){
return 0;
    }

    public void update(float[] newValues){
        //linearAxis.
    }

    protected void setXDrawBound(Number max){
        XAxis.setDrawBound((int)max);
    }

    protected boolean validate(int index){
        if(index>=0&&index<realData.length){
            return true;
        }
        adjustRange(index);
        return false;
    }

    /**
     * If autoranging, change range when outlier is reached
     * @param outLier
     */
    abstract protected void adjustRange(int outLier);



    public void drawUp(){

    }

}
