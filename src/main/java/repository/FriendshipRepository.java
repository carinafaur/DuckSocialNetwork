package repository;

import domain.*;
import utils.Page;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FriendshipRepository {
    DataBaseLoaderFriendship dataBaseLoader;

    public FriendshipRepository() {
        this.dataBaseLoader = new DataBaseLoaderFriendship();
    }

    public List<Friendship> getAllFriendships() throws SQLException {
        return dataBaseLoader.getAllFriendships();
    }

    public Page<Friendship> getFriendships(int pageNumber, int pageSize) throws SQLException {
        int offset = pageNumber * pageSize;

        int totalElements = dataBaseLoader.getTotalFriendships();

        if (offset >= totalElements && totalElements > 0) {
            return new Page<>(new ArrayList<>(), 0, totalElements);
        }

        List<Friendship> pageContent = dataBaseLoader.getAllFriendships(pageSize, offset);

        return new Page<>(pageContent, pageContent.size(), totalElements);
    }

    public void addFriendship(Friendship friendship) throws SQLException {
        dataBaseLoader.saveFriendship(friendship);
    }

    public void removeFriendship(Friendship friendship) throws SQLException {

        dataBaseLoader.deleteFriendship(friendship);
    }

    public Friendship findFriendship(Long id1, Long id2) throws SQLException {
        return dataBaseLoader.getFriendshipById(id1, id2);
    }

    public List<Friendship> findFriendships(Long id1) throws SQLException {
        return dataBaseLoader.getFriendshipsForUser(id1);
    }

    public void updateFriendship(Long id1, Long id2,FriendshipStatus status) throws SQLException {
        dataBaseLoader.updateFriendshipStatus(id1, id2, status);
    }

    public List<Friendship> getFriendshipsForUser(Long id) throws SQLException {
        return dataBaseLoader.getFriendshipsForUser(id);
    }
}
