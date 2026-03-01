package controller;

import domain.DuckType;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import service.DuckService;

public class AddDuckController {
    private DuckService duckService;
    private int currentPageDucks = 0;
    private int pageSize = 5;
    private UserController userController;

    @FXML
    private TextField duckIdField;
    @FXML
    private TextField duckUsernameField;
    @FXML
    private TextField duckEmailField;
    @FXML
    private TextField duckPasswordField;
    @FXML
    private TextField duckTypeField;
    @FXML
    private TextField duckSpeedField;
    @FXML
    private TextField duckResistanceField;

    public void setServices(DuckService duckService, UserController userController) {
        this.duckService = duckService;
        this.userController = userController;
    }

    public void setPages(int currentPageDucks, int pageSize) {
        this.currentPageDucks = currentPageDucks;
        this.pageSize = pageSize;
    }

    @FXML
    private void addDuckAction() {
        try {
            Long id = Long.parseLong(duckIdField.getText());
            String username = duckUsernameField.getText();
            String email = duckEmailField.getText();
            String password = duckPasswordField.getText();
            DuckType type = DuckType.valueOf(duckTypeField.getText());
            Double speed = Double.parseDouble(duckSpeedField.getText());
            Double resistance = Double.parseDouble(duckResistanceField.getText());
            duckService.addDuck(id, username, email, password, type, speed, resistance);
            currentPageDucks = duckService.getAllDucks(currentPageDucks, pageSize).getTotalNumberOfElements() / (pageSize + 1);
            userController.setPageDucks(currentPageDucks);
            userController.updateButtonStatesDucks();
            userController.refreshDucks(duckService.getAllDucks(currentPageDucks, pageSize).getElementsOnPage());
            userController.addNotify("DUCK");
            new Alert(Alert.AlertType.CONFIRMATION, "Duck added successfully").show();
        } catch (Exception ex) {
            new Alert(Alert.AlertType.ERROR, ex.getMessage()).show();
        }
    }
}
