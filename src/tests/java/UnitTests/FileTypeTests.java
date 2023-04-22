package UnitTests;

import MP3Player.application.MP3Application;
import MP3Player.util.general.FileErrorHandler;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.media.Media;
import javafx.stage.Stage;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public class FileTypeTests
{
    MP3Application mp3Application = new MP3Application();
    private String PATH_DEFAULT = new File(System.getProperty("user.dir")).toString(); //.toURI().toString();

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

        //keeps the application open for the duration
        thread.start();
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //a legit mp3 file
    @Test
    public void mp3_1()
    {
        String expected = "READY";

        //set media
        String path = PATH_DEFAULT + "/out/production/AnotherMp3Test/TestFiles/Legit.mp3";

        FileErrorHandler.fileCheck(path);

        mp3Application.setMediaPlayer(path);

        String actual = mp3Application.getMediaPlayer().getStatus().toString();

        Assert.assertEquals(expected, actual);
    }

    //youtube to mp3 file
    @Test
    public void mp3_2()
    {
        String expected = "READY";

        //set media
        String path = PATH_DEFAULT + "/out/production/AnotherMp3Test/TestFiles/NotLegit.mp3";
        mp3Application.setMediaPlayer(path);

        String actual = mp3Application.getMediaPlayer().getStatus().toString();

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void mp4()
    {
        String expected = "READY";

        //set media
        String path = PATH_DEFAULT + "/out/production/AnotherMp3Test/TestFiles/video.mp4";
        mp3Application.setMediaPlayer(path);

        String actual = mp3Application.getMediaPlayer().getStatus().toString();

        Assert.assertEquals(expected, actual);
    }

    //not accepted file types
    @Test
    public void exe()
    {
        String expected = "UNKOWN";

        //set media
        String path = PATH_DEFAULT + "/out/production/AnotherMp3Test/TestFiles/executable.exe";
        mp3Application.setMediaPlayer(path);

        String actual = mp3Application.getMediaPlayer().getStatus().toString();

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void jpeg()
    {
        String expected = "UNKOWN";

        //set media
        String path = PATH_DEFAULT + "/out/production/AnotherMp3Test/TestFiles/image.jpeg";
        mp3Application.setMediaPlayer(path);

        String actual = mp3Application.getMediaPlayer().getStatus().toString();

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void invalidPath()
    {
        String expected = "UNKOWN";

        //set media
        String path = PATH_DEFAULT + "lolNope";
        mp3Application.setMediaPlayer(path);

        String actual = mp3Application.getMediaPlayer().getStatus().toString();

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void m4a()
    {
        String expected = "READY";

        //set media
        String path = PATH_DEFAULT + "/out/production/AnotherMp3Test/TestFiles/legit2.m4a";
        mp3Application.setMediaPlayer(path);

        String actual = mp3Application.getMediaPlayer().getStatus().toString();

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void audioBook()
    {
        String expected = "READY";

        //set media
        String path = PATH_DEFAULT + "/out/production/AnotherMp3Test/TestFiles/audioBook.mp3";
        mp3Application.setMediaPlayer(path);

        String actual = mp3Application.getMediaPlayer().getStatus().toString();

        Assert.assertEquals(expected, actual);
    }
}
