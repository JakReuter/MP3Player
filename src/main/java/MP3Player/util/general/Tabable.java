package MP3Player.util.general;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public abstract class Tabable extends Stage{
    public String getTabName() {
        return tabName;
    }

    private String tabName;
    private AnchorPane root;

    public Tabable(String name){
        root = new AnchorPane();
        tabName = name;
    }

    public Tab getTab(){
        Tab outTab = new Tab(tabName, root);
        return outTab;
    }
    public Stage getWindow()//pose a new stage and show it ?
    {
        this.setTitle(tabName);
        this.setScene(new Scene(root,250, 250));
        return this;
    }
    public AnchorPane getRoot(){
        return root;
    }

    public void setChildAction(EventHandler<ActionEvent> value){
        Button newButton = new Button("Tab->Window");
        newButton.setOnAction(value);
        root.getChildren().add(newButton);
    }
}
