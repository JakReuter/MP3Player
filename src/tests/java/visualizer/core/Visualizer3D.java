package visualizer.core;

import MP3Player.mp3Player.visualizer.core.Series;
import MP3Player.mp3Player.visualizer.core.Visualizer;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.event.EventHandler;
import javafx.scene.*;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.*;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.stage.FileChooser;

import javax.swing.tree.TreeNode;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.TreeMap;

public class Visualizer3D extends Visualizer {
    private Point3d[][] points;
    private int numOfColumns;
    private SubScene subScene;
    private Group subSceneRoot;
    private Group objectGroup;
    private double sceneWidth = 800;
    private double sceneHeight = 800;
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
    private final double CAMERA_DISTANCE = -100;


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

    private RingTree treeMesh;

    /**
     * Given points 0-n on the X axis, construct proportionate
     * amount of points to extrude the given point into the z axis
     * by means of rotation about the Y axis, i.e. rings.
     *      //tree
     *      addLevel(int level);                    -Forloop the addNode function with node(cos/sin(theta*i),cos/sin(theta*i), defualt_z),
     *      ArrayList<Node> getLevel(int level);
     *
     *      //Node
     *
     * Bind all constructed points of a ring to the initial given point so
     * that updates to the original data will be represented in the
     * third dimension but modified in 2 dimensions.
     *      //tree
     *      bindLevel(int level); - sets data onChangeListener to new listener that calls and updates meshView.getPoints().set(*eachIndexOfDataArray*, newValue)
     *
     *      //Node
     *      boolean isBinded;
     *      function bind();        -someway to bind a node objects z
     *
     * Create faces between points for the meshview. For (x=0),
     * create faces between the root and each of the outer points.
     * For (x>=1), create faces between each inner point and the 3        //could ommit prev angles to preserve resources, i.e. pi/4 points on circle 1, just pi/8 points on circle 2 instead
     * outer points nearest to it, which will give an amount of faces
     * equal to twice the amount of inner points. Next, a face must be
     * made between each inner node and the outer node they both share.
     * The total amount of faces will then be 3*amount of inner points.
     *      //Node
     *      boolean hasMultipleParents();
     *      Node[] parents;
     *      arrayList<Node> children;
     *
     *
     *
     *
     * Textures and faces should remain same as data changes. TexCoords
     * will not use yValue and therefore will remain constant. Faces uses
     * indices of texCoords and Points so faces will remain constant
     *
     *///TODO:draw out everything that we want to happen in order
    public class RingTree {
        private TriangleMesh triangleMesh;
        private int entryIndex;
        private int max_level;
        private int countPerRing;
        private ArrayList<ArrayList<TreeNode>> dataArray;

        /**
         * Amount of nodes per level =
         * Lvl 0 = 1
         * Lvl 1 = countPerRing
         * lvl 2 = lvl1.nodes*2
         * lvl 3 = lvl2.nodes*2
         *
         * Amount of faces per level =
         * lvl 0 = countPerRing
         * lvl 1 =
         */
        protected class TreeNode{
            int index;
            int level;
            double xValue;
            double yValue;
            double zValue;

            /**
             * Node class should only be used to populate the faces
             * and textures
             * @param xValue
             * @param level
             */
            protected TreeNode(double xValue, double zValue, int level, int index){
                this.xValue=Math.floor((xValue)*100)/100;
                this.yValue=0;
                this.zValue=Math.floor((zValue)*100)/100;
                //System.out.print("   ("+this.xValue+","+this.zValue+") --> ");
                //System.out.println("("+(this.xValue+sceneHeight*.5)+","+(float)(this.zValue+sceneWidth*.5)+")");
                this.level=level;
                this.index=index;
                buildPoints((float)(this.xValue), 0f, (float)(this.zValue)); // every node creation adds the associated point to mesh
                buildTextures((float)(this.xValue), (float)(this.zValue)); // every node creation adds the associated texture coordinate to mesh
            }

            public int getIndex() {
                return index;
            }

            public int getLevel() {
                return level;
            }

            public void setXValue(double xValue) {
                this.xValue = xValue;
            }
            public void setYValue(double yValue) {
                this.yValue = yValue;
            }
            public void setZValue(double zValue) {
                this.zValue = zValue;
            }
            public float getXFloat(){
                return (float)xValue;
            }
            public float getYFloat(){
                return (float)yValue;
            }
            public float getZFloat(){
                return (float)zValue;
            }
        }

        public RingTree(int countPerRing, int rings){
            triangleMesh = new TriangleMesh();
            entryIndex=0;
            max_level = 0;
            this.countPerRing=countPerRing;
            dataArray = new ArrayList<>();

        }

        public void addRoot(double xValue, ObjectProperty<Number> yProperty){
            dataArray.add(0, new ArrayList<>());
            dataArray.get(0).add(new TreeNode(xValue, yProperty.get().doubleValue(), 0,0));
            entryIndex++;
            Sphere testDot = new Sphere(5);
            testDot.setTranslateX(0);
            testDot.setTranslateY(0);
            testDot.setTranslateZ(0);
            objectGroup.getChildren().add(testDot);
        }

        /**
         * Adds a new node to the tree
         * @param level acts as x index outside of tree, i.g. for visualizer: bands 0 - 128;
         */
        public void add(int level, double xValue, ObjectProperty<Number> yProperty){
            TreeNode newNode = new TreeNode(xValue, yProperty.get().doubleValue(), level, entryIndex);
            dataArray.add(level, new ArrayList<>());
            dataArray.get(level).add(newNode);
            entryIndex++;

            populateLevel(level);
            yProperty.addListener(getRingListener(level));
            // bind children
        }

        public void populateLevel(int level){
            double theta_delta = (2*Math.PI)/(countPerRing*Math.pow(2,(level-1)));
            double amplitude =  dataArray.get(level).get(0).xValue;
            ArrayList<TreeNode> levelArray = dataArray.get(level);
            for(int i=1; i<(countPerRing*Math.pow(2,(level-1))); i++){
                levelArray.add(new TreeNode(amplitude*Math.cos(i*theta_delta), amplitude*Math.sin(i*theta_delta), level, entryIndex));
                entryIndex++;
            }
        }

        public ChangeListener<Number> getRingListener(int level){
            ChangeListener<Number> newListener = (observable, oldValue, newValue) -> {
                ArrayList<TreeNode> levelArray = dataArray.get(level);
                for(TreeNode node : levelArray){
                    node.setYValue(newValue.doubleValue());
                    triangleMesh.getPoints().set(node.index*3+1, node.getYFloat());
                }
            };
            return newListener;
        }

        /**
         * potentially perform function on points here
         * i.e. scaling output for scene size
         * @param in 1 to n complete vector coords [x1,y1,z1,...,xn,yn,zn]
         */
        public void buildPoints(float... in){
           /** for(int i = 0; i<in.length; i+=3){
                Sphere testDot = new Sphere(5);
                testDot.setTranslateX(in[0]);
                testDot.setTranslateY(in[1]);
                testDot.setTranslateZ(in[2]);
                objectGroup.getChildren().add(testDot);
            }**/
            triangleMesh.getPoints().addAll(in);
        }
        public void buildTextures(float... in){
            float[] newTexCoords = new float[in.length];                    //         0
            for(int i = 0; i<newTexCoords.length;i++){                      //1     2     3     4
                newTexCoords[i]=(float)((in[i])/sceneHeight);//5  6  7  8  9 10 11 12
            }
            triangleMesh.getTexCoords().addAll(newTexCoords);
        }
        public void buildFaces(){
            max_level=dataArray.size();

            ArrayList<TreeNode> currentLevel = dataArray.get(1);
            ArrayList<TreeNode> parentLevel = dataArray.get(0);

            int parentId = parentLevel.get(0).getIndex();
            int childId = currentLevel.get(0).getIndex();

            for(int i = 0; i<currentLevel.size()-1; i++){
                triangleMesh.getFaces().addAll(parentId,parentId,childId,childId,childId+1,childId+1);
                childId = currentLevel.get(i+1).getIndex();
            }
            triangleMesh.getFaces().addAll(parentId,parentId,childId,childId,1,1); //add face between last element of array and first.

            /**
             * Expected Faces with 4 ring size
             * (1,5) [12,5,1],    [4,12,1],   [5,6,1]
             * (2,7) [6,7,2],     [1,6,2],    [7,8,2]
             * (3,9) [8,9,3],     [2,8,3],    [9,10,3]
             * (4,11)[10,11,4]    [3,10,4]    [11,12,4]
             */
            for(int i = 2; i<max_level; i++) {
                parentLevel = currentLevel;
                currentLevel = dataArray.get(i);
                for(int j = 0; j<parentLevel.size(); j++){
                    parentId = parentLevel.get(j).getIndex(); //1 2 3 4
                                                                //5 7 9 11
                    childId = currentLevel.get(j*2).getIndex(); //0, 2, 4, 6... center child of parent
                    if(j==0){
                        triangleMesh.getFaces().addAll(childId+currentLevel.size()-1,childId+currentLevel.size()-1,childId,childId,parentId,parentId); //Left child, center child, parent
                        triangleMesh.getFaces().addAll(parentId+parentLevel.size()-1,parentId+parentLevel.size()-1,childId+currentLevel.size()-1,childId+currentLevel.size()-1,parentId,parentId); //Left Parent, shared center child, Right Parent

                    } else {
                        triangleMesh.getFaces().addAll(childId-1,childId-1,childId,childId,parentId,parentId);//Left child, center child, parent
                        triangleMesh.getFaces().addAll(parentId-1,parentId-1,childId-1,childId-1,parentId,parentId);//Left Parent, shared center child, Right Parent

                    }
                    triangleMesh.getFaces().addAll(childId,childId,childId+1,childId+1,parentId,parentId); //Center child, right child, parent  // all points should be calculable like this here.
                }
            }

        }
        public TriangleMesh getTriangleMesh(){
            if(triangleMesh.getFaces().size()<entryIndex)buildFaces();
            return triangleMesh;
        }
    }

    public RingTree createRingTree(int countPerRing, int rings){
        return (treeMesh=new RingTree(countPerRing, rings));
    }

    public RingTree getRingTree(){
        return treeMesh;
    }

    public void displayRingTree(){
        MeshView meshView = new MeshView(treeMesh.getTriangleMesh());
        meshView.setMaterial(new PhongMaterial(Color.FIREBRICK));
        objectGroup.getChildren().add(meshView);
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
        subScene = new SubScene(subSceneRoot, sceneWidth, sceneHeight, true, SceneAntialiasing.DISABLED);
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
        cameraPlaneGroup.rx.setAngle(45);
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

    public void addNode(Node newNode){
        objectGroup.getChildren().add(newNode);
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
