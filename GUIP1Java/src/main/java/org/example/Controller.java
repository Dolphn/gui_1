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
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
    private VBox vBoxEmployees;

    @FXML
    private VBox vBoxTeams;
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
        int height = 18;
        int width = 60;
        DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("dd.MM.yyyy");

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

        LocalDate myDateObj = LocalDate.now();
        LocalDate highest = AbsencePlanner.getHighetDate();
        long countOfDays = Duration.between(myDateObj, highest).toDays();
        String formattedDate = myDateObj.format(myFormatObj);

        for (long d = 0; d < countOfDays; d++) {
            LocalDate day = myDateObj.plusDays(d);

            VBox dateBox = new VBox();
            dateBox.setMaxWidth(width);
            dateBox.setMinWidth(width);

            Label label = new Label(day.format(myFormatObj));
            label.maxHeight(height);
            label.minHeight(height);
            label.setMaxWidth(width);
            label.setMinWidth(width);

            dateBox.getChildren().add(label);

            for (Employee e : emps) {


            }
        }

        for (Employee e:emps) {
            HBox hBox = new HBox();
            hBox.maxHeight(height);
            hBox.minHeight(height);
            hBox.maxWidth(200);
            hBox.minWidth(200);

            Label lastname;
        }
    }
}
