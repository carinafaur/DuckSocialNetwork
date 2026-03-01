package domain;

import java.time.LocalDateTime;
import java.util.List;

public class Message {
    private String message;
    private Long id;
    private Long from;
    private List<Long> to;
    private LocalDateTime date;

    public Message(Long id, Long from, List<Long> to, LocalDateTime date, String message) {
        this.id = id;
        this.from = from;
        this.to = to;
        this.date = date;
        this.message = message;
    }

    public Long getId() {
        return id;
    }

    public Long getFrom() {
        return from;
    }

    public List<Long> getTo() {
        return to;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public String getMessage() {
        return message;
    }
}
