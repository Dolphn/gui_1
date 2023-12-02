package org.example;

import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.paint.Color;

import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.time.LocalDate;


public class AbsenceController implements Initializable {

    private Absence absence = null;
    private boolean edit = false;
    private String lastName;
    private String firstname;
    private AbsenceType absenceType;

    public AbsenceController(){

    }
    public AbsenceController(Absence absence, boolean edit){
        this.absence = absence;
        this.edit = edit;
    }
    @FXML
    private CheckBox approved;

    @FXML
    private DatePicker datePickerEnd;

    @FXML
    private DatePicker datePickerStart;

    @FXML
    private Label headline;

    @FXML
    private Label selectedType;

    @FXML
    private ListView<String> typesList;

    @FXML
    private ListView<String> employeesList;

    @FXML
    private Label selectedEmployee;

    @FXML
    void confirm(ActionEvent event) {
        DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        AbsencePlanner.requestAbsence(firstname + " "+ lastName, absenceType, datePickerStart.getValue().format(myFormatObj), datePickerEnd.getValue().format(myFormatObj));
    }

    @FXML
    void deleteAbsence(ActionEvent event) {
        if(edit) {
            AbsencePlanner.deleteAbsence(absence);
        }
    }

    @FXML
    void interrupt(ActionEvent event) {
        ((Node)event.getSource()).getScene().getWindow().hide();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        ArrayList<String> types = AbsencePlanner.getAllAbsenceTypes();
        ArrayList<Employee> employees = AbsencePlanner.getAllEmployees();
        ArrayList<String> emps = new ArrayList<>();
        for (Employee e: employees) {
            emps.add(e.lastName +", " + e.firstName);
        }
        if (this.edit) {
            DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("dd.MM.yyyy");
            datePickerStart.setValue(LocalDate.parse(absence.startDate, myFormatObj));
            datePickerEnd.setValue(LocalDate.parse(absence.endDate, myFormatObj));

            String currentType = absence.type.toString();
            headline.setText("Abwesenheit anpassen");
        }
        if (types != null){
            typesList.getItems().addAll(types);
        }
        employeesList.getItems().addAll(emps);
        typesList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        typesList.getSelectionModel().selectedItemProperty().addListener(this::selectionChanged);

        employeesList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        employeesList.getSelectionModel().selectedItemProperty().addListener(this::selectionChanged);
    }

    private void selectionChanged(ObservableValue<? extends String> observable, String oldV, String newV){
        ObservableList<String> selectedItems = typesList.getSelectionModel().getSelectedItems();
        String selectedItem = (selectedItems.isEmpty())? "" : selectedItems.toString();
        selectedType.setText(selectedItem);
        absenceType = AbsenceType.getAbscenceTypeByString(selectedItem);

        ObservableList<String> sel = employeesList.getSelectionModel().getSelectedItems();
        String selString = (sel.isEmpty())? "" : sel.toString();
        selectedEmployee.setText(selectedItem);
        String[] param = selectedItem.trim().split(",");
        lastName = param[0];
        firstname = param[1];

    }
}
