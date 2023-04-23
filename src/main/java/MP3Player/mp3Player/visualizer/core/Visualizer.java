package MP3Player.mp3Player.visualizer.core;

import MP3Player.mp3Player.equalizer.Equalizer;
import MP3Player.util.general.Tabable;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.util.Duration;
import java.util.ArrayList;

/**
 * Abstract visualizer class that serves to
 *  handle the visuals of more complex visualizers
 *  in the future.
 * @Authors Brendan Reuter
 */



    //TODO: add animation functionality
public abstract class Visualizer extends Tabable {

    protected int bands;
    protected KeyValue[] keyValues;

    public Timeline animator;
    private boolean animationEnabled = false;
    protected Duration animationDuration;
    protected boolean animationIsInterruptable = true;

    protected int cutOffIndex;

    protected Series series;

    private Axis XAxis;
    private Axis YAxis;

    public Equalizer equalizer;

    public Visualizer(int bands, String name){
        super(name);
        this.bands=bands;
        //realData = new double[bands];
        series = new Series(bands, this);
        cutOffIndex = (int)(bands*.68);

    }

    public Series getSeries() {
        return series;
    }

    public void setSeries(Series series){
        series.bindNewVisualizer(this);
        this.series=series;
    }




    protected Axis getXAxis(){
        if(XAxis==null){
            XAxis= new Axis(0,1,cutOffIndex-1,22050,null);
        }
        return XAxis;
    }
    protected Axis getYAxis(){
        if(YAxis==null){
            YAxis= new Axis(-100,5,0,24,((in, translate, scale) -> -1*(in.getyValue().doubleValue()*scale)
            //{
                //double y = in.getyValue().doubleValue();
                //double log = Math.log10(y+101);
                //double next = (log*450);
                //next = -1*next;
                //next = 898+next;
                //return next;
                //return (-1*((Math.log((y+101)/(-1*y))/Math.log(1.1))+50))+898;
                //return (-1*((Math.log((y+100.001)/(-1*y))/Math.log(1.03))))+400;
            //}//Test logit grow
            ));
        }
        return YAxis;
    }

    protected void setXAxis(Axis newAxis){
        XAxis=newAxis;
    }
    protected void setYAxis(Axis newAxis){
        YAxis=newAxis;
    }

    //Update individual data points
    //Cant animate here because we need
    //to update all nodes at once for animate.
    public void changedData(Series.Data changed){





        Number newX = XAxis.getDrawPoint(changed, getRoot().getPrefWidth());
        Number newY = YAxis.getDrawPoint(changed, getRoot().getMaxHeight());
        changed.setyPosition(newY);
        changed.setxPosition(newX);
    }
    //Animate here
    public void changedData(Series.Data[] changed){
        if(!animationEnabled){
            for(Series.Data d : changed){
                changedData(d);
            }
        } else {
            if(isAnimationRead()) {
                animate(keyValues);
                KeyValue[] newKeyValues = new KeyValue[cutOffIndex*2];
                for (int i = 0; i < cutOffIndex; i++) {


                    Number newY = YAxis.getDrawPoint(changed[i], getRoot().getMaxHeight());
                    Number newX = XAxis.getDrawPoint(changed[i], getRoot().getPrefWidth());
                    newKeyValues[2*i] = (new KeyValue(changed[i].yPositionProperty(), newY));
                    newKeyValues[(2*i)+1] = (new KeyValue(changed[i].xPositionProperty(), newX));

                    //We could ignore these for y chart visualizers but
                    //we need the x position to be animated for circle chart
                }
                keyValues=newKeyValues;
            } else {
                System.out.println("animation not ready. "+animator.getCurrentTime()+" < "+animationDuration);
                System.out.println("delay of: "+(animationDuration.subtract(animator.getCurrentTime())));
            }
        }
    }

    public void setAnimationDuration(Duration newDur){
        this.animationDuration=newDur;
    }

    public void setAnimationEnabled(boolean enable){
        this.animationEnabled=true;
        animator =new Timeline();
    }

    public boolean isAnimationRead(){
        if(animationIsInterruptable) return true;
        if(animator.getStatus().equals(Animation.Status.RUNNING)) return false;
        return true;
    }

    public void animate(KeyValue ... inValues){

        animator.stop();
        animator = new Timeline();
        if(inValues!=null) {
            animator.getKeyFrames().add(
                    new KeyFrame(animationDuration, inValues)
            );
            animator.play();
        }
    }

    protected boolean validate(int index){
        if(index>=0&&index<cutOffIndex){
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
