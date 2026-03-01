package main;

import controller.LoginController;
import domain.Observer;
import domain.Subject;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import repository.*;
import service.*;

import java.util.ArrayList;
import java.util.List;


public class Main extends Application implements Subject {
    private DuckService duckService;
    private PersonService personService;
    private FriendshipService friendshipService;
    private MessageService messageService;
    private EventService eventService;
    private UserPageService userPageService;
    private List<Observer> observers=new ArrayList<>();

    @Override
    public void start(Stage stage) {
        try{
            setServices();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/login.fxml"));
            Parent root =  loader.load();
            LoginController loginController = loader.getController();

            loginController.setServices(
                    duckService,
                    personService,
                    friendshipService,
                    messageService,eventService,userPageService,this
            );
            stage.setOnCloseRequest(e -> {
                loginController.logoutUser();
            });
            Scene scene = new Scene(root);
            stage.setTitle("Login");
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void setServices() {
        FriendshipRepository friendshipRepository = new FriendshipRepository();
        DuckRepository duckRepository = new DuckRepository();
        PersonRepository personRepository = new PersonRepository();
        UserRepository userRepository = new UserRepository();
        MessageRepository messageRepository = new MessageRepository();
        EventsRepository eventsRepository=new EventsRepository(duckRepository);

        duckService = new DuckService(duckRepository, friendshipRepository);
        personService = new PersonService(personRepository, friendshipRepository);
        friendshipService = new FriendshipService(friendshipRepository, userRepository);
        messageService = new MessageService(messageRepository, userRepository);
        eventService=new EventService(eventsRepository,duckRepository);
        userPageService=new UserPageService(duckService,friendshipService,eventService);
    }

    public static void main(String[] args) {
        launch(args);
    }


    @Override
    public void registerObserver(Observer o) {
        observers.add(o);
    }

    @Override
    public void removeObserver(Observer o) {
        observers.remove(o);
    }

    @Override
    public void notifyObservers(String message) {
        for (Observer o : observers) {
            o.update(this, message);
        }
    }
}
