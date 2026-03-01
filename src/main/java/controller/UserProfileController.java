package controller;

import domain.*;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import service.DuckService;
import service.UserPageService;

import java.util.Objects;

public class UserProfileController implements Observer {
    private DuckService duckService;
    private UserPageService userPageService;
    private User profileUser,loggedInUser;
    private Subject subject;
    private UserPage profilePage;

    @FXML
    private ObservableList<User> friendsList;
    @FXML
    private ListView<User> friendsListView;
    @FXML
    private ListView<Event> eventsListView;
    @FXML
    private ObservableList<Event> eventsList;
    @FXML
    private Label friendsCountLabel;
    @FXML
    private Label likeCountLabel;
    @FXML
    private Label descriptionLabel;
    @FXML
    private Label statusLabel;
    @FXML
    private Label usernameLabel;
    @FXML
    private Circle profileCircle;

    public void setService(DuckService duckService,UserPageService userPageService) {
        this.duckService = duckService;
        this.userPageService = userPageService;
        initialize();
    }

    private void initialize(){
        if(friendsList==null)
        {
            friendsList= FXCollections.observableArrayList();
            friendsListView.setItems(friendsList);
            friendsListView.setCellFactory(lv -> new ListCell<User>() {
                @Override
                protected void updateItem(User item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(item.getUsername());
                    }
                }
            });
        }
        if(eventsList==null)
        {
            eventsList= FXCollections.observableArrayList();
            eventsListView.setItems(eventsList);
            eventsListView.setCellFactory(lv->new ListCell<Event>() {
                @Override
                protected void updateItem(Event item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    }else {
                        setText(item.getName());
                    }
                }
            });
        }
        var resource = getClass().getResource(profilePage.getProfilePicture());

        Image img = new Image(resource.toExternalForm());
        profileCircle.setFill(new ImagePattern(img));
        refreshPage();
        likeCountLabel.setText(String.valueOf(profilePage.getLikeCount())+" likes");
        descriptionLabel.setText(profilePage.getDescription());
        usernameLabel.setText(profileUser.getUsername());

    }

    public void setUsers(User profileUser, User loggedInUser,UserPage profilePage) {
        this.profileUser = profileUser;
        this.profilePage = profilePage;
        this.loggedInUser = loggedInUser;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
        subject.registerObserver(this);
    }

    private void refreshPage(){
        try {
            this.profilePage = userPageService.getUserPage(profileUser);
        }catch (Exception e){
            new Alert(Alert.AlertType.ERROR,e.getMessage()).show();
        }
        friendsList.clear();
        friendsList.addAll(profilePage.getFriends());
        eventsList.clear();
        eventsList.addAll(profilePage.getEvents());
        friendsCountLabel.setText(String.valueOf(profilePage.getNrFriends())+" friends");
        try{
            boolean isActive = duckService.isActive(profileUser);
            if(isActive)
                statusLabel.setText("active");
            else
                statusLabel.setText("inactive");
        }catch(Exception e){
            new Alert(Alert.AlertType.ERROR,e.getMessage()).show();
        }
    }

    @Override
    public void update(Subject subject,String message){
        if(Objects.equals(message,"LOGIN") || Objects.equals(message,"FRIENDSHIP") || Objects.equals(message,"EVENT"))
            Platform.runLater(this::refreshPage);
    }
}
