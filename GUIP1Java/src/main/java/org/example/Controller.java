package org.example;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.*;

public class Controller implements Initializable {

    @FXML
    private AnchorPane anchor;

    @FXML
    private HBox hBoxAbseces;

    @FXML
    private HBox hBoxEmployees;

    @FXML
    private HBox hBoxTeamDates;

    @FXML
    private HBox hBoxTeams;

    @FXML
    private ScrollPane scrollPaneCalendar;

    @FXML
    private ScrollPane scrollPaneTeams;

    @FXML
    private ScrollPane scrollPaneUsers;
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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ArrayList<Employee> emps = AbsencePlanner.getAllEmployees();
        HashMap<Employee, ArrayList<Absence>> allAbsences = new HashMap<>();
        for (Employee e: emps) {
            allAbsences.put(e, AbsencePlanner.getAllAbsencesByEmployeeId(e.id));
        }

        Collections.sort(emps, new Comparator<Employee>() {
            @Override
            public int compare(Employee o1, Employee o2) {
                return o1.lastName.compareTo(o2.lastName);
            }
        });

        for (Employee e: emps) {







        }
    }
}
