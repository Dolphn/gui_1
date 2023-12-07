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
import java.util.HashMap;
import java.util.Map;

import static org.example.TestDB.testDbErschaffen;

public class AbsencePlanner extends Application {
    public static ArrayList<Employee> employees;
    private static Connection connection;
    private static boolean debug = true;

    public AbsencePlanner() {
    }

    public static ArrayList<AbsenceType> getAllAbsenceTypes() {
        return new ArrayList<>();
    }

    public static void main(String[] args) {
        connection = SQLiteConnection.connect();
        //testDbErschaffen(); //Alte DB löschen, wenn die ids zu hoch werden/um die ids zu reseten
        initializeDatabase();

        //Debug
        if (debug) {

        }
        //Ende

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

        String createTeamsTableSQL = """
                CREATE TABLE IF NOT EXISTS teams (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name STRING);
                """;

        String createTeamEmployeeTableSQL = """
                CREATE TABLE IF NOT EXISTS teamEmployee (
                employee_id INTEGER,
                team_id INTEGER);
                """;


        try (PreparedStatement preparedStatement1 = connection.prepareStatement(createEmployeesTableSQL);
             PreparedStatement preparedStatement2 = connection.prepareStatement(createAbsencesTableSQL);
             PreparedStatement preparedStatement3 = connection.prepareStatement(createTeamsTableSQL);
             PreparedStatement preparedStatement4 = connection.prepareStatement(createTeamEmployeeTableSQL)) {
            preparedStatement1.execute();
            preparedStatement2.execute();
            preparedStatement3.execute();
            preparedStatement4.execute();
            System.out.println("Tabellen 'employees', 'absences', 'teams' und'teamEmployee erstellt.");
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    //Employess
    public static int addEmployee(String firstName, String lastName, String favoriteColor) {
        int id;
        String insertEmployeeSQL = "INSERT INTO employees (first_name, last_name, favorite_color) VALUES (?, ?, ?);";

        try (PreparedStatement preparedStatement = connection.prepareStatement(insertEmployeeSQL, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, firstName);
            preparedStatement.setString(2, lastName);
            preparedStatement.setString(3, favoriteColor);
            preparedStatement.executeUpdate();

            System.out.println("Mitarbeiter '" + firstName + " " + lastName + "' hinzugefügt.");
            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    System.out.println(generatedKeys.getInt(1));
                    return generatedKeys.getInt(1);
                }
                else {
                    throw new SQLException("Creating user failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return 0;
        }
    }

/*
    public static boolean addEmployee(Employee employee){
        //TTODO Wie wählen wir das Team aus?
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

    public static boolean addEmployee(String firstName, String lastName, String favoriteColor, String team_name) {
        String insertEmployeeSQL = "INSERT INTO employees (first_name, last_name, favorite_color) VALUES (?, ?, ?);";

        try (PreparedStatement preparedStatement = connection.prepareStatement(insertEmployeeSQL)) {
            preparedStatement.setString(1, firstName);
            preparedStatement.setString(2, lastName);
            preparedStatement.setString(3, favoriteColor);
            preparedStatement.executeUpdate();

            System.out.println("Mitarbeiter '" + firstName + " " + lastName + "' hinzugefügt.");
            fetchAllEmployees();
            addEmployeeToTeam(getAllEmployees().getLast().id, team_name);
            return true;
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return false;
        }
    }

    public static boolean addEmployee(String firstName, String lastName, String favoriteColor, int team_id) {
        String insertEmployeeSQL = "INSERT INTO employees (first_name, last_name, favorite_color) VALUES (?, ?, ?);";

        try (PreparedStatement preparedStatement = connection.prepareStatement(insertEmployeeSQL)) {
            preparedStatement.setString(1, firstName);
            preparedStatement.setString(2, lastName);
            preparedStatement.setString(3, favoriteColor);
            preparedStatement.executeUpdate();

            System.out.println("Mitarbeiter '" + firstName + " " + lastName + "' hinzugefügt.");
            fetchAllEmployees();
            addEmployeeToTeam(getAllEmployees().getLast().id, team_id);
            return true;
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return false;
        }
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


    public static boolean updateEmployee(String firstName, String lastName, String favoriteColor, int id) {
        String updateEmployeeSQL = """
                UPDATE employees
                SET first_name = ?, last_name = ?, favorite_color = ?
                WHERE id = ?;""";

        try (PreparedStatement preparedStatement = connection.prepareStatement(updateEmployeeSQL)) {
            preparedStatement.setString(1, firstName);
            preparedStatement.setString(2, lastName);
            preparedStatement.setString(3, favoriteColor);
            preparedStatement.setInt(4, id);
            preparedStatement.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return false;
        }
    }

    public static boolean updateEmployee(String firstName, String lastName, String favoriteColor, int id,ArrayList<String> teams) {
        updateEmployeeTeams(id, teams);
        String updateEmployeeSQL = """
                UPDATE employees
                SET first_name = ?, last_name = ?, favorite_color = ?
                WHERE id = ?;""";

        try (PreparedStatement preparedStatement = connection.prepareStatement(updateEmployeeSQL)) {
            preparedStatement.setString(1, firstName);
            preparedStatement.setString(2, lastName);
            preparedStatement.setString(3, favoriteColor);
            preparedStatement.setInt(4, id);
            preparedStatement.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return false;
        }
    }

    private static void updateEmployeeTeams(int employee_id, ArrayList<String> teams) {
        deleteEmployeeFromAllTeams(employee_id);
        for(String team: teams){
            addEmployeeToTeam(employee_id,team);
        }
    }

    public static boolean deleteEmployee(int id) {
        //Absences des Mitarbeiters aus der DB loeschen
        ArrayList<Absence> absences = getAllAbsencesByEmployeeId(id);
        for (Absence a : absences) {
            deleteAbsences(a.id);
        }
        //Loeschen aller Teammiedgliedschaften
        deleteEmployeeFromAllTeams(id);
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
                preparedStatement.setBoolean(5, approved);
                preparedStatement.executeUpdate();

                //System.out.println("Abwesenheitsantrag für '" + employeeName + "' erstellt.");
            } catch (SQLException e) {
                System.err.println(e.getMessage());
            }
        }
    }

    public static ArrayList<Absence> getAllAbsences() {
        ArrayList<Absence> absences = new ArrayList<>();
        String getAbsencesByIdSQL = "SELECT * FROM absences ;";
        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(getAbsencesByIdSQL);

            while (resultSet.next()) {
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
    public static void approveAbsence(int id){
        String aproveAbsenceSQL = "UPDATE absences SET approved = 1 WHERE id = ?;";

        try (PreparedStatement preparedStatement = connection.prepareStatement(aproveAbsenceSQL)) {
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

     */

    public static ArrayList<Absence> getAllAbsencesByEmployeeId(int id) {
        ArrayList<Absence> absences = new ArrayList<>();
        String getAbsencesByIdSQL = "SELECT * FROM absences WHERE employee_id= ? ;";
        try (PreparedStatement preparedStatement = connection.prepareStatement(getAbsencesByIdSQL)) {
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
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

    public static Map<Employee, Absence> getAbsencesPerEmployeeByDay(LocalDate date) {
        Map<Employee, Absence> toDayAbsences = new HashMap<>();
        for (Employee e : getAllEmployees()) {
            for (Absence a : e.absences) {
                if ((a.getStartDate().isBefore(date) && a.getEndDate().isAfter(date)) || a.getStartDate().isEqual(date) || a.getEndDate().isEqual(date)) {
                    toDayAbsences.put(e, a);
                }
            }
        }
        return toDayAbsences;
    }

    public static boolean updateAbsence(int id, LocalDate start, LocalDate end, boolean approved, Employee employee, AbsenceType absenceType) {
        String updateAbsenceSQL = """
                UPDATE absences
                SET employee_id = ?, type = ?, start_date = ?, end_date = ?,approved = ?
                WHERE id = ?;""";

        try (PreparedStatement preparedStatement = connection.prepareStatement(updateAbsenceSQL)) {
            preparedStatement.setInt(1, employee.id);
            preparedStatement.setString(2, absenceType.toString());
            preparedStatement.setString(3, start.toString());
            preparedStatement.setString(4, end.toString());
            preparedStatement.setBoolean(5, approved);
            preparedStatement.setInt(6, id);
            preparedStatement.execute();
            return true;
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return false;
        }
    }

    public static boolean deleteAbsences(int id) { //id ist für absence
        String deletAbsence = "DELETE FROM absences WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(deletAbsence)) {
            preparedStatement.setInt(1, id);
            preparedStatement.execute();

            return true;
        } catch (SQLException e) {
            System.err.println("Absence nicht gefunden!");
            throw new RuntimeException(e);
        }
    }

    public static void deleteAbsence(Absence absence) {
        String deletAbsenceSQL = "DELETE FROM absences WHERE id = ?;";
        try (PreparedStatement preparedStatement = connection.prepareStatement(deletAbsenceSQL)) {
            preparedStatement.setInt(1, absence.id);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    public static void addTeam(String name) {
        String insertTeamSQL = "INSERT INTO teams (name) VALUES (?);";
        try (PreparedStatement preparedStatement = connection.prepareStatement(insertTeamSQL)) {
            preparedStatement.setString(1, name);
            preparedStatement.executeUpdate();

            System.out.println("Team " + name + " hinzugefügt");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    //weiter (Test) Methoden

    /*
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


     */


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


    //Teams

    public static String getTeamNameById(int team_id) {
        String team_name = null;
        String selectTeamsSQL = "SELECT name FROM teams WHERE id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(selectTeamsSQL)) {
            preparedStatement.setInt(1, team_id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                team_name = (resultSet.getString("name"));
                return team_name;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return team_name;
    }

    public static int getTeamIdByName(String team_name) {
        int team_id = -1;
        String selectTeamsSQL = "SELECT id FROM teams WHERE name = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(selectTeamsSQL)) {
            preparedStatement.setString(1, team_name);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                team_id = (resultSet.getInt("id"));
                return team_id;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return team_id;
    }

    public static ArrayList<String> getTeams() {
        ArrayList<String> list = new ArrayList<>();
        String selectTeamsSQL = "SELECT * FROM teams";

        try (PreparedStatement preparedStatement = connection.prepareStatement(selectTeamsSQL)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                list.add(resultSet.getString(2));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    public static void deleteTeam(String name) {
        int team_id = getTeamIdByName(name);

        String deleteTeamSQL = "DELETE FROM teams WHERE name = ?;";
        String deleteAllTeamEmployeeSQL = "DELETE FROM teamEmployee WHERE team_id = ?;";

        try (PreparedStatement preparedStatement1 = connection.prepareStatement(deleteTeamSQL);
             PreparedStatement preparedStatement2 = connection.prepareStatement(deleteAllTeamEmployeeSQL)) {
            preparedStatement1.setString(1, name);
            preparedStatement2.setInt(1, team_id);
            preparedStatement2.executeUpdate();
            preparedStatement1.executeUpdate();

            System.out.println("Team " + name + " gelöscht.");

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void deleteTeam(int id) {
        //Delete alle Einträge aus teamEmployees!!!


        String deleteTeamSQL = "DELETE FROM teams WHERE id = ?;";
        String deleteAllTeamEmployeeSQL = "DELETE FROM teamEmployee WHERE team_id = ?;";

        try (PreparedStatement preparedStatement1 = connection.prepareStatement(deleteTeamSQL);
             PreparedStatement preparedStatement2 = connection.prepareStatement(deleteAllTeamEmployeeSQL)) {
            preparedStatement1.setInt(1, id);
            preparedStatement1.executeUpdate();
            preparedStatement2.setInt(1, id);

            preparedStatement2.executeUpdate();

            System.out.println("Team " + id + " gelöscht.");

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void addEmployeeToTeam(int employee_id, int team_id) {
        if (debug) {
            System.out.println("addEmployeeToTeam!");
        }
        //Elseteil ist dummy code, damit die setInts keinen Error erzeugen
        String insertIntoTeamEmployeeSQL = "INSERT INTO teamEmployee (employee_id,team_id) VALUES (?,?);";
                /**
                IF EXISTS (SELECT employee_id FROM teamEmployees WHERE team_id = ?)
                    BEGIN
                        SELECT * FROM teamEmployee WHERE employee_id = ? AND team_id = ?
                    END
                ELSE
                    BEGIN
                        INSERT INTO teamEmployee VALUES (?,?)
                    END
                */
        try (PreparedStatement preparedStatement = connection.prepareStatement(insertIntoTeamEmployeeSQL)) {
            preparedStatement.setInt(1, employee_id);
            preparedStatement.setInt(2, team_id);
            preparedStatement.executeUpdate();

            System.out.println("Employee " + employee_id + " zum Team " + team_id + " hinzugefügt");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void addEmployeeToTeam(Employee e, int team_id) {
        int employee_id = e.id;
        addEmployeeToTeam(employee_id, team_id);
    }

    public static void addEmployeeToTeam(int employee_id, String team_name) {
        int team_id = getTeamIdByName(team_name);
        addEmployeeToTeam(employee_id, team_id);
    }

    /**
     * NOT WORKING!
     *
     * @param employee_id
     * @param team_id
     * @return
     */
    public static boolean isEmployeeInTeam(int employee_id, int team_id) {
        boolean is = false;
        //String


        return is;
    }


    //Read

    public static ArrayList<String> getTeamsOfEmployee(int employee_id) {
        ArrayList<String> teams = new ArrayList<>();
        String selectTeamsSQL = "SELECT team_id FROM teamEmployee WHERE employee_id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(selectTeamsSQL)) {
            preparedStatement.setInt(1, employee_id);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                teams.add(getTeamNameById(resultSet.getInt("team_id")));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return teams;
    }

    //Delete
    public static void deleteEmployeeFromTeam(int employee_id, int team_id) {
        String insertIntoTeamEmployeeSQL = "DELETE FROM teamEmployee WHERE employee_id = ? AND team_id = ?;";
        try (PreparedStatement preparedStatement = connection.prepareStatement(insertIntoTeamEmployeeSQL)) {
            preparedStatement.setInt(1, employee_id);
            preparedStatement.setInt(2, team_id);
            preparedStatement.executeUpdate();

            System.out.println("Employee " + employee_id + " vom Team " + team_id + " geloescht");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    //Update

    private static void deleteEmployeeFromTeam(int employee_id, String team_name) {
        int team_id = getTeamIdByName(team_name);
        deleteEmployeeFromTeam(employee_id, team_id);
    }

    private static void deleteEmployeeFromAllTeams(int id) {
        ArrayList<String> teams = getTeamsOfEmployee(id);
        for (String team : teams) {
            deleteEmployeeFromTeam(id, team);
        }
    }

    /**
     * @param team_name     der Name des Teams, das geändert werden soll
     * @param team_name_new der neue Name, den das Team haben soll
     */
    public static void updateTeam(String team_name, String team_name_new) {
        String updateAbsenceSQL = """
                UPDATE teams
                SET name = ?
                WHERE name = ?;""";

        try (PreparedStatement preparedStatement = connection.prepareStatement(updateAbsenceSQL)) {
            preparedStatement.setString(1, team_name_new); //neuer Teamname
            preparedStatement.setString(2, team_name);     //alter Teamname

            preparedStatement.execute();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    /**
     * @param team_id       Id des Teams
     * @param team_name_new Neuer Name des Teams
     */
    public static void updateTeam(int team_id, String team_name_new) {
        String updateAbsenceSQL = """
                UPDATE teams
                SET name = ?
                WHERE id = ?;""";

        try (PreparedStatement preparedStatement = connection.prepareStatement(updateAbsenceSQL)) {
            preparedStatement.setString(1, team_name_new); //neuer Teamname
            preparedStatement.setInt(2, team_id);     //alter Teamname

            preparedStatement.execute();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    public static Connection getConnection() {
        return connection;
    }

    public static LocalDate getHighestDate() {
        LocalDate highestDate = LocalDate.now();

        ArrayList<Absence> absences = getAllAbsences();
        if (!absences.isEmpty()) {
            highestDate = LocalDate.parse(absences.get(0).endDate);
            for (Absence a : absences) {
                if (a.getEndDate().isAfter(highestDate)) {
                    highestDate = a.getEndDate();
                }
            }
        }

        return highestDate;
    }

    public static void testDbErschaffen() {
        //Datenbank erschaffen
        initializeDatabase();
        //Datenbankdaten loeschen
        String clearEmployeesTableSQL = "DELETE FROM employees;";
        String clearAbsencesTableSQL = "DELETE FROM absences;";
        String clearteamsTableSQL = "DELETE FROM teams;";
        String clearTeamEmployeeTableSQL = "DELETE FROM teamEmployee;";

        try (PreparedStatement preparedStatement1 = connection.prepareStatement(clearEmployeesTableSQL);
             PreparedStatement preparedStatement2 = connection.prepareStatement(clearAbsencesTableSQL);
             PreparedStatement preparedStatement3 = connection.prepareStatement(clearteamsTableSQL);
             PreparedStatement preparedStatement4 = connection.prepareStatement(clearTeamEmployeeTableSQL)) {
            preparedStatement1.execute();
            preparedStatement2.execute();
            preparedStatement3.execute();
            preparedStatement4.execute();
            System.out.println("Tabellen 'employees', 'absences', 'teams' und 'teamEmployee geleert.");
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        //Datenbank füllen

        addEmployee("Bruno", "Brenner", "#00CED1");
        addEmployee("Daniel", "Deiters", "#A0CED5");
        addEmployee("Ruben", "Reiter", "#4A0D65");

        fetchAllEmployees();

        addTeam("Lachs");
        addTeam("Wal");
        addTeam("Hai");

        addEmployeeToTeam(employees.get(0).id, getTeamIdByName("Lachs"));
        addEmployeeToTeam(employees.get(0).id, getTeamIdByName("Wal"));
        addEmployeeToTeam(employees.get(1).id, getTeamIdByName("Wal"));
        addEmployeeToTeam(employees.get(1).id, getTeamIdByName("Hai"));
        addEmployeeToTeam(employees.get(2).id, getTeamIdByName("Hai"));

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy");

        requestAbsence(employees.get(0), AbsenceType.SICKNESS, LocalDate.parse("01-01-2024", dtf), LocalDate.parse("05-01-2024", dtf), false);
        requestAbsence(employees.get(1), AbsenceType.REMOTE_WORK, LocalDate.parse("02-01-2024", dtf), LocalDate.parse("04-01-2024", dtf), true);
        requestAbsence(employees.get(2), AbsenceType.SICKNESS, LocalDate.parse("01-02-2024", dtf), LocalDate.parse("02-01-2024", dtf), false);
        requestAbsence(employees.get(2), AbsenceType.TRAINING, LocalDate.parse("03-01-2024", dtf), LocalDate.parse("10-01-2024", dtf), false);

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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}