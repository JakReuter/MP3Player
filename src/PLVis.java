import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;


public class PLVis {

    @FXML private Button newButton;
    @FXML private Button removeButton;
    @FXML private Button renameButton;
    @FXML private TextField textField;
    @FXML private Button descriptionButton;
    @FXML private TableView<Playlist> tableView;
    @FXML private TableColumn<Playlist, String> nameColumn;
    @FXML private TableColumn<Playlist, Integer> songCountColumn;
    @FXML private TableColumn<Playlist, String> durationColumn;
    @FXML private TableColumn<Playlist, String> descriptionColumn;

    private ObservableList<Playlist> playlists;

    public void initialize() {
        this.playlists = FXCollections.observableArrayList(
                new Playlist("Playlist 1", 10, "1:00:00", "This is a playlist."),
                new Playlist("Playlist 2", 5, "0:30:00", "This is another playlist.")
        );

        tableView.setItems(playlists);
        tableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

    }

    @FXML
    private void handleNewButton(ActionEvent event) {
        // code to handle new button click
        if (textField.isVisible()) {
            playlists.add(new Playlist(textField.getText(),0,"00:00:00",""));
            textField.setVisible(false);
            textField.clear();
        } else {
            textField.setVisible(true);
        }
    }

    @FXML
    private void handleRemoveButton(ActionEvent event) {
        // code to handle remove button click
        playlists.remove(tableView.getSelectionModel().getSelectedItem());
    }

    @FXML
    private void handleRenameButton(ActionEvent event) {
        // code to handle rename button click
        if(textField.isVisible()) {
            playlists.get(tableView.getSelectionModel().getSelectedIndex()).setName(textField.getText());
            tableView.refresh();
            textField.setVisible(false);
            textField.clear();
        }
        else {
            textField.setVisible(true);
        }
    }

    @FXML
    private void handleDescriptionButton(ActionEvent event) {
        // code to handle description button click
        if(textField.isVisible()) {
            playlists.get(tableView.getSelectionModel().getSelectedIndex()).setDescription(textField.getText());
            tableView.refresh();
            textField.setVisible(false);
            textField.clear();
        }
        else {
            textField.setVisible(true);
        }
    }
}
