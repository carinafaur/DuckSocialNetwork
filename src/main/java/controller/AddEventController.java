package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import service.EventService;

import java.util.ArrayList;
import java.util.List;

public class AddEventController {
    private EventController eventController;
    private EventService eventService;
    private int currentPageEvent=0,pageSize=5;

    @FXML
    private TextField eventNameField;
    @FXML
    private TextField distancesField;

    public void setServices(EventService eventService, EventController eventController) {
        this.eventService = eventService;
        this.eventController = eventController;
    }

    public void setPages(int currentPageEvent, int pageSize) {
        this.currentPageEvent = currentPageEvent;
        this.pageSize = pageSize;
    }

    @FXML
    private void addEventAction() {
        String eventName = eventNameField.getText();
        String[] distances = distancesField.getText().split(",");
        List<Integer> dist=new ArrayList<>();
        for(String distance : distances) {
            dist.add(Integer.parseInt(distance));
        }
        try{
            eventService.addEvent(eventName,dist,dist.size());
            currentPageEvent = eventService.getEvents(currentPageEvent, pageSize).getTotalNumberOfElements() / (pageSize + 1);
            eventController.refreshEvents(eventService.getEvents(currentPageEvent,pageSize).getElementsOnPage());
            eventController.setPageEvent(currentPageEvent);
            eventController.updateButtonStatesEvent();
            eventController.addNotify("EVENT");
            new Alert(Alert.AlertType.CONFIRMATION, "Event added successfully").show();

        }catch(Exception e){
            new Alert(Alert.AlertType.ERROR,e.getMessage()).show();
        }
    }
}
