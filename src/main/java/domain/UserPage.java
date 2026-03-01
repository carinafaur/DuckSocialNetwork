package domain;

import service.DuckService;
import service.PersonService;

import java.util.List;
import java.util.Random;

public class UserPage {
    private User user;
    private List<User> friends;
    private String description;
    private List<Event> events;

    public UserPage(User user, List<User> friends, String description,List<Event> events) {
        this.user = user;
        this.friends = friends;
        this.description = description;
        this.events = events;
    }

    public User getUser() {
        return user;
    }

    public List<User> getFriends() {
        return friends;
    }

    public int getNrFriends() {
        return friends.size();
    }

    public List<Event> getEvents() {
        return events;
    }

    public int getLikeCount(){
        Random random = new Random();
        return random.nextInt(4000);
    }

    public String getDescription() {
        return description;
    }

    public String getProfilePicture() {
        String path = "/images/" + user.getUsername() + ".png";
        var resource = getClass().getResource(path);

        if (resource == null) {
            path = "/images/default.png";
        }
        return path;
    }
}
