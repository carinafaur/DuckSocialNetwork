package repository;

import data.CreateConnection;
import domain.Message;
import domain.ReplyMessage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DataBaseLoaderMessage {

    public List<Message> findBySenderAndReceiver(Long userId1,Long userId2) throws SQLException {
        String selectSql = "SELECT * FROM \"Message\" WHERE \"From\"=?";
        List<Message> messages = new ArrayList<>();
        Connection conn = CreateConnection.getConnection();
        PreparedStatement ps = conn.prepareStatement(selectSql);
        ps.setLong(1, userId1);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            Long id = rs.getLong("MId");
            Long from = rs.getLong("From");
            String toLine = rs.getString("To");
            List<Long> to;
            String[] toValues = toLine.split(",");
            to = Arrays.stream(toValues).map(Long::parseLong).toList();
            if(to.contains(userId2)){
                LocalDateTime dateTime = rs.getTimestamp("Date").toLocalDateTime();
                String Mess = rs.getString("Mes");
                messages.add(new Message(id, from, to, dateTime, Mess));
            }

        }
        return messages;
    }

    public List<Message> findBySender(Long userId) throws SQLException {
        String selectSql = "SELECT * FROM \"Message\" WHERE \"From\"=?";
        List<Message> messages = new ArrayList<>();
        Connection conn = CreateConnection.getConnection();
        PreparedStatement ps = conn.prepareStatement(selectSql);
        ps.setLong(1, userId);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            Long id = rs.getLong("MId");
            Long from = rs.getLong("From");
            String toLine = rs.getString("To");
            List<Long> to;
            String[] toValues = toLine.split(",");
            to = Arrays.stream(toValues).map(Long::parseLong).toList();
            LocalDateTime dateTime = rs.getTimestamp("Date").toLocalDateTime();
            String Mess = rs.getString("Mes");
            messages.add(new Message(id, from, to, dateTime, Mess));
            }
        return messages;
        }

    public void saveMessage(Message message) throws SQLException {
        String insertSql = "INSERT INTO \"Message\" (MId,\"From\",\"To\",Date,Mes,Replied) VALUES (?,?,?,?,?,?)";

        Connection conn = CreateConnection.getConnection();
        conn.setAutoCommit(false);
        PreparedStatement ps = conn.prepareStatement(insertSql);
        ps.setLong(1, message.getId());
        ps.setLong(2, message.getFrom());
        String toLine = message.getTo().stream()
                .map(String::valueOf)
                .collect(java.util.stream.Collectors.joining(","));
        ps.setString(3, toLine);
        ps.setTimestamp(4, java.sql.Timestamp.valueOf(message.getDate()));
        ps.setString(5, message.getMessage());
        if(message instanceof ReplyMessage){
            ps.setLong(6, ((ReplyMessage) message).getReplyMessage().getId());
        }else ps.setNull(6, java.sql.Types.BIGINT);
        ps.executeUpdate();
        conn.commit();
    }

    public void removeMessage(Message message) throws SQLException {
        String deleteMessageSQL = "DELETE FROM \"Message\" WHERE Mid=?;";
        Connection conn = CreateConnection.getConnection();
        conn.setAutoCommit(false);
        PreparedStatement psMessage = conn.prepareStatement(deleteMessageSQL);
        psMessage.setLong(1, message.getId());
        psMessage.executeUpdate();
        conn.commit();
    }

}
