package org.example;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Stage;

import javax.swing.*;
import java.io.IOException;
import java.net.URL;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Controller implements Initializable {

    private int sort = 0;
    //0 Nachname aufsteigend
    //1 Nachname ab
    //2 Vorname auf
    //3 Vorname ab

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
    public void empsSortedLastnameAsc(ActionEvent e){
        sort = 0;
        initialize(null, null);
    }

    @FXML
    public void empsSortedLastnameDesc(ActionEvent e){
        sort = 1;
        initialize(null, null);
    }

    @FXML
    public void empsSortedFirstnameAsc(ActionEvent e){
        sort = 2;
        initialize(null, null);
    }

    public void empsSortedFirstnameDesc(ActionEvent e){
        sort = 3;
        initialize(null, null);
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
                if (sort == 1){
                    return o2.lastName.compareTo(o1.lastName);
                }
                if (sort == 2) {
                    return o1.firstName.compareTo(o2.firstName);
                }
                if (sort == 3) {
                    return o2.firstName.compareTo(o1.firstName);

                }
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
            label.setAlignment(Pos.CENTER);
            label.setStyle("-fx-font-weight: bold");
            dateBox.getChildren().add(label);

            // Per date: manage every employee
            int i = 0;
            for (Employee e : emps) {
                //TODO Feiertage
                Absence absence = absencesOfDay.get(e);
                if (absence == null) {
                    Label l = new Label("");
                    setSize(width, height, l);
                    if (i%2 == 0) l.setStyle("-fx-background-color: #cccccc;" );
                    dateBox.getChildren().add(l);
                    i++;
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
                String color =  "-fx-background-color: " + e.favoriteColor + ";";
                edit.setStyle(color);
                dateBox.getChildren().add(edit);

                i ++;
            }
            hBoxAbsences.getChildren().add(dateBox);
            // Team-Calendar


            VBox teamsDates = new VBox();
            teamsDates.setMinWidth(width);
            teamsDates.setMinWidth(width);
            i = 0;
            for (String team:teams) {
                Label label1 = new Label(teamsAbsences.get(team).toString());
                if (i%2 == 0) label1.setStyle("-fx-background-color: #cccccc;" );
                setSize(width, height, label1);
                label1.setMinWidth(width);
                label1.setAlignment(Pos.CENTER);
                teamsDates.getChildren().add(label1);
                i++;
            }
            hBoxTeamDates.getChildren().add(teamsDates);




            //End of Day loop
        }

        int i = 0;
        for (Employee e:emps) {
            i++;
            if (i == 1){
                HBox hBox = new HBox();
                setSize(200, height, hBox);

                Button lNA = new Button("Nachn. Auf");
                setSize(30, height, lNA);
                lNA.setOnAction(this::empsSortedLastnameAsc);

                Button lND = new Button("Nachn. Ab");
                setSize(30, height, lNA);
                lNA.setOnAction(this::empsSortedLastnameDesc);

                Button fNA = new Button("Vorn. Auf");
                setSize(30, height, lNA);
                lNA.setOnAction(this::empsSortedFirstnameAsc);

                Button fND = new Button("Vorn. Ab");
                setSize(30, height, lNA);
                lNA.setOnAction(this::empsSortedFirstnameDesc);

                hBox.getChildren().addAll(lNA, lND, fNA, fND);
                vBoxEmployees.getChildren().add(hBox);
                continue;
            }
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



        int j = 0;
        for (String team:AbsencePlanner.getTeams()) {
            HBox hBox = new HBox();
            setSize(200, height, hBox);
            if (j%2 == 0) hBox.setStyle("-fx-background-color: #cccccc;" );


            Label teamL = new Label(team);

            setSize(170, height, teamL);
            if (j%2 == 0) teamL.setStyle("-fx-background-color: #cccccc;" );

            Button edit = new Button("Edit");
            setSize(20, height - 2, edit);
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
            if (j%2 == 0) hBox.setStyle("-fx-background-color: #cccccc;" );


            hBox.getChildren().addAll(teamL, edit);
            vBoxTeams.getChildren().add(hBox);
            j++;
        }

        paneInscroll.setMinWidth(width * countOfDays + 205);
        scrollPaneCalendar.setFitToWidth(true);
        scrollPaneUsers.setFitToWidth(false);
        scrollPaneTeams.setFitToWidth(false);
        scrollPaneUsers.setMinWidth(width * countOfDays + 200);
        scrollPaneTeams.setMinWidth(width * countOfDays + 200);
        scrollPaneUsers.setMaxWidth(width * countOfDays + 200);
        scrollPaneTeams.setMaxWidth(width * countOfDays + 200);

    }

    private void setSize(int width, int height, HBox node){
        node.setMinHeight(height);
        node.setMaxHeight(height);
        node.setMinWidth(width);
        node.setMaxWidth(width);
    }

    private void setSize(int width, int height, Label node){
        node.setMinHeight(height);
        node.setMaxHeight(height);
        node.setMinWidth(width);
        node.setMaxWidth(width);
    }

    private void setSize(int width, int height, Button node){
        node.setMinHeight(height);
        node.setMaxHeight(height);
        node.setMinWidth(width);
        node.setMaxWidth(width);
    }

}
