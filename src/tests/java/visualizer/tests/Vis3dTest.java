package visualizer.tests;

import com.sun.javafx.fxml.builder.TriangleMeshBuilder;
import javafx.application.Application;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ObservableDoubleValue;
import javafx.collections.*;
import javafx.scene.Scene;
import javafx.scene.control.Slider;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.*;
import javafx.stage.Stage;
import sun.java2d.pipe.SpanShapeRenderer;
import visualizer.core.Visualizer3D;

import java.util.ArrayList;
//TODO: add rings that are binded per index, having binding per ring

public class Vis3dTest extends Application {
    Visualizer3D vizzy;
    Shape3D testObject;
    ArrayList<Shape3D> shapes;
    int dim;
    SimpleDoubleProperty scalar;

    @Override
    public void start(Stage stage) throws Exception {
        vizzy = new Visualizer3D(11, "3d");
        shapes = new ArrayList<>();
        scalar = new SimpleDoubleProperty(25);
        //SphereTest();
        PointFieldTest(65);
        MeshBuilderTest();
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
                    scalar.set(scalar.get()+1);
                    System.out.println(scalar);
                    break;
                case DOWN:
                    scalar.set(scalar.get()-1);
                    System.out.println(scalar);
                    break;
            }
        });
    }


    public static void main(String[] args) {
        launch();
    }
}
