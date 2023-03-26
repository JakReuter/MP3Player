import javafx.beans.InvalidationListener;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.media.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

//not sure if this is exactly how we want to be doing this but I figure this way it won't interfere with other stuff until I can get it working

public class TimeControl
{
    MediaPlayer mediaPlayer = null;

    TimeControl(MediaPlayer mp)
    {
        mediaPlayer = mp;
    }

    //does a time skip forward in seconds
    public void fastForward(double seconds)
    {
        //adds the required skip time to whatever the current time is and then converts that to a duration
        double newTime = mediaPlayer.getCurrentTime().toSeconds() + seconds;
        Duration asDuration = Duration.seconds(newTime);

        mediaPlayer.seek(asDuration);
    }

    //does a time skip backward in seconds
    public void rewind(double seconds)
    {
        //adds the required skip time to whatever the current time is and then converts that to a duration
        double newTime = mediaPlayer.getCurrentTime().toSeconds() - seconds;
        Duration asDuration = Duration.seconds(newTime);

        mediaPlayer.seek(asDuration);
    }

    public void changePlaybackSpeed(double speed)
    {
        //negative values aren't allowed
        if(speed < 0)
        {
            System.err.println("Negative playback speeds aren't allowed, please choose a value greater than or equal to 0");
            speed = .1;
        }

        mediaPlayer.setRate(speed);
    }

    /*
    This appears to already have been taken care of elsewhere so umm, yea, thanks whoever did that



    //this should bind the sliders value to a label that will display current time in minutes, should be called right after initialization of media player/scene
    public void bindSliderToDisplay(MediaPlayer mp, Slider durationSlider, Label timeLabel)
    {
        timeLabel.textProperty().bind(Bindings.format("%.2f", durationSlider.valueProperty()));
    }

    //this will set the values on the slider equal to the song duration, should be called on every new song
    public void setSliderDuration(MediaPlayer mp, Slider durationSlider)
    {
        durationSlider.setMax(mp.getTotalDuration().toMinutes());


        //bind the duration to the slider value so when it is changed it seeks a new part of the song
        Duration currentSliderDuration = new Duration(0);

        mp.seek(currentSliderDuration);
    }

     */
}
