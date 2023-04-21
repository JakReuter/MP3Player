package MP3Player.util.general;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * Acts as a tab that can transfer to a
 *  window or another tab pane while preserve
 *  its contents. Unless a ui element is created
 *  in a bad scope like eq sliders.
 * @Authors
 */
public abstract class Tabable extends Stage{
    public String getTabName() {
        return tabName;
    }
    public String childId = "";

    private String tabName;
    private AnchorPane root;

    private ReadOnlyDoubleProperty parentWidthProperty;

    public Tabable(String name){
        root = new AnchorPane();
        tabName = name;
    }

    public Tabable(String name, Node toTabable){
        childId = toTabable.toString();
        root = new AnchorPane();
        root.setPrefSize(10,10);
        toTabable.prefWidth(root.getPrefWidth());
        AnchorPane.setLeftAnchor(toTabable,0.0);
        AnchorPane.setRightAnchor(toTabable,0.0);
        AnchorPane.setTopAnchor(toTabable,0.0);
        AnchorPane.setBottomAnchor(toTabable,0.0);
        root.getChildren().add(toTabable);
        System.out.println("this tabable root is:"+root.toString());
        tabName = name;
    }

    public Tab getTab(ObjectProperty<Tab> dragTabProp){
        Tab outTab = new Tab("", root);
        Label label = new Label(tabName);
        outTab.setGraphic(label);
        //if(root.getChildren().size()>0) {
        //    childId = root.getChildren().get(0).toString();
        //}
        if(childId.contains("AnchorPane")){
            System.out.println("oh no!");
        }
        label.setOnDragDetected(event -> {
            Dragboard dragboard = label.startDragAndDrop(TransferMode.MOVE);
            ClipboardContent cB = new ClipboardContent();
            cB.putString("tab");
            dragboard.setContent(cB);
            dragTabProp.set(outTab);
            event.consume();
        });


        //root.prefWidthProperty().addListener((observable, oldValue, newValue) -> System.out.println("tabable root prefwidth changed to:"+newValue));
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

    public DoubleProperty prefWidthProperty(){
        return root.prefWidthProperty();
    }
    public DoubleProperty prefHeightProperty(){
        return root.prefHeightProperty();
    }

}
