package Presentaion.application.Presenter;

import Presentaion.application.View.Messages.MessagesList;
import Service.ServiceInitializer;
import Service.UserService;
import Utilities.Messages.Message;
import org.springframework.stereotype.Component;

import java.util.Queue;

@Component
public class MessagesPresenter {
    MessagesList messagesList;
    UserService service;


    public MessagesPresenter(){
        service= ServiceInitializer.getInstance().getUserService();


    }
    public void attachView(MessagesList view) {
        this.messagesList = view;
    }

    public void popMessage(){
       MessagesList.getMessages().poll();


    }

    /**
     * init all messages
     * @param user
     * @return
     */
    public Queue<Message> initMessages(String user){


        return ServiceInitializer.getInstance().getUserService().getMessages(user).getData();

    }


}
