package Domain.Users.Subscriber.Messages;

import Utilities.Response;

import java.util.Date;

public abstract class Message {
    protected String message;
    protected Date date;

    public Message(String message) {
        this.message = message;
        this.date = new Date();
    }

    public abstract Response<Message> response(boolean answer);
}
