package org.example;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Controller implements Initializable {

    Map<String, String> holidays = new HashMap<>();
    private void initHolidays(){
        holidays.put("25.12.2023", "1. Weihnachtsfeiertag");
        holidays.put("26.12.2023", "2. Weihnachtsfeiertag");
        holidays.put("01.01.2024", "Neujahr");
        holidays.put("08.12.2023", "Tag der Gürtelrose");

//TODO
    }



    private int sort = 1;
    //0 Nachname aufsteigend
    //1 Nachname ab
    //2 Vorname auf
    //3 Vorname ab

    public Button buttonNewAbsence;

    @FXML
    private HBox hBoxAbsences;

    @FXML
    private Pane paneInscroll;


    @FXML
    private HBox hBoxTeamDates;


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
        reload();
    }

    public void reload(){
        hBoxTeamDates.getChildren().clear();
        vBoxEmployees.getChildren().clear();
        hBoxTeamDates.getChildren().clear();
        vBoxTeams.getChildren().clear();

        initialize(null, null);
    }

    @FXML
    public void empsSortedLastnameDesc(ActionEvent e){
        sort = 1;
        reload();
    }

    @FXML
    public void empsSortedFirstnameAsc(ActionEvent e){
        sort = 2;
        reload();
    }

    public void empsSortedFirstnameDesc(ActionEvent e){
        sort = 3;
        reload();
    }
    @FXML
    public void ButtonNewEmployee(ActionEvent e) throws IOException {
        Stage window = new Stage();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/newEmployee.fxml"));
            loader.setController(new EmployeeController());
            window.setScene(new Scene(loader.load()));
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("Neuen Mitarbeiter anlegen");
        window.showAndWait();
    }

    @FXML
    public void buttonNewAbsence(ActionEvent e) throws IOException {
        Stage window = new Stage();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/absences.fxml"));
            loader.setController(new AbsenceController());
            window.setScene(new Scene(loader.load()));
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("Neue Abwesenheit beantragen");
        window.showAndWait();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        AbsencePlanner.fetchAllEmployees();
        initHolidays();

        int height = 18;
        int width = 80;
        DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        HashMap<String, Integer> teamsAbsences = new HashMap<>();


        ArrayList<Employee> emps = AbsencePlanner.getAllEmployees();

        System.out.println(emps);

        Collections.sort(emps, new Comparator<Employee>() {
            @Override
            public int compare(Employee o1, Employee o2) {
                if (sort == 0){
                    return o2.lastName.compareTo(o1.lastName);
                }
                if (sort == 1) {
                    return o1.lastName.compareTo(o2.lastName);
                }
                if (sort == 2) {
                    return o2.firstName.compareTo(o1.firstName);

                }
                return o1.firstName.compareTo(o2.firstName);
            }
        });

        long countOfDays = 20;
        LocalDate myDateObj = LocalDate.now();
        if (AbsencePlanner.getHighestDate() != null) {
            LocalDate highest = AbsencePlanner.getHighestDate();
            countOfDays = Duration.between(myDateObj.atStartOfDay(), highest.atStartOfDay()).toDays() + 1;
        }

        // Per day

        for (long d = 0; d < countOfDays; d++) {

            ArrayList<String> teams = AbsencePlanner.getTeams();
            for (String team:teams) {
                teamsAbsences.put(team, 0);
            }

            LocalDate day = myDateObj.plusDays(d);
            Map<Employee, Absence> absencesOfDay = AbsencePlanner.getAbsencesPerEmployeeByDay(day);


            VBox dateBox = new VBox();
            dateBox.setMaxWidth(width);
            dateBox.setMinWidth(width);

            Label label = new Label(day.format(myFormatObj));
            setSize(width, height, label);
            label.setAlignment(Pos.CENTER);
            label.setStyle("-fx-font-weight: bold");
            dateBox.getChildren().add(label);

            //Holidays
            if (holidays.containsKey(day.format(myFormatObj))) {
                Label label1 = new Label(holidays.get(day.format(myFormatObj)));
                setSize(500, width, label1);
                label1.setAlignment(Pos.CENTER);
                label1.setRotate(-90);
                label1.setTranslateX(-210 );
                label1.setTranslateY(210 );
                label1.setStyle("-fx-background-color: #444444; -fx-text-fill: white; -fx-font-size: 26px;" );
                dateBox.getChildren().add(label1);
                hBoxAbsences.getChildren().add(dateBox);

                VBox box = new VBox();
                setSize(width, 200, box);
                box.setStyle("-fx-background-color: #444444;" );
                hBoxTeamDates.getChildren().add(box);

                continue;

            }
            //Holidays

            // Per date: manage every employee
            int i = 0;
            for (Employee e : emps) {
                // Feiertage
                if (day.getDayOfWeek() == DayOfWeek.SATURDAY || day.getDayOfWeek() == DayOfWeek.SUNDAY){
                    HBox box = new HBox();
                    setSize(width, height, box);
                    box.setStyle("-fx-background-color: #444444;" );
                    dateBox.getChildren().add(box);
                    continue;
                }


                // Feiertage
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
                setSize(width, height, edit);
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
                    reload();
                });

                String color =  "-fx-background-color: " + e.favoriteColor + ";";
                int r = Integer.parseInt(e.favoriteColor.substring(1,3), 16);
                int g = Integer.parseInt(e.favoriteColor.substring(3,5), 16);
                int b = Integer.parseInt(e.favoriteColor.substring(5), 16);
                String fontC;

                if ((0.2126*r + 0.7152*g + 0.0722*b) > 160){
                    fontC = "-fx-color: #fff";
                } else {
                    fontC = "-fx-color: #000000";
                }
                edit.setStyle(color);
                edit.setStyle("-fx-padding: 0.2;  -fx-font-size: 10; -fx-background-color: " + e.favoriteColor + ";" + fontC + ";");
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

                // Feiertage
                if (day.getDayOfWeek() == DayOfWeek.SATURDAY || day.getDayOfWeek() == DayOfWeek.SUNDAY){
                    HBox box = new HBox();
                    setSize(width, height, box);
                    box.setStyle("-fx-background-color: #444444;" );
                    teamsDates.getChildren().add(box);
                    continue;
                }


                // Feiertage
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

            HBox hBox1 = new HBox();
            setSize(200, height, hBox1);

            Button lNA = new Button("Nachn. Auf");
            setSize(50, height, lNA);
            lNA.setStyle("-fx-padding: 0.2;  -fx-font-size: 10;");
            lNA.setOnAction(this::empsSortedLastnameAsc);

            Button lND = new Button("Nachn. Ab");
            setSize(50, height, lND);
            lND.setStyle("-fx-padding: 0.2;  -fx-font-size: 10;");
            lND.setOnAction(this::empsSortedLastnameDesc);

            Button fNA = new Button("Vorn. Auf");
            setSize(50, height, fNA);
            fNA.setStyle("-fx-padding: 0.2;  -fx-font-size: 10;");
            fNA.setOnAction(this::empsSortedFirstnameAsc);

            Button fND = new Button("Vorn. Ab");
            setSize(50, height, fND);
            fND.setStyle("-fx-padding: 0.2;  -fx-font-size: 10;");
            fND.setOnAction(this::empsSortedFirstnameDesc);

        hBox1.getChildren().addAll(lNA, lND, fNA, fND);
            vBoxEmployees.getChildren().add(hBox1);
        int i = 1;
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
            edit.setStyle("-fx-padding: 0.1;  -fx-font-size: 10;");
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
                reload();
            });
            //if (i%2 == 0) edit.setStyle("-fx-background-color: #cccccc;" );

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
            edit.setStyle("-fx-padding: 0.1;  -fx-font-size: 10;");
            edit.setOnAction(event -> {
                Stage window = new Stage();
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/teams.fxml"));
                    loader.setController(new TeamsController(team, true));
                    window.setScene(new Scene(loader.load()));
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }

                window.initModality(Modality.APPLICATION_MODAL);
                window.setTitle("Team ändern");
                window.showAndWait();
                reload();
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

    private void setSize(int width, int height, VBox node){
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

    @FXML
    void buttonNewTeam(ActionEvent event) throws IOException {
        Stage window = new Stage();

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/teams.fxml"));
            loader.setController(new TeamsController());
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("Neues Team anlegen");
            window.setScene(new Scene(loader.load()));
        window.showAndWait();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

}
