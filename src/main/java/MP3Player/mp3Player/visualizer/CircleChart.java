package MP3Player.mp3Player.visualizer;

import MP3Player.mp3Player.visualizer.core.Axis;
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

    public CircleChart(int bands, String name) {

        super(bands, name);
        centerX=new SimpleDoubleProperty(200);
        centerY=new SimpleDoubleProperty(200);
        centerX.bind(getRoot().prefWidthProperty().multiply(.5));
        centerY.bind(getRoot().maxHeightProperty().multiply(.5));
        double circularRange = Math.PI/bands;
        double circularRangeTrans = Math.PI/2;
        setXAxis(new Axis(0,1,bands-1,22050, (in, translate, scale)-> {
            //System.out.println("center sent to circle:"+centerX.get());
            double out = ((in.getyValue().doubleValue() + 100)*(scale) * Math.cos(in.getxValue().doubleValue() * circularRange+circularRangeTrans)) + centerX.get();
            //System.out.print("["+in.getxValue().doubleValue()+"] = x: "+out);
            return out;
        }));
        setYAxis(new Axis(-100,0,0,24, (in, translate, scale)->{
            //System.out.println("Y scale sent to circle:"+scale);
            double out =centerY.get()-((in.getyValue().doubleValue()+100)*(scale*.4)*Math.sin(in.getxValue().doubleValue()*circularRange+circularRangeTrans));
            //System.out.print(" y: "+(out) + "\n");
            return out;
        }));

        getXAxis();
        getYAxis();
        dots = new ArrayList<>();
        mirrored = new ArrayList<>();
        paths = new ArrayList<>();
        getBindedDots();
        Pane childRoot = new Pane();
        childRoot.getChildren().addAll(dots);
        childRoot.getChildren().addAll(getBindedPath());
        dots.get(0).toFront();
        dots.get(0).setOpacity(1);
        childId = childRoot.toString();
        this.getRoot().getChildren().add(childRoot);

    }


    protected void getBindedDots(){
        Circle centerCircle = new Circle(centerX.get(),centerY.get()-30,40);
        centerCircle.centerXProperty().bind(centerX);
        centerCircle.centerYProperty().bind(centerY);
        dots.add(centerCircle);
        for(int i = 0; i<bands; i++){
            Circle circle = new Circle(1);
            circle.centerXProperty().bind(series.getData(i).xPositionProperty());
            circle.centerYProperty().bind(series.getData(i).yPositionProperty());
            //Circle mirroredCircle = new Circle(1);
            //mirroredCircle.centerXProperty().bind(circle.centerXProperty());
            //mirroredCircle.centerYProperty().bind(circle.centerYProperty().multiply(-1).add(centerY+centerY));
            dots.add(circle);
            //dots.add(mirroredCircle);
        }
        for(int i = 0; i<bands; i++){
            Circle mirroredCircle = new Circle(1);
            mirroredCircle.centerYProperty().bind(dots.get(i+1).centerYProperty());
            mirroredCircle.centerXProperty().bind(dots.get(i+1).centerXProperty().multiply(-1).add(centerX).add(centerX));
            int finalI = i;
            mirrored.add(mirroredCircle);
        }

        centerCircle.radiusProperty().bind(dots.get(bands).centerYProperty().multiply(.1));

    }

    protected Path getBindedPath(){
        MoveTo start = new MoveTo();
        start.xProperty().bind(dots.get(1).centerXProperty());
        start.yProperty().bind(dots.get(1).centerYProperty());
        Path newPath = new Path(start);
        for(int i = 0; i<bands; i++) {
            LineTo newLine = new LineTo();
            newLine.xProperty().bind(dots.get(i+1).centerXProperty());
            newLine.yProperty().bind(dots.get(i+1).centerYProperty());
            newPath.getElements().add(newLine);
        }
        for(int i = mirrored.size()-1; i>=0; i--) {
            LineTo newLine = new LineTo();
            newLine.xProperty().bind(mirrored.get(i).centerXProperty());
            newLine.yProperty().bind(mirrored.get(i).centerYProperty());
            newPath.getElements().add(newLine);
        }
        ClosePath closer = new ClosePath();
        newPath.getElements().add(closer);

        Color c = Color.color(0,1,0,1);
        dots.get(0).setFill(c);
        newPath.setFill(c);
        return newPath;

    }
    @Override
    protected void adjustRange(int outLier) {

    }
}
