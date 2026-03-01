package domain;

import javafx.application.Platform;
import javafx.scene.control.Alert;

import java.util.*;

public abstract class User implements Observer {
    private long id;
    private String username;
    private String email;
    private String password;
    private Set<Friendship> friendships = new HashSet<>();


    User(long id, String username, String email, String password) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
    }


    public List<Long> getFriends() {
        List<Long> friends = new ArrayList<>();
        for (Friendship f : friendships) {
            Long id2 = f.getOther(id);
            if (id2 != null)
                friends.add(id2);
        }
        return friends;
    }

    public void addFriendship(Friendship friendship) {
        friendships.add(friendship);
    }

    public void clearFriends(){
        friendships.clear();
    }

    public long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public void update(Subject sub, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Notificare Eveniment: " + ((Event) sub).getName());
            alert.setHeaderText("Mesaj nou pentru " + this.username);
            alert.setContentText(message);

            alert.show();
        });
    }

    @Override
    public String toString() {
        return "USER id=" + id + ", username='" + username + "', email='" + email + "' ";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User user)) return false;
        return Objects.equals(getId(), user.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

}
