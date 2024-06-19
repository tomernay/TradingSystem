package Utilities.Messages;

import Utilities.Response;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

public abstract class Message implements Serializable {
    private final String id;
    protected String message;
    protected Date date;
    protected boolean read;

    public Message(String message) {
        this.id = UUID.randomUUID().toString(); // Generate a unique ID
        this.message = message;
        this.date = new Date();
        this.read = false; // Default to unread
    }

    public String getId() {
        return id;
    }

    public abstract Response<Message> response(boolean answer);

    public String getMessage() {
        return message;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    @Override
    public String toString() {
        return "Message{" +
                "id='" + id + '\'' +
                ", message='" + message + '\'' +
                ", date=" + date +
                ", read=" + read +
                '}';
    }

    public String getContent() {
        return message;
    }
}
