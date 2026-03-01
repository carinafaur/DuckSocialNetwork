package domain;

import domain.Message;

import java.time.LocalDateTime;
import java.util.List;

public class ReplyMessage extends Message{
    private Message replyMessage;

    public ReplyMessage(Long id, Long from, List<Long> to, LocalDateTime date, String message,Message replyMessage){
        super(id,from,to,date,message);
        this.replyMessage = replyMessage;
    }

    public Message getReplyMessage() {
        return replyMessage;
    }
}
