package MP3Player.application;

import MP3Player.database.Database;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;

public class MP3Application extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        Database.connect();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/MainView.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1440, 1028);
//        Font.loadFont(getClass().getResourceAsStream("fonts/RobotoMono-Regular.ttf"), 16);
        //scene.getStylesheets().add(getClass().getResource("MP3.css").toExternalForm());
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
        stage.setOnCloseRequest((event) -> {
            Database.close();
        });
    }

    public static void main(String[] args) {
        launch();
    }
}