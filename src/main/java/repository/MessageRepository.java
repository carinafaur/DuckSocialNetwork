package repository;

import domain.Message;
import exceptions.DataBaseException;

import java.sql.SQLException;
import java.util.List;

public class MessageRepository {
    private DataBaseLoaderMessage dataBaseLoaderMessage;

    public MessageRepository() {
        dataBaseLoaderMessage = new DataBaseLoaderMessage();
    }

    public List<Message> findBySenderAndReceiver(Long userId1,Long userId2) throws SQLException {
        return dataBaseLoaderMessage.findBySenderAndReceiver(userId1,userId2);
    }

    public List<Message> findBySender(Long userId) throws SQLException {
        return dataBaseLoaderMessage.findBySender(userId);
    }

    public void addMessage(Message message) throws SQLException {
        dataBaseLoaderMessage.saveMessage(message);
    }

    public void removeMessage(Message message) throws SQLException {
        dataBaseLoaderMessage.removeMessage(message);
    }


}
