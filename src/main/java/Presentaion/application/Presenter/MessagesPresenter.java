package Presentaion.application.Presenter;

import Presentaion.application.CookiesHandler;
import Presentaion.application.View.Messages.MessagesList;
import Presentaion.application.View.Payment.PaymentPage;
import Service.ServiceInitializer;
import Service.UserService;
import Utilities.Messages.Message;
import org.springframework.stereotype.Component;

import java.util.List;

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

}
