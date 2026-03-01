package service;

import domain.Event;
import domain.User;
import domain.UserPage;
import exceptions.EventException;
import exceptions.UserNotFoundException;
import repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.sql.SQLException;

public class UserPageService {
    private DuckService duckService;
    private FriendshipService friendshipService;
    private EventService eventService;

    public UserPageService( DuckService duckService,FriendshipService friendshipService,EventService eventService) {
        this.duckService = duckService;
        this.friendshipService = friendshipService;
        this.eventService = eventService;
    }

    public UserPage getUserPage(User user) throws SQLException, UserNotFoundException, EventException {
        user.clearFriends();
        friendshipService.setFriendshipsForUsers(user);
        String description=user.getEmail();
        List<User> friends=new ArrayList<>();
        List<Event> events=eventService.getEventsForUser(user);
        List<Long> ids=user.getFriends();
        for(Long id:ids){
            User friend=duckService.findUser(id);
            friends.add(friend);
        }
        return new UserPage(user,friends,description,events);
    }
}
