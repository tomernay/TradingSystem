package Utilities.Messages;

import Utilities.Response;

public class NormalMessage extends Message{

    public NormalMessage(String message) {
        super(message);
    }

    @Override
    public Response<Message> response(boolean answer) {
        return null;
    }
}
