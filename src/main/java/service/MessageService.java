package service;

import domain.Message;
import domain.ReplyMessage;
import domain.User;
import exceptions.UserNotFoundException;
import repository.MessageRepository;
import repository.UserRepository;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;

public class MessageService {
    private MessageRepository messageRepository;
    private UserRepository userRepository;

    public MessageService(MessageRepository messageRepository, UserRepository userRepository) {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
    }

    public List<Message> getConversation(Long userId1, Long userId2) throws SQLException, UserNotFoundException {
        if (userRepository.findUser(userId1) == null || userRepository.findUser(userId2) == null) {
            throw new UserNotFoundException("Error", "User not found!");
        } else {
            List<Message> messages1to2 = messageRepository.findBySenderAndReceiver(userId1, userId2);
            List<Message> messages2to1 = messageRepository.findBySenderAndReceiver(userId2, userId1);

            messages1to2.addAll(messages2to1);
            messages1to2.sort((m1, m2) -> m1.getDate().compareTo(m2.getDate()));

            return messages1to2;
        }
    }

    public List<Message> getMessagesForUser(Long id) throws SQLException {
        List<Message> messages = messageRepository.findBySender(id);
        return messages;
}

public void removeMessage(Message message) throws SQLException {
    messageRepository.removeMessage(message);
}

public void sendReply(Long id, Long from, List<Long> to, String mess, Message message) throws SQLException, UserNotFoundException {
    if (userRepository.findUser(from) == null)
        throw new UserNotFoundException("Error", "User not found!");
    for (Long i : to) {
        if (userRepository.findUser(i) == null)
            throw new UserNotFoundException("Error", "User not found!");
    }
    LocalDateTime date = LocalDateTime.now();
    ReplyMessage replyMessage = new ReplyMessage(id, from, to, date, mess, message);
    messageRepository.addMessage(replyMessage);
}

public void sendMessage(Long id, Long from, List<Long> to, String mess) throws UserNotFoundException, SQLException {
    if (userRepository.findUser(from) == null)
        throw new UserNotFoundException("Error", "User not found!");
    for (Long i : to) {
        if (userRepository.findUser(i) == null)
            throw new UserNotFoundException("Error", "User not found!");
    }
    LocalDateTime date = LocalDateTime.now();
    Message message = new Message(id, from, to, date, mess);
    messageRepository.addMessage(message);
}

}
