package MP3Player.mp3Player.visualizer;

import MP3Player.mp3Player.visualizer.core.Axis;
import MP3Player.mp3Player.visualizer.core.Series;
import MP3Player.mp3Player.visualizer.core.Visualizer;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Slider;
import javafx.scene.layout.*;
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

    private Axis xAxis;
    private Axis yAxis;

    private XYChart.Series<Number, Number> data;

    private final double CURVE_SCALAR = 1.00;
    private final int numYticks = 3;



    public ChartVisualizer(int bands, Window stage) {
        super(bands,"Chart");
        curves = new ArrayList<>();
        xAxis = getXAxis();
        yAxis = getYAxis();

        mainPath = new Path();
        canvas = new Pane();

        //xAxis.getWindowSize().bind(canvas.widthProperty());
        //yAxis.getWindowSize().bind(canvas.heightProperty());
        stage.widthProperty().addListener(xAxis.getWindowSizeListner());
        canvas.heightProperty().addListener(yAxis.getWindowSizeListner());
        canvas.setMaxHeight(1200);
        //System.out.println(getWindow().getWidth());
        getBindedCurve();
        MoveTo moveTo = new MoveTo(0,yAxis.getWindowSize().get());
        LineTo lineTo = new LineTo(0, 100);
        mainPath.getElements().addAll(moveTo, lineTo);
        Slider newSlider = new Slider();
        newSlider.setMin(-100);
        newSlider.setMax(0);
        newSlider.valueProperty().bind(series.getData(0).yPositionProperty());
        mainPath.getElements().addAll(curves);
        canvas.getChildren().add(mainPath);
        borderPane = new BorderPane(canvas, null, null, newSlider, null );
        //canvas.heightProperty().addListener((observable, oldValue, newValue) -> );
        this.getRoot().getChildren().add(borderPane);
    }

    public void initGraphic(){

    }

    private void getBindedCurve(){

        for(int i = 0; i<bands; i++){
            double xRet = xAxis.getDrawPoint(i,bands).doubleValue();
            double yRet = 100;
           // System.out.println("x: " + i);
            CubicCurveTo newCurve = new CubicCurveTo(xRet, yRet, xRet, yRet, xRet, yRet);

            newCurve.controlY1Property().bind(series.getData(i).yPositionProperty());
            newCurve.controlX2Property().bind(series.getData(i).xPositionProperty());

            if(i+1<bands){
                newCurve.controlX1Property().bind(series.getData(i+1).yPositionProperty());
                newCurve.controlY2Property().bind(series.getData(i+1).xPositionProperty());
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
