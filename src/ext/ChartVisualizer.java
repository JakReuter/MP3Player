package ext;

import com.sun.scenario.effect.impl.sw.java.JSWBlend_SRC_OUTPeer;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.layout.*;
import javafx.scene.media.*;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.chart.NumberAxis;

import java.net.URL;

public class ChartVisualizer extends Stage{
    NumberAxis dispXAxis;
    NumberAxis dispYAxis;
    double MAXV;
    @FXML
    public HBox Rectangles;
    @FXML
    public void initialize(){
        MAXV = 60;
        System.out.println(
                "visualizer initialized"
        );

    }

    public void setRectangles1(HBox in){
        Rectangles = in;


    }

    public void update(int index, double magnitude){
        Rectangles.getChildren().get(index).setScaleY((magnitude+120)/60.0);
    }



    public ChartVisualizer(){
        Rectangles = new HBox();
        Rectangles.setAlignment(Pos.valueOf("CENTER"));
        for(int i = 0; i < 20; i++){
            Rectangle newRec = new Rectangle(10,60);
            newRec.setFill(Paint.valueOf("DODGERBLUE"));
            newRec.setStroke(Paint.valueOf("BLACK"));
            Rectangles.getChildren().add(newRec);
        }
        try {
            Tab windowTest = new Tab("anal", Rectangles);
            Tab winn = new Tab("an");
            TabPane tabs = new TabPane(winn, windowTest);
            windowTest.setStyle("-fx-background-color: #09C");
            System.out.println();
            AnchorPane root = new AnchorPane(tabs);
            tabs.setOnMouseDragged(event -> {
                    tabs.setLayoutX(event.getSceneX());
                    tabs.setLayoutY(event.getSceneY());

            });

            root.setPrefSize(200,200);
            Scene scene = new Scene(root, 250, 250);
            this.setTitle("spectrum");
            this.setScene(scene);
            this.show();
        } catch (Exception e){
            System.out.println("could not load file");
            e.printStackTrace();
        }
    }

}
