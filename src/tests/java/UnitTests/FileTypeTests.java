package UnitTests;

import MP3Player.application.MP3Application;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.stage.Stage;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

public class FileTypeTests
{
    MP3Application mp3Application = new MP3Application();

    @Before
    //ok so I don't really understand this part, but it's what's needed to get the application open for unit testing
    public void init()
    {
        Thread thread = new Thread(new Runnable() {

            // Initializes the JavaFx Platform
            @Override
            public void run() {
                new JFXPanel();
                Platform.runLater(new Runnable() {

                    //initialize the player
                    @Override
                    public void run() {
                        try {
                            mp3Application.start(new Stage()); // Create and
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });

        //keeps the application open for 5 seconds to run tests (can make it longer if needed)
        thread.start();
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //accepted file types
    @Test
    public void mp3()
    {
        System.out.println(mp3Application.getStage().getTitle());
        assert false;
    }

    @Test
    public void mp4()
    {
        assert false;
    }



    //not accepted file types
    @Test
    public void exe()
    {
        assert false;
    }

    @Test
    public void jpeg()
    {
        assert false;
    }
}
