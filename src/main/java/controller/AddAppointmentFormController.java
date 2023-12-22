package controller;

import model.Appointment;
import model.Contact;
import model.Customer;
import model.User;
import dao.AppointmentQuery;
import dao.ContactQuery;
import dao.CustomerQuery;
import dao.UserQuery;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;

public class AddAppointmentFormController extends Controller {

    private ObservableList<Contact> allContacts = FXCollections.observableArrayList();
    private ObservableList<Customer> allCustomers = FXCollections.observableArrayList();
    private ObservableList<User> allUsers = FXCollections.observableArrayList();

    @FXML
    private Button cancelButton;

    @FXML
    private ComboBox<Contact> contactComboBox;

    @FXML
    private ComboBox<Customer> customerComboBox;

    @FXML
    private TextField descriptionField;

    @FXML
    private DatePicker endDatePicker;

    @FXML
    private TextField endTimeField;

    @FXML
    private TextField idField;

    @FXML
    private TextField locationField;

    @FXML
    private Button saveButton;

    @FXML
    private DatePicker startDatePicker;

    @FXML
    private TextField startTimeField;

    @FXML
    private TextField titleField;

    @FXML
    private ComboBox<User> userComboBox;

    @FXML
    private TextField typeField;

    @FXML
    private Label startTimeZoneLabel;


    @FXML
    private Label endTimeZoneLabel;

    /**Initializes the add appointment form controller. Queries the database to adds all contacts, users, and customers to the appropriate combo boxes, and sets the time zone label.*/
    public void initialize() throws SQLException {
        allContacts = ContactQuery.getAllContacts();
        contactComboBox.setItems(allContacts);
        contactComboBox.setPromptText("Select a contact:");

        allUsers = UserQuery.getAllUsers();
        userComboBox.setItems(allUsers);
        userComboBox.setPromptText("Select a user:");

        allCustomers = CustomerQuery.getAllCustomers();
        customerComboBox.setItems(allCustomers);
        customerComboBox.setPromptText("Select a customer:");

        startTimeZoneLabel.setText(ZoneId.systemDefault().getDisplayName(TextStyle.SHORT.asStandalone(), Locale.getDefault()));
        endTimeZoneLabel.setText(ZoneId.systemDefault().getDisplayName(TextStyle.SHORT.asStandalone(), Locale.getDefault()));
        }
    /**Closes the window without saving any changes.  */
    @FXML
    void cancelButtonClicked() throws IOException {
        Stage window = (Stage) cancelButton.getScene().getWindow();
        window.close();
    }
    /**Saves a new appointment to the database.
     * First, this method gets the data from the relevant fields and combo-boxes and makes sure they aren't empty.
     * If they are empty, an error message indicating which field or combo-box is shown.
     * It then formats the string from the start and end time fields, and combines it with the dates from the start and end date pickers to create a start and an end LocalDateTime.
     * Then it loops through all the appointments for the selected customer to check for any appointment overlaps.
     * If the appointment start LocalDateTime is the same or during an existing appointment,
     * if the end LocalDateTime is the same or during an existing appointment,
     * or if the start LocalDateTime is before an existing appointment and the end LocalDateTime is after an existing appointment,
     * the new appointment overlaps with an existing appointment, an error is shown, and new appointment is not saved.
     * Finally, the LocalDateTimes are compared with the business hours of 8:00 am EST to 10:00pm EST, converted from a ZonedDateTime in EST to a LocalDateTime.
     * As long as the start LocalDateTime is not before 8:00am EST, and the end LocalDateTime is not after 10:00 pm EST,
     * the appointment is saved to the database, and the window is closed.
     * If the appointment falls outside business hours a relevant error message is shown.
     * */
    @FXML
    void saveButtonClicked() throws IOException, SQLException {
        String title = titleField.getText();
        if (title.isEmpty()){
            error("Invalid data. Title cannot be empty, please enter a title.");
            return;
        }

        String description = descriptionField.getText();
        if (description.isEmpty()){
            error("Invalid data. Description cannot be empty, please enter a description.");
            return;
        }

        String location = locationField.getText();
        if (location.isEmpty()){
            error("Invalid data. Location cannot be empty, please enter a location.");
            return;
        }

        String type = typeField.getText();
        if (type.isEmpty()){
            error("Invalid data. Type cannot be empty, please enter an appointment type.");
            return;
        }

        Contact contact = contactComboBox.getSelectionModel().getSelectedItem();
        if (contact == null){
            error("Invalid data. Please select a contact.");
            return;
        }
        int contactId = contact.getContactId();

        LocalDate startDate = startDatePicker.getValue();
        if (startDate == null){
            error("Invalid data. Please enter a start date.");
            return;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

        LocalTime startTime;

        try {

            startTime = LocalTime.parse(startTimeField.getText(), formatter);
        } catch (Exception DateTimeParseException) {
            error("Invalid start time entered. Please enter time as HH:mm in 24 hour format.");
            return;
        }

        LocalDateTime startDateTime = LocalDateTime.of(startDate, startTime);

        LocalDate endDate = endDatePicker.getValue();
        if (endDate == null){
            error("Invalid data. Please enter an end date.");
            return;
        }

        LocalTime endTime;

        try {
            endTime = LocalTime.parse(endTimeField.getText(), formatter);
        } catch (Exception DateTimeParseException) {
            error("Invalid end time entered. Please enter time as HH:mm in 24 hour format.");
            return;
        }

        LocalDateTime endDateTime = LocalDateTime.of(endDate, endTime);

        Customer customer = customerComboBox.getSelectionModel().getSelectedItem();
        if (customer == null){
            error("Invalid data. Please select a customer.");
            return;
        }
        int customerId = customer.getCustomerId();

        User user = userComboBox.getSelectionModel().getSelectedItem();
        if (user == null){
            error("Invalid data. Please select a user.");
            return;
        }
        int userId = user.getUserId();


        ObservableList<Appointment> customerAppointments = AppointmentQuery.getCustomerAppointments(customer.getCustomerId());

        for (Appointment appointment : customerAppointments) {
            if ((startDateTime.isAfter(appointment.getStart()) || startDateTime.isEqual(appointment.getStart())) && startDateTime.isBefore(appointment.getEnd())) {
                error("This appointment starts during an existing appointment for this customer. The conflicting appointment:\r\n\r\n" +
                        "Appointment ID #:" + appointment.getAppointmentId() + "\r\n" +
                        "Title: " + appointment.getTitle() + "\r\n" +
                        "From: " + appointment.getStart() + "\r\n" +
                        "To: " + appointment.getEnd() + "\r\n" + "\r\n" +
                        "Please select a new start time and try again.");
                return;
            }
            if (endDateTime.isAfter(appointment.getStart()) && (endDateTime.isBefore(appointment.getEnd()) || endDateTime.isEqual(appointment.getEnd()))) {
                error("This appointment ends during an existing appointment for this customer. The conflicting appointment:\r\n\r\n" +
                        "Appointment ID #:" + appointment.getAppointmentId() + "\r\n" +
                        "Title: " + appointment.getTitle() + "\r\n" +
                        "From: " + appointment.getStart() + "\r\n" +
                        "To: " + appointment.getEnd() + "\r\n" + "\r\n" +
                        "Please select a new end time and try again.");
                return;
            }
            if ((startDateTime.isBefore(appointment.getStart()) || endDateTime.isEqual(appointment.getStart())) && (endDateTime.isAfter(appointment.getEnd()) || endDateTime.isEqual(appointment.getEnd()))) {
                error("This appointment overlaps with an existing appointment for this customer. The conflicting appointment:\r\n\r\n" +
                        "Appointment ID #:" + appointment.getAppointmentId() + "\r\n" +
                        "Title: " + appointment.getTitle() + "\r\n" +
                        "From: " + appointment.getStart() + "\r\n" +
                        "To: " + appointment.getEnd() + "\r\n" + "\r\n" +
                        "Please select a new start and end time and try again.");
                return;
            }
        }

        LocalDate today = LocalDate.now();
        LocalTime openTime = LocalTime.of(8,0);
        LocalTime closeTime = LocalTime.of(22,0);
        ZoneId businessTimezone = ZoneId.of("America/New_York");

        ZonedDateTime zonedOpenDateTime =  ZonedDateTime.of(today, openTime, businessTimezone);
        ZonedDateTime zonedCloseDateTime =  ZonedDateTime.of(today, closeTime, businessTimezone);

        LocalDateTime localOpenDateTime = zonedOpenDateTime.withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime();
        LocalDateTime localCloseDateTime = zonedCloseDateTime.withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime();

        if (startDateTime.isBefore(endDateTime)) {
            if (!startDateTime.toLocalTime().isBefore(localOpenDateTime.toLocalTime().minusSeconds(1))) {
                if (!endDateTime.toLocalTime().isAfter(localCloseDateTime.toLocalTime().plusSeconds(1))) {
                    AppointmentQuery.addAppointment(title, description, location, type, startDateTime, endDateTime, customerId, userId, contactId);
                    Stage window = (Stage) saveButton.getScene().getWindow();
                    window.close();
                } else {
                    error("Invalid data. End time must be in-between business hours of 8:00 am and 10:00 pm EST.");
                }
            } else {
                error("Invalid data. Start time must be in-between business hours of 8:00 am and 10:00 pm EST.");
            }
        } else {
            error("Invalid data. Start date and time must be before end date and time.");
        }
            }

    }
