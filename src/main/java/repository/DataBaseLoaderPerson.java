package repository;

import data.CreateConnection;
import domain.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DataBaseLoaderPerson {
    DataBaseLoaderPerson() {
    }

    public void savePerson(Person person) throws SQLException {
        String insertPersonSQL = "INSERT INTO \"Person\" (Pid,Username, Email, PPassword, FName,LName , DateOfBirth,Ocupation,Empathy) VALUES (?,?,?,?, ?, ?, ?,?,?)";
        Connection conn = CreateConnection.getConnection();
        conn.setAutoCommit(false);
        PreparedStatement psPerson = conn.prepareStatement(insertPersonSQL);
        psPerson.setLong(1, person.getId());
        psPerson.setString(2, person.getUsername());
        psPerson.setString(3, person.getEmail());
        psPerson.setString(4, person.getPassword());
        psPerson.setString(5, person.getFirstName());
        psPerson.setString(6, person.getLastName());
        psPerson.setString(7, person.getDateOfBirth().toString());
        psPerson.setString(8, person.getOcupation());
        psPerson.setInt(9, person.getEmpathyLevel());
        psPerson.executeUpdate();
        conn.commit();
    }

    public void removePerson(Person person) throws SQLException {
        String deletePersonSQL = "DELETE FROM \"Person\" WHERE Pid=?;";
        Connection conn = CreateConnection.getConnection();
        conn.setAutoCommit(false);
        PreparedStatement psPerson = conn.prepareStatement(deletePersonSQL);
        psPerson.setLong(1, person.getId());
        psPerson.executeUpdate();
        conn.commit();

    }

    public List<Person> getPersonsPage(int limit, int offset) throws SQLException {
        List<Person> persons = new ArrayList<>();
        String selectSQL = "SELECT Pid, Username, Email, PPassword, FName, LName, DateOfBirth,Ocupation,Empathy FROM \"Person\" LIMIT ? OFFSET ?;";

        Connection conn = CreateConnection.getConnection();
        PreparedStatement ps = conn.prepareStatement(selectSQL);
        ps.setInt(1, limit);
        ps.setInt(2, offset);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            Person person;
            Long id = rs.getLong("Pid");
            String username = rs.getString("Username");
            String email = rs.getString("Email");
            String password = rs.getString("PPassword");
            String firstName = rs.getString("FName");
            String lastName = rs.getString("LName");
            LocalDate dateOfBirth = LocalDate.parse(rs.getString("DateOfBirth"));
            String ocupation = rs.getString("Ocupation");
            int empathyLevel = rs.getInt("Empathy");
            person = new Person(id, username, email, password, firstName, lastName, dateOfBirth, ocupation, empathyLevel);
            persons.add(person);
        }
        return persons;
    }


    public int getTotalPersonCount() throws SQLException {
        int total = 0;
        String countPersonSQL = "SELECT COUNT(*) FROM \"Person\"";
        Connection conn = CreateConnection.getConnection();
        try (PreparedStatement psPerson = conn.prepareStatement(countPersonSQL);
             ResultSet rsPerson = psPerson.executeQuery()) {
            if (rsPerson.next()) {
                total += rsPerson.getInt(1);
            }
        }
        return total;
    }

    public Person findPersonById(Long id) throws SQLException {
        String selectPersonSQL = "SELECT * FROM \"Person\" WHERE Pid = ?";

        Connection conn = CreateConnection.getConnection();
        PreparedStatement psPerson = conn.prepareStatement(selectPersonSQL);
        psPerson.setLong(1, id);
        ResultSet rs = psPerson.executeQuery();
        if (rs.next()) {
            Person person;
            Long Pid = rs.getLong("Pid");
            String username = rs.getString("Username");
            String email = rs.getString("Email");
            String password = rs.getString("PPassword");
            String firstName = rs.getString("FName");
            String lastName = rs.getString("LName");
            LocalDate dateOfBirth = LocalDate.parse(rs.getString("DateOfBirth"));
            String ocupation = rs.getString("Ocupation");
            int empathyLevel = rs.getInt("Empathy");
            person = new Person(Pid, username, email, password, firstName, lastName, dateOfBirth, ocupation, empathyLevel);
            return person;
        }

        return null;
    }

    public Person findPersonByUsername(String username) throws SQLException {
        String selectPersonSQL = "SELECT * FROM \"Person\" WHERE Username = ?";

        Connection conn = CreateConnection.getConnection();
        PreparedStatement psPerson = conn.prepareStatement(selectPersonSQL);
        psPerson.setString(1, username);
        ResultSet rs = psPerson.executeQuery();
        if (rs.next()) {
            Person person;
            Long Pid = rs.getLong("Pid");
            String email = rs.getString("Email");
            String password = rs.getString("PPassword");
            String firstName = rs.getString("FName");
            String lastName = rs.getString("LName");
            LocalDate dateOfBirth = LocalDate.parse(rs.getString("DateOfBirth"));
            String ocupation = rs.getString("Ocupation");
            int empathyLevel = rs.getInt("Empathy");
            person = new Person(Pid, username, email, password, firstName, lastName, dateOfBirth, ocupation, empathyLevel);
            return person;
        }

        return null;
    }

    public List<Person> getAllPersons() throws SQLException {
        List<Person> persons = new ArrayList<>();
        String selectSQL = "SELECT Pid, Username, Email, PPassword, FName, LName, DateOfBirth,Ocupation,Empathy FROM \"Person\"";

        Connection conn = CreateConnection.getConnection();
        PreparedStatement ps = conn.prepareStatement(selectSQL);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            Person person;
            Long id = rs.getLong("Pid");
            String username = rs.getString("Username");
            String email = rs.getString("Email");
            String password = rs.getString("PPassword");
            String firstName = rs.getString("FName");
            String lastName = rs.getString("LName");
            LocalDate dateOfBirth = LocalDate.parse(rs.getString("DateOfBirth"));
            String ocupation = rs.getString("Ocupation");
            int empathyLevel = rs.getInt("Empathy");
            person = new Person(id, username, email, password, firstName, lastName, dateOfBirth, ocupation, empathyLevel);
            persons.add(person);
        }
        return persons;
    }

    public boolean isActive(Long id) throws SQLException {
        String selectSQL = "SELECT isActive FROM \"Person\" WHERE Pid = ?";
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
        String updateSQL = "Update \"Person\" set isActive=? WHERE Pid = ?";
        Connection conn = CreateConnection.getConnection();
        conn.setAutoCommit(false);
        PreparedStatement ps = conn.prepareStatement(updateSQL);
        ps.setBoolean(1, status);
        ps.setLong(2, id);
        ps.executeUpdate();
        conn.commit();
    }

}