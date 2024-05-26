package Domain.Users.Subscriber.Messages;

import Utilities.Response;

import java.io.Serializable;

public class NormalMessage extends Message {

    public NormalMessage(String message) {
        super(message);
    }

    @Override
    public Response<Message> response(boolean answer) {
        return null;
    }
}
