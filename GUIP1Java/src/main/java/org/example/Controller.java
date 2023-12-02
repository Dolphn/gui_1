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
    private HBox hBoxAbsences;

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

        Collections.sort(emps, new Comparator<Employee>() {
            @Override
            public int compare(Employee o1, Employee o2) {
                return o1.lastName.compareTo(o2.lastName);
            }
        });

        long countOfDays = 20;
        LocalDate myDateObj = LocalDate.now();
        if (AbsencePlanner.getHighestDate() != null) {
            LocalDate highest = AbsencePlanner.getHighestDate();
            countOfDays = Duration.between(myDateObj, highest).toDays();
        }

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
                Button edit = new Button();
                edit.minHeight(height);
                edit.maxHeight(height);
                edit.setMinWidth(30);
                edit.setMaxWidth(30);
                edit.setOnAction(event -> {
                    Stage window = new Stage();
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/newEmployee.fxml"));
                        loader.setController(new EmployeeController(e, true));
                        window.setScene(new Scene(loader.load()));
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }

                    window.initModality(Modality.APPLICATION_MODAL);
                    window.setTitle("Mitarbeiter ändern");
                    window.showAndWait();
                });

            }
        }
        HBox hBoxE = new HBox();
        hBoxE.maxHeight(height);
        hBoxE.minHeight(height);
        hBoxE.maxWidth(200);
        hBoxE.setMinWidth(200);
        vBoxEmployees.getChildren().add(hBoxE);

        for (Employee e:emps) {
            HBox hBox = new HBox();
            hBox.maxHeight(height);
            hBox.minHeight(height);
            hBox.maxWidth(200);
            hBox.setMinWidth(200);

            Label lastname = new Label(e.lastName);
            Label firstname = new Label(e.firstName);

            lastname.minHeight(height);
            lastname.maxHeight(height);
            lastname.setMinWidth(85);
            lastname.setMaxWidth(85);
            firstname.minHeight(height);
            firstname.maxHeight(height);
            firstname.setMinWidth(85);
            firstname.setMaxWidth(85);

            Button edit = new Button("Edit");
            edit.minHeight(height);
            edit.maxHeight(height);
            edit.setMinWidth(30);
            edit.setMaxWidth(30);
            edit.setOnAction(event -> {
                Stage window = new Stage();
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/newEmployee.fxml"));
                    loader.setController(new EmployeeController(e, true));
                    window.setScene(new Scene(loader.load()));
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }

                window.initModality(Modality.APPLICATION_MODAL);
                window.setTitle("Mitarbeiter ändern");
                window.showAndWait();
            });
            hBox.getChildren().addAll(lastname, firstname, edit);
            vBoxEmployees.getChildren().add(hBox);
        }
    }
}
