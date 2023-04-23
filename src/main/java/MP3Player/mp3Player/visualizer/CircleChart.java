package MP3Player.mp3Player.visualizer;

import MP3Player.mp3Player.visualizer.core.Axis;
import MP3Player.mp3Player.visualizer.core.Series;
import MP3Player.mp3Player.visualizer.core.Visualizer;
import javafx.beans.binding.ListBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.ObservableList;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.*;

import java.util.ArrayList;

public class CircleChart extends Visualizer {
    protected ArrayList<Circle> dots;
    protected ArrayList<Circle> mirrored;
    protected ArrayList<LineTo> paths;
    protected DoubleProperty centerX;
    protected DoubleProperty centerY;
    protected int divisionLength;

    public CircleChart(int bands, String name, int division) {

        super(bands, name);
        divisionLength=cutOffIndex/division;
        centerX=new SimpleDoubleProperty(200);
        centerY=new SimpleDoubleProperty(200);
        centerX.bind(getRoot().prefWidthProperty().multiply(.5));
        centerY.bind(getRoot().maxHeightProperty().multiply(.5));
        double circularRange = Math.PI/divisionLength;
        double circularRangeTrans = Math.PI/2;
        setXAxis(new Axis(0,1,cutOffIndex-1,22050, (in, translate, scale)->
            ((in.getyValue().doubleValue() + 105)*(scale) * Math.cos(in.getxValue().doubleValue() * circularRange+circularRangeTrans)) + centerX.get()
        ){
            @Override
            public Number getDrawPoint(Series.Data valueIn, Number windowBound){
                return function.valueToCoord(valueIn, 0, (centerX.doubleValue())/100);
            }
        });

        //Have y values scale with xWindow, not y window. SQUARE!
        setYAxis(new Axis(-100,0,0,24, (in, translate, scale)->
            centerY.get()                                                                       //Translation to center of screen
                    -((in.getyValue().doubleValue()+105)                                        //amplitude from y value, add 100 for threshold
                    *(scale)                                                                    //Scale for window dimension
                    *Math.sin(in.getxValue().doubleValue()*circularRange+circularRangeTrans))   //Y component in respect to angle X
        ){
            @Override
            public Number getDrawPoint(Series.Data valueIn, Number windowBound){
                return function.valueToCoord(valueIn, 0, (centerX.doubleValue())/100);
            }
        }
        );

        getXAxis();
        getYAxis();
        dots = new ArrayList<>();
        mirrored = new ArrayList<>();
        paths = new ArrayList<>();
        Pane childRoot = new Pane();

        Circle centerCircle = new Circle(centerX.get(),centerY.get(),5);
        centerCircle.centerXProperty().bind(centerX);
        centerCircle.centerYProperty().bind(centerY);
        dots.add(centerCircle);
        for(int i = 0; i<division; i++) {
            getBindedDots((i*divisionLength), (i+1)*divisionLength);
            childRoot.getChildren().addAll(getBindedPath((i*divisionLength), (i+1)*divisionLength));
        }

        centerCircle.radiusProperty().bind(dots.get(division*divisionLength).centerYProperty().subtract(centerY).multiply(-1));
        centerCircle.setFill(Color.color(0,1,1));
        //dots.get(cutOffIndex).centerYProperty().addListener((observable, oldValue, newValue) -> System.out.println(newValue.doubleValue()-centerY.get()));
        childRoot.getChildren().addAll(dots);
        dots.get(0).toFront();
        dots.get(0).setOpacity(1);
        childId = childRoot.toString();
        this.getRoot().getChildren().add(childRoot);

    }

    /**
     *
     *
     * @param startIndex    Starting index of this division.
     * @param endIndex      size or length of this division. Non inclusive
     */
    protected void getBindedDots(int startIndex, int endIndex){
        for(int i = startIndex; i<endIndex; i++){
            Circle circle = new Circle(1);
            circle.centerXProperty().bind(series.getData(i).xPositionProperty());
            circle.centerYProperty().bind(series.getData(i).yPositionProperty());
            circle.setOpacity(0);
            //Circle mirroredCircle = new Circle(1);
            //mirroredCircle.centerXProperty().bind(circle.centerXProperty());
            //mirroredCircle.centerYProperty().bind(circle.centerYProperty().multiply(-1).add(centerY+centerY));
            dots.add(circle);
            //dots.add(mirroredCircle);
        }
        for(int i = startIndex; i<endIndex; i++){
            Circle mirroredCircle = new Circle(1);
            mirroredCircle.centerYProperty().bind(dots.get(i+1).centerYProperty());
            mirroredCircle.centerXProperty().bind(dots.get(i+1).centerXProperty().multiply(-1).add(centerX).add(centerX));
            mirroredCircle.setOpacity(0);
            mirrored.add(mirroredCircle);
        }


    }

    /**
     *
     *
     * @param startIndex    Starting index of this division.
     * @param endIndex      size or length of this division. Non inclusive
     */
    protected Path getBindedPath(int startIndex, int endIndex){
        MoveTo start = new MoveTo();
        start.xProperty().bind(dots.get(startIndex+1).centerXProperty());
        start.yProperty().bind(dots.get(startIndex+1).centerYProperty());
        Path newPath = new Path(start);
        for(int i = startIndex; i<endIndex; i++) {
            LineTo newLine = new LineTo();
            newLine.xProperty().bind(dots.get(i+1).centerXProperty());
            newLine.yProperty().bind(dots.get(i+1).centerYProperty());
            newPath.getElements().add(newLine);
        }
        for(int i = endIndex-1; i>=startIndex; i--) {
            LineTo newLine = new LineTo();
            newLine.xProperty().bind(mirrored.get(i).centerXProperty());
            newLine.yProperty().bind(mirrored.get(i).centerYProperty());
            newPath.getElements().add(newLine);
        }
        ClosePath closer = new ClosePath();
        newPath.getElements().add(closer);

        double colororor = (double)endIndex/(double)cutOffIndex;
        System.out.println(colororor);
        Color c = Color.color(0,1,colororor,.9);
        //dots.get(startIndex).setFill(c);
        newPath.setFill(c);
        return newPath;

    }
    @Override
    protected void adjustRange(int outLier) {

    }
}
