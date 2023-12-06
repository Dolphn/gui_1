package org.example;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.example.TestDB.testDbErschaffen;

public class AbsencePlanner extends Application {
    private static Connection connection;
    public static ArrayList<Employee> employees;

    public AbsencePlanner() {
        //team = new Team();
        //connection = SQLiteConnection.connect();
        //initializeDatabase();
    }

    public static ArrayList<AbsenceType> getAllAbsenceTypes() {
        return new ArrayList<>();
    }

    public static void fetchAllEmployees() {
        ArrayList<Employee> employeesL = new ArrayList<>();

        String getEmployeeIdSQL = "SELECT * FROM employees;";

        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(getEmployeeIdSQL);
            while (resultSet.next()) {
                Employee employee = new Employee();
                employee.id = resultSet.getInt("id");
                employee.firstName = resultSet.getString("first_name");
                employee.lastName = resultSet.getString("last_name");
                employee.favoriteColor = resultSet.getString("favorite_color");
                employee.absences = getAllAbsencesByEmployeeId(employee.id);
                employeesL.add(employee);
            }
            resultSet.close();

        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        employees = employeesL;
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
    }


    public static void main(String[] args) {
        connection = SQLiteConnection.connect();
        initializeDatabase();
        //testDbErschaffen(); //Alte DB löschen, wenn die ids zu hoch werden/um die ids zu reseten


        launch(args);


        // Datenbankverbindung schließen
        SQLiteConnection.disconnect(connection);
    }

    //Databases
    public static void initializeDatabase() {
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

/*
    public static boolean addEmployee(Employee employee){
        //TODO Wie wählen wir das Team aus?
        String insertEmployeeSQL = "INSERT INTO employees (first_name, last_name, favorite_color) VALUES (?, ?, ?);";

        try (PreparedStatement preparedStatement = connection.prepareStatement(insertEmployeeSQL)) {
            preparedStatement.setString(1, employee.firstName);
            preparedStatement.setString(2, employee.lastName);
            preparedStatement.setString(3, employee.favoriteColor);
            preparedStatement.executeUpdate();

            System.out.println("Mitarbeiter '" + employee.firstName + " " + employee.lastName + "' hinzugefügt.");
            return true;
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return false;
        }
    }
    */


    //Employess
    public static boolean addEmployee(String firstName, String lastName, String favoriteColor) {
        //TODO Wie wählen wir das Team aus?
        String insertEmployeeSQL = "INSERT INTO employees (first_name, last_name, favorite_color) VALUES (?, ?, ?);";

        try (PreparedStatement preparedStatement = connection.prepareStatement(insertEmployeeSQL)) {
            preparedStatement.setString(1, firstName);
            preparedStatement.setString(2, lastName);
            preparedStatement.setString(3, favoriteColor);
            preparedStatement.executeUpdate();

            System.out.println("Mitarbeiter '" + firstName + " " + lastName + "' hinzugefügt.");
            return true;
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return false;
        }
    }


    private static Employee getEmployeeById(int id) {
        Employee employee = new Employee();
        String getEmployeeIdSQL = "SELECT * FROM employees WHERE id = ?;";

        try (PreparedStatement preparedStatement = connection.prepareStatement(getEmployeeIdSQL)) {
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                employee.id = resultSet.getInt("id");
                employee.firstName = resultSet.getString("first_name");
                employee.lastName = resultSet.getString("last_name");
                employee.favoriteColor = resultSet.getString("favorite_color");
                employee.absences = getAllAbsencesByEmployeeId(employee.id);
                return employee;
            }
            resultSet.close();

        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return null;
    }


    public static ArrayList<Employee> getAllEmployees() {
       return employees;
    }


    public static boolean updateEmployee(String firstName, String lastName, String favoriteColor, int id){
        //einen bestehenden Employee (am besten anhand seiner ID updaten, also where ID = ... Nochmal bitte checken, es passiert in der db nichts!
        String updateEmployeeSQL = """
                UPDATE employees
                SET first_name = ?, last_name = ?, favorite_color = ?
                WHERE id = ?;""";

        try(PreparedStatement preparedStatement = connection.prepareStatement(updateEmployeeSQL)){
            preparedStatement.setString(1, firstName);
            preparedStatement.setString(2, lastName);
            preparedStatement.setString(3, favoriteColor);
            preparedStatement.setInt(4, id);
            preparedStatement.executeUpdate();
            return true;
        }catch(SQLException e){
            System.err.println(e.getMessage());
            return false;
        }
    }


    public static boolean deleteEmployee(int id){
        //Absences des Mitarbeiters aus der DB loeschen
        ArrayList<Absence> absences = getAllAbsencesByEmployeeId(id);
        for(Absence a: absences){
            deleteAbsences(a.id);
        }
        //Mitarbeiter aus der db loeschen
        String deleteEmployeeSQL = "DELETE FROM employees WHERE id = ?;";

        try (PreparedStatement preparedStatement = connection.prepareStatement(deleteEmployeeSQL)) {
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();

            System.out.println("Mitarbeiter '" + id + "' geloescht.");
            return true;
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return false;
        }
    }



    //Absences
    public static void requestAbsence(Employee employee, AbsenceType type, LocalDate startDate, LocalDate endDate, boolean approved) {
        if (employee != null) {
            String insertAbsenceSQL = "INSERT INTO absences (employee_id, type, start_date, end_date, approved) VALUES (?, ?, ?, ?, ?);";

            try (PreparedStatement preparedStatement = connection.prepareStatement(insertAbsenceSQL)) {
                preparedStatement.setInt(1, employee.id);
                preparedStatement.setString(2, type.toString());
                preparedStatement.setString(3, startDate.toString());
                preparedStatement.setString(4, endDate.toString());
                preparedStatement.setBoolean(5,approved);
                preparedStatement.executeUpdate();

                //System.out.println("Abwesenheitsantrag für '" + employeeName + "' erstellt.");
            } catch (SQLException e) {
                System.err.println(e.getMessage());
            }
        }
    }

    /**
     * Setzt approved auf 1
     * @param id Id der Absence
     */
    public static void approveAbsence(int id){
        String aproveAbsenceSQL = "UPDATE absences SET approved = 1 WHERE id = ?;";

        try (PreparedStatement preparedStatement = connection.prepareStatement(aproveAbsenceSQL)) {
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }


    public static ArrayList<Absence> getAllAbsences(){
        ArrayList<Absence> absences = new ArrayList<>();
        String getAbsencesByIdSQL = "SELECT * FROM absences ;";
        try(Statement statement = connection.createStatement()){
            ResultSet resultSet = statement.executeQuery(getAbsencesByIdSQL);

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


    public static Map<Employee, Absence> getAbsencesPerEmployeeByDay(LocalDate date){
        Map<Employee, Absence> toDayAbsences = new HashMap<>();
        for(Employee e:getAllEmployees()){
            for(Absence a: e.absences){
                if((a.getStartDate().isBefore(date) && a.getEndDate().isAfter(date)) || a.getStartDate().isEqual(date) || a.getEndDate().isEqual(date)){
                    toDayAbsences.put(e,a);
                }
            }
        }
        return toDayAbsences;
    }


    public static boolean updateAbsence(int id, LocalDate start, LocalDate end, boolean approved, Employee employee, AbsenceType absenceType){
        String updateAbsenceSQL = """
                UPDATE absences
                SET employee_id = ?, type = ?, start_date = ?, end_date = ?,approved = ?
                WHERE id = ?;""";

        try(PreparedStatement preparedStatement = connection.prepareStatement(updateAbsenceSQL)){
            preparedStatement.setInt(1,employee.id);
            preparedStatement.setString(2,absenceType.toString());
            preparedStatement.setString(3,start.toString());
            preparedStatement.setString(4,end.toString());
            preparedStatement.setBoolean(5,approved);
            preparedStatement.setInt(6,id);
            preparedStatement.execute();
            return true;
        }catch(SQLException e){
            System.err.println(e.getMessage());
            return false;
        }
    }


    public static boolean deleteAbsences(int id){ //id ist für absence
        String deletAbsence = "DELETE FROM absences WHERE id = ?";
        try(PreparedStatement preparedStatement = connection.prepareStatement(deletAbsence)){
            preparedStatement.setInt(1,id);
            preparedStatement.execute();

            return true;
        } catch (SQLException e) {
            System.err.println("Absence nicht gefunden!");
            throw new RuntimeException(e);
        }
    }


    public static void deleteAbsence(Absence absence) {
        String deletAbsenceSQL = "DELETE FROM absences WHERE id = ?;";
        try(PreparedStatement preparedStatement = connection.prepareStatement(deletAbsenceSQL)){
            preparedStatement.setInt(1,absence.id);
            preparedStatement.executeUpdate();
        }catch(SQLException e){
            System.err.println(e.getMessage());
        }
    }


    //weiter (Test) Methoden


    private static int getIdByName(String name){
        String firstname = name.split(" ")[0];
        String lastname = name.split(" ")[1];
        String getEmployeeByNameSQL = "SELECT id FROM employees WHERE first_name = ? AND last_name = ?;";
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



    /*



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



    //TODO Methoden für die AbsenceTypes?? Brauchen wir das?



    //Teams
    public static ArrayList<String> getTeams(){
        ArrayList<String> list = new ArrayList<>();
        list.add("Test");
        list.add("Rosso");
        return list; //TODO
    }
    public static void deleteTeam(String name){
        //TODO
    }
    public static void addTeam(String name) {
        //TODO
    }



    public static LocalDate getHighestDate() {
        LocalDate highestDate = LocalDate.now();

        ArrayList<Absence> absences = getAllAbsences();
        if(!absences.isEmpty()) {
            highestDate = LocalDate.parse(absences.get(0).endDate);
            for (Absence a : absences) {
                if (a.getEndDate().isAfter(highestDate)) {
                    highestDate = a.getEndDate();
                }
            }
        }

        return highestDate;
    }


    //TODO Bitte die Dates als LocalDate-Objekte ausgeben, wenn möglich; Heißt, in der Klasse Absence und die returns ändern.
    // https://stackoverflow.com/questions/20165564/calculating-days-between-two-dates-with-java



    public static ArrayList<String> getTeamsOfEmployee(int id){
        return new ArrayList<>();
        //TODO
    }

    public static void updateTeam(String team, String text) {
    }


    //Tests

}
