package controller;

import domain.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import service.DuckService;
import service.FriendshipService;
import service.MessageService;
import service.PersonService;
import utils.Page;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public class UserController implements Observer{
    private DuckService duckService;
    private PersonService personService;
    private FriendshipService friendshipService;
    private MessageService messageService;
    private LoginController loginController;
    private int currentPageDucks = 0, currentPagePersons = 0, currentPageFriendship = 0;
    private int pageSize = 5;
    private User loggedInUser=null;
    private Subject subject;

    @FXML
    private ObservableList<Duck> duckList;
    @FXML
    private ObservableList<Person> personList;
    @FXML
    private TableView<Duck> duckTableView;
    @FXML
    private TableColumn<Duck, Long> duckIdColumn;
    @FXML
    private TableColumn<Duck, String> duckUsernameColumn;
    @FXML
    private TableColumn<Duck, String> duckEmailColumn;
    @FXML
    private TableColumn<Duck, String> duckPasswordColumn;
    @FXML
    private TableColumn<Duck, DuckType> duckTypeColumn;
    @FXML
    private TableColumn<Duck, Double> duckSpeedColumn;
    @FXML
    private TableColumn<Duck, Double> duckResistanceColumn;
    @FXML
    private TableView<Person> personTableView;
    @FXML
    private TableColumn<Person, Long> personIdColumn;
    @FXML
    private TableColumn<Person, String> personUsernameColumn;
    @FXML
    private TableColumn<Person, String> personEmailColumn;
    @FXML
    private TableColumn<Person, String> personPasswordColumn;
    @FXML
    private TableColumn<Person, String> personFNameColumn;
    @FXML
    private TableColumn<Person, String> personLNameColumn;
    @FXML
    private TableColumn<Person, LocalDate> personDateColumn;
    @FXML
    private TableColumn<Person, String> personOcupationColumn;
    @FXML
    private TableColumn<Person, Integer> personEmpathyLevelColumn;
    @FXML
    private TableView<Friendship> friendshipTableView;
    @FXML
    private TableColumn<Friendship, Long> friendshipId1Column;
    @FXML
    private TableColumn<Friendship, Long> friendshipId2Column;
    @FXML
    private TableColumn<Friendship, FriendshipType> friendshipTypeColumn;
    @FXML
    private ObservableList<Friendship> friendshipList;
    @FXML
    private ComboBox<String> userComboBox;
    @FXML
    private Button nextDuckButton;
    @FXML
    private Button nextPersonButton;
    @FXML
    private Button previousDuckButton;
    @FXML
    private Button previousPersonButton;
    @FXML
    private Button nextFriendshipButton;
    @FXML
    private Button previousFriendshipButton;
    @FXML
    private Button refreshDucksButton;


    public void setServices(DuckService duckService, PersonService personService, FriendshipService friendshipService, MessageService messageService,LoginController loginController) {
        this.duckService = duckService;
        this.personService = personService;
        this.friendshipService = friendshipService;
        this.messageService = messageService;
        this.loginController = loginController;
        initialize();
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
        subject.registerObserver(this);
    }

    public void setLoggedInUser(User loggedInUser) {
        this.loggedInUser = loggedInUser;
    }

    private void initialize() {
        duckList = FXCollections.observableArrayList();
        duckTableView.setItems(duckList);
        Page<Duck> duckPage = duckService.getAllDucks(currentPageDucks, pageSize);
        List<Duck> ducks = duckPage.getElementsOnPage();
        duckList.addAll(ducks);
        duckIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        duckUsernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        duckEmailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        duckPasswordColumn.setCellValueFactory(new PropertyValueFactory<>("password"));
        duckTypeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        duckSpeedColumn.setCellValueFactory(new PropertyValueFactory<>("speed"));
        duckResistanceColumn.setCellValueFactory(new PropertyValueFactory<>("resistance"));

        personList = FXCollections.observableArrayList();
        personTableView.setItems(personList);
        Page<Person> personPage = personService.getAllPersons(currentPagePersons, pageSize);
        List<Person> persons = personPage.getElementsOnPage();
        personList.addAll(persons);
        personIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        personUsernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        personEmailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        personPasswordColumn.setCellValueFactory(new PropertyValueFactory<>("password"));
        personFNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        personLNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        personDateColumn.setCellValueFactory(new PropertyValueFactory<>("dateOfBirth"));
        personOcupationColumn.setCellValueFactory(new PropertyValueFactory<>("ocupation"));
        personEmpathyLevelColumn.setCellValueFactory(new PropertyValueFactory<>("empathyLevel"));

        friendshipList = FXCollections.observableArrayList();
        friendshipTableView.setItems(friendshipList);
        Page<Friendship> friendshipPage = friendshipService.getFriendships(currentPageFriendship, pageSize);
        List<Friendship> friendships = friendshipPage.getElementsOnPage();
        friendshipList.addAll(friendships);
        friendshipId1Column.setCellValueFactory(new PropertyValueFactory<>("User1"));
        friendshipId2Column.setCellValueFactory(new PropertyValueFactory<>("User2"));
        friendshipTypeColumn.setCellValueFactory(new PropertyValueFactory<>("friendshipType"));

        userComboBox.getItems().addAll("Filter Flying Ducks", "Filter Swimming Ducks", "Filter Flying and Swimming DUcks");
        updateButtonStatesDucks();
        updateButtonStatesPersons();
        updateButtonStatesFriendships();
        refreshDucksButton.setOnAction(event -> {
            refreshDucks(duckService.getAllDucks(currentPageDucks, pageSize).getElementsOnPage());
        });
    }

    private void filterDucks(DuckType duckType) {
        if (duckType == null)
            new Alert(Alert.AlertType.ERROR, "Please select a filter method").show();
        else {
            try {
                List<Duck> ducks = duckService.getDucksByType(currentPageDucks, pageSize, duckType).getElementsOnPage();
                if (ducks.isEmpty())
                    new Alert(Alert.AlertType.ERROR, "There are no ducks with the filter").show();
                else {
                    refreshDucks(ducks);
                    new Alert(Alert.AlertType.INFORMATION, "Ducks filtered successfully").show();
                }
            } catch (Exception e) {
                new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
            }

        }
    }

    public void addNotify(String message){
        subject.notifyObservers(message);
    }

    public void refreshDucks(List<Duck> ducks) {
        duckList.clear();
        duckList.addAll(ducks);
    }

    public void refreshPersons(List<Person> persons) {
        personList.clear();
        personList.addAll(persons);
    }

    public void refreshFriendships(List<Friendship> friendships) {
        friendshipList.clear();
        friendshipList.addAll(friendships);
    }

    public void setPageDucks(int currentPage) {
        this.currentPageDucks = currentPage;
    }

    public void setPagePersons(int currentPage) {
        this.currentPagePersons = currentPage;
    }

    public void setPageFriendships(int currentPage) {
        this.currentPageFriendship = currentPage;
    }

    @FXML
    private void userComboBoxAction() {
        String value = userComboBox.getSelectionModel().getSelectedItem().toString();
        if (value.equals("Filter Flying Ducks")) {
            filterDucks(DuckType.FLYING);
        } else if (value.equals("Filter Swimming Ducks")) {
            filterDucks(DuckType.SWIMMING);
        } else
            filterDucks(DuckType.FLYING_AND_SWIMMING);
    }

    public void updateButtonStatesDucks() {
        if (currentPageDucks == 0) {
            previousDuckButton.setDisable(true);
        } else
            previousDuckButton.setDisable(false);
        Page<Duck> ducks = duckService.getAllDucks(currentPageDucks, pageSize);
        if (ducks.getNumberOfElementsPage() < pageSize) {

            nextDuckButton.setDisable(true);
        } else if (ducks.getNumberOfElementsPage() == pageSize && duckService.getAllDucks(currentPageDucks + 1, pageSize).getNumberOfElementsPage() == 0) {
            nextDuckButton.setDisable(true);
        } else nextDuckButton.setDisable(false);
    }

    public void updateButtonStatesPersons() {
        if (currentPagePersons == 0) {
            previousPersonButton.setDisable(true);
        } else
            previousPersonButton.setDisable(false);
        Page<Person> persons = personService.getAllPersons(currentPagePersons, pageSize);
        if (persons.getNumberOfElementsPage() < pageSize) {

            nextPersonButton.setDisable(true);
        } else if (persons.getNumberOfElementsPage() == pageSize && personService.getAllPersons(currentPagePersons + 1, pageSize).getNumberOfElementsPage() == 0) {
            nextPersonButton.setDisable(true);
        } else
            nextPersonButton.setDisable(false);
    }

    public void updateButtonStatesFriendships() {
        if (currentPageFriendship == 0) {
            previousFriendshipButton.setDisable(true);
        } else
            previousFriendshipButton.setDisable(false);
        Page<Friendship> friendships = friendshipService.getFriendships(currentPageFriendship, pageSize);
        if (friendships.getNumberOfElementsPage() < pageSize) {
            nextFriendshipButton.setDisable(true);
        } else if (friendships.getNumberOfElementsPage() == pageSize && friendshipService.getFriendships(currentPageFriendship + 1, pageSize).getNumberOfElementsPage() == 0) {
            nextFriendshipButton.setDisable(true);
        } else nextFriendshipButton.setDisable(false);
    }

    @FXML
    private void nextDuckAction() {
        currentPageDucks++;
        refreshDucks(duckService.getAllDucks(currentPageDucks, pageSize).getElementsOnPage());
        updateButtonStatesDucks();
    }

    @FXML
    private void previousDuckAction() {
        currentPageDucks--;
        refreshDucks(duckService.getAllDucks(currentPageDucks, pageSize).getElementsOnPage());
        updateButtonStatesDucks();
    }

    @FXML
    private void nextPersonAction() {
        currentPagePersons++;
        refreshPersons(personService.getAllPersons(currentPagePersons, pageSize).getElementsOnPage());
        updateButtonStatesPersons();
    }

    @FXML
    private void previousPersonAction() {
        currentPagePersons--;
        refreshPersons(personService.getAllPersons(currentPagePersons, pageSize).getElementsOnPage());
        updateButtonStatesPersons();
    }

    @FXML
    private void nextFriendshipAction() {
        currentPageFriendship++;
        refreshFriendships(friendshipService.getFriendships(currentPageFriendship, pageSize).getElementsOnPage());
        updateButtonStatesFriendships();
    }

    @FXML
    private void previousFriendshipAction() {
        currentPageFriendship--;
        refreshFriendships(friendshipService.getFriendships(currentPageFriendship, pageSize).getElementsOnPage());
        updateButtonStatesFriendships();
    }

    @FXML
    private void addPersonScene() {
        try {
            Stage stage = new Stage();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/addPerson.fxml"));
            Parent root = loader.load();
            AddPersonController addPersonController = loader.getController();

            addPersonController.setPages(currentPagePersons, pageSize);

            addPersonController.setServices(
                    personService,
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

    @FXML
    private void addDuckScene() {
        try {
            Stage stage = new Stage();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/addDuck.fxml"));
            Parent root = loader.load();
            AddDuckController addDuckController = loader.getController();

            addDuckController.setPages(currentPageDucks, pageSize);

            addDuckController.setServices(
                    duckService,
                    this
            );
            Scene scene = new Scene(root);
            stage.setTitle("Add Duck");
            stage.setScene(scene);
            stage.show();
        } catch (IOException ex) {
            new Alert(Alert.AlertType.ERROR, ex.getMessage()).show();
        }
    }

    @FXML
    private void addFriendshipScene() {
        try {
            Stage stage = new Stage();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/addFriendship.fxml"));
            Parent root = loader.load();
            AddFriendshipController addFriendshipController = loader.getController();

            addFriendshipController.setPages(currentPageDucks, pageSize);

            addFriendshipController.setServices(
                    friendshipService,
                    this
            );
            Scene scene = new Scene(root);
            stage.setTitle("Add Friendship");
            stage.setScene(scene);
            stage.show();
        } catch (IOException ex) {
            new Alert(Alert.AlertType.ERROR, ex.getMessage()).show();
        }
    }

    @FXML
    private void showNumberCommunities() {
        try {
            int nr = friendshipService.countCommunities();
            if (nr != 0)
                new Alert(Alert.AlertType.INFORMATION, "Number of communities: " + String.valueOf(nr)).show();
            else
                new Alert(Alert.AlertType.WARNING, "There are no communities").show();
        } catch (Exception ex) {
            new Alert(Alert.AlertType.ERROR, ex.getMessage()).show();
        }
    }

    @FXML
    private void showMostSociable() {
        try {
            Set<User> community = friendshipService.mostSociableCommunity();
            String message = "";
            for (User u : community) {
                message += "USER ID: " + u.getId() + "\n";
            }
            if (community.isEmpty())
                new Alert(Alert.AlertType.WARNING, "There are no communities").show();
            else
                new Alert(Alert.AlertType.INFORMATION, "Most sociable community:\n" + message).show();
        } catch (Exception ex) {
            new Alert(Alert.AlertType.ERROR, ex.getMessage()).show();
        }
    }

    @FXML
    private void deleteDuck() {
        Duck selected = duckTableView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            try {
                if(loggedInUser.getId()==selected.getId())
                {
                    loggedInUser=null;
                    loginController.showLoginLayout();
                }
                duckService.removeDuck(selected.getId());
                if (duckService.getAllDucks(currentPageDucks, pageSize).getNumberOfElementsPage() == 0)
                    currentPageDucks--;
                List<Message> messages=messageService.getMessagesForUser(selected.getId());
                for (Message message:messages) {
                    messageService.removeMessage(message);
                }
                subject.notifyObservers("DUCK");
                new Alert(Alert.AlertType.CONFIRMATION, "Duck deleted successfully").show();
            } catch (Exception e) {
                new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
            }
        } else {
            new Alert(Alert.AlertType.ERROR, "Please select a duck").show();
        }
    }

    @FXML
    private void deletePerson() {
        Person selected = personTableView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            try {
                if(loggedInUser.getId()==selected.getId()) {
                    loggedInUser = null;
                    loginController.showLoginLayout();
                }
                personService.removePerson(selected.getId());
                if (personService.getAllPersons(currentPagePersons, pageSize).getNumberOfElementsPage() == 0)
                    currentPagePersons--;
                List<Message> messages=messageService.getMessagesForUser(selected.getId());
                for(Message message:messages){
                    messageService.removeMessage(message);
                }
                subject.notifyObservers("PERSON");
                new Alert(Alert.AlertType.CONFIRMATION, "Person deleted successfully").show();
            } catch (Exception e) {
                new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
            }
        } else {
            new Alert(Alert.AlertType.ERROR, "Please select a person").show();
        }
    }

    @FXML
    private void deleteFriendship() {
        Friendship selected = friendshipTableView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            try {
                friendshipService.removeFriendship(selected.getUser1(), selected.getUser2());
                if (friendshipService.getFriendships(currentPageFriendship, pageSize).getNumberOfElementsPage() == 0)
                    currentPageFriendship--;
                subject.notifyObservers("FRIENDSHIP");
                new Alert(Alert.AlertType.CONFIRMATION, "Friendship deleted successfully").show();
            } catch (Exception e) {
                new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
            }
        } else
            new Alert(Alert.AlertType.ERROR, "Please select a friendship").show();
    }

    @Override
    public void update(Subject sub, String message) {
        if(message.equals("DUCK")){
            refreshDucks(duckService.getAllDucks(currentPageDucks, pageSize).getElementsOnPage());
        }
        else if(message.equals("PERSON")){
            refreshPersons(personService.getAllPersons(currentPagePersons, pageSize).getElementsOnPage());
        }else if(message.equals("FRIENDSHIP")){
            refreshFriendships(friendshipService.getFriendships(currentPageFriendship, pageSize).getElementsOnPage());
        }
    }
}
