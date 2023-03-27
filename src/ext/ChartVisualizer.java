package ext;

//import com.sun.scenario.effect.impl.sw.java.JSWBlend_SRC_OUTPeer;
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

public class ChartVisualizer extends Tabable{

    NumberAxis dispXAxis;
    NumberAxis dispYAxis;
    double MAXV;

    @FXML
    public HBox Rectangles;

    public void update(int index, double magnitude){
        if(index<Rectangles.getChildren().size()) {
            Rectangles.getChildren().get(index).setScaleY((magnitude + 120) / 60.0);
        }
    }



    public ChartVisualizer(int bands){
        super("ChartVisualizer");
        Rectangles = new HBox();
        Rectangles.setAlignment(Pos.valueOf("CENTER"));
        for(int i = 0; i < bands; i++){
            Rectangle newRec = new Rectangle(180.0/bands,60);
            newRec.setFill(Paint.valueOf("DODGERBLUE"));
            newRec.setStroke(Paint.valueOf("BLACK"));
            Rectangles.getChildren().add(newRec);
        }

        this.getRoot().setPrefSize(200,200);
        this.getRoot().getChildren().add(Rectangles);
    }

}
