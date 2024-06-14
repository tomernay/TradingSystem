package Presentation.application.View;


import Presentation.application.CookiesHandler;
import Presentation.application.Presenter.MessagesPresenter;
import Utilities.Messages.Message;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.ItemClickEvent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinRequest;
import com.vaadin.flow.server.VaadinServletRequest;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

@Route("MessagesList")
public class MessagesList extends VerticalLayout {

   static private Queue<Message> messages = new LinkedBlockingQueue<>();
   String user;
MessagesPresenter presenter;
    public MessagesList() {
        setClassName("list-view");
     user= CookiesHandler.getUsernameFromCookies(getRequest());
     presenter=new MessagesPresenter();
     messages=presenter.initMessages(user);
     presenter.attachView(this);
        // Create grid to display messages
        Grid<Message> grid = new Grid<>(Message.class);
        grid.setItems(messages);
        grid.setColumns("message");
        grid.addItemClickListener(new ComponentEventListener<ItemClickEvent<Message>>() {
            @Override
            public void onComponentEvent(ItemClickEvent<Message> event) {
               presenter.popMessage();
                UI.getCurrent().getPage().reload();
            }
        });

        // Add grid to the layout
        add(grid);
    }
    public HttpServletRequest getRequest() {
        return ((VaadinServletRequest) VaadinRequest.getCurrent()).getHttpServletRequest();
    }

    public static void setMessages(Queue<Message> messages) {
        MessagesList.messages = messages;
    }

    public String getUser() {
        return user;
    }

    public static Queue<Message> getMessages() {
        return messages;
    }
}

