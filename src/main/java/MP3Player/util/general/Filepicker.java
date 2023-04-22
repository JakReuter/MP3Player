package MP3Player.util.general;

import java.io.File;
import java.util.List;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public final class Filepicker extends Application {
    ReadMP3Tag readmusictag = new ReadMP3Tag();

    @Override
    public void start(final Stage stage) {
        stage.setTitle("Choose Music file");

        final FileChooser fileChooser = new FileChooser();

        final Button openButton = new Button("Choose one music file");
        final Button openMultipleButton = new Button("Choose multiple music files");

        openButton.setOnAction(
                new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(final ActionEvent e) {
                        File file = fileChooser.showOpenDialog(stage);
                        if (file != null) {
                            readmusictag.getMetadata(file.getPath());
                            System.out.println(file.getPath());
                        }
                    }
                });

        openMultipleButton.setOnAction(
                new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(final ActionEvent e) {
                        List<File> list =
                                fileChooser.showOpenMultipleDialog(stage);
                        if (list != null) {
                            for (File file : list) {
                                readmusictag.getMetadata(file.getPath());
                                System.out.println(file.getPath());
                            }
                        }
                    }
                });


        final GridPane inputPane = new GridPane();

        GridPane.setConstraints(openButton, 0, 0);
        GridPane.setConstraints(openMultipleButton, 1, 0);
        inputPane.setHgap(6);
        inputPane.setVgap(6);
        inputPane.getChildren().addAll(openButton, openMultipleButton);

        final Pane rootGroup = new VBox(12);
        rootGroup.getChildren().addAll(inputPane);
        rootGroup.setPadding(new Insets(12, 12, 12, 12));

        stage.setScene(new Scene(rootGroup));
        stage.show();
    }

    public static void main(String[] args) {
        Application.launch(args);
    }

}