package org.example;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class MainApp extends Application {
    EmployeeController employeeController = new EmployeeController();
    LocalDate startDate = LocalDate.now();
    LocalDate endDate = startDate.plusDays(60);

    String vacationColor = "#87CEFA"; // light blue
    String trainingColor = "#FFD700"; // gold
    String sickColor = "#FF0000"; // red
    String remoteWorkColor = "#90EE90"; // light green
    String unpaidLeaveColor = "#FFA500"; // orange
    String accidentColor = "#FFC0CB"; // pink


    @Override
    public void start(Stage primaryStage) {
        BorderPane root = new BorderPane();

        // Use the EmployeeController to get employee names from the database
        ObservableList<String> teamMembers = employeeController.getEmployeeNames();
        System.out.println(teamMembers);

        VBox timelineSection = createTimeline(teamMembers);
        timelineSection.setMinHeight(400);
        root.setCenter(timelineSection);

        // Create the competence team section
        VBox competenceTeamSection = createCompetenceTeamSection();
        root.setBottom(competenceTeamSection);

        Scene scene = new Scene(root, 1024, 600);
        primaryStage.setTitle("Absence Planner");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private VBox createTimeline(ObservableList<String> teamMembers) {
        VBox timelineVBox = new VBox();
        ScrollPane scrollPane = new ScrollPane();
        GridPane gridPane = new GridPane();
        gridPane.setHgap(1);
        gridPane.setVgap(1);
        gridPane.setPadding(new Insets(5));

        // Header row for dates
        int colIndex = 1; // Start from 1 to leave space for team member names
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            String dayOfWeek = date.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.getDefault());
            String dateString = String.format("%s %d", dayOfWeek, date.getDayOfMonth());

            Label dateLabel = new Label(dateString);
            dateLabel.setMinWidth(70);
            dateLabel.setMinHeight(30);
            dateLabel.setStyle("-fx-background-color: lightgrey; -fx-alignment: center;");
            dateLabel.setPadding(new Insets(2));
            gridPane.add(dateLabel, colIndex++, 0);
        }

        // Create rows for each team member
        for (int row = 0; row < teamMembers.size(); row++) {
            Label nameLabel = new Label(teamMembers.get(row));
            nameLabel.setMinWidth(200);
            nameLabel.setMinHeight(30);
            String userColor = employeeController.getUserColor(teamMembers.get(row));
            nameLabel.setStyle("-fx-background-color: "+userColor+"; -fx-alignment: center-left;");
            gridPane.add(nameLabel, 0, row + 1); // +1 because the first row is for dates

            int employeeId = employeeController.getUserId(teamMembers.get(row)); // Use the controller to get the user ID
            List<Absence> employeeAbsences = employeeController.getEmployeeAbsences(employeeId); // Get the list of absences

            // Loop through each day on the timeline
            for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
                Label absenceLabel = new Label();
                absenceLabel.setMinSize(70, 30); // set min size for uniformity
                absenceLabel.setStyle("-fx-background-color: white; -fx-border-color: "+userColor+"; -fx-alignment: center;");
                long daysBetween = ChronoUnit.DAYS.between(startDate, date);
                int col = (int) daysBetween + 1;

                // Check if the current date is within any of the absence periods
                for (Absence absence : employeeAbsences) {
                    LocalDate start = LocalDate.parse(absence.startDate);
                    LocalDate end = LocalDate.parse(absence.endDate);
                    if (!date.isBefore(start) && !date.isAfter(end)) {
                        // Set the color based on the type of absence
                        String color = switch (absence.type) {
                            case VACATION -> vacationColor;
                            case SICKNESS -> sickColor;
                            case TRAINING -> trainingColor;
                            case REMOTE_WORK -> remoteWorkColor;
                            case UNPAID_LEAVE -> unpaidLeaveColor;
                            case ACCIDENT -> accidentColor;
                            default -> "white";
                        };
                        absenceLabel.setStyle("-fx-background-color: " + color + "; -fx-border-color: black; -fx-alignment: center;");
                        break; // No need to check further if we found an absence
                    }
                }

                gridPane.add(absenceLabel, col, row + 1);
            }
        }

        // Apply weekend styling
        colIndex = 1; // Reset colIndex for weekend styling
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            if (isWeekendOrHoliday(date)) { // Implement this method based on your locale's weekends and holidays
                for (int row = 1; row <= teamMembers.size(); row++) {
                    Label weekendLabel = (Label) getNodeByRowColumnIndex(row, colIndex, gridPane);
                    if (weekendLabel != null) {
                        weekendLabel.setStyle("-fx-background-color: #D3D3D3; -fx-border-color: black;"); // A light grey to indicate weekend
                    }
                }
            }
            colIndex++;
        }

        scrollPane.setContent(gridPane);
        scrollPane.setMinHeight(400);
        timelineVBox.getChildren().add(scrollPane);
        return timelineVBox;
    }

    // Helper method to get the Node at a specific row and column in a GridPane
    private Node getNodeByRowColumnIndex(final int row, final int column, GridPane gridPane) {
        Node result = null;
        ObservableList<Node> children = gridPane.getChildren();

        for (Node node : children) {
            if(GridPane.getRowIndex(node) == row && GridPane.getColumnIndex(node) == column) {
                result = node;
                break;
            }
        }

        return result;
    }

    // Method to check if a date is a weekend or a public holiday
    private boolean isWeekendOrHoliday(LocalDate date) {
        DayOfWeek day = date.getDayOfWeek();
        // Assuming Saturday and Sunday as weekends; you would also check for holidays here
        return day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY;
    }


    private VBox createCompetenceTeamSection() {
        VBox competenceTeamSection = new VBox();
        competenceTeamSection.setPrefHeight(200);
        Label competenceLabel = new Label("Competence Teams Placeholder");
        competenceTeamSection.getChildren().add(competenceLabel);
        return competenceTeamSection;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
//jj