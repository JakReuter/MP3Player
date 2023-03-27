package MP3Player.mp3Player.visualizer.core;

public class Axis {
    private boolean isLog;
    private double translation;
    private double scalar;

    public Axis(double lowInput, double lowOutput, double highInput, double highOutput, boolean isLogarithmic){
        isLog = isLogarithmic;
        translation = lowOutput-lowInput;
        if(isLog){
            scalar = 1;//math.log here
        } else {
            scalar = highOutput/(translation+highInput);
        }
    }

    public void setDrawBound(int max){

    }

    public double getDrawPoint(){
        //Xlog2(maxfrequency) = maxPoint
        return 0.0;
    }

}
