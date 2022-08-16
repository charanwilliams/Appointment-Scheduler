package controller;

import helper.controller;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;

import java.io.IOException;

public class MainMenuController extends controller {
    @FXML
    private Button addAppointmentButton;

    @FXML
    private Button addCustomerButton;

    @FXML
    private Button appointmentHistoryButton;

    @FXML
    private ToggleGroup appointmentToggle;

    @FXML
    private Button deleteAppointment;

    @FXML
    private Button deleteCustomerButton;

    @FXML
    private Button logoutButton;

    @FXML
    private RadioButton monthAppointmentToggle;

    @FXML
    private Button schedulesButton;

    @FXML
    private Label titleLabel;

    @FXML
    private Button totalAppointmentsButton;

    @FXML
    private Button updateAppointmentButton;

    @FXML
    private Button updateCustomerButton;

    @FXML
    private RadioButton weekAppointmentToggle;

    @FXML
    private RadioButton allAppointmentToggle;

    @FXML
    void addAppointmentButtonClicked(ActionEvent event) {

    }

    @FXML
    void addCustomerButtonClicked(ActionEvent event) {

    }

    @FXML
    void appointmentHistoryButtonClicked(ActionEvent event) {

    }

    @FXML
    void deleteAppointmentButtonClicked(ActionEvent event) {

    }

    @FXML
    void deleteCustomerButtonClicked(ActionEvent event) {

    }

    @FXML
    void logoutButtonClicked(ActionEvent event) throws IOException {
    changeScene(event, "/com/example/model/LoginForm.fxml");
    }

    @FXML
    void monthAppointmentToggleSelected(ActionEvent event) {

    }

    @FXML
    void schedulesButtonClicked(ActionEvent event) {

    }

    @FXML
    void totalAppointmentsButtonClicked(ActionEvent event) {

    }

    @FXML
    void updateAppointmentButtonClicked(ActionEvent event) {

    }

    @FXML
    void updateCustomerButtonClicked(ActionEvent event) {

    }

    @FXML
    void weekAppointmentToggleSelected(ActionEvent event) {

    }


    @FXML
    void allAppointmentToggleSelected(ActionEvent event) {

    }
}