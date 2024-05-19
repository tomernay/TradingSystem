package src.main.java.Domain.Users.Subscriber.Messages;

public class NormalMessage extends Message{

    public NormalMessage(String message) {
        super(message);
    }

    @Override
    public void response(boolean answer) {

    }
}
