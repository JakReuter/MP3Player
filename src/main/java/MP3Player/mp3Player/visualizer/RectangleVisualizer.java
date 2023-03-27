package MP3Player.mp3Player.visualizer;

import MP3Player.util.general.Tabable;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.chart.NumberAxis;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;

/**
 * Classic Rectangles in a Tabable node.
 *  could be used to make EQ look fancy.
 *  todo: Should switch to averages
 * @Authors
 */
public class RectangleVisualizer extends Tabable {

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



    public RectangleVisualizer(int bands){
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
