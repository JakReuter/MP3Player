package MP3Player.mp3Player.visualizer;

import MP3Player.mp3Player.visualizer.core.Axis;
import MP3Player.mp3Player.visualizer.core.Series;
import MP3Player.mp3Player.visualizer.core.Visualizer;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Slider;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.stage.Window;

import java.util.ArrayList;

/**
 * This class serves to replace AreaChart/XYChart. Will
 * be able to display log axis for visualizer. Made
 * abstract to not throw errors for now.
 */
public class ChartVisualizer extends Visualizer {
    protected Path mainPath;
    protected Path eqPath;
    protected ArrayList<CubicCurveTo> curves;

    protected BorderPane borderPane;

    protected VBox YAxis;
    protected Pane canvas;
    protected HBox XAxis;

    protected Axis xAxis;
    protected Axis yAxis;


    private final double CURVE_SCALAR = 1.00;


//TODO: clean up messy code
    public ChartVisualizer(int bands, Window stage) {
        super(bands,"Chart");
        curves = new ArrayList<>();


        /**
        setXAxis(new Axis(0,1,bands,22050,(in, translate1, scale1) ->
                ((Math.log10(in.getxValue().doubleValue()+1))*100))
                 {
                     @Override
                     public Number getDrawPoint(Series.Data valueIn, Number windowBound){
                         //System.out.println("="+(windowBound.doubleValue()/highInput));
                         return function.valueToCoord(valueIn, 0, windowBound.doubleValue()/Math.log10(highInput+1));
                     }
                 }
        ); **/

        xAxis = getXAxis();
        yAxis = getYAxis();

        mainPath = new Path();
        canvas = new Pane();

        //xAxis.getWindowSize().bind(canvas.widthProperty());
        //yAxis.getWindowSize().bind(canvas.heightProperty());
        //stage.widthProperty().addListener(xAxis.getWindowSizeListner());
        //canvas.heightProperty().addListener(yAxis.getWindowSizeListner());
        canvas.setMaxHeight(1200);
        //System.out.println(getWindow().getWidth());
        getBindedCurve();
        MoveTo moveTo = new MoveTo(0,0);
        moveTo.yProperty().bind(getRoot().maxHeightProperty());
        moveTo.yProperty().addListener((observable, oldValue, newValue) -> System.out.println("moveTo y"+newValue));
        mainPath.getElements().addAll(moveTo);
        mainPath.getElements().addAll(curves);
        mainPath.setFill(Color.RED);
        canvas.getChildren().add(mainPath);
        borderPane = new BorderPane(canvas, null, null, null, null );
        this.childId = borderPane.toString();
        //canvas.heightProperty().addListener((observable, oldValue, newValue) -> );
        this.getRoot().getChildren().add(borderPane);
    }


    protected void getBindedCurve(){

        for(int i = 0; i<cutOffIndex; i++){
            double xRet = xAxis.getDrawPoint(series.getData(i),700).doubleValue();
            double yRet = 100;
           // System.out.println("x: " + i);
            CubicCurveTo newCurve = new CubicCurveTo(xRet, yRet, xRet, yRet, xRet, yRet);

            newCurve.controlY1Property().bind(series.getData(i).yPositionProperty());
            newCurve.controlX2Property().bind(series.getData(i).xPositionProperty());

            if(i+1<cutOffIndex){
                newCurve.controlX1Property().bind(series.getData(i+1).xPositionProperty());
                newCurve.controlY2Property().bind(series.getData(i+1).yPositionProperty());
                newCurve.yProperty().bind(series.getData(i+1).yPositionProperty());
                newCurve.xProperty().bind(series.getData(i+1).xPositionProperty());
            } else {
                newCurve.controlX1Property().bind(getRoot().prefWidthProperty());
                newCurve.controlY2Property().bind(getRoot().maxHeightProperty());
                newCurve.yProperty().bind(getRoot().maxHeightProperty());
                newCurve.xProperty().bind(getRoot().prefWidthProperty());
            }
            curves.add(newCurve);
        }
    }
/**
    public void update(int i, double value){
        //System.out.println("canvas width: "+canvas.getWidth());
        CubicCurveTo newCurve = curves.get(i);
        newCurve.setX(xAxis.getDrawPoint(i+1));
        newCurve.setControlX1(xAxis.getDrawPoint(i+1));
        newCurve.setControlX2(xAxis.getDrawPoint(i));
        curves.get(i).setY(200-yAxis.getDrawPoint(value));


    }


**/
    protected void getYTicks(){

    }

    /**
     * If autoranging, change range when outlier is reached
     *
     * @param outLier
     */
    @Override
    protected void adjustRange(int outLier) {

    }
}
