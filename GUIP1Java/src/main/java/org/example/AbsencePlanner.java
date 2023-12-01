package org.example;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Map;
import java.util.Random;

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

        try (Statement statement = connection.createStatement()) {
            statement.execute(createEmployeesTableSQL);
            statement.execute(createAbsencesTableSQL);
            System.out.println("Tables 'employees' and 'absences' created.");
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }


    public void addAbsencesForAllEmployees() {
        AbsenceType[] absenceTypes = AbsenceType.values();
        Random random = new Random();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String query = "SELECT id FROM employees;";

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                int employeeId = resultSet.getInt("id");

                // Add 5 random absences for the employee
                for (int i = 0; i < 5; i++) {
                    AbsenceType randomType = absenceTypes[random.nextInt(absenceTypes.length)];
                    LocalDate randomStartDate = getRandomStartDate();
                    // Random duration from 1 to 4 days
                    LocalDate randomEndDate = randomStartDate.plusDays(random.nextInt(4) + 1);
                    requestAbsence(employeeId, randomType, randomStartDate.format(formatter), randomEndDate.format(formatter));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error adding absences for all employees: " + e.getMessage());
        }
    }

    private LocalDate getRandomStartDate() {
        Random random = new Random();
        // Randomly choose a month (11 for December, 0 for January)
        int month = random.nextBoolean() ? 11 : 0;
        // Random day in December 2023 or January 2024
        int day = month == 11 ? random.nextInt(31) + 1 : random.nextInt(31) + 1;
        int year = month == 11 ? 2023 : 2024;
        return LocalDate.of(year, month + 1, day);
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

    public void requestAbsence(int employeeId, AbsenceType type, String startDate, String endDate) {
        String insertAbsenceSQL = "INSERT INTO absences (employee_id, type, start_date, end_date, approved) VALUES (?, ?, ?, ?, 0);";

        try (PreparedStatement preparedStatement = connection.prepareStatement(insertAbsenceSQL)) {
            preparedStatement.setInt(1, employeeId);
            preparedStatement.setString(2, type.name());
            preparedStatement.setString(3, startDate);
            preparedStatement.setString(4, endDate);
            preparedStatement.executeUpdate();

                System.out.println("Abwesenheitsantrag für '" + employeeId + "' erstellt.");
            } catch (SQLException e) {
                System.err.println(e.getMessage());
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

    public static void main(String[] args) {
        AbsencePlanner planner = new AbsencePlanner();

        planner.addAbsencesForAllEmployees();

        SQLiteConnection.disconnect(planner.connection);
    }
}
