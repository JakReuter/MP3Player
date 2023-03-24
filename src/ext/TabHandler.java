package ext;


import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Arrays;

public class TabHandler {
    private TabPane mainPane;
    private ArrayList<Tabable> apps;
    private Stage mainStage;

    public TabHandler(){
        mainPane = new TabPane(new Tab("Default"));
        apps = new ArrayList<>();
    }

    public void addApp(Tabable... tApps){
        apps.addAll(Arrays.asList(tApps));
    }

    public void toWindow(Tabable tabable){
        tabable.setOnCloseRequest(event -> toTab(tabable));
        mainPane.getSelectionModel().clearSelection();
        System.out.println(tabable.getTabName());
        tabable.getWindow().show();
    }

    public void toTab(Tabable tabable){
        mainPane.getTabs().add(tabable.getTab());
        if(tabable.isShowing()){
            tabable.close();
        }
    }

    public void tabToApp(Tab tab){

    }

    public void Display(){
        if(mainStage==null){
            mainStage = new Stage();
            mainStage.setTitle("Main");
        }
        Tab[] temp = new Tab[apps.size()];
        for(int i=0; i< apps.size();i++){
            temp[i]=apps.get(i).getTab();
        }

        mainPane = new TabPane(temp);

        mainStage.setScene(new Scene(mainPane,250, 250));
        mainStage.show();
    }

}
