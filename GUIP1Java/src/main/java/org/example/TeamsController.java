package org.example;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class TeamsController implements Initializable {

    private boolean edit;
    private String team;

    public TeamsController(){

    }
    public TeamsController(String team, boolean edit){
        this.team = team;
        this.edit = edit;
    }
    @FXML
    private Button deleteTeam;

    @FXML
    private Button submit;

    @FXML
    private Button interrupt;

    @FXML
    private Label label;

    @FXML
    private TextField teamName;

    @FXML
    void deleteTeam(ActionEvent event) {
        AbsencePlanner.deleteTeam(teamName.getText());
        ((Node)event.getSource()).getScene().getWindow().hide();
    }

    @FXML
    void interrupt(ActionEvent event) {
        ((Node)event.getSource()).getScene().getWindow().hide();
    }

    @FXML
    void submitTeam(ActionEvent event) {
        if (edit){
            AbsencePlanner.updateTeam(team, teamName.getText());
        }
        else {
            AbsencePlanner.addTeam(teamName.getText());
        }
        ((Node)event.getSource()).getScene().getWindow().hide();
        ((Node)event.getSource()).getScene().getWindow().hide();

    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (edit){
            teamName.setText(team);
            label.setText("Team bearbeiten, alt: " + team);
        }
        else {
            deleteTeam.setDisable(true);
        }
    }
}
