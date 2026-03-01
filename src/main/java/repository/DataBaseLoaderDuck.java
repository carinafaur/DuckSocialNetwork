package repository;

import data.CreateConnection;
import domain.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DataBaseLoaderDuck {
    DataBaseLoaderDuck() {
    }

    public void saveDuck(Duck duck) throws SQLException {
        String insertDuckSQL = "INSERT INTO \"Duck\" (Did,Username, Email, DPassword,DType, Speed, Resistance) VALUES (?,?,?,?, ?, ?, ?)";
        Connection conn = CreateConnection.getConnection();
        conn.setAutoCommit(false);
        PreparedStatement psDuck = conn.prepareStatement(insertDuckSQL);
        psDuck.setLong(1, duck.getId());
        psDuck.setString(2, duck.getUsername());
        psDuck.setString(3, duck.getEmail());
        psDuck.setString(4, duck.getPassword());
        psDuck.setString(5, duck.getType().toString());
        psDuck.setDouble(6, duck.getSpeed());
        psDuck.setDouble(7, duck.getResistance());
        psDuck.executeUpdate();

        conn.commit();
    }

    public void removeDuck(Duck duck) throws SQLException {
        String deleteDuckSQL = "DELETE FROM \"Duck\" WHERE Did=?;";
        Connection conn = CreateConnection.getConnection();
        conn.setAutoCommit(false);
        PreparedStatement psDuck = conn.prepareStatement(deleteDuckSQL);
        psDuck.setLong(1, duck.getId());
        psDuck.executeUpdate();
        conn.commit();
    }


    public List<Duck> getDucksPage(int limit, int offset) throws SQLException {
        List<Duck> ducks = new ArrayList<>();
        String selectSQL = "SELECT Did, Username, Email, DPassword, DType, Speed, Resistance,\"DuckGroup\" FROM \"Duck\" LIMIT ? OFFSET ?;";

        Connection conn = CreateConnection.getConnection();
        PreparedStatement ps = conn.prepareStatement(selectSQL);
        ps.setInt(1, limit);
        ps.setInt(2, offset);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            Duck duck;
            Long id = rs.getLong("Did");
            String username = rs.getString("Username");
            String email = rs.getString("Email");
            String password = rs.getString("DPassword");
            DuckType type = DuckType.valueOf(rs.getString("DType"));
            double speed = rs.getDouble("Speed");
            double resistance = rs.getDouble("Resistance");
            Long did = rs.getLong("DuckGroup");
            if (type == DuckType.FLYING) {
                duck = new FlyingDuck(id, username, email, password, type, speed, resistance);
            } else if (type == DuckType.SWIMMING) {
                duck = new SwimmingDuck(id, username, email, password, type, speed, resistance);
            } else
                duck = new FlyingAndSwimmingDuck(id, username, email, password, type, speed, resistance);
            duck.setGroup(did);
            ducks.add(duck);
        }
        return ducks;
    }

    public int getTotalDuckCount() throws SQLException {
        int total = 0;
        String countDuckSQL = "SELECT COUNT(*) FROM \"Duck\"";

        Connection conn = CreateConnection.getConnection();
        PreparedStatement psDuck = conn.prepareStatement(countDuckSQL);
        ResultSet rsDuck = psDuck.executeQuery();
        if (rsDuck.next()) {
            total += rsDuck.getInt(1);
        }
        return total;
    }


    public List<SwimmingDuck> getAllSwimmingDucks() throws SQLException {
        List<SwimmingDuck> swimmingDucks = new ArrayList<>();

        String selectSQL = "SELECT Did, Username, Email, DPassword, DType, Speed, Resistance, \"DuckGroup\" FROM \"Duck\" WHERE DType='SWIMMING'";

        Connection conn = CreateConnection.getConnection();
        PreparedStatement ps = conn.prepareStatement(selectSQL);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            Long id = rs.getLong("Did");
            String username = rs.getString("Username");
            String email = rs.getString("Email");
            String password = rs.getString("DPassword");

            DuckType type = DuckType.valueOf(rs.getString("DType"));

            double speed = rs.getDouble("Speed");
            double resistance = rs.getDouble("Resistance");
            Long did = rs.getLong("DuckGroup");

            SwimmingDuck swimmingDuck = new SwimmingDuck(id, username, email, password, type, speed, resistance);

            if (did != 0) {
                swimmingDuck.setGroup(did);
            }

            swimmingDucks.add(swimmingDuck);
        }
        return swimmingDucks;
    }

    public Duck findDuckById(Long id) throws SQLException {
        String selectDuckSQL = "SELECT * FROM \"Duck\" WHERE Did = ?";

        Connection conn = CreateConnection.getConnection();
        PreparedStatement psDuck = conn.prepareStatement(selectDuckSQL);
        psDuck.setLong(1, id);
        try (ResultSet rs = psDuck.executeQuery()) {
            if (rs.next()) {
                Duck duck;
                Long Did = rs.getLong("Did");
                String username = rs.getString("Username");
                String email = rs.getString("Email");
                String password = rs.getString("DPassword");
                DuckType type = DuckType.valueOf(rs.getString("DType"));
                double speed = rs.getDouble("Speed");
                double resistance = rs.getDouble("Resistance");
                if (type == DuckType.FLYING) {
                    duck = new FlyingDuck(Did, username, email, password, type, speed, resistance);
                } else if (type == DuckType.SWIMMING) {
                    duck = new SwimmingDuck(Did, username, email, password, type, speed, resistance);
                } else
                    duck = new FlyingAndSwimmingDuck(Did, username, email, password, type, speed, resistance);
                return duck;
            }
        }
        return null;
    }

    public int getTotalDuckCountByType(DuckType type) throws SQLException {
        int total = 0;
        String countDuckSQL = "SELECT COUNT(*) FROM \"Duck\" WHERE DType = ?";

        Connection conn = CreateConnection.getConnection();
        PreparedStatement psDuck = conn.prepareStatement(countDuckSQL);
        psDuck.setString(1, type.toString());
        ResultSet rsDuck = psDuck.executeQuery();
        if (rsDuck.next()) {
            total += rsDuck.getInt(1);
        }
        return total;
    }

    public List<Duck> getDuckByType(int limit, int offset, DuckType type) throws SQLException {
        List<Duck> ducks = new ArrayList<>();
        String selectSQL = "SELECT Did, Username, Email, DPassword, DType, Speed, Resistance,\"DuckGroup\" FROM \"Duck\" WHERE DType=? LIMIT ? OFFSET ?;";

        Connection conn = CreateConnection.getConnection();
        PreparedStatement ps = conn.prepareStatement(selectSQL);
        ps.setString(1, type.toString());
        ps.setInt(2, limit);
        ps.setInt(3, offset);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            Duck duck;
            Long id = rs.getLong("Did");
            String username = rs.getString("Username");
            String email = rs.getString("Email");
            String password = rs.getString("DPassword");
            double speed = rs.getDouble("Speed");
            double resistance = rs.getDouble("Resistance");
            Long did = rs.getLong("DuckGroup");
            if (type == DuckType.FLYING) {
                duck = new FlyingDuck(id, username, email, password, type, speed, resistance);
            } else if (type == DuckType.SWIMMING) {
                duck = new SwimmingDuck(id, username, email, password, type, speed, resistance);
            } else
                duck = new FlyingAndSwimmingDuck(id, username, email, password, type, speed, resistance);
            duck.setGroup(did);
            ducks.add(duck);
        }
        return ducks;
    }

    public Duck findDuckByUsername(String username) throws SQLException {
        String selectDuckSQL = "SELECT * FROM \"Duck\" WHERE Username = ?";

        Connection conn = CreateConnection.getConnection();
        PreparedStatement psDuck = conn.prepareStatement(selectDuckSQL);
        psDuck.setString(1, username);
        try (ResultSet rs = psDuck.executeQuery()) {
            if (rs.next()) {
                Duck duck;
                Long Did = rs.getLong("Did");
                String email = rs.getString("Email");
                String password = rs.getString("DPassword");
                DuckType type = DuckType.valueOf(rs.getString("DType"));
                double speed = rs.getDouble("Speed");
                double resistance = rs.getDouble("Resistance");
                if (type == DuckType.FLYING) {
                    duck = new FlyingDuck(Did, username, email, password, type, speed, resistance);
                } else if (type == DuckType.SWIMMING) {
                    duck = new SwimmingDuck(Did, username, email, password, type, speed, resistance);
                } else
                    duck = new FlyingAndSwimmingDuck(Did, username, email, password, type, speed, resistance);
                return duck;
            }
        }
        return null;
    }

    public List<Duck> getAllDucks() throws SQLException {
        List<Duck> ducks = new ArrayList<>();
        String selectSQL = "SELECT Did, Username, Email, DPassword, DType, Speed, Resistance,\"DuckGroup\" FROM \"Duck\"";

        Connection conn = CreateConnection.getConnection();
        PreparedStatement ps = conn.prepareStatement(selectSQL);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            Duck duck;
            Long id = rs.getLong("Did");
            String username = rs.getString("Username");
            String email = rs.getString("Email");
            String password = rs.getString("DPassword");
            DuckType type = DuckType.valueOf(rs.getString("DType"));
            double speed = rs.getDouble("Speed");
            double resistance = rs.getDouble("Resistance");
            Long did = rs.getLong("DuckGroup");
            if (type == DuckType.FLYING) {
                duck = new FlyingDuck(id, username, email, password, type, speed, resistance);
            } else if (type == DuckType.SWIMMING) {
                duck = new SwimmingDuck(id, username, email, password, type, speed, resistance);
            } else
                duck = new FlyingAndSwimmingDuck(id, username, email, password, type, speed, resistance);
            duck.setGroup(did);
            ducks.add(duck);
        }
        return ducks;
    }

    public boolean isActive(Long id) throws SQLException {
        String selectSQL = "SELECT isActive FROM \"Duck\" WHERE Did = ?";
        Connection conn = CreateConnection.getConnection();
        conn.setAutoCommit(false);
        PreparedStatement ps = conn.prepareStatement(selectSQL);
        ps.setLong(1, id);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            boolean isActive = rs.getBoolean("isActive");
            return isActive;
        }
        return false;
    }

    public void updateActive(Long id,boolean status) throws SQLException {
        String updateSQL = "Update \"Duck\" set isActive=? WHERE Did = ?";
        Connection conn = CreateConnection.getConnection();
        conn.setAutoCommit(false);
        PreparedStatement ps = conn.prepareStatement(updateSQL);
        ps.setBoolean(1, status);
        ps.setLong(2, id);
        ps.executeUpdate();
        conn.commit();
    }
}