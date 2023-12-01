package org.example;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Map;

public class AbsencePlanner extends Application {
    private static Team team;
    private static Connection connection;

    public AbsencePlanner() {
        team = new Team();
        connection = SQLiteConnection.connect();
        initializeDatabase();
    }

    @Override
    public void start(Stage stage) throws Exception {
        //GUI erzeugen
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/main_window.fxml"));
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Abwesenheitsplaner");
            stage.show();
        } catch (Exception e){
            e.printStackTrace();

        }
        //
    }


    public static void main(String[] args) {
        launch(args);
        //Test
        //Testgit
        AbsencePlanner planner = new AbsencePlanner();
        planner.initializeDatabase();

        Connection con = SQLiteConnection.connect();

        // Datenbankverbindung schließen
        SQLiteConnection.disconnect(con);
    }
    private static void initializeDatabase() {
        String createEmployeesTableSQL = """
                CREATE TABLE IF NOT EXISTS employees (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                first_name TEXT NOT NULL,
                last_name TEXT NOT NULL,
                favorite_color TEXT NOT NULL);
                """;

        String createAbsencesTableSQL = """
                CREATE TABLE IF NOT EXISTS absences (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                employee_id INTEGER,
                type TEXT NOT NULL,
                start_date TEXT NOT NULL,
                end_date TEXT NOT NULL,
                approved INTEGER NOT NULL,
                FOREIGN KEY (employee_id) REFERENCES employees(id));
                """;

        try (PreparedStatement preparedStatement1 = connection.prepareStatement(createEmployeesTableSQL);
             PreparedStatement preparedStatement2 = connection.prepareStatement(createAbsencesTableSQL)) {
            preparedStatement1.execute();
            preparedStatement2.execute();
            System.out.println("Tabellen 'employees' und 'absences' erstellt.");
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    public static void updateEmployee(String firstName, String lastName, String favoriteColor){
        //TODO
    }
    public static void addEmployee(String firstName, String lastName, String favoriteColor) {
        String insertEmployeeSQL = "INSERT INTO employees (first_name, last_name, favorite_color) VALUES (?, ?, ?);";

        try (PreparedStatement preparedStatement = connection.prepareStatement(insertEmployeeSQL)) {
            preparedStatement.setString(1, firstName);
            preparedStatement.setString(2, lastName);
            preparedStatement.setString(3, favoriteColor);
            preparedStatement.executeUpdate();

            System.out.println("Mitarbeiter '" + firstName + " " + lastName + "' hinzugefügt.");
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    public static void deleteEmployee(int id){
        String deleteEmployeeSQL = "DELETE FROM employees WHERE id = ?;";

        try (PreparedStatement preparedStatement = connection.prepareStatement(deleteEmployeeSQL)) {
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();

            System.out.println("Mitarbeiter '" + id + "' geloescht.");
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    public static void requestAbsence(String employeeName, AbsenceType type, String startDate, String endDate) {
        Employee employee = getEmployeeByName(employeeName);
        if (employee != null) {
            String insertAbsenceSQL = "INSERT INTO absences (employee_id, type, start_date, end_date, approved) VALUES (?, ?, ?, ?, 0);";

            try (PreparedStatement preparedStatement = connection.prepareStatement(insertAbsenceSQL)) {
                preparedStatement.setInt(1, employee.id);
                preparedStatement.setString(2, type.toString());
                preparedStatement.setString(3, startDate);
                preparedStatement.setString(4, endDate);
                preparedStatement.executeUpdate();

                //System.out.println("Abwesenheitsantrag für '" + employeeName + "' erstellt.");
            } catch (SQLException e) {
                System.err.println(e.getMessage());
            }
        }
    }

    private static Employee getEmployeeByName(String employeeName) {
        return new Employee(); //TODO auf ID ändern, I guess?
    }

    public static void approveAbsence(int id, int absenceIndex){
        Employee employee = getEmployeeByid(id);
        if (employee != null && absenceIndex >= 0 && absenceIndex < employee.absences.size()) {
            int absenceId = employee.absences.get(absenceIndex).id;
            String aproveAbsenceSQL = "UPDATE absences SET approved = 1 WHERE id = ?;";

            try (PreparedStatement preparedStatement = connection.prepareStatement(aproveAbsenceSQL)) {
                preparedStatement.setInt(1, absenceId);
                preparedStatement.executeUpdate();

                //System.out.println("Abwesenheit genehmigt für '" + employeeName + "'.");
            } catch (SQLException e) {
                System.err.println(e.getMessage());
            }
        }
    }

    public static void deleteAbsence(String employeeName, int absenceIndex) {
        Employee employee = getEmployeeByName(employeeName);
        if (employee != null && absenceIndex >= 0 && absenceIndex < employee.absences.size()) {
            int absenceId = employee.absences.get(absenceIndex).id;
            String deleteAbsenceSQL = "DELETE FROM absences WHERE id = ?;";

            try (PreparedStatement preparedStatement = connection.prepareStatement(deleteAbsenceSQL)) {
                preparedStatement.setInt(1, absenceId);
                preparedStatement.executeUpdate();

                //System.out.println("Abwesenheit gelöscht für '" + employeeName + "'.");
            } catch (SQLException e) {
                System.err.println(e.getMessage());
            }
        }
    }

    /*
    private static ArrayList<Employee> getAllAbsencesByEmployeeId(int id){
        ArrayList<Employee> employees = new ArrayList<>();

        String getEmployeesSQL = "SELECT * FROM employees";
        try(Statement stm = connection.createStatement()){
            ResultSet rs = stm.executeQuery(getEmployeesSQL);
            while(rs.next()){
                Employee employee = new Employee();
                employee.id = rs.getInt(1);
                employee.firstName = rs.getString(2);
                employee.lastName = rs.getString(3);
                employee.favoriteColor = rs.getString(4);
                employee.absences = getAllAbsencesByEmployeeId(employee.id);
                employees.add(employee);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return employees;
    }
*/
    public static ArrayList<Absence> getAllAbsencesByEmployeeId(int id){
        ArrayList<Absence> absences = new ArrayList<>();
        String getAbsencesByIdSQL = "SELECT * FROM absences WHERE employee_id= ? ;";
        try(PreparedStatement preparedStatement = connection.prepareStatement(getAbsencesByIdSQL)){
            preparedStatement.setInt(1,id);
            ResultSet resultSet = preparedStatement.executeQuery();

            while(resultSet.next()){
                Absence absence = new Absence();
                absence.id = resultSet.getInt(1);
                absence.employeeId = resultSet.getInt(2);
                absence.type = AbsenceType.getAbscenceTypeByString(resultSet.getString(3));
                absence.startDate = resultSet.getString(4);
                absence.endDate = resultSet.getString(5);
                absence.approved = resultSet.getBoolean(6);
                absences.add(absence);
            }
            resultSet.close();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return absences;
    }

/*
    private static Employee getEmployeeByName(String employeeName) {
        //alter Code Funktioniert nicht, weil team nicht benutzt wird.
        for (Employee employee : team.employees.values()) {
            if ((employee.firstName + " " + employee.lastName).equals(employeeName)) {
                return employee;
    private int getIdByName(String name){
        String firstname = name.split(" ")[0];
        String lastname = name.split(" ")[1];
        String getEmployeeByNameSQL = "SELECT id FROM employees WHERE firstname = ? AND lastname = ?;";
        try(PreparedStatement preparedStatement = connection.prepareStatement(getEmployeeByNameSQL)){
            preparedStatement.setString(1,firstname);
            preparedStatement.setString(2,lastname);
            ResultSet resultSet = preparedStatement.executeQuery(getEmployeeByNameSQL);
            if(resultSet.next()){
                return resultSet.getInt(1);
            }
            resultSet.close();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        return -1;
    }
    */


    private static Employee getEmployeeByid(int id) {

        //neuer versuch
        Employee employee = new Employee();
        //String getEmployeeIdSQL = "SELECT id FROM employees WHERE first_name="+first_name+" AND last_name="+last_name+";";
        String getEmployeeIdSQL = "SELECT * FROM employees WHERE id= ?;";

        try (PreparedStatement preparedStatement = connection.prepareStatement(getEmployeeIdSQL)) {
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                employee.id = resultSet.getInt("id");
                employee.firstName = resultSet.getString("first_name");
                employee.lastName = resultSet.getString("last_name");
                employee.favoriteColor = resultSet.getString("favorite_color");
                employee.absences = getAllAbsencesByEmployeeId(employee.id);
                //System.out.println("Employee "+ employeeName + " gefunden!");
                resultSet.close();
                return employee;
            }
            resultSet.close();

        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return null;
    }

    public static ArrayList<String> getTeams(){
        return null; //TODO
    }
    public static void deleteTeam(String name){
        //TODO
    }
    public static void addTeam(String name) {
        //TODO
    }

    public static ArrayList<AbsenceType> getAllAbsenceTypes() {
        //TODO
        return null;
    }

    public static void addAbsenceType(String type) {
        //TODO
    }

    public static void deleteAbsenceType(AbsenceType type) {
        //TODO
    }

    public static ArrayList<Employee> getAllEmployees() {
        //TODO
        return null;
    }

    public static LocalDate getHighetDate() {
        return null; //TODO
    }


    //TODO Bitte die Dates als LocalDate-Objekte ausgeben, wenn möglich; Heißt, in der Klasse Absence und die returns ändern.
    // https://stackoverflow.com/questions/20165564/calculating-days-between-two-dates-with-java

    public static ArrayList<Map<Team, Integer>> getAbsancesPerTeamByDay(LocalDate date){
        return null;
        //TODO
    }
}
