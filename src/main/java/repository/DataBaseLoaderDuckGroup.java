package repository;

import data.CreateConnection;
import domain.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class DataBaseLoaderDuckGroup {
    DataBaseLoaderDuckGroup(){}

    public List<DuckGroup<? extends Duck>> getDuckGroupsPage(int limit,int offset) throws SQLException{
        List<DuckGroup<? extends Duck>> duckGroups = new ArrayList<>();

        String selectSQL = "SELECT * FROM \"duckgroup\" LIMIT ? OFFSET ?";

       Connection conn = CreateConnection.getConnection();

            PreparedStatement ps = conn.prepareStatement(selectSQL);
            ps.setInt(1,limit);
            ps.setInt(2,offset);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                long id = rs.getLong("DGid");
                String name = rs.getString("Name");
                DuckType groupType = DuckType.valueOf(rs.getString("GroupType"));

                switch (groupType) {

                    case FLYING -> {
                        List<FlyingDuck> list = loadFlyingDucks(id);
                        DuckGroup<? extends Duck> duckGroup=new DuckGroup<>(id, name, list,groupType);
                        duckGroups.add(duckGroup);
                    }

                    case SWIMMING -> {
                        List<SwimmingDuck> list = loadSwimmingDucks(id);
                        DuckGroup<? extends Duck> duckGroup=new DuckGroup<>(id, name, list,groupType);
                        duckGroups.add(duckGroup);
                    }

                    default -> {
                        List<FlyingAndSwimmingDuck> list = loadFSDucks(id);
                        DuckGroup<? extends Duck> duckGroup=new DuckGroup<>(id, name, list,groupType);
                        duckGroups.add(duckGroup);
                    }
                }
            }

        return duckGroups;
    }

    public DuckGroup<? extends Duck> findDuckGroupById(long id) throws SQLException {
        String selectSQL = "SELECT * FROM \"duckgroup\" WHERE DGid=?";
       Connection conn = CreateConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(selectSQL);
            ps.setLong(1,id);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String name = rs.getString("Name");
                DuckType groupType = DuckType.valueOf(rs.getString("GroupType"));

                switch (groupType) {

                    case FLYING -> {
                        List<FlyingDuck> list = loadFlyingDucks(id);
                        return new DuckGroup<>(id, name, list, groupType);
                    }

                    case SWIMMING -> {
                        List<SwimmingDuck> list = loadSwimmingDucks(id);
                        return new DuckGroup<>(id, name, list, groupType);

                    }

                    default -> {
                        List<FlyingAndSwimmingDuck> list = loadFSDucks(id);
                        return new DuckGroup<>(id, name, list, groupType);
                    }
                }
            }
        return null;
    }

    public List<FlyingDuck> loadFlyingDucks(long groupId) throws SQLException {
        List<FlyingDuck> list = new ArrayList<>();

        String sql = "SELECT Did, Username, Email, DPassword, DType, Speed, Resistance FROM \"Duck\" WHERE \"DuckGroup\" = ? AND DType = 'FLYING'";
        Connection conn = CreateConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setLong(1,groupId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(new FlyingDuck(
                        rs.getLong("Did"),
                        rs.getString("Username"),
                        rs.getString("Email"),
                        rs.getString("DPassword"),
                        DuckType.FLYING,
                        rs.getDouble("Speed"),
                        rs.getDouble("Resistance")));
            }
        return list;
    }

    public List<SwimmingDuck> loadSwimmingDucks(long groupId) throws SQLException {
        List<SwimmingDuck> list = new ArrayList<>();

        String sql = "SELECT Did, Username, Email, DPassword, DType, Speed, Resistance FROM \"Duck\" WHERE \"DuckGroup\" = ? AND DType = 'SWIMMING'";
       Connection conn = CreateConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setLong(1,groupId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(new SwimmingDuck(
                        rs.getLong("Did"),
                        rs.getString("Username"),
                        rs.getString("Email"),
                        rs.getString("DPassword"),
                        DuckType.SWIMMING,
                        rs.getDouble("Speed"),
                        rs.getDouble("Resistance")));
            }
        return list;
    }

    private List<FlyingAndSwimmingDuck> loadFSDucks(long groupId) throws SQLException {
        List<FlyingAndSwimmingDuck> list = new ArrayList<>();

        String sql = "SELECT Did, Username, Email, DPassword, DType, Speed, Resistance FROM \"Duck\" WHERE \"DuckGroup\" = ? AND DType = 'FLYING_AND_SWIMMING'";
        Connection conn = CreateConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setLong(1,groupId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(new FlyingAndSwimmingDuck(
                        rs.getLong("Did"),
                        rs.getString("Username"),
                        rs.getString("Email"),
                        rs.getString("DPassword"),
                        DuckType.FLYING_AND_SWIMMING,
                        rs.getDouble("Speed"),
                        rs.getDouble("Resistance")));
            }
        return list;
    }


    public void saveDuckGroup(DuckGroup<? extends Duck> duckGroup) throws SQLException{
        String insertSQL="INSERT INTO \"duckgroup\" (DGid,Name,\"GroupType\") VALUES (?,?,?)";
        Connection conn=CreateConnection.getConnection();
            conn.setAutoCommit(false);
            PreparedStatement ps = conn.prepareStatement(insertSQL);
            ps.setLong(1,duckGroup.getId());
            ps.setString(2,duckGroup.getGroupName());
            ps.setString(3,duckGroup.getDuckType().toString());
            List<? extends Duck> ducks=duckGroup.getMembers();
            for(Duck duck:ducks){
                conn.setAutoCommit(false);
                String insertDuck="UPDATE \"Duck\" SET \"DuckGroup\"=(?) WHERE Did="+duck.getId();
                PreparedStatement pps=conn.prepareStatement(insertDuck);
                pps.setLong(1,duckGroup.getId());
                pps.executeUpdate();
                conn.commit();
            }
            ps.executeUpdate();
            conn.commit();
    }

    public void deleteDuckGroup(DuckGroup<? extends Duck> duckGroup) throws SQLException{
        String deleteSQL="DELETE FROM \"duckgroup\" WHERE DGid=?";
        Connection conn=CreateConnection.getConnection();
            conn.setAutoCommit(false);
            PreparedStatement ps = conn.prepareStatement(deleteSQL);
            ps.setLong(1,duckGroup.getId());
            ps.executeUpdate();
            conn.commit();
    }

    public int getDuckGroupCount() throws SQLException{
        int total=0;
        String countSQL="SELECT COUNT(*) FROM \"DuckGroup\"";
       Connection conn = CreateConnection.getConnection();
            try (PreparedStatement ps = conn.prepareStatement(countSQL);
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    total +=rs.getInt(1);
                }
            }
        return total;
    }
}
