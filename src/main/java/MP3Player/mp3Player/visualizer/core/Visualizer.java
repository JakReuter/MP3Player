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

//TODO: have visualizer class handle parent boundary (implement in tabable?)
    //TODO: make appropriate axis function
    //TODO: add animation functionality
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
        //realData = new double[bands];
        series = new Series(bands, this);

    }

    public Series getSeries() {
        return series;
    }
    public void setSeries(Series series){
        series.bindNewVisualizer(this);
        this.series=series;
    }

    public void setEnabled(boolean enable){
        this.animationEnabled=true;
        timeline=new Timeline();
    }

    protected Axis getXAxis(){
        if(XAxis==null){
            XAxis= new Axis(0,1,bands,22050,null);
        }
        return XAxis;
    }
    protected Axis getYAxis(){
        if(YAxis==null){
            YAxis= new Axis(-100,0,0,24,((in, translate, scale) -> -1*(in.getyValue().doubleValue()*scale)));
        }
        return YAxis;
    }

    protected void setXAxis(Axis newAxis){
        XAxis=newAxis;
    }
    protected void setYAxis(Axis newAxis){
        YAxis=newAxis;
    }

    //Animate here
    //change to pass data into xvalue
    public void changedData(Series.Data changed){
        //System.out.println("changed!");
        //System.out.println(getRoot().getWidth());
        Number newX = XAxis.getDrawPoint(changed, 700);
        Number newY = YAxis.getDrawPoint(changed, 400);
        //System.out.println("x: " +changed.getxValue()+"->"+newX.doubleValue());
        //System.out.println("y: " +changed.getyValue()+"->"+newY.doubleValue());
        //System.out.println("setting: ("+newX+", "+newY+")");
        changed.setyPosition(newY);
        changed.setxPosition(newX);
    }

    //protected void setXDrawBound(Number max){
     //   XAxis.setDrawBound((int)max);
   // }

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


}
