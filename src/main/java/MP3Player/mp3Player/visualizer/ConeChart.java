package MP3Player.mp3Player.visualizer;

import javafx.scene.shape.CubicCurveTo;
import javafx.stage.Window;

public class ConeChart extends ChartVisualizer{

    public ConeChart(int bands, Window stage) {
        super(bands, stage);
    }

    @Override
    protected void getBindedCurve(){

        for(int i = 0; i<bands; i++){
            double xRet = xAxis.getDrawPoint(i,700).doubleValue();
            double yRet = 100;
            // System.out.println("x: " + i);
            CubicCurveTo newCurve = new CubicCurveTo(xRet, yRet, xRet, yRet, xRet, yRet);

            newCurve.controlY1Property().bind(series.getData(i).yPositionProperty());
            newCurve.controlX2Property().bind(series.getData(i).xPositionProperty());

            if(i+1<bands){
                newCurve.controlX1Property().bind(series.getData(i+1).yPositionProperty());
                newCurve.controlY2Property().bind(series.getData(i+1).xPositionProperty());
            }
            curves.add(newCurve);
        }
    }
}
