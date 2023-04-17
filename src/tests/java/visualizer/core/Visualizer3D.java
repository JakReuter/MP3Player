package visualizer.core;

import MP3Player.mp3Player.visualizer.core.Series;
import MP3Player.mp3Player.visualizer.core.Visualizer;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.EventHandler;
import javafx.scene.*;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;

import javafx.scene.shape.Line;
import javafx.scene.shape.Mesh;
import javafx.scene.shape.Shape3D;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;

public class Visualizer3D extends Visualizer {
    private Point3d[][] points;
    private int numOfColumns;
    private SubScene subScene;
    private Group subSceneRoot;
    private Group objectGroup;
    private double sceneWidth = 400;
    private double sceneHeight = 400;
    //private double sceneMaxDimension = Math.sqrt((sceneWidth*sceneWidth+sceneHeight*sceneHeight)/2);

    private double mousePosX;
    private double mousePosY;
    private double mouseOldX;
    private double mouseOldY;
    private double mouseDeltaX;
    private double mouseDeltaY;

    /**
     * Camera for 3d depth scene
     */
    private PerspectiveCamera camera;
    /**
     * We want the camera to be fixed at the origin so
     * we create a group at the origin and attach
     * the camera at a distance behind the plane
     *
     */
    private TransformableGroup cameraPlaneGroup;
    private final double CAMERA_NEAR_CLIP = 0.1;
    private final double CAMERA_FAR_CLIP = 10000;
    private final double CAMERA_DISTANCE = -1100;


    protected Pane rootOfDisplay;

    /**
     * A group that holds 3 rotation transforms and
     *  a scalar, which allows easier manipulation
     *  of the transformation of a node without
     *  indexing node.getTransforms()
     */
    private class TransformableGroup extends Group {

        public Rotate rx = new Rotate();
        public Rotate ry = new Rotate();
        public Rotate rz = new Rotate();
        public Scale s = new Scale();

        public TransformableGroup() {
            super();
            rx.setAxis(Rotate.X_AXIS);
            ry.setAxis(Rotate.Y_AXIS);
            rz.setAxis(Rotate.Z_AXIS);
            getTransforms().addAll(rx, ry, rz, s);
        }
    }
    public class Series3d extends Series {

        public Series3d(Number[] arr, int size, Visualizer vis) {
            super(arr, size, vis);
        }
        public class Data3d extends Series.Data {

            private Number zValue;
            private ObjectProperty<Number> zPosition;

            public Data3d(Number x, Number y) {
                this(x, y, 0);
            }

            public Data3d(Number x, Number y, Number z) {
                super(x, y);
                zPosition = new SimpleObjectProperty<Number>(this, "yPosition");
            }

            //Animation Properties
            public Number getzPosition() {
                return zPosition.get();
            }

            public void setzPosition(Number value) {
                zPosition.set(value);
            }

            public ObjectProperty<Number> zPositionProperty() {
                return zPosition;
            }
        }
    }
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
        rootOfDisplay = new Pane();
        objectGroup = new Group();
    }

    @Override
    protected void adjustRange(int outLier) {

    }


    public Scene buildScene() {
        Scene mainScene = new Scene(rootOfDisplay, sceneWidth, sceneHeight);
        buildSubScene();
        return mainScene;
    }

    public void buildSubScene() {
        subSceneRoot = new Group();
        subScene = new SubScene(subSceneRoot, sceneWidth, sceneHeight, true, SceneAntialiasing.BALANCED);
        subSceneRoot.getChildren().addAll(objectGroup);
        buildCamera();
        rootOfDisplay.getChildren().addAll(subScene);
        handleMouse(subScene);
    }

    /**
     * Builds and initializes the perspective Camera
     */
    private void buildCamera() {
        camera = new PerspectiveCamera(true);
        subScene.setCamera(camera);
        cameraPlaneGroup = new TransformableGroup();
        subSceneRoot.getChildren().add(cameraPlaneGroup);
        cameraPlaneGroup.getChildren().add(camera);
        cameraPlaneGroup.rz.setAngle(0);

        camera.setNearClip(CAMERA_NEAR_CLIP);
        camera.setFarClip(CAMERA_FAR_CLIP);
        camera.setTranslateZ(CAMERA_DISTANCE);
        cameraPlaneGroup.ry.setAngle(0);
        cameraPlaneGroup.rx.setAngle(0);
    }
    /**
     * Sets up mouse handlers for various mouse triggered events.
     * @param scene	Draw3D Scene Group
     */
    private void handleMouse(SubScene scene) {
        scene.setOnScroll(new EventHandler<ScrollEvent>() {
            @Override public void handle(ScrollEvent se) {
                objectGroup.setScaleX(objectGroup.getScaleX() + se.getDeltaY()*.01);
                objectGroup.setScaleY(objectGroup.getScaleY() + se.getDeltaY()*.01);
                objectGroup.setScaleZ(objectGroup.getScaleZ() + se.getDeltaY()*.01);
            }
        });

//		scene.setOnMouseClicked(new EventHandler<MouseEvent>() {
//          @Override public void handle(MouseEvent me) {
//            	if (me.getClickCount() == 1) {
//                    clearView();
//              }
//          }
//      });

//        scene.setOnMouseClicked(new EventHandler<MouseEvent>() {
//            @Override public void handle(MouseEvent me) {
//                if (me.getClickCount() > 1) {
//                    reset();
//                }
//            }
//        });

        scene.setOnMousePressed(me -> {
            mousePosX = me.getSceneX();
            mousePosY = me.getSceneY();
            mouseOldX = me.getSceneX();
            mouseOldY = me.getSceneY();
        });

        scene.setOnMouseDragged(me -> {
            mouseOldX = mousePosX;
            mouseOldY = mousePosY;
            mousePosX = me.getSceneX();
            mousePosY = me.getSceneY();
            mouseDeltaX = -(mousePosX - mouseOldX);
            mouseDeltaY = -(mousePosY - mouseOldY);

            double modifier = .2;

            if (me.isPrimaryButtonDown()) {
                cameraPlaneGroup.ry.setAngle(cameraPlaneGroup.ry.getAngle() - mouseDeltaX*modifier);
                cameraPlaneGroup.rx.setAngle(cameraPlaneGroup.rx.getAngle() + mouseDeltaY*modifier);
//                   System.out.println("X= " + cameraXform.rx.getAngle() +
//                    		", Y= " + cameraXform.ry.getAngle() +
//                    		", Z= " + cameraXform.rz.getAngle());
            }
        });
    }

    public void manuallyAdd3dShape(Shape3D newShape){
        System.out.println("Adding shape:" + newShape.toString()+" at x:"+newShape.getTranslateX()+" y:"+newShape.getTranslateY()+" z:"+newShape.getTranslateZ());
        objectGroup.getChildren().add(newShape);
    }

    public void manuallyAdd3dShape(Collection<? extends Shape3D> list){
        //System.out.println("Adding shape:" + newShape.toString()+" at x:"+newShape.getTranslateX()+" y:"+newShape.getTranslateY()+" z:"+newShape.getTranslateZ());
        objectGroup.getChildren().addAll(list);
    }



    //test
    public double flipYforDisplay(double yValueIn){
        return (yValueIn*-1)+800;
    }

    //test
    public double getCoordFromIndex(double index){
        return (400/numOfColumns)*index;
    }
    //test
    public double getCoordFromIndex(double index,int max){
        return (400/max)*index;
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
                rootOfDisplay.getChildren().add(newLine);
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
                rootOfDisplay.getChildren().add(newLine);
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
        childId= rootOfDisplay.toString();
        rootOfDisplay.setMinHeight(400);
        rootOfDisplay.setMinWidth(400);
        this.getRoot().getChildren().add(rootOfDisplay);

    }


}
