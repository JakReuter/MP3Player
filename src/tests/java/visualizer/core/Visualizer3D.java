package visualizer.core;

import MP3Player.mp3Player.visualizer.core.Series;
import MP3Player.mp3Player.visualizer.core.Visualizer;
import javafx.beans.property.ObjectProperty;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import org.junit.Test;

import javafx.scene.shape.Line;
import java.util.ArrayList;

public class Visualizer3D extends Visualizer {
    private Point3d[][] points;
    private int numOfColumns;


    protected Pane childRoot;

    public class Point3d{
        private double scalarFromCamPos;
        private double[] coords3d;
        private double[] rotateAngles;
        private double[] originCoords3d;
        private double focalLength;
        private double xValue;
        private double yValue;
        private final double ORIGIN = 200;

        private ObjectProperty<Double> xPosition;
        private ObjectProperty<Double> yPosition;

        public void setxValue(double xValue) {this.xValue = xValue;}
        public void setyValue(double yValue) {this.yValue = yValue;}
        public double getxValue() {return this.xValue;}
        public double getyValue() {return this.yValue;}



        //Animation Properties
        public double getxPosition() { return xPosition.get(); }
        public void setxPosition(double value) {xPosition.set(value); }
        public ObjectProperty<Double> xPositionProperty() { return xPosition; }
        public double getyPosition() { return yPosition.get(); }
        public void setyPosition(double value) {yPosition.set(value); }
        public ObjectProperty<Double> yPositionProperty() { return yPosition; }


        public Point3d(double x, double y){
            coords3d = new double[]{x-ORIGIN,y-ORIGIN,356};
            originCoords3d = new double[]{x-ORIGIN,y-ORIGIN,356};

        }

        public void setRotateAngles(double x, double y, double z, double focalLength){
            rotateAngles = new double[]{x,y,z};
            setXRotate();
            setYRotate();
            setZRotate();
            this.focalLength=focalLength;

        }
        public void setXRotate(){
            double rotatedY = Math.cos(rotateAngles[0])*coords3d[1]-Math.sin(rotateAngles[0])*coords3d[2];
            double rotatedZ = Math.sin(rotateAngles[0])*coords3d[1]+Math.cos(rotateAngles[0])*coords3d[2];
            coords3d[1] = rotatedY;
            coords3d[2] = rotatedZ;
        }

        public void setYRotate(){
            double rotatedX = Math.cos(rotateAngles[1])*coords3d[0]+Math.sin(rotateAngles[1])*coords3d[2];
            double rotatedZ = -1*Math.sin(rotateAngles[1])*coords3d[0]+Math.cos(rotateAngles[1])*coords3d[2];
            coords3d[0] = rotatedX;
            coords3d[2] = rotatedZ;
        }

        public void setZRotate(){
            double rotatedX = Math.cos(rotateAngles[2])*coords3d[0]-Math.sin(rotateAngles[2])*coords3d[1];
            double rotatedY = Math.sin(rotateAngles[2])*coords3d[0]+Math.cos(rotateAngles[2])*coords3d[1];
            //Math.sin(rotateAngles[2]);
            coords3d[0] = rotatedX;
            coords3d[1] = rotatedY;
        }

        public double getX(){
            return (focalLength/coords3d[2])*coords3d[0]+ORIGIN;

        }

        public double getY(){
            return (focalLength/coords3d[2])*coords3d[1]+ORIGIN;
        }

    }

    public Visualizer3D(int bands, String name) {
        super(bands,name);
        childRoot = new Pane();
    }

    @Override
    protected void adjustRange(int outLier) {
    }


    //test
    public double flipYforDisplay(double yValueIn){
        return (yValueIn*-1)+800;
    }

    //test
    public double getCoordFromIndex(double index){
        return (400/numOfColumns)*index;
    }

    //Test
    public void populatePointArray(int widthCount){
        numOfColumns = widthCount;
        points = new Point3d[numOfColumns][numOfColumns];
        for(int xVal=0; xVal<numOfColumns; xVal++){
            for(int yVal=0; yVal<numOfColumns; yVal++){
                points[xVal][yVal]=new Point3d(getCoordFromIndex(xVal),getCoordFromIndex(yVal));
            }
        }
    }//Test
    public void populatePointArray(int widthCount, double xRotateAngle, double yRotateAngle, double zRotateAngle, double focal){
        numOfColumns = widthCount;
        points = new Point3d[numOfColumns][numOfColumns];
        for(int xVal=0; xVal<numOfColumns; xVal++){
            for(int yVal=0; yVal<numOfColumns; yVal++){
                Point3d newPoint = new Point3d(getCoordFromIndex(xVal),getCoordFromIndex(yVal));
                newPoint.setRotateAngles(xRotateAngle,yRotateAngle,zRotateAngle, focal);
                points[xVal][yVal]=newPoint;
            }
        }
    }

    //test
    public ArrayList<Line> getXLine(){
        ArrayList<Line> out = new ArrayList<>();
        for(int yVal=0; yVal<numOfColumns; yVal++){
            for(int xVal=0; xVal<numOfColumns-1; xVal++){
                Point3d start = points[xVal][yVal];
                Point3d end = points[xVal+1][yVal];
                Line newLine = new Line(start.getX(),start.getY(),end.getX(),end.getY());
                childRoot.getChildren().add(newLine);
                newLine.setLayoutX(0);
                newLine.setLayoutY(0);
                out.add(newLine);
            }
        }
        return out;
    }

    //test
    public ArrayList<Line> getYLine(){
        ArrayList<Line> out = new ArrayList<>();
        for(int xVal=0; xVal<numOfColumns; xVal++){
            for(int yVal=0; yVal<numOfColumns-1; yVal++){
                Point3d start = points[xVal][yVal];
                Point3d end = points[xVal][yVal+1];
                Line newLine = new Line(start.getX(),start.getY(),end.getX(),end.getY());
                childRoot.getChildren().add(newLine);
                newLine.setLayoutX(0);
                newLine.setLayoutY(0);
                out.add(newLine);
            }
        }
        return out;
    }



    public void finalizeWindow(){
        getYLine();
        getXLine();
        //childRoot.getChildren().addAll(getXLine());
       // childRoot.getChildren().addAll(getYLine());
        childId=childRoot.toString();
        childRoot.setMinHeight(400);
        childRoot.setMinWidth(400);
        this.getRoot().getChildren().add(childRoot);

    }

}
