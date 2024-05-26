package Utilities.Messages;

import Utilities.Response;

import java.io.Serializable;
import java.util.Date;

public abstract class Message implements Serializable {
    protected String message;
    protected Date date;

    public Message(String message) {
        this.message = message;
        this.date = new Date();
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

    @Override
    public String toString() {
        return "Message{" +
                "message='" + message + '\'' +
                ", date=" + date +
                '}';
    }
}
