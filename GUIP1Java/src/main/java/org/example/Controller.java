package org.example;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class Controller {

    @FXML
    private AnchorPane anchor;

    @FXML
    private TableView<?> datesTeams;

    @FXML
    private TableColumn<?, ?> firstnameEmployees;

    @FXML
    private TableColumn<?, ?> lastnameEmployees;

    @FXML
    private TableColumn<?, ?> nameTeam;

    @FXML
    private ScrollPane scrollPaneCalendar;

    @FXML
    private ScrollPane scrollPaneTeams;

    @FXML
    private ScrollPane scrollPaneUsers;

    @FXML
    private TableView<?> tableDates;

    @FXML
    private TableView<?> tableEmpoyees;

    @FXML
    private TableView<?> tableTeams;

    @FXML
    private TableColumn<?, ?> today;

    @FXML
    private TableColumn<?, ?> todayTeams;

    @FXML
    private ListView<String> teamsList;

    @FXML
    private Label selectedTeams;
    @FXML
    public void employeesSorted(ActionEvent e){

    }
    @FXML
    public void teamsSorted(ActionEvent e){

    }
    @FXML
    public void ButtonNewEmployee(ActionEvent e) throws IOException {
        Stage window = new Stage();
        window.setScene(new Scene(FXMLLoader.load(getClass().getResource("/newEmployee.fxml"))));

        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("Neuen Mitarbeiter anlegen");

        window.showAndWait();
    }
    @FXML
    public void newEmployee(ActionEvent e) {

    }
    @FXML
    public void ButtonNewAbsence(ActionEvent e) {

    }
}
