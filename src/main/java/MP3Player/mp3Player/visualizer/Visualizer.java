package MP3Player.mp3Player.visualizer;

import MP3Player.mp3Player.visualizer.core.Axis;

public abstract class Visualizer {
    private double[] realData;
    private int bands;
    private final double MAXFREQ = 22050;
    private double currentScale;

    private Axis XAxis;
    private Axis YAxis;

    public Visualizer(int bands){

        this.bands=bands;
        realData = new double[bands];

    }

    public int linearScalar(double maxIndex, double maxValue){
return 0;
    }

    public void update(int xIndex, double yValue){
        //linearAxis.
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

}
