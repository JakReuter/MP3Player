package MP3Player.mp3Player.visualizer;

import MP3Player.mp3Player.visualizer.core.Visualizer;

public class CircleChart extends Visualizer {

    public CircleChart(int bands, String name) {
        super(bands, name);
    }

    @Override
    protected void adjustRange(int outLier) {

    }
}
