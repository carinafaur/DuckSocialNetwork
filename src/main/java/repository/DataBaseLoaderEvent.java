package repository;

import data.CreateConnection;
import domain.*;
import javafx.util.Pair;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class DataBaseLoaderEvent {

    public Map<String, List<Integer>> getEvents(int limit, int offset) throws SQLException {
        Map<String, List<Integer>> events = new HashMap<>();
        String selectSQL = "SELECT Name,Distances,M from \"event\" LIMIT ? OFFSET ?";
        Connection conn = CreateConnection.getConnection();
        PreparedStatement preparedStatement = conn.prepareStatement(selectSQL);
        preparedStatement.setInt(1, limit);
        preparedStatement.setInt(2, offset);
        ResultSet rs = preparedStatement.executeQuery();
        while (rs.next()) {
            List<Integer> list = new ArrayList<>();
            String name = rs.getString("Name");
            String dLine = rs.getString("Distances");
            String[] dist = dLine.split(",");
            if (!Objects.equals(dist[0], "")) {
                for (String d : dist) {
                    list.add(Integer.parseInt(d));
                }
            }
            int M = rs.getInt("M");
            list.add(M);
            events.put(name, list);
        }
        return events;
    }

    public Map<String,List<Integer>> getAllEvents() throws SQLException {
        Map<String, List<Integer>> events = new HashMap<>();
        String selectSQL = "SELECT Name,Distances,M from \"event\"";
        Connection conn = CreateConnection.getConnection();
        PreparedStatement preparedStatement = conn.prepareStatement(selectSQL);
        ResultSet rs = preparedStatement.executeQuery();
        while (rs.next()) {
            List<Integer> list = new ArrayList<>();
            String name = rs.getString("Name");
            String dLine = rs.getString("Distances");
            String[] dist = dLine.split(",");
            if (!Objects.equals(dist[0], "")) {
                for (String d : dist) {
                    list.add(Integer.parseInt(d));
                }
            }
            int M = rs.getInt("M");
            list.add(M);
            events.put(name, list);
        }
        return events;
    }

    public void saveEvent(RaceEvent event) throws SQLException {
        String insertSQL = "INSERT INTO \"event\" (Name,Distances,M) VALUES (?,?,?)";
        Connection conn = CreateConnection.getConnection();
        PreparedStatement ps = conn.prepareStatement(insertSQL);
        conn.setAutoCommit(false);
        ps.setString(1, event.getName());
        String line = "";
        List<Integer> distances = event.getDistances();
        for (int i = 0; i < distances.size(); i++) {
            line += distances.get(i) + ",";
        }
        ps.setString(2, line);
        ps.setInt(3, event.getNrDucks());
        ps.executeUpdate();
        conn.commit();
    }

    public void deleteEvent(RaceEvent event) throws SQLException {
        String deleteSQL = "DELETE FROM \"event\" WHERE Name=?";
        Connection conn = CreateConnection.getConnection();
        conn.setAutoCommit(false);
        PreparedStatement ps = conn.prepareStatement(deleteSQL);
        ps.setString(1, event.getName());
        ps.executeUpdate();
        conn.commit();
    }

    public int getEventsCount() throws SQLException {
        int total = 0;
        String countSQL = "SELECT COUNT(*) FROM \"event\"";
        Connection conn = CreateConnection.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(countSQL);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                total += rs.getInt(1);
            }
        }
        return total;
    }

    public Pair<String, List<Integer>> findEventByName(String name) throws SQLException {
        String sqlSelect = "SELECT * FROM \"event\" WHERE Name=?";
        Connection conn = CreateConnection.getConnection();
        PreparedStatement ps = conn.prepareStatement(sqlSelect);
        ps.setString(1, name);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            String eventName = rs.getString("Name");
            String distancesLine = rs.getString("Distances");
            List<Integer> distances = new ArrayList<>();
            String[] dist = distancesLine.split(",");
            if (!Objects.equals(dist[0], "")) {
                for (String d : dist) {
                    distances.add(Integer.parseInt(d));
                }
            }
            int M = rs.getInt("M");
            distances.add(M);
            return new Pair<>(eventName, distances);
        }
        return  null;
    }

    public Long findEventIdByName(String name) throws SQLException {
        String sqlSelect = "SELECT Eid FROM \"event\" WHERE Name=?";
        Connection conn = CreateConnection.getConnection();
        PreparedStatement ps = conn.prepareStatement(sqlSelect);
        ps.setString(1, name);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            Long eid = rs.getLong("Eid");
            return eid;
        }
        return  null;
    }

    public void subscribeDuck(Long did,String name) throws SQLException {
        String insertSQL = "INSERT INTO \"eventsubscribersducks\" (Eid,Did) VALUES (?,?)";
        Connection conn = CreateConnection.getConnection();
        conn.setAutoCommit(false);
        PreparedStatement ps = conn.prepareStatement(insertSQL);
        Long eid = findEventIdByName(name);
        ps.setLong(1, eid);
        ps.setLong(2, did);
        ps.executeUpdate();
        conn.commit();
    }

    public void subscribePerson(Long pid,String name) throws SQLException {
        String insertSQL = "INSERT INTO \"eventsubscriberspersons\" (EidP,Pid) VALUES (?,?)";
        Connection conn = CreateConnection.getConnection();
        conn.setAutoCommit(false);
        PreparedStatement ps = conn.prepareStatement(insertSQL);
        Long eid = findEventIdByName(name);
        ps.setLong(1, eid);
        ps.setLong(2, pid);
        ps.executeUpdate();
        conn.commit();
    }

    public void unsubscribeDuck(Long did,String name) throws SQLException {
        String deleteSQL = "DELETE FROM \"eventsubscribersducks\" WHERE Eid=? and  Did=?";
        Connection conn = CreateConnection.getConnection();
        conn.setAutoCommit(false);
        PreparedStatement ps = conn.prepareStatement(deleteSQL);
        Long eid = findEventIdByName(name);
        ps.setLong(1, eid);
        ps.setLong(2, did);
        ps.executeUpdate();
        conn.commit();
    }

    public void unsubscribePerson(Long pid,String name) throws SQLException {
        String deleteSQL = "DELETE FROM \"eventsubscriberspersons\" WHERE EidP=? and  Pid=?";
        Connection conn = CreateConnection.getConnection();
        conn.setAutoCommit(false);
        PreparedStatement ps = conn.prepareStatement(deleteSQL);
        Long eid = findEventIdByName(name);
        ps.setLong(1, eid);
        ps.setLong(2, pid);
        ps.executeUpdate();
        conn.commit();
    }

    public List<Long> getSubscribers(String name) throws SQLException {
        String selectSQL1 = "SELECT * FROM \"eventsubscribersducks\" WHERE Eid=?";
        String selectSQL2 = "SELECT * FROM \"eventsubscriberspersons\" WHERE EidP=?";
        Long eid = findEventIdByName(name);
        Connection conn = CreateConnection.getConnection();
        PreparedStatement ps1 = conn.prepareStatement(selectSQL1);
        ps1.setLong(1, eid);
        ResultSet rs = ps1.executeQuery();
        List<Long> subscribers = new ArrayList<>();
        while (rs.next()) {
            subscribers.add(rs.getLong("Did"));
        }
        PreparedStatement ps2 = conn.prepareStatement(selectSQL2);
        ps2.setLong(1, eid);
        ResultSet rs2 = ps2.executeQuery();
        while (rs2.next()) {
            subscribers.add(rs2.getLong("Pid"));
        }
        return subscribers;
    }
}
