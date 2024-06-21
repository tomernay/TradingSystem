package Presentation.application.Presenter;

import Presentation.application.CookiesHandler;
import Presentation.application.View.MessagesList;
import Service.ServiceInitializer;
import Service.UserService;
import Utilities.Messages.Message;
import Utilities.Messages.nominateManagerMessage;
import Utilities.Messages.nominateOwnerMessage;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Queue;

@Component
public class MessagesPresenter {

    private MessagesList view;
    UserService userService;
    private final HttpServletRequest request;

    public MessagesPresenter(HttpServletRequest request){
        userService = ServiceInitializer.getInstance().getUserService();
        this.request = request;
    }

    public void attachView(MessagesList view) {
        this.view = view;
    }

    public List<Message> initMessages(String user) {
        // Initialize messages based on the user
        // This is a placeholder; replace with actual implementation
        return userService.getMessages(user).getData();
    }

    public void handleRequest(Message message, Boolean answer) {
        String username = CookiesHandler.getUsernameFromCookies(request);
        String token = CookiesHandler.getTokenFromCookies(request);
        if (message instanceof nominateManagerMessage) {
            userService.managerNominationResponse(message.getId(), username, answer, token);
        }
        else if (message instanceof nominateOwnerMessage) {
            userService.ownerNominationResponse(message.getId(), username, answer, token);
        }
    }

    public void removeMessageById(Integer messageId) {
        String username = CookiesHandler.getUsernameFromCookies(request);
        String token = CookiesHandler.getTokenFromCookies(request);
        userService.removeMessage(username, token, messageId);
        List<Message> messages = MessagesList.getMessages();
        messages.removeIf(message -> message.getId().equals(messageId));
        MessagesList.setMessages(messages);
    }

    public boolean isLoggedIn() {
        String token = CookiesHandler.getTokenFromCookies(request);
        String username = CookiesHandler.getUsernameFromCookies(request);
        return userService.isValidToken(token, username);
    }
}
