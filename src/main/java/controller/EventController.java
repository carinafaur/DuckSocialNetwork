package controller;

import domain.Event;
import domain.Observer;
import domain.Subject;
import domain.User;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import service.EventService;
import utils.Page;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class EventController implements Observer {
    private EventService eventService;
    private User currentUser;
    private Subject subject;
    private int currentPageEvent=0,pageSize=5;

    @FXML
    private ListView<Event> eventsListView;
    @FXML
    private ObservableList<Event> eventsList;
    @FXML
    private Button previousEventButton;
    @FXML
    private Button nextEventButton;

    public void setServices(EventService service, User user) {
        this.eventService = service;
        this.currentUser = user;
        initialize();
    }

    private void initialize() {
        Page<Event> eventPage = eventService.getEvents(currentPageEvent, pageSize);
        if(eventsList==null){
            eventsList= FXCollections.observableArrayList();
            eventsListView.setItems(eventsList);
        }
        if (eventPage != null) {
            refreshEvents(eventPage.getElementsOnPage());
        }
    }

    @FXML
    public void subscribeUser() {
        Event selected = eventsListView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            try {
                eventService.subscribeToEvent(selected.getName(), currentUser);
                subject.notifyObservers("EVENT");
                new Alert(Alert.AlertType.CONFIRMATION, "Te-ai abonat cu succes!").show();
            } catch (Exception e) {
                new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
            }
        }
    }

    @FXML
    public void unsubscribeUser() {
        Event selected = eventsListView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            try{
                eventService.unsubscribeFromEvent(selected.getName(),currentUser.getId());
                subject.notifyObservers("EVENT");
                new Alert(Alert.AlertType.CONFIRMATION, "Te-ai dezabonat cu succes!").show();
            }catch (Exception e){
                new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
            }
        }
    }

    public void setPageEvent(int page) {
        this.currentPageEvent = page;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
        subject.registerObserver(this);
    }

    @FXML
    private void addEventScene() {
        try {
            Stage stage = new Stage();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/addEvent.fxml"));
            Parent root = loader.load();
            AddEventController addEventController = loader.getController();

            addEventController.setPages(currentPageEvent, pageSize);

            addEventController.setServices(
                    eventService,
                    this
            );
            Scene scene = new Scene(root);
            stage.setTitle("Add Person");
            stage.setScene(scene);
            stage.show();
        } catch (IOException ex) {
            new Alert(Alert.AlertType.ERROR, ex.getMessage()).show();
        }
    }

    public void addNotify(String message){
        subject.notifyObservers(message);
    }

    @FXML
    public void finishEvent() {
        Event selected = eventsListView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            CompletableFuture.runAsync(() -> {
                try {
                    eventService.finishEvent(selected.getName());
                    Platform.runLater(() -> subject.notifyObservers("EVENT"));
                } catch (Exception e) {
                    Platform.runLater(() -> new Alert(Alert.AlertType.ERROR, "Eroare la finalizare!").show());
                }
            });
        }
    }

    public void updateButtonStatesEvent() {
        if (currentPageEvent == 0) {
            previousEventButton.setDisable(true);
        } else
            previousEventButton.setDisable(false);
        Page<Event> eventPage = eventService.getEvents(currentPageEvent, pageSize);
        if (eventPage.getNumberOfElementsPage() < pageSize) {

            nextEventButton.setDisable(true);
        } else if (eventPage.getNumberOfElementsPage() == pageSize && eventService.getEvents(currentPageEvent + 1, pageSize).getNumberOfElementsPage() == 0) {
            nextEventButton.setDisable(true);
        } else nextEventButton.setDisable(false);
    }

    @FXML
    private void nextEventAction(){
        currentPageEvent++;
        refreshEvents(eventService.getEvents(currentPageEvent,pageSize).getElementsOnPage());
        updateButtonStatesEvent();
    }

    @FXML
    private void previousEventAction(){
        currentPageEvent--;
        refreshEvents(eventService.getEvents(currentPageEvent,pageSize).getElementsOnPage());
        updateButtonStatesEvent();
    }

    public void refreshEvents(List<Event> events) {
        eventsList.clear();
        eventsList.addAll(events);
        updateButtonStatesEvent();
    }

    @Override
    public void update(Subject sub, String message) {
        if(Objects.equals(message,"EVENT")) {
            Platform.runLater(() -> {
                Page<Event> eventPage = eventService.getEvents(currentPageEvent, pageSize);
                if (eventPage != null) {
                    refreshEvents(eventPage.getElementsOnPage());
                }
            });
        }
    }
}