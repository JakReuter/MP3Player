package MP3Player.mp3Player.visualizer;

import MP3Player.util.general.Tabable;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Path;

/**
 * This class serves to replace AreaChart/XYChart. Will
 * be able to display log axis for visualizer. Made
 * abstract to not throw errors for now.
 */
public abstract class ChartVisualizer extends Visualizer {
    private Path mainPath;
    private Path eqPath;

    private BorderPane borderPane;

    private VBox YAxis;
    private Region canvas;
    private HBox XAxis;


    public ChartVisualizer(int bands) {
        super(bands,"Chart");

        mainPath = new Path();
        canvas = new Region();
        canvas.setPrefSize(200,200);
        //canvas.heightProperty().addListener((observable, oldValue, newValue) -> );
        this.getRoot().getChildren().add(mainPath);
    }



    protected void getYTicks(){

    }
}
