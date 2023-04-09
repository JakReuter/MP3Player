package MP3Player.mp3Player.visualizer.core;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.shape.Circle;

import java.util.ArrayList;

//TODO: add functionality to round data into buckets
//TODO: Impact functionality - time dependent that visualizes spikes
    //Could be done with buckets, assign weights to ranges and just add them together
public class Series {

    private Visualizer visualizer;
    private ArrayList<Visualizer> visualizers; //used for updating multiple charts at once

    private int arraySize;
    private Data[] dataArray;

    private double bucket; //null if not bucket

    public class Data{

        private Number xValue;
        private Number yValue;

        private double displayX;
        private double displayY;

        private ObjectProperty<Number> xPosition;
        private ObjectProperty<Number> yPosition;

        public Data(Number x,Number y) {
            xValue = x;
            yValue = y;

            xPosition = new SimpleObjectProperty<Number>(this, "xPosition");
            yPosition = new SimpleObjectProperty<Number>(this, "yPosition");
        }

        public void setxValue(Number xValue) {this.xValue = xValue;}
        public void setyValue(Number yValue) {this.yValue = yValue;}
        public Number getxValue() {return this.xValue;}
        public Number getyValue() {return this.yValue;}
        

        //Animation Properties
        public Number getxPosition() { return xPosition.get(); }
        public void setxPosition(Number value) {xPosition.set(value); }
        public ObjectProperty<Number> xPositionProperty() { return xPosition; }
        public Number getyPosition() { return yPosition.get(); }
        public void setyPosition(Number value) {yPosition.set(value); }
        public ObjectProperty<Number> yPositionProperty() { return yPosition; }
    }

    public Series(Number[] arr, int size, Visualizer vis){
        visualizer = vis;
        arraySize = size;
        bucket=0;
        dataArray = new Data[size];
        for(int i = 0; i < arraySize; i++){
            dataArray[i] = new Data(i, arr[i]);
        }
    }

    public Series(int size, Visualizer vis){
        visualizer = vis;
        arraySize = size;
        bucket=0;
        dataArray = new Data[size];
        initEmptyArray();
    }

    public void bindNewVisualizer(Visualizer vis){
        if(visualizers==null){
            visualizers=new ArrayList<>();
            visualizers.add(visualizer);
        }
        visualizers.add(vis);
    }

    //possibly taking intervals of data instead of averages for performance
    public Series(Number[] arr, int size, double bucketFactor){
        //this(arr, size);
        //bucket=bucketFactor;

    }

    public void initEmptyArray(){
        for(int i = 0; i < arraySize; i++){
            dataArray[i] = new Data(i, 0);
        }
    }

    public Data getData(int index){
        return dataArray[index];
    }

    //TODO: undo multiple series attempt
    public void change(int index, Number y){
        dataArray[index].setyValue(y);
        if(visualizers==null) {
            visualizer.changedData(dataArray[index]);
        } else {
            for(Visualizer v : visualizers){
                v.changedData(dataArray[index]);
            }
            System.out.println(visualizers.size());
        }

    }

    public void updateAll(Number[] array){
        if(bucket>0){

        } else {
            for(Number i : array){
                //visualizer.changedData(dataArray[i]);
            }
        }
        visualizer.drawRefresh();
    }
}
