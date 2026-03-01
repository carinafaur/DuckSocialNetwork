package controller;

import domain.*;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import service.DuckService;
import service.FriendshipService;
import service.MessageService;
import utils.Page;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class MessageController implements Observer {
    private DuckService duckService;
    private MessageService messageService;
    private FriendshipService friendshipService;
    private User loggedInUser = null, chatUser = null;
    private Subject subject;

    @FXML
    ListView<User> contactsListView;
    @FXML
    ListView<Message> conversationListView;
    @FXML
    TextField messageInputField;

    public void setServices(DuckService duckService,MessageService messageService,FriendshipService friendshipService) {
        this.duckService = duckService;
        this.messageService = messageService;
        this.friendshipService = friendshipService;
        initialize();
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
        subject.registerObserver(this);
    }

    public void setLoggedInUser(User loggedInUser) {
        this.loggedInUser = loggedInUser;
    }

    private void initialize(){
        try {
            friendshipService.setFriendshipsForUsers(loggedInUser);
            List<Long> friends=loggedInUser.getFriends();
            List<User> receivers = friends.stream().map(id-> {try {
                return duckService.findUser(id);
            } catch (Exception e) {
                 return null;
            }}).filter(user -> user != null).collect(Collectors.toList());
            receivers.remove(loggedInUser);
            contactsListView.setItems(FXCollections.observableArrayList(receivers));
            contactsListView.setCellFactory(lv -> new ListCell<User>() {
                @Override
                protected void updateItem(User user, boolean empty) {
                    super.updateItem(user, empty);
                    setText(empty || user == null ? null : user.getUsername());
                }
            });
            conversationListView.setItems(FXCollections.observableArrayList());
            conversationListView.setPrefHeight(700);
            conversationListView.setCellFactory(lv -> new ListCell<Message>() {
                private final Long currentUserId = loggedInUser.getId();

                @Override
                protected void updateItem(Message message, boolean empty) {
                    super.updateItem(message, empty);
                    if (empty || message == null) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        boolean isSentByMe = message.getFrom().equals(currentUserId);

                        Label content = new Label(message.getMessage());
                        Label time = new Label(message.getDate().toLocalTime().toString().substring(0, 5));
                        time.setStyle("-fx-font-size: 8pt; -fx-text-fill: #555;");

                        VBox messageBubble = new VBox(content, time);
                        messageBubble.setPadding(new javafx.geometry.Insets(8, 10, 8, 10));

                        String color = isSentByMe ? "#DCF8C6" : "#E0E0E0";
                        messageBubble.setStyle("-fx-background-color: " + color + "; -fx-background-radius: 10;");
                        messageBubble.setMaxWidth(300);
                        content.setWrapText(true);

                        HBox container = new HBox();
                        javafx.scene.layout.Region spacer = new javafx.scene.layout.Region();
                        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

                        if (isSentByMe) {
                            container.getChildren().addAll(spacer, messageBubble);
                            container.setAlignment(Pos.CENTER_RIGHT);
                        } else {
                            container.getChildren().addAll(messageBubble, spacer);
                            container.setAlignment(Pos.CENTER_LEFT);
                        }

                        setGraphic(container);
                        setStyle("-fx-background-color: transparent;");
                    }
                }
            });
            setChatUser();

        }catch (Exception e){
            new Alert(Alert.AlertType.ERROR,e.getMessage()).show();
        }
    }

    private void setChatUser() {
        contactsListView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newContact) -> {
            if (newContact != null) {
                chatUser = newContact;
                updateMessages(newContact);
            }
        });
    }

    @FXML
    private void sendMessage() {
        String message = messageInputField.getText();
        if (message.isEmpty())
           new Alert(Alert.AlertType.ERROR, "Please enter a message");
        else {
            try {
                Random random = new Random();
                long idRandom = random.nextLong();
                List<Long> to = new ArrayList<>();
                to.add(chatUser.getId());
                messageService.sendMessage(idRandom, loggedInUser.getId(), to, message);
                updateMessages(chatUser);
                messageInputField.clear();
                subject.notifyObservers("MESSAGE");
            } catch (Exception e) {
                new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
            }
        }
    }

    private void updateMessages(User user2) {
        try {
            List<Message> messages = messageService.getConversation(loggedInUser.getId(), user2.getId());
            if (!messages.isEmpty()) {
                conversationListView.setItems(FXCollections.observableArrayList(messages));
                conversationListView.scrollTo(messages.size() - 1);
            }
            else conversationListView.setItems(FXCollections.observableArrayList());
        } catch (Exception ex) {
            new Alert(Alert.AlertType.ERROR, ex.getMessage()).show();
        }
    }

    private void updateContacts(){
        friendshipService.setFriendshipsForUsers(loggedInUser);
        List<Long> friends=loggedInUser.getFriends();
        List<User> receivers = friends.stream().map(id-> {try {
            return duckService.findUser(id);
        } catch (Exception e) {
            return null;
        }}).filter(user -> user != null).collect(Collectors.toList());
        receivers.remove(loggedInUser);
        contactsListView.setItems(FXCollections.observableArrayList(receivers));
    }

    @Override
    public void update(Subject sub, String message) {
        if(message.equals("MESSAGE")){
            updateMessages(chatUser);
        }
        else if(message.equals("FRIENDSHIP") || message.equals("FRIEND")){
            updateContacts();
        }
    }
}
