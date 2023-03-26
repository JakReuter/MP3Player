package MP3Player.application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class TestApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/TestView.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 600, 400);
//        Font.loadFont(getClass().getResourceAsStream("fonts/RobotoMono-Regular.ttf"), 16);
        //scene.getStylesheets().add(getClass().getResource("MP3.css").toExternalForm());
        stage.setTitle("Test");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}