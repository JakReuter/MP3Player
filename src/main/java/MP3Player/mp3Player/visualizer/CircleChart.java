package MP3Player.mp3Player.visualizer;

import MP3Player.mp3Player.visualizer.core.Axis;
import MP3Player.mp3Player.visualizer.core.Visualizer;
import javafx.beans.binding.ListBinding;
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
    protected double centerX = 200;
    protected double centerY = 200;

    public CircleChart(int bands, String name) {

        super(bands, name);
        double circularRange = Math.PI/bands;
        double circularRangeTrans = Math.PI/2;
        setXAxis(new Axis(0,1,bands,22050, (in, translate, scale)-> {
            double out = ((in.getyValue().doubleValue() + 100) * Math.cos(in.getxValue().doubleValue() * circularRange+circularRangeTrans)) + centerX;
            //System.out.print("["+in.getxValue().doubleValue()+"] = x: "+out);
            return out;
        }));
        setYAxis(new Axis(-100,0,0,24, (in, translate, scale)->{
            double out =centerY-((in.getyValue().doubleValue()+100)*Math.sin(in.getxValue().doubleValue()*circularRange+circularRangeTrans));
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
        childId = childRoot.toString();
        this.getRoot().getChildren().add(childRoot);

    }


    protected void getBindedDots(){
        Circle centerCircle = new Circle(centerX,centerY-100,25);
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
            mirroredCircle.centerXProperty().bind(dots.get(i+1).centerXProperty().multiply(-1).add(centerX+centerX));
            mirrored.add(mirroredCircle);
        }

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

        Color c = Color.color(0,1,0,.5);
        newPath.setFill(c);
        return newPath;

    }
    @Override
    protected void adjustRange(int outLier) {

    }
}
