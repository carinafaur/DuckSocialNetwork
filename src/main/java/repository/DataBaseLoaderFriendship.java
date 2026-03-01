package repository;

import data.CreateConnection;
import domain.Friendship;
import domain.FriendshipStatus;
import domain.FriendshipType;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DataBaseLoaderFriendship {
    DataBaseLoaderFriendship() {
    }

    public List<Friendship> getAllFriendships(int limit, int offset) throws SQLException {
        List<Friendship> friendships = new ArrayList<>();
        String selectSQL = "SELECT Id1,Id2,FType FROM Friendship WHERE \"friendshipstatus\" = ? LIMIT ? OFFSET ? ";
        Connection conn = CreateConnection.getConnection();
        PreparedStatement ps = conn.prepareStatement(selectSQL);
        ps.setInt(2, limit);
        ps.setInt(3, offset);
        ps.setString(1, FriendshipStatus.FRIENDS.toString());
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            Long Id1 = rs.getLong("Id1");
            Long Id2 = rs.getLong("Id2");
            FriendshipType FType = FriendshipType.valueOf(rs.getString("FType"));
            FriendshipStatus FStatus = FriendshipStatus.FRIENDS;
            Friendship fr = new Friendship(Id1, Id2, FType);
            fr.setFriendshipStatus(FStatus);
            friendships.add(fr);
        }
        return friendships;
    }

    public void saveFriendship(Friendship friendship) throws SQLException {
        String insertSQL = "INSERT INTO Friendship (Id1,Id2,FType,friendshipstatus) VALUES (?,?,?,?)";
        Connection conn = CreateConnection.getConnection();
        conn.setAutoCommit(false);
        PreparedStatement ps = conn.prepareStatement(insertSQL);
        ps.setLong(1, friendship.getUser1());
        ps.setLong(2, friendship.getUser2());
        ps.setString(3, friendship.getFriendshipType().toString());
        ps.setString(4, friendship.getFriendshipStatus().toString());
        ps.executeUpdate();
        conn.commit();
    }

    public void deleteFriendship(Friendship friendship) throws SQLException {
        String deleteSQL = "DELETE FROM Friendship WHERE Id1=? AND Id2=?";
        Connection conn = CreateConnection.getConnection();
        conn.setAutoCommit(false);
        PreparedStatement ps = conn.prepareStatement(deleteSQL);
        ps.setLong(1, friendship.getUser1());
        ps.setLong(2, friendship.getUser2());
        ps.executeUpdate();
        conn.commit();
    }

    public int getTotalFriendships() throws SQLException {
        int total = 0;
        String countSQL = "SELECT COUNT(*) FROM \"friendship\"";
        Connection conn = CreateConnection.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(countSQL);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                total += rs.getInt(1);
            }
        }
        return total;
    }

    public Friendship getFriendshipById(Long id1, Long id2) throws SQLException {
        String selectSQL = "SELECT * FROM Friendship WHERE Id1=? AND Id2=?";
        Connection conn = CreateConnection.getConnection();
        PreparedStatement ps = conn.prepareStatement(selectSQL);
        ps.setLong(1, id1);
        ps.setLong(2, id2);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            Long Id1 = rs.getLong("Id1");
            Long Id2 = rs.getLong("Id2");
            FriendshipType FType = FriendshipType.valueOf(rs.getString("FType"));
            FriendshipStatus FStatus = FriendshipStatus.valueOf(rs.getString("friendshipstatus"));
            Friendship fr= new Friendship(Id1, Id2, FType);
            fr.setFriendshipStatus(FStatus);
            return fr;
        }
        return null;
    }

    public List<Friendship> getFriendshipsForUser(Long idUser) throws SQLException {
        List<Friendship> friendships = new ArrayList<>();
        String selectSQL = "SELECT * FROM Friendship WHERE Id1=? OR Id2=?";
        Connection conn = CreateConnection.getConnection();
        PreparedStatement ps = conn.prepareStatement(selectSQL);
        ps.setLong(1, idUser);
        ps.setLong(2, idUser);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            Long Id1 = rs.getLong("Id1");
            Long Id2 = rs.getLong("Id2");
            FriendshipType FType = FriendshipType.valueOf(rs.getString("FType"));
            FriendshipStatus FStatus = FriendshipStatus.valueOf(rs.getString("friendshipstatus"));
            if(FStatus==FriendshipStatus.FRIENDS){
            Friendship fr = new Friendship(Id1, Id2, FType);
            fr.setFriendshipStatus(FStatus);
            friendships.add(fr);}
        }
        return friendships;
    }

    public List<Friendship> getAllFriendships() throws SQLException {
        List<Friendship> friendships = new ArrayList<>();
        String selectSQL = "SELECT Id1,Id2,FType,friendshipstatus FROM Friendship";
        Connection conn = CreateConnection.getConnection();
        PreparedStatement ps = conn.prepareStatement(selectSQL);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            Long Id1 = rs.getLong("Id1");
            Long Id2 = rs.getLong("Id2");
            FriendshipType FType = FriendshipType.valueOf(rs.getString("FType"));
            FriendshipStatus FStatus = FriendshipStatus.valueOf(rs.getString("friendshipstatus"));
            if(FStatus==FriendshipStatus.FRIENDS){
            Friendship fr = new Friendship(Id1, Id2, FType);
            fr.setFriendshipStatus(FStatus);
            friendships.add(fr);}
        }
        return friendships;
    }

    public void updateFriendshipStatus(Long id1, Long id2, FriendshipStatus FStatus) throws SQLException {
        String sqlUpdate="update Friendship set friendshipstatus=? where Id1=? and Id2=?";
        Connection conn = CreateConnection.getConnection();
        conn.setAutoCommit(false);
        PreparedStatement ps = conn.prepareStatement(sqlUpdate);
        ps.setString(1, FStatus.toString());
        ps.setLong(2, id1);
        ps.setLong(3, id2);
        ps.executeUpdate();
        conn.commit();
    }

}
