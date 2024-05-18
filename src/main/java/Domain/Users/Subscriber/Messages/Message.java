package src.main.java.Domain.Users.Subscriber.Messages;

import java.util.Date;

public abstract class Message {
    protected String message;
    protected Date date;

    public Message(String message) {
        this.message = message;
        this.date = new Date();
    }

    public abstract void response(boolean answer);
}
