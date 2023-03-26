package MP3Player.mp3Player.visualizer;

import MP3Player.util.general.Tabable;
import javafx.beans.value.ChangeListener;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.AnchorPane;

public class XYChartVisualizer extends Tabable {

    private AreaChart chart;
    private XYChart.Series<Number, Number> amplitudes;
    private XYChart.Series<Number, Number> EQGraph;
    private ChangeListener<Number>[] eqListener;
    private XYChart.Data[] magPlots;
    private XYChart.Data[] eqPlots;
    private int eq_bands;
    private double low;
    private double high;
    private double scalar;


    public void update(int x, double y){
        double dat = y;
        if(dat<low){
            low=dat;
            System.out.println("new low: " + low + " at " + x);
            scalar=Math.abs(24.0/(high-low));
        }
        if(dat>high){
            high=dat;
            System.out.println("new high: " + high + " at " + x);
            System.out.println("scalar: " + scalar + " times "+(dat-low)+" = "+ ((dat-low)*scalar));
        }
        dat=(dat-low)*scalar;
        magPlots[x].setYValue(dat);
    }

    public XYChartVisualizer(String name, int bands) {

        super(name);
        eq_bands = bands;
        low = 100;
        high = 0;
        scalar = 1;


        chart = new AreaChart(new NumberAxis(), new NumberAxis(0,24,6));
        amplitudes = new XYChart.Series<>();
        magPlots = new XYChart.Data[bands];
        System.out.println("Bands: "+bands+" --> "+ (Math.log10(bands+1)/Math.log10(2))*3000);
        for(int i = 0; i< magPlots.length; i++){
            magPlots[i] = new XYChart.Data<>(Math.log10(i+1)/Math.log10(2)*3000, 0f);
            amplitudes.getData().add(magPlots[i]);
        }
        //visualizerChart = new AreaChart<Number, Number>(new LogAxis(1, BANDS), visualizerChart.getYAxis());
        chart.setAnimated(false);
        chart.getData().add(amplitudes);
        chart.setCreateSymbols(false);
        initializeEQ();

        this.getRoot().getChildren().add(chart);
    }

    public void initializeEQ(){
        eqPlots = new XYChart.Data[10];
        eqListener = new ChangeListener[10];

        EQGraph = new XYChart.Series<>();

        int scalar = eq_bands/10;
        for(int i = 0; i<10; i++){
            eqPlots[i] = new XYChart.Data<>(Math.pow(2,i+5), 12);
            EQGraph.getData().add(eqPlots[i]);
            int finalI = i;
            eqListener[finalI] = (observable, oldValue, newValue) -> {eqPlots[finalI].setYValue(((double)newValue+24)/2);
                System.out.println(newValue);};
        }

        chart.getData().add(EQGraph);
    }

    public ChangeListener<Number>[] getEQListener(){
        return eqListener;
    }
}
