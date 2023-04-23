package visualizer.tests;

import com.sun.javafx.fxml.builder.TriangleMeshBuilder;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.beans.InvalidationListener;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.binding.FloatBinding;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableDoubleValue;
import javafx.beans.value.ObservableValue;
import javafx.collections.*;
import javafx.scene.Scene;
import javafx.scene.control.Slider;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import visualizer.core.Visualizer3D;

import java.io.File;
import javafx.util.Duration;
import java.util.ArrayList;
import java.util.Observable;
//TODO: add rings that are binded per index, having binding per ring

public class Vis3dTest extends Application {
    Visualizer3D vizzy;
    Shape3D testObject;
    ArrayList<Shape3D> shapes;
    ArrayList<ObjectProperty<Number>> properties;
    ArrayList<CustomProperty> Customproperties;
    int dim;
    SimpleDoubleProperty scalar;
    Timeline animator;


    protected final String PATH_DEFAULT = System.getProperty("user.dir");
    private final String PATH_MVMT = PATH_DEFAULT+"/out/production/AnotherMp3Test/song/4th Mvmt.mp3";

    MediaPlayer mediaPlayer;
    @Override
    public void start(Stage stage) throws Exception {
        //Todo: assume 1920p resolution is max pixel precision



        Media newMedia;
        File file = new File(PATH_MVMT);
        newMedia = new Media(file.toURI().toString());
        mediaPlayer = new MediaPlayer(newMedia);
        mediaPlayer.setAudioSpectrumNumBands(20);
        mediaPlayer.setAudioSpectrumThreshold(-100);
        mediaPlayer.setAudioSpectrumInterval(0.04);
        vizzy = new Visualizer3D(11, "3d");
        shapes = new ArrayList<>();
        properties = new ArrayList<>();
        MeshGridTest(400);

        //newMeshTest();
        //scalar = new SimpleDoubleProperty(25);
        //MeshTreeTestMan(20,10,4 );
        //SphereTest();
       // PointFieldTest(65);
       // MeshBuilderTest();
        Scene scene = vizzy.buildScene();
        handleKey(scene);
        stage.setTitle("test");
        stage.setScene(scene);
        stage.show();
    }

    public void GridTest(){
    }
    public void SphereTest(){
        testObject = new Sphere(1);
        vizzy.manuallyAdd3dShape(testObject);
    }
    public void CubeTest(){
        testObject = new Box(200,200,200);
        //newSphere.setHeight(50);
        vizzy.manuallyAdd3dShape(testObject);
    }
    public void PointFieldTest(int dimension){
        dim=dimension;
        for(int i = 0; i<dimension;i++){
            for(int j = 0; j<dimension;j++){
                Sphere dot = new Sphere(1);
                double xCoord = vizzy.getCoordFromIndex(i,dimension)-200;
                double zCoord = vizzy.getCoordFromIndex(j,dimension)-200;
                //double yCoord = 25*Math.sin(.1*Math.sqrt(xCoord*xCoord+zCoord*zCoord));
                double distFromOrigin = Math.sqrt(xCoord*xCoord+zCoord*zCoord);
                DoubleBinding binding = new DoubleBinding() {
                    {
                        super.bind(scalar);
                    }
                    @Override
                    protected double computeValue() {
                        return scalar.doubleValue()*Math.sin(.1*distFromOrigin);
                    }
                };
                dot.translateYProperty().bind(binding);
                dot.setTranslateX(xCoord);
                dot.setTranslateZ(zCoord);
                //dot.setTranslateY(yCoord);
                shapes.add(dot);
            }
        }

        //vizzy.manuallyAdd3dShape(shapes);
        //newSphere.setHeight(50);
    }

    public void MeshAnimationTest(int pixels, Duration dur, TriangleMesh mesh){
        animator = new Timeline();
        ArrayList<KeyValue> keyValues = new ArrayList<>();
        ArrayList<KeyFrame> keyFrames = new ArrayList<>();
        Customproperties.get(0).set(50f);
       /** for(int i = 0; i<pixels-1; i++){
            keyValues.add(new KeyValue(Customproperties.get(i),Customproperties.get(i).getValue()));
            keyValues.add(new KeyValue(Customproperties.get(i+1),Customproperties.get(i+1).getValue()));
            keyFrames.add(new KeyFrame(dur.multipliedBy(i),keyValues.get(0)));
        }**/
        Float targetValue = -5f;
        for(int i = 0; i <pixels-1; i++) {
            animator.getKeyFrames().addAll(
                    new KeyFrame(dur.multiply(i), new KeyValue(Customproperties.get(i), targetValue, Interpolator.EASE_IN),new KeyValue(Customproperties.get(i+1), 0f, Interpolator.EASE_IN)),
                    new KeyFrame(dur.multiply(i+1), new KeyValue(Customproperties.get(i), 0f,Interpolator.EASE_IN),new KeyValue(Customproperties.get(i+1), targetValue, Interpolator.EASE_IN))
            );
        }



    }


    public void MeshGridTest(int pixels){
        TriangleMesh triangleMesh = new TriangleMesh();
        for(int x = 0; x<pixels; x++){
            for(int z = 0; z<pixels; z++){
                triangleMesh.getPoints().addAll(x,0f,z);
                triangleMesh.getTexCoords().addAll(x/pixels,z/pixels);
            }
        }
        for(int x = 0; x<pixels-1;x++) {
            int currentCol = x * pixels;
            int nextCol=(x+1)*pixels;
            for (int y = 0; y < pixels - 1; y++) {
                int currentRow = currentCol+y;
                int nextRow = nextCol+y;
                triangleMesh.getFaces().addAll(
                        currentRow,currentRow, nextRow, nextRow, currentRow+1,currentRow+1,
                        currentRow+1, currentRow+1, nextRow+1, nextRow+1,nextRow,nextRow
                );
            }
        }
        MeshView meshView = new MeshView(triangleMesh);

        PopulatePropertyArray(pixels, triangleMesh);
        MeshAnimationTest(pixels, new Duration(500), triangleMesh);
        meshView.setMaterial(new PhongMaterial(Color.FIREBRICK));
       // meshView.setDrawMode(DrawMode.LINE);
        vizzy.manuallyAdd3dShape(meshView);
    }

    public void newMeshTest(){
        mediaPlayer.setAudioSpectrumListener((timestamp, duration, magnitudes, phases) -> {

            if(timestamp>5&&timestamp<5.4) {
                GridPane grid = new GridPane();

                for (int i = 0; i < magnitudes.length; i++) {

                    double amplitude = i;
                    double radius = amplitude * 2; // Adjust as needed

                    // Create a polyline to represent the circle
                    Polyline polyline = new Polyline();
                    for (int j = 0; j < 8; j++) {
                        double theta = j * 2 * Math.PI / 8;
                        double x = radius * Math.cos(theta);
                        double z = radius * Math.sin(theta);
                        polyline.getPoints().addAll(x, 0.0, z); // Add the x, y, and z coordinates of each point
                    }

                    // Set the color of the polyline based on the band index or amplitude
                    polyline.setStroke(Color.RED); // Adjust as needed

                    // Add the polyline to the GridPane
                    grid.add(polyline, i, 0);
                }
                vizzy.addNode(grid);
            }
    });
        mediaPlayer.play();
        //vizzy.manuallyAdd3dShape(new Sphere(50));
    }

    public void MeshTreeTestMan(int xCount, double xScalar, int countPerRing){
        Visualizer3D.RingTree tree = vizzy.createRingTree(countPerRing,xCount);
        for(int i = 0; i<xCount; i++){
            properties.add(new SimpleObjectProperty<>(0));
        }
        mediaPlayer.setAudioSpectrumListener((timestamp, duration, magnitudes, phases) -> {
            for(int i = 0; i < xCount; i++){
                properties.get(i).set(100+magnitudes[i]);
            }
        });
        tree.addRoot(0,properties.get(0));
        for(int i = 1; i<xCount; i++){
            tree.add(i,i*xScalar,  properties.get(i));
        }
        vizzy.displayRingTree();
        mediaPlayer.play();

    }

    public void PopulatePropertyArray(int pixels, TriangleMesh mesh){
        Customproperties = new ArrayList<>();
        for(int i = 0; i<pixels; i++){
            Customproperties.add(new CustomProperty(pixels, i, mesh));
        }
    }

    public class CustomProperty extends ObjectProperty<Float>{
            int pixels;
            int colum;
            TriangleMesh mesh;
            Float value;

            public CustomProperty(int pixels, int colum, TriangleMesh mesh){
                super();
                this.value=0f;
                this.pixels=pixels;
                this.colum=colum;
                this.mesh=mesh;
            }

            @Override
            public void bind(ObservableValue<? extends Float> observable) {

            }

            @Override
            public void unbind() {

            }

            @Override
            public boolean isBound() {
                return false;
            }

            @Override
            public Object getBean() {
                return null;
            }

            @Override
            public String getName() {
                return null;
            }

            @Override
            public Float get() {
                return this.value;
            }

            @Override
            public void addListener(ChangeListener<? super Float> listener) {

            }

            @Override
            public void removeListener(ChangeListener<? super Float> listener) {

            }

            @Override
            public void addListener(InvalidationListener listener) {

            }

            @Override
            public void removeListener(InvalidationListener listener) {

            }

        @Override
        public void setValue(Float value) {
            this.value=value;
            //System.out.println("setting points "+(colum*pixels)+"-"+(colum*pixels+pixels)+" to yvalue: "+value);
            for(int i =0; i<pixels; i++){
                int index = (i+(colum*(pixels)));
                mesh.getPoints().set(index*3+1, (float)value);
            }
        }

        @Override
        public Float getValue() {
            return value;
        }

        @Override
            public void set(Float value) {
                this.setValue(value);
            }

        @Override
        public String toString() {
            return "CustomProperty("+colum+")";
        }
    }


    public void MeshTest(){
        for(int xC = 0; xC<2;xC++){
            for(int yC = 0; yC<2;yC++){
                Sphere dot = new Sphere(5);
                dot.setTranslateX(xC*50);
                dot.setTranslateY(yC*50);
                dot.setTranslateZ(0);
                shapes.add(dot);
            }
        }
        TriangleMesh mesh = new TriangleMesh();
        mesh.getPoints().addAll(
                0,0,0,
                0,50,0,
                50,0,0,
                50,50,0
        );
        mesh.getTexCoords().addAll(
                0f,0f,
                0f,1f,
                1f,0f,
                1f,1f
        );
        mesh.getFaces().addAll(
                0,0,2,2,1,1,
                1,1,2,2,3,3
        );
        vizzy.manuallyAdd3dShape(shapes);
        MeshView test = new MeshView(mesh);
        test.setMaterial(new PhongMaterial(Color.FIREBRICK));
        vizzy.manuallyAdd3dShape(test);
    }
    public void MeshBuilderTest(){
        TriangleMesh newMesh = new TriangleMesh();
        ObservableFloatArray points = FXCollections.observableFloatArray();
        ObservableFloatArray textures = FXCollections.observableFloatArray();
        ObservableIntegerArray faces = FXCollections.observableIntegerArray();
        shapes.get(0).setMaterial(new PhongMaterial(Color.GREEN));
        for (int i = 0; i < shapes.size(); i++) {
                Shape3D dot1 = shapes.get(i);
                //Shape3D dot2 = shapes.get(dim + k+i);

                float f1X = (float) dot1.getTranslateX();
                float f1Y = (float) dot1.getTranslateY();
                float f1Z = (float) dot1.getTranslateZ();
                points.addAll(f1X, f1Y, f1Z);
            int finalI = i;
            dot1.translateYProperty().addListener((observable, oldValue, newValue) -> {
                    newMesh.getPoints().set(finalI *3+1,newValue.floatValue());
                });

                textures.addAll(buildTextures(f1X, f1Z));
                if ((i > dim)/*&&((i-dim)%2)==1*/) {
                    int j = i - dim;
                    faces.addAll(
                            j-1,j-1,i-1,i-1,j,j,
                            i-1,i-1,i,i,j,j);
                }
            }
        newMesh.getPoints().addAll(points);
        newMesh.getTexCoords().addAll(textures);
        newMesh.getFaces().addAll(faces);
        MeshView test = new MeshView(newMesh);

        test.setMaterial(new PhongMaterial(Color.FIREBRICK));
        vizzy.manuallyAdd3dShape(test);
    }
    protected float[] buildTextures(float... in){
        float[] out = new float[in.length];
        for(int i = 0; i<out.length;i++){
            out[i]=((in[i]+200)/400);
        }
        return out;
    }
    public void PathTest(){
        for(int i=0;i< dim/*shapes.size()-dim-1*/; i++){
            Path newPath = new Path();
            MoveTo firstMove = new MoveTo(0,0);
            LineTo line = new LineTo();

        }
    }
    public void ConnectPointTest(){
        ArrayList<Shape3D> lines = new ArrayList<>();
        for(int i = 0; i<dim;i++){
            for(int j = 0; j<dim;j++){

            }
        }
    }

    public void handleKey(Scene scene){
        scene.setOnKeyPressed( keyEvent -> {
            KeyCode code = keyEvent.getCode();
            switch(code){
                case UP:
                    for(int i = 0; i<properties.size(); i++){
                        properties.get(i).set(properties.get(i).getValue().doubleValue()+i+1*(Math.random()*100)+1);
                    }
                    break;
                case DOWN:
                    for(int i = 0; i<properties.size(); i++){
                        properties.get(i).set(properties.get(i).getValue().doubleValue()-i-1);
                    }
                    break;
                case SPACE:
                    animator.play();
                    break;
            }
        });
    }


    public static void main(String[] args) {
        launch();
    }
}
