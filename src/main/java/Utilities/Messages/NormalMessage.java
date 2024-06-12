package Utilities.Messages;

import Utilities.Response;

import java.io.Serializable;

public class NormalMessage extends Message {

    public NormalMessage(String message) {
        super(message);
    }

    @Override
    public Response<Message> response(boolean answer) {
        return Response.success("Successfully responded to message.", this);
    }
}
