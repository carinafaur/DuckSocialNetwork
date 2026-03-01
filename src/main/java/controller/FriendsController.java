package controller;

import domain.*;
import exceptions.FriendshipAlreadyExists;
import exceptions.FriendshipException;
import exceptions.FriendshipNotFound;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import service.DuckService;
import service.FriendshipService;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class FriendsController implements Observer {
    private FriendshipService friendshipService;
    private DuckService duckService;
    private User loggedInUser;
    private Subject subject;

    @FXML
    private ObservableList<User> friends;
    @FXML
    private TableView<User> friendsTableView;
    @FXML
    private TableColumn<User, String> usernameColumn;
    @FXML
    private TableColumn<User, User> actionColumn;

    public void setServices(FriendshipService friendshipService, DuckService duckService) {
        this.friendshipService = friendshipService;
        this.duckService = duckService;
        initialize();
    }

    public void setLoggedInUser(User loggedInUser) {
        this.loggedInUser = loggedInUser;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
        subject.registerObserver(this);
    }

    @FXML
    private void initialize() {
        try {
            usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
            actionColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue()));

            actionColumn.setCellFactory(column -> new TableCell<User, User>() {
                @Override
                protected void updateItem(User user, boolean empty) {
                    super.updateItem(user, empty);

                    if (empty || user == null) {
                        setGraphic(null);
                    } else {
                        try {
                            FriendshipStatus status = friendshipService.getFriendshipStatus(loggedInUser.getId(), user.getId());

                            Node actionNode = createActionNode(status, user);
                            setGraphic(actionNode);
                        } catch (FriendshipNotFound fne) {
                            FriendshipStatus status = FriendshipStatus.NOT_FRIENDS;

                            Node actionNode = createActionNode(status, user);
                            setGraphic(actionNode);
                        } catch (Exception e) {
                            new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
                        }
                    }
                }
            });
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
        }
    }

    public void loadData() {
        if (friends == null) {
            friends = FXCollections.observableArrayList();
            friendsTableView.setItems(friends);
        }

        handleRefreshFriends();
        friendshipService.setFriendshipsForUsers(loggedInUser);
    }

    private Node createActionNode(FriendshipStatus friendshipStatus, User user) {
        switch (friendshipStatus) {
            case FRIENDS:
                return new Label("Friends");

            case REQUESTED1:
                return new Label("Requested");

            case REQUESTED2:
               HBox actionBox = new javafx.scene.layout.HBox(10);
                Button acceptButton = new Button("Accept");
                Button denyButton = new Button("Deny");

                acceptButton.setStyle("-fx-base: #b3ffb3;");
                denyButton.setStyle("-fx-base: #ffb3b3;");

                acceptButton.setOnAction(e -> {
                    try {
                        friendshipService.addFriendship(loggedInUser.getId(), user.getId(), FriendshipStatus.FRIENDS);
                        new Alert(Alert.AlertType.INFORMATION, "Friendship accepted!").show();
                        handleRefreshFriends();
                        subject.notifyObservers("FRIEND");
                        subject.notifyObservers("FRIENDSHIP");
                    } catch (FriendshipAlreadyExists e1) {
                        try {
                            friendshipService.updateFriendshipStatus(loggedInUser.getId(), user.getId(), FriendshipStatus.FRIENDS);
                            handleRefreshFriends();
                            subject.notifyObservers("FRIEND");
                            subject.notifyObservers("FRIENDSHIP");
                        } catch(Exception ex) {
                            new Alert(Alert.AlertType.ERROR, ex.getMessage()).show();
                        }
                    } catch (Exception ex) {
                        new Alert(Alert.AlertType.ERROR, ex.getMessage()).show();
                    }
                });

                denyButton.setOnAction(e -> {
                    try {

                        friendshipService.removeFriendship(loggedInUser.getId(), user.getId());
                        new Alert(Alert.AlertType.INFORMATION, "Request denied.").show();
                        handleRefreshFriends();
                        subject.notifyObservers("FRIEND");
                        subject.notifyObservers("FRIENDSHIP");
                    }catch(FriendshipNotFound fnf)
                    {
                        new Alert(Alert.AlertType.INFORMATION, "Request denied.").show();
                        handleRefreshFriends();
                        subject.notifyObservers("FRIEND");
                        subject.notifyObservers("FRIENDSHIP");
                    }
                    catch (Exception ex) {
                        new Alert(Alert.AlertType.ERROR, "Could not deny: " + ex.getMessage()).show();
                    }
                });

                actionBox.getChildren().addAll(acceptButton, denyButton);
                actionBox.setAlignment(javafx.geometry.Pos.CENTER);
                return actionBox;

            case NOT_FRIENDS:
            default:
                Button sendButton = new Button("Send Request");
                sendButton.setOnAction(e -> {
                    try {
                        friendshipService.addFriendship(loggedInUser.getId(), user.getId(), FriendshipStatus.REQUESTED1);
                        subject.notifyObservers("FRIEND_REQUEST:"+user.getUsername());
                        handleRefreshFriends();
                        new Alert(Alert.AlertType.INFORMATION, "Request sent!").show();
                    } catch (FriendshipAlreadyExists fe) {
                        try {
                            friendshipService.updateFriendshipStatus(loggedInUser.getId(), user.getId(), FriendshipStatus.REQUESTED1);
                            subject.notifyObservers("FRIEND_REQUEST:"+user.getUsername());
                            handleRefreshFriends();
                            new Alert(Alert.AlertType.INFORMATION, "Request sent!").show();

                        } catch (Exception ex) {
                            new Alert(Alert.AlertType.ERROR, ex.getMessage()).show();
                        }
                    } catch (Exception ex) {
                        new Alert(Alert.AlertType.ERROR, ex.getMessage()).show();
                    }
                });
                return sendButton;
        }
    }

    @FXML
    private void handleRefreshFriends() {
        try {
            List<User> allUsers = duckService.getAllUsers();
            allUsers.remove(loggedInUser);

            friends.clear();

            friends.addAll(allUsers);
            friendsTableView.refresh();

        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
        }
    }

    @Override
    public void update(Subject sub, String message) {
        if(Objects.equals(message, "FRIEND") || message.equals("FRIENDSHIP")) {
            handleRefreshFriends();
        }
        else if(Objects.equals(message, "FRIEND_REQUEST:"+loggedInUser.getUsername())) {
            new Alert(Alert.AlertType.INFORMATION, "You got a new friend request!").show();
            handleRefreshFriends();
        }
    }
}
