package Presentaion.application.View.Messages;


import Utilities.Messages.Message;
import Utilities.Messages.NormalMessage;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

import java.util.ArrayList;
import java.util.List;

@Route("MessagesList")
public class MessagesList extends VerticalLayout {

    private List<Message> messages = new ArrayList<>();

    public MessagesList() {
        setClassName("list-view");
        this.messages = new ArrayList<>();
        messages.add(new NormalMessage("yssysys"));
        messages.add(new NormalMessage("tttttt"));
        // Create grid to display messages
        Grid<Message> grid = new Grid<>(Message.class);
        grid.setItems(messages);
        grid.setColumns("message");

        // Add grid to the layout
        add(grid);
    }

    public void loadMessages(List<Message> messages){
        this.messages=messages;
    }


}

