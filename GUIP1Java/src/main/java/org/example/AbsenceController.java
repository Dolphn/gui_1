package org.example;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

public class AbsenceController {

    private Absence absence = null;
    private boolean edit = false;

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
    private ListView<?> typesList;

    @FXML
    void confirm(ActionEvent event) {

    }

    @FXML
    void deleteAbsence(ActionEvent event) {

    }

    @FXML
    void interrupt(ActionEvent event) {

    }

}
