package service;

import domain.*;
import exceptions.FriendshipAlreadyExists;
import exceptions.FriendshipException;
import exceptions.FriendshipNotFound;
import repository.FriendshipRepository;
import repository.UserRepository;
import utils.Page;
import validation.Strategy;
import validation.StrategyValidation;

import java.sql.SQLException;
import java.util.Set;
import java.util.List;

public class FriendshipService {
    private FriendshipRepository friendshipRepository;
    private UserRepository userRepository;
    private StrategyValidation<Friendship> validator;

    public FriendshipService(FriendshipRepository friendshipRepository, UserRepository userRepository) {
        this.friendshipRepository = friendshipRepository;
        this.userRepository = userRepository;
    }

    public Page<Friendship> getFriendships(int pageNumber, int pageSize) {
        try {
            return friendshipRepository.getFriendships(pageNumber, pageSize);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public void setFriendshipsForUsers(User user) {
        try {
            user.clearFriends();
            List<Friendship> friendships = friendshipRepository.getFriendshipsForUser(user.getId());
            friendships.forEach(user::addFriendship);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public FriendshipType getFriendshipType(User friend1, User friend2) {
        if (friend1 instanceof Person) {
            if (friend2 instanceof Person) {
                return FriendshipType.PERSON_PERSON;
            } else if (friend2 instanceof Duck) {
                return FriendshipType.DUCK_PERSON;
            }
        } else if (friend1 instanceof Duck) {
            if (friend2 instanceof Person) {
                return FriendshipType.DUCK_PERSON;
            } else if (friend2 instanceof Duck) {
                return FriendshipType.DUCK_DUCK;
            }
        }
        return null;
    }

    public void addFriendship(Long id1, Long id2, FriendshipStatus status) throws FriendshipException, SQLException {
        User user1 = userRepository.findUser(id1);
        User user2 = userRepository.findUser(id2);
        if (user1 == user2) {
            throw new FriendshipException("The users are the same!", "Error!");
        }
        if (user1 == null || user2 == null) {
            throw new FriendshipException("The users are invalid!", "Error!");
        }
        if (friendshipRepository.findFriendship(id1, id2) != null || friendshipRepository.findFriendship(id2, id1) != null) {
            throw new FriendshipAlreadyExists("The friendship already exists!", "Error!");
        }
        Friendship friendship = new Friendship(id1, id2, getFriendshipType(user1, user2));
        validator = new StrategyValidation<Friendship>(Strategy.FRIENDSHIP);
        if (!validator.validate(friendship))
            throw new FriendshipException("The friendship is invalid!", "Error!");
        else {
            friendshipRepository.addFriendship(friendship);
            friendship.setFriendshipStatus(status);
            user1.addFriendship(friendship);
            user2.addFriendship(friendship);
        }
    }

    public void removeFriendship(Long id1, Long id2) throws FriendshipException, SQLException {
        Friendship friendship = friendshipRepository.findFriendship(id1, id2);
        if (friendship == null) {
            friendship = friendshipRepository.findFriendship(id2, id1);
            if (friendship == null)
                throw new FriendshipException("The friendship does not exist!", "Error!");
            else
                friendshipRepository.removeFriendship(friendship);
        } else
            friendshipRepository.removeFriendship(friendship);
    }

    public int countCommunities() throws SQLException {
        return NetworkManager.countCommunities(userRepository.getAllUsers(), friendshipRepository.getAllFriendships());
    }

    public Set<User> mostSociableCommunity() throws SQLException {
        return NetworkManager.mostSociableCommunity(userRepository.getAllUsers(), friendshipRepository.getAllFriendships());
    }

    public FriendshipStatus getFriendshipStatus(Long id1, Long id2) throws FriendshipNotFound, SQLException {
        Friendship friendship = friendshipRepository.findFriendship(id1, id2);
        if (friendship == null) {
            friendship = friendshipRepository.findFriendship(id2, id1);
            if (friendship == null) {
                throw new FriendshipNotFound("The friendship does not exist!", "Error!");
            } else {
                FriendshipStatus status = friendship.getFriendshipStatus();
                if (id1.equals(friendship.getUser2()) && status == FriendshipStatus.REQUESTED1) {
                    return FriendshipStatus.REQUESTED2;
                } else if (id2.equals(friendship.getUser1()) && status == FriendshipStatus.REQUESTED2) {
                    return FriendshipStatus.REQUESTED1;
                } else return status;
            }
        } else {
            FriendshipStatus status = friendship.getFriendshipStatus();
            if (id1.equals(friendship.getUser2()) && status == FriendshipStatus.REQUESTED1) {
                return FriendshipStatus.REQUESTED2;
            } else if (id2.equals(friendship.getUser1()) && status == FriendshipStatus.REQUESTED2) {
                return FriendshipStatus.REQUESTED1;
            } else return status;
        }
    }

    public void updateFriendshipStatus(Long id1, Long id2, FriendshipStatus status) throws SQLException, FriendshipException {
        Friendship friendship = friendshipRepository.findFriendship(id1, id2);
        if (friendship == null) {
            friendship = friendshipRepository.findFriendship(id2, id1);
            if (friendship == null) {
                throw new FriendshipException("The friendship does not exist!", "Error!");
            } else {
                friendship.setFriendshipStatus(status);
                friendshipRepository.updateFriendship(id2, id1, status);
            }
        } else {
            friendship.setFriendshipStatus(status);
            friendshipRepository.updateFriendship(id1, id2, status);
        }
    }
}
