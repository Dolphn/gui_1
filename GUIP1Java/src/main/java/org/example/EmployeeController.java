package org.example;

        import javafx.beans.value.ObservableValue;
        import javafx.collections.ObservableList;
        import javafx.event.ActionEvent;
        import javafx.fxml.FXML;
        import javafx.fxml.Initializable;
        import javafx.scene.Node;
        import javafx.scene.control.*;
        import javafx.scene.paint.Color;

        import java.net.URL;
        import java.util.ArrayList;
        import java.util.Arrays;
        import java.util.ResourceBundle;

public class EmployeeController implements Initializable {

    @FXML
    private TextField firstname;

    @FXML
    private TextField lastname;

    @FXML
    private Label selectedTeams;

    @FXML
    private ListView<String> teamsList;

    @FXML
    private ColorPicker colorPicker;


    @FXML
    void AddSelTeam(ActionEvent event) {

    }

    @FXML
    void RemoveSelTeam(ActionEvent event) {

    }

    @FXML
    public void interrupt(ActionEvent e) {
        ((Node)e.getSource()).getScene().getWindow().hide();
    }

    @FXML
    void newEmployee(ActionEvent event) {
        Color color = colorPicker.getValue();
        String str = String.format( "#%02X%02X%02X", (int)( color.getRed() * 255 ), (int)( color.getGreen() * 255 ), (int)( color.getBlue() * 255 ) );
        AbsencePlanner.addEmployee(firstname.getText(), lastname.getText(), str);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        String[] a = new String[]{"lol", "go", "haha"};
        ArrayList<String> teams = AbsencePlanner.getTeams();
        teamsList.getItems().addAll(teams);
        teamsList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        teamsList.getSelectionModel().selectedItemProperty().addListener(this::selectionChanged);
    }

    private void selectionChanged(ObservableValue<? extends String> observable, String oldV, String newV){
        ObservableList<String> selectedItems = teamsList.getSelectionModel().getSelectedItems();
        String selectedItem = (selectedItems.isEmpty())? "Kein Team ausgewählt" : selectedItems.toString();
        selectedTeams.setText(selectedItem);
    }
}
