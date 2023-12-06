package org.example;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;

public class AbsenceController implements Initializable {
    DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("dd.MM.yyyy");


    @FXML
    private Spinner<AbsenceType> absenceType;

    private Absence absence = null;
    private boolean edit = false;
    private ArrayList<Employee> employees;
    private ArrayList<AbsenceType> absenceTypes;

    @FXML
    private CheckBox approved;

    @FXML
    private DatePicker datePickerEnd;

    @FXML
    private DatePicker datePickerStart;

    @FXML
    private Spinner<String> employee;

    @FXML
    private Label headline;

    @FXML
    private Label selectedEmployee;

    @FXML
    private Label selectedType;

    public AbsenceController(Absence absence, boolean edit){
        this.absence = absence;
        this.edit = edit;
    }
    public AbsenceController(){

    }

    @FXML
    void confirm(ActionEvent event) {
        if (edit){
            for (Employee e: employees){
                if (Integer.parseInt(employee.getValue().split("\\.")[0]) == e.id){
                    AbsencePlanner.updateAbsence(absence.id, datePickerStart.getValue(), datePickerEnd.getValue(), approved.isSelected(), e, absenceType.getValue());
                }
            }
        }
        else {
            for (Employee e: employees){
                if (Integer.parseInt(employee.getValue().split("\\.")[0]) == e.id){
                    AbsencePlanner.requestAbsence(e, absenceType.getValue(), datePickerStart.getValue(), datePickerEnd.getValue(), approved.isSelected());
                }
            }

        }

        ((Node)event.getSource()).getScene().getWindow().hide();
    }

    @FXML
    void deleteAbsence(ActionEvent event) {
        if (edit){
            AbsencePlanner.deleteAbsence(absence);
        }

        ((Node)event.getSource()).getScene().getWindow().hide();
    }

    @FXML
    void interrupt(ActionEvent event) {
        ((Node)event.getSource()).getScene().getWindow().hide();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        absenceTypes = new ArrayList<>(Arrays.asList(AbsenceType.values()));
        employees = AbsencePlanner.getAllEmployees();
        ArrayList<String> empStr = new ArrayList<>();
        for (Employee e: employees) {
            empStr.add(e.id + ". " + e.lastName + ", " + e.firstName);
        }

        ObservableList<String> emps = FXCollections.observableArrayList(empStr);

        // Value factory.
        SpinnerValueFactory<String> valueFactory = //
                new SpinnerValueFactory.ListSpinnerValueFactory<String>(emps);


        ObservableList<AbsenceType> absenceTypesList = FXCollections.observableArrayList(absenceTypes);

        // Value factory.
        SpinnerValueFactory<AbsenceType> valueFactory1 = //
                new SpinnerValueFactory.ListSpinnerValueFactory<AbsenceType>(absenceTypesList);

        employee.setValueFactory(valueFactory);
        absenceType.setValueFactory(valueFactory1);
        if (!edit) return;

        approved.setSelected(absence.approved);


        datePickerStart.setValue(LocalDate.parse(absence.startDate));
        datePickerEnd.setValue(LocalDate.parse(absence.endDate));

    }
}
