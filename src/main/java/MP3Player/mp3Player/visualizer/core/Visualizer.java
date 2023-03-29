package MP3Player.mp3Player.visualizer.core;

import MP3Player.mp3Player.visualizer.core.Axis;
import MP3Player.util.general.Tabable;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
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
    protected int bands;
    private final double MAXFREQ = 22050;
    private double currentScale;
    private boolean animationEnabled = false;

    protected Series series;

    private Axis XAxis;
    private Axis YAxis;

    public Visualizer(int bands, String name){
        super(name);
        this.bands=bands;
        realData = new double[bands];
        series = new Series(bands, this);

    }

    public Series getSeries() {
        return series;
    }

    public void setEnabled(boolean enable){
        this.animationEnabled=true;
        timeline=new Timeline();
    }

    protected void logVisualizer(){

    }

    protected Axis getXAxis(){
        if(XAxis==null){
            XAxis= new Axis(0,1,bands,22050,false);
        }
        return XAxis;
    }
    protected Axis getYAxis(){
        if(YAxis==null){
            YAxis= new Axis(-100,0,0,24,false);
        }
        return YAxis;
    }


    public void changedData(Series.Data changed){
        //System.out.println("changed!");
        Number newX = XAxis.getDrawPoint(changed.getxValue(), bands);
        Number newY = YAxis.getDrawPoint(changed.getyValue(), 400);
        System.out.println("setting: ("+newX+", "+newY+")");
        changed.setyPosition(newY);
        changed.setxPosition(newX);
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

    public void drawRefresh(){

    }
}
