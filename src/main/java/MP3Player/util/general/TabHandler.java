package MP3Player.util.general;


import javafx.beans.property.ObjectProperty;
import javafx.collections.ListChangeListener;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Arrays;
//TODO: make tabs refocus after dragging
// TODO: get rid of default tabs
/**
 * Holds and handles a bunch of tabs.
 * Turns into windows
 * @Authors Brendan Reuter
 */
public class TabHandler {
    private TabPane mainPane;
    private ArrayList<Tabable> apps;
    private Stage mainStage;
    protected ObjectProperty<Tab> draggedTab;

    public TabHandler(){
        mainPane = new TabPane(new Tab("Default"));
        apps = new ArrayList<>();
    }
    public TabHandler(ObjectProperty<Tab> draggedTab){
        mainPane = new TabPane(new Tab("Default"));
        apps = new ArrayList<>();
        this.draggedTab = draggedTab;
    }

    public void addApp(Tabable... tApps){
        apps.addAll(Arrays.asList(tApps));
        System.out.println("tried to add app");
    }

    public void toWindow(Tabable tabable){
        tabable.setOnCloseRequest(event -> toTab(tabable));
        mainPane.getSelectionModel().clearSelection();
        System.out.println(tabable.getTabName());
        tabable.getWindow().show();
    }

    public void toTab(Tabable tabable){
        mainPane.getTabs().add(tabable.getTab(draggedTab));
        if(tabable.isShowing()){
            tabable.close();
        }
    }

    public void tabToApp(Tab tab){

    }

    public void DisplayNewWindow(){
        if(mainStage==null){
            mainStage = new Stage();
            mainStage.setTitle("Main");
        }
        Tab[] temp = new Tab[apps.size()];
        for(int i=0; i< apps.size();i++){
            temp[i]=apps.get(i).getTab(draggedTab);
        }

        mainPane = new TabPane(temp);

        mainStage.setScene(new Scene(mainPane,250, 250));
        mainStage.show();
    }

    public void setDraggingTabProperty(ObjectProperty<Tab> draggingTabProperty){
        draggedTab = draggingTabProperty;
    }

    public TabPane getPane(){
        Tab[] temp = new Tab[apps.size()];
        for(int i=0; i< apps.size();i++){
            Tabable app = apps.get(i);
            temp[i]=app.getTab(draggedTab);
        }
        if(apps.size()==0){
            temp = new Tab[]{new Tab("Default", new AnchorPane(new Pane()))};
        }
        if(mainPane!=null){
            TabPane tempP = new TabPane(temp);
            tempP.setContextMenu(mainPane.getContextMenu());
            mainPane = tempP;
        } else {
            mainPane = new TabPane(temp);
        }

        mainPane.setMaxSize(1920,1080);
        mainPane.setPrefSize(1920,1080);

        mainPane.setOnDragDropped(event -> {
           Dragboard dragged = event.getDragboard();
           if(dragged.getString().compareTo("tab")==0){
               Tab theDraggedTab = draggedTab.get();
               theDraggedTab.getTabPane().getTabs().remove(theDraggedTab);
               Node n = theDraggedTab.getContent();
               Node t  = ((AnchorPane) n).getChildren().get(0);
               System.out.println("id:"+((Label)theDraggedTab.getGraphic()).getText());
               this.apps.add(new Tabable(((Label)theDraggedTab.getGraphic()).getText(), t) {});

               refresh();
               //doesnt update apps array, maybe switch to Tabable instead?
               event.setDropCompleted(true);
               draggedTab.set(null);
               event.consume();
           }
        });

        mainPane.setOnDragOver(event -> {
            Dragboard dragged = event.getDragboard();
            if(dragged.getString().compareTo("tab")==0){
                event.acceptTransferModes(TransferMode.MOVE);
                event.consume();
            }
        });
        mainPane.getTabs().addListener((ListChangeListener.Change<? extends Tab> c) -> {

            c.next();
            if(c.wasRemoved()&&!c.wasReplaced()) {
                System.out.println("Removing "+((AnchorPane) c.getRemoved().get(0).getContent()).getChildren().get(0).toString()+" from: " + mainPane.toString());

                System.out.print("List b4: [");
                for(Tabable t : apps){
                    System.out.print(t.childId + ", ");
                }
                System.out.println("]");
                refresh(((AnchorPane) c.getRemoved().get(0).getContent()).getChildren().get(0).toString());
                System.out.print("List aftar: [");
                for(Tabable t : apps){
                    System.out.print(t.childId + ", ");
                }
                System.out.println("]");
            }
        });

        return mainPane;
    }

    public void refresh(){

        ArrayList<Tab> temp = new ArrayList<>();
        for(int i=0; i< apps.size();i++){
            Tab tab = apps.get(i).getTab(draggedTab);
            int finalI = i;
            tab.setOnClosed(event -> {apps.remove(finalI);});
            temp.add(tab);

        }
        mainPane.getTabs().setAll(temp);
        mainPane.getSelectionModel().select(mainPane.getTabs().size() - 1);

    }

    /**
     * called to remove any duplicates on refresh.
     * TODO:Rename to remove as it no longer refreshes
     * @param childID
     */
    public boolean refresh(String childID){
        for(Tabable t : apps){
            if(t.childId.equals(childID)){
                apps.remove(t);
                return true;
            }
        }
        return false;
    }

    public void testApps(){
        System.out.println(
                //mainPane.getTabs().get(0).getContent().getParent()
        );
    }

    public double getTabHeight(){
        return apps.get(mainPane.getSelectionModel().getSelectedIndex()).getHeight();
    }


}
