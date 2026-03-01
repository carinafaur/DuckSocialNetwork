package controller;

import domain.FriendshipStatus;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import service.FriendshipService;

public class AddFriendshipController {
    private FriendshipService friendshipService;
    private UserController userController;
    private int currentPageFriendship = 0;
    private int pageSize = 5;

    @FXML
    private TextField friendshipId1Field;
    @FXML
    private TextField friendshipId2Field;

    public void setServices(FriendshipService friendshipService, UserController userController) {
        this.friendshipService = friendshipService;
        this.userController = userController;
    }

    public void setPages(int currentPageFriendship, int pageSize) {
        this.currentPageFriendship = currentPageFriendship;
        this.pageSize = pageSize;
    }

    @FXML
    private void addFriendshipAction(){
        Long id1 = Long.parseLong(friendshipId1Field.getText());
        Long id2 = Long.parseLong(friendshipId2Field.getText());
        try {
            friendshipService.addFriendship(id1, id2, FriendshipStatus.FRIENDS);
            currentPageFriendship = friendshipService.getFriendships(currentPageFriendship, pageSize).getTotalNumberOfElements() / (pageSize + 1);
            userController.refreshFriendships(friendshipService.getFriendships(currentPageFriendship, pageSize).getElementsOnPage());
            userController.setPageFriendships(currentPageFriendship);
            userController.updateButtonStatesFriendships();
            userController.addNotify("FRIENDSHIP");
            new Alert(Alert.AlertType.CONFIRMATION, "Friendship added successfully").show();
        } catch (Exception ex) {
            new Alert(Alert.AlertType.ERROR, ex.getMessage()).show();
        }
    }
}
