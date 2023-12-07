package org.example.control;

        import javafx.beans.value.ObservableValue;
        import javafx.collections.ObservableList;
        import javafx.event.ActionEvent;
        import javafx.fxml.FXML;
        import javafx.fxml.Initializable;
        import javafx.scene.Node;
        import javafx.scene.control.*;
        import javafx.scene.paint.Color;
        import org.example.impl.AbsencePlanner;
        import org.example.entities.Employee;

        import java.net.URL;
        import java.util.ArrayList;
        import java.util.ResourceBundle;

public class EmployeeController implements Initializable {

    Employee employee = null;
    ArrayList<String> teamsOfEmployee = new ArrayList<>();
    boolean edit = false;

    public EmployeeController(){
    }
    public EmployeeController(Employee employee, boolean edit){
        this.employee = employee;
        this.edit = edit;
    }

    @FXML
    private TextField firstname;
    @FXML
    private Button buttonDelete;
    @FXML
    private TextField lastname;
    @FXML
    private Label selectedTeams;
    @FXML
    private ListView<String> teamsList;
    @FXML
    private ColorPicker colorPicker;
    @FXML
    private Label headline;


    @FXML
    public void interrupt(ActionEvent e) {
        ((Node)e.getSource()).getScene().getWindow().hide();
    }

    @FXML
    void newEmployee(ActionEvent event) {
        Color color = colorPicker.getValue();
        String str = String.format( "#%02X%02X%02X", (int)( color.getRed() * 255 ), (int)( color.getGreen() * 255 ), (int)( color.getBlue() * 255 ) );
        if (edit){
            AbsencePlanner.updateEmployee(firstname.getText(), lastname.getText(),str, employee.id);
            for (String team : teamsOfEmployee){
                if (!teamsList.getSelectionModel().getSelectedItems().contains(team)){
                    AbsencePlanner.deleteEmployeeFromTeam(employee.id, AbsencePlanner.getTeamIdByName(team));
                }
            }
            for (String team :  teamsList.getSelectionModel().getSelectedItems()){
                if (!teamsOfEmployee.contains(team)){
                    AbsencePlanner.addEmployeeToTeam(employee.id, AbsencePlanner.getTeamIdByName(team));
                }
            }

        }
        else {
            int id = AbsencePlanner.addEmployee(firstname.getText(), lastname.getText(), str);
            for (String team : teamsList.getSelectionModel().getSelectedItems()){

            AbsencePlanner.addEmployeeToTeam(id, AbsencePlanner.getTeamIdByName(team));
            }
        }

        ((Node)event.getSource()).getScene().getWindow().hide();
    }

    @FXML
    void deleteEmployee(ActionEvent event) {
        if(edit) {
            AbsencePlanner.deleteEmployee(employee.id);
        }
        ((Node)event.getSource()).getScene().getWindow().hide();
    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ArrayList<String> teams = AbsencePlanner.getTeams();

            teamsList.getItems().addAll(teams);
        teamsList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        teamsList.getSelectionModel().selectedItemProperty().addListener(this::selectionChanged);

        if (this.edit) {
            teamsOfEmployee = AbsencePlanner.getTeamsOfEmployee(employee.id);

            lastname.setText(employee.lastName);
            firstname.setText(employee.firstName);
            colorPicker.setValue(Color.valueOf(employee.favoriteColor));

            ArrayList<String> currentTeams = AbsencePlanner.getTeamsOfEmployee(employee.id);
            for (String s : currentTeams) {
                teamsList.getSelectionModel().select(s);
            }
            headline.setText("Mitarbeiter verwalten");
        }
        else {
            buttonDelete.setDisable(true);

        }
    }

    private void selectionChanged(ObservableValue<? extends String> observable, String oldV, String newV){
        ObservableList<String> selectedItems = teamsList.getSelectionModel().getSelectedItems();
        String selectedItem = (selectedItems.isEmpty())? "Kein Team ausgewählt" : selectedItems.toString();
        selectedTeams.setText(selectedItem);
    }
}
