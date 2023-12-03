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
import javafx.scene.layout.Pane;
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

    public Button buttonNewAbsence;
    @FXML
    private AnchorPane anchor;

    @FXML
    private HBox hBoxAbsences;

    @FXML
    private Pane paneInscroll;

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
    public void buttonNewAbsence(ActionEvent e) throws IOException {
        Stage window = new Stage();
        window.setScene(new Scene(FXMLLoader.load(getClass().getResource("/absences.fxml"))));

        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("Neuen Abwesenheit beantragen");
        window.showAndWait();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        int height = 18;
        int width = 80;
        DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        HashMap<String, Integer> teamsAbsences = new HashMap<>();

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

        //

        for (long d = 0; d < countOfDays; d++) {

            ArrayList<String> teams = AbsencePlanner.getTeams();
            for (String team:teams) {
                teamsAbsences.put(team, 0);
            }

            LocalDate day = myDateObj.plusDays(d);
            Map<Employee, Absence> absencesOfDay = AbsencePlanner.getAbsancesPerEmployeeByDay(day);

            VBox dateBox = new VBox();
            dateBox.setMaxWidth(width);
            dateBox.setMinWidth(width);

            Label label = new Label(day.format(myFormatObj));
            setSize(width, height, label);
            label.setStyle("-fx-font-weight: bold");
            dateBox.getChildren().add(label);

            // Per date: manage every employee
            int i = 0;
            for (Employee e : emps) {
                //TODO Feiertage
                i ++;
                Absence absence = absencesOfDay.get(e);
                if (absence == null) {
                    Label l = new Label("");
                    setSize(width, height, l);
                    if (i%2 == 0) l.setStyle("-fx-background-color: #cccccc;" );
                    dateBox.getChildren().add(l);
                    continue;
                }
                for (String t: AbsencePlanner.getTeamsOfEmployee(e.id) ) {
                    Integer in = teamsAbsences.get(t);
                    teamsAbsences.replace(t, in + 1);
                }
                Button edit = new Button(absence.type.toString());
                setSize(30, height, edit);
                edit.setOnAction(event -> {
                    Stage window = new Stage();
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/absences.fxml"));
                        loader.setController(new AbsenceController(absence, true));
                        window.setScene(new Scene(loader.load()));
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }

                    window.initModality(Modality.APPLICATION_MODAL);
                    window.setTitle("Abwesenheit ändern");
                    window.showAndWait();
                });
                String color =  "-fx-background-color:" + e.favoriteColor + ";";
                edit.setStyle(color);
                dateBox.getChildren().add(edit);

            }
            hBoxAbsences.getChildren().add(dateBox);
            // Team-Calendar


            VBox teamsDates = new VBox();
            teamsDates.setMinWidth(width);
            teamsDates.setMinWidth(width);
            for (String team:teams) {
                Label label1 = new Label(teamsAbsences.get(team).toString());
                if (i%2 == 0) label1.setStyle("-fx-background-color: #cccccc;" );
                setSize(width, height, label1);
                teamsDates.getChildren().add(label1);
            }
            hBoxTeamDates.getChildren().add(teamsDates);





        }

        int i = 0;
        for (Employee e:emps) {
            i++;
            HBox hBox = new HBox();
            setSize(200, height, hBox);
            if (i%2 == 0) hBox.setStyle("-fx-background-color: #cccccc;" );


            Label lastname = new Label(e.lastName);
            Label firstname = new Label(e.firstName);

            setSize(85, height, lastname);
            setSize(85, height, firstname);
            if (i%2 == 0) lastname.setStyle("-fx-background-color: #cccccc;" );
            if (i%2 == 0) firstname.setStyle("-fx-background-color: #cccccc;" );


            Button edit = new Button("Edit");
            setSize(30, height, edit);
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
            if (i%2 == 0) edit.setStyle("-fx-background-color: #cccccc;" );

            hBox.getChildren().addAll(lastname, firstname, edit);
            vBoxEmployees.getChildren().add(hBox);
        }


        //
        // Teams
        //



        i = 0;
        for (String team:AbsencePlanner.getTeams()) {
            i++;
            HBox hBox = new HBox();
            setSize(200, height, hBox);
            if (i%2 == 0) hBox.setStyle("-fx-background-color: #cccccc;" );


            Label teamL = new Label(team);

            setSize(170, height, teamL);
            if (i%2 == 0) teamL.setStyle("-fx-background-color: #cccccc;" );

            Button edit = new Button("Edit");
            setSize(30, height, edit);
            edit.setOnAction(event -> {
                Stage window = new Stage();
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/teams.fxml"));
                    //TODO loader.setController(new EmployeeController(e, true));
                    window.setScene(new Scene(loader.load()));
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }

                window.initModality(Modality.APPLICATION_MODAL);
                window.setTitle("Team ändern");
                window.showAndWait();
            });
            if (i%2 == 0) edit.setStyle("-fx-background-color: #cccccc;" );

            hBox.getChildren().addAll(teamL, edit);
            vBoxTeams.getChildren().add(hBox);
        }

       scrollPaneUsers.setFitToWidth(true);
        scrollPaneTeams.setFitToWidth(true);

    }

    private void setSize(int width, int height, Node node){
        node.minHeight(height);
        node.maxHeight(height);
        node.minHeight(width);
        node.maxWidth(width);
    }
}
