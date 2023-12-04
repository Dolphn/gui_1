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
    private Spinner<Employee> employee;

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
            AbsencePlanner.updateAbsence(absence.id, datePickerStart.getValue(), datePickerEnd.getValue(), approved.isSelected(), employee.getValue(), absenceType.getValue());
        }
        else {
            AbsencePlanner.requestAbsence(employee.getValue(), absenceType.getValue(), datePickerStart.getValue(), datePickerEnd.getValue(), approved.isSelected());
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
        absenceTypes = AbsencePlanner.getAllAbsenceTypes();
        employees = AbsencePlanner.getAllEmployees();

        ObservableList<Employee> emps = FXCollections.observableArrayList(employees);

        // Value factory.
        SpinnerValueFactory<Employee> valueFactory = //
                new SpinnerValueFactory.ListSpinnerValueFactory<Employee>(emps);


        ObservableList<AbsenceType> absenceTypesList = FXCollections.observableArrayList(absenceTypes);

        // Value factory.
        SpinnerValueFactory<AbsenceType> valueFactory1 = //
                new SpinnerValueFactory.ListSpinnerValueFactory<AbsenceType>(absenceTypesList);
        if (!edit) return;

        employee.setValueFactory(valueFactory);
        absenceType.setValueFactory(valueFactory1);

        approved.setSelected(absence.approved);


        datePickerStart.setValue(LocalDate.parse(absence.startDate));
        datePickerEnd.setValue(LocalDate.parse(absence.endDate));

    }
}
