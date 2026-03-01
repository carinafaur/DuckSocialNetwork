package controller;

import domain.User;
import domain.UserPage;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import main.Main;
import service.*;

import java.io.IOException;

public class LoginController {
    private DuckService duckService;
    private PersonService personService;
    private FriendshipService friendshipService;
    private MessageService messageService;
    private EventService eventService;
    private UserPageService userPageService;
    private User loggedInUser = null;
    private Main mainApp;

    @FXML
    private TextField usernameTextField;
    @FXML
    private PasswordField passwordField;
    @FXML
    Label usernameLabel;
    @FXML
    Label passwordLabel;
    @FXML
    Label loginLabel;
    @FXML
    Button logoutButton;
    @FXML
    Button loginButton;
    @FXML
    private TextField searchUserField;
    @FXML
    private HBox searchBarContainer;
    @FXML
    private Button profileButton;
    @FXML
    private Label profileLabel;
    @FXML
    private HBox foundUser;

    public LoginController() {
    }

    public void setServices(DuckService duckService, PersonService personService, FriendshipService friendshipService, MessageService messageService, EventService eventService, UserPageService userPageService,Main mainApp) {
        this.duckService = duckService;
        this.personService = personService;
        this.friendshipService = friendshipService;
        this.messageService = messageService;
        this.eventService = eventService;
        this.userPageService = userPageService;
        this.mainApp = mainApp;
        logoutButton.setVisible(false);
        searchBarContainer.setVisible(false);
    }

    @FXML
    private void loginUser() {
        String username = usernameTextField.getText();
        String password = passwordField.getText();
        if (username.isEmpty() || password.isEmpty()) {
            new Alert(Alert.AlertType.ERROR, "Username or password is empty");
        } else {
            try {
                User user = duckService.findUserByUsernameAndPassword(username, password);
                duckService.updateStatus(user, true);
                loginLabel.setText("Logged in as " + user.getUsername());
                loginLabel.setStyle(
                        "-fx-font-size: 18px; " +
                                "-fx-font-weight: bold; " +
                                "-fx-text-fill: #00b894; " +
                                "-fx-background-color: #e6fffb; " +
                                "-fx-padding: 10 20 10 20; " +
                                "-fx-background-radius: 10; " +
                                "-fx-border-color: #b7eb8f; " +
                                "-fx-border-radius: 10;"
                );
                usernameTextField.setVisible(false);
                passwordField.setVisible(false);
                usernameLabel.setVisible(false);
                passwordLabel.setVisible(false);
                loginLabel.setVisible(true);
                loginButton.setVisible(false);
                logoutButton.setVisible(true);
                searchBarContainer.setVisible(true);
                this.loggedInUser = user;
                mainApp.notifyObservers("LOGIN");
            } catch (Exception ex) {
                new Alert(Alert.AlertType.ERROR, ex.getMessage());
            }
        }
    }

    @FXML
    private void messageScene() {
        if (this.loggedInUser == null) {
            new Alert(Alert.AlertType.ERROR, "You are not logged in").show();
        } else {
            try {
                Stage stage = new Stage();
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/messages.fxml"));
                Parent root = loader.load();
                MessageController messageController = loader.getController();

                messageController.setLoggedInUser(loggedInUser);
                messageController.setServices(
                        duckService,
                        messageService,
                        friendshipService
                );
                messageController.setSubject(mainApp);
                stage.setOnCloseRequest(e -> {
                    mainApp.removeObserver(messageController);
                });

                Scene scene = new Scene(root);
                stage.setTitle("Messages");
                stage.setScene(scene);
                stage.show();
            } catch (IOException ex) {
                new Alert(Alert.AlertType.ERROR, ex.getMessage()).show();
            }
        }
    }

    @FXML
    private void friendsScene() {
        if (this.loggedInUser == null) {
            new Alert(Alert.AlertType.ERROR, "You are not logged in").show();
        } else {
            try {
                Stage stage = new Stage();
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/friends.fxml"));
                Parent root = loader.load();
                FriendsController friendsController = loader.getController();

                friendsController.setLoggedInUser(loggedInUser);
                friendsController.setServices(
                        friendshipService, duckService
                );
                friendsController.setSubject(mainApp);
                stage.setOnCloseRequest(e -> {
                    mainApp.removeObserver(friendsController);
                });

                friendsController.loadData();
                Scene scene = new Scene(root);
                stage.setTitle("Friends");
                stage.setScene(scene);
                stage.show();
            } catch (IOException ex) {
                new Alert(Alert.AlertType.ERROR, ex.getMessage()).show();
            }
        }
    }

    @FXML
    private void usersScene() {
        if (this.loggedInUser == null) {
            new Alert(Alert.AlertType.ERROR, "You are not logged in").show();
        } else {
            try {
                Stage stage = new Stage();
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/users.fxml"));
                Parent root = loader.load();
                UserController userController = loader.getController();

                userController.setLoggedInUser(loggedInUser);
                userController.setServices(
                        duckService,
                        personService,
                        friendshipService,
                        messageService, this
                );
                userController.setSubject(mainApp);
                stage.setOnCloseRequest(e -> {
                    mainApp.removeObserver(userController);
                });

                Scene scene = new Scene(root);
                stage.setTitle("Users");
                stage.setScene(scene);
                stage.show();
            } catch (IOException ex) {
                new Alert(Alert.AlertType.ERROR, ex.getMessage()).show();
            }
        }
    }

    public void showLoginLayout() {
        loginLabel.setVisible(false);
        usernameLabel.setVisible(true);
        passwordLabel.setVisible(true);
        usernameTextField.setVisible(true);
        usernameTextField.clear();
        passwordField.clear();
        passwordField.setVisible(true);
    }

    @FXML
    private void handleNewWindow() {
        try {
            Stage newStage = new Stage();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/login.fxml"));
            Parent root = loader.load();

            LoginController newController = loader.getController();

            newController.setServices(
                    this.duckService,
                    this.personService,
                    this.friendshipService,
                    this.messageService, eventService,userPageService, mainApp
            );
            newStage.setOnCloseRequest(e -> {
                logoutUser();
            });

            Scene scene = new Scene(root);
            newStage.setTitle("New Window");
            newStage.setScene(scene);
            newStage.show();

        } catch (IOException e) {
            new Alert(Alert.AlertType.ERROR, "Nu s-a putut deschide fereastra: " + e.getMessage()).show();
        }
    }

    @FXML
    private void eventsScene() {
        if (this.loggedInUser == null) {
            new Alert(Alert.AlertType.ERROR, "You are not logged in").show();
        } else {
            try {
                Stage stage = new Stage();
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/events.fxml"));
                Parent root = loader.load();
                EventController eventController = loader.getController();

                eventController.setServices(
                        eventService, loggedInUser
                );
                eventController.setSubject(mainApp);

                stage.setOnCloseRequest(e -> {
                    mainApp.removeObserver(eventController);
                });

                Scene scene = new Scene(root);
                stage.setTitle("Events");
                stage.setScene(scene);
                stage.show();
            } catch (IOException ex) {
                new Alert(Alert.AlertType.ERROR, ex.getMessage()).show();
            }
        }
    }

    @FXML
    public void logoutUser() {
        usernameTextField.setVisible(true);
        passwordField.setVisible(true);
        usernameLabel.setVisible(true);
        passwordLabel.setVisible(true);
        loginButton.setVisible(true);
        loginLabel.setVisible(false);
        try {
            duckService.updateStatus(loggedInUser, false);
            loggedInUser = null;
            mainApp.notifyObservers("LOGIN");
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
        }
    }

    @FXML
    private void searchUser() {
        String username = searchUserField.getText();
        try{
            foundUser.getChildren().clear();
            User user=duckService.findUserByUsername(username);
            foundUser.setSpacing(10);
            profileLabel=new Label(username);
            profileButton=new Button("See profile!");
            profileButton.setOnMouseClicked(e -> {userProfileScene(user);});
            foundUser.getChildren().add(profileLabel);
            foundUser.getChildren().add(profileButton);
            foundUser.setVisible(true);
        }catch(Exception e){
            new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
        }
    }

    private void userProfileScene(User user) {
        try {
            Stage stage = new Stage();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/profile.fxml"));
            Parent root = loader.load();

            UserProfileController controller = loader.getController();

            UserPage page=userPageService.getUserPage(user);
            controller.setUsers(user, loggedInUser,page);
            controller.setService(duckService,userPageService);
            controller.setSubject(mainApp);

            Scene scene = new Scene(root);
            stage.setTitle(user.getUsername()+"'s Profile");
            stage.setScene(scene);
            stage.show();
        } catch (Exception ex) {
            new Alert(Alert.AlertType.ERROR, "Eroare la deschiderea profilului: " + ex.getMessage()).show();
        }
    }

}
