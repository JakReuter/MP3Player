package UnitTests;

import MP3Player.application.MP3Application;
import MP3Player.util.general.FileErrorHandler;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.control.Alert;
import javafx.scene.media.Media;
import javafx.stage.Stage;
import org.checkerframework.checker.units.qual.A;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public class FileTypeTests
{
    MP3Application mp3Application = new MP3Application();
    FileErrorHandler fileErrorHandler = null;
    private String PATH_DEFAULT = new File(System.getProperty("user.dir")).toURI().toString();
    private String PATH_DEFAULT_NOT_URI = new File(System.getProperty("user.dir")).toString();

    private Object mp3Alert = "make these different";
    private Object handlerAlert = "so don't initialze the same";

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
                            mp3Application.start(new Stage());
                            mp3Alert = mp3Application.getAlert();
                            fileErrorHandler = new FileErrorHandler(mp3Application.getAlert());
                            handlerAlert = fileErrorHandler.getAlert();
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
        String path = "/out/production/AnotherMp3Test/TestFiles/Legit.mp3";

        if(fileErrorHandler.fileCheck(PATH_DEFAULT_NOT_URI + path) != 0)
        {
            Assert.fail("File path or type not accepted");
        }

        mp3Application.setMediaPlayer(PATH_DEFAULT + path);

        String actual = mp3Application.getMediaPlayer().getStatus().toString();

        Assert.assertEquals(expected, actual);
    }

    //youtube to mp3 file
    @Test
    public void mp3_2()
    {
        String expected = "READY";

        //set media
        String path = "/out/production/AnotherMp3Test/TestFiles/NotLegit.mp3";

        if(fileErrorHandler.fileCheck(PATH_DEFAULT_NOT_URI + path) != 0)
        {
            Assert.fail("File path or type not accepted");
        }

        mp3Application.setMediaPlayer(PATH_DEFAULT + path);

        String actual = mp3Application.getMediaPlayer().getStatus().toString();

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void mp4()
    {
        String expected = "READY";

        //set media
        String path = "/out/production/AnotherMp3Test/TestFiles/video.mp4";

        if(fileErrorHandler.fileCheck(PATH_DEFAULT_NOT_URI + path) != 0)
        {
            Assert.fail("File path or type not accepted");
        }

        mp3Application.setMediaPlayer(PATH_DEFAULT + path);

        String actual = mp3Application.getMediaPlayer().getStatus().toString();

        Assert.assertEquals(expected, actual);
    }

    //not accepted file types (currently unable to test as the error handler popup isn't detectible with this code, instead check
    //to see if the alert objects are the same between the handler and main application

    @Test
    public void alertSame()
    {
        Assert.assertEquals(mp3Alert, handlerAlert);
    }

    /*
    @Test
    public void exe()
    {
        //this FileErrorHandler.fileCheck() method needs to be called when file paths are accepted, this is just checking if it works

        String path = "/out/production/AnotherMp3Test/TestFiles/executable.exe";

        if(fileErrorHandler.fileCheck(PATH_DEFAULT_NOT_URI + path) == 0)
        {
            Assert.fail("File path or type should not be accepted");
        }
    }

    @Test
    public void jpeg()
    {
        //this FileErrorHandler.fileCheck() method needs to be called when file paths are accepted, this is just checking if it works

        String path = "/out/production/AnotherMp3Test/TestFiles/image.jpg";

        if(fileErrorHandler.fileCheck(PATH_DEFAULT_NOT_URI + path) == 0)
        {
            Assert.fail("File path or type should not be accepted");
        }
    }

    @Test
    public void invalidPath()
    {
        //this FileErrorHandler.fileCheck() method needs to be called when file paths are accepted, this is just checking if it works

        String path = "lolNope";

        if(fileErrorHandler.fileCheck(PATH_DEFAULT_NOT_URI + path) == 0)
        {
            Assert.fail("File path or type should not be accepted");
        }
    }

     */

    @Test
    public void m4a()
    {
        String expected = "READY";

        //set media
        String path = "/out/production/AnotherMp3Test/TestFiles/legit2.m4a";

        if(fileErrorHandler.fileCheck(PATH_DEFAULT_NOT_URI + path) != 0)
        {
            Assert.fail("File path or type not accepted");
        }

        mp3Application.setMediaPlayer(PATH_DEFAULT + path);

        String actual = mp3Application.getMediaPlayer().getStatus().toString();

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void audioBook()
    {
        String expected = "READY";

        //set media
        String path = "/out/production/AnotherMp3Test/TestFiles/audioBook.mp3";

        if(fileErrorHandler.fileCheck(PATH_DEFAULT_NOT_URI + path) != 0)
        {
            Assert.fail("File path or type not accepted");
        }

        mp3Application.setMediaPlayer(PATH_DEFAULT + path);

        String actual = mp3Application.getMediaPlayer().getStatus().toString();

        Assert.assertEquals(expected, actual);
    }
}
