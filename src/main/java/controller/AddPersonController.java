package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import service.PersonService;

import java.time.LocalDate;

public class AddPersonController {
    private PersonService personService;
    private int currentPagePersons = 0;
    private int pageSize = 5;
    private UserController userController;

    @FXML
    private TextField idField;
    @FXML
    private TextField usernameField;
    @FXML
    private TextField emailField;
    @FXML
    private TextField passwordField;
    @FXML
    private TextField fnameField;
    @FXML
    private TextField lnameField;
    @FXML
    private TextField dateField;
    @FXML
    private TextField ocupationField;
    @FXML
    private TextField empathyField;

    public void setServices(PersonService personService, UserController userController) {
        this.personService = personService;
        this.userController = userController;
    }

    public void setPages(int currentPagePersons, int pageSize) {
        this.currentPagePersons = currentPagePersons;
        this.pageSize = pageSize;
    }

    @FXML
    private void addPersonAction() {
        try {
            Long id = Long.parseLong(idField.getText());
            String username = usernameField.getText();
            String email = emailField.getText();
            String password = passwordField.getText();
            String fname = fnameField.getText();
            String lname = lnameField.getText();
            LocalDate date = LocalDate.parse(dateField.getText());
            String ocupation = ocupationField.getText();
            int empathy = Integer.parseInt(empathyField.getText());

            personService.addPerson(id, username, email, password, fname, lname, date, ocupation, empathy);
            currentPagePersons = personService.getAllPersons(currentPagePersons, pageSize).getTotalNumberOfElements() / (pageSize + 1);
            userController.refreshPersons(personService.getAllPersons(currentPagePersons, pageSize).getElementsOnPage());
            userController.setPagePersons(currentPagePersons);
            userController.updateButtonStatesPersons();
            userController.addNotify("PERSON");
            new Alert(Alert.AlertType.CONFIRMATION, "Person added successfully").show();
        } catch (Exception ex) {
            new Alert(Alert.AlertType.ERROR, ex.getMessage()).show();
        }
    }
}
