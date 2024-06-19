package Presentation.application.View;

import Presentation.application.CookiesHandler;
import Presentation.application.Presenter.MessagesPresenter;
import Presentation.application.View.UtilitiesView.WSClient;
import Utilities.Messages.Message;
import Utilities.Messages.nominateManagerMessage;
import Utilities.Messages.nominateOwnerMessage;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.ItemClickEvent;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinRequest;
import com.vaadin.flow.server.VaadinServletRequest;
import jakarta.servlet.http.HttpServletRequest;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

@Route(value = "MessagesList", layout = MainLayoutView.class)
@StyleSheet("context://login-view-styles.css")
public class MessagesList extends VerticalLayout {

    static private List<Message> messages = new ArrayList<>();
    String user;
    MessagesPresenter presenter;
    WSClient wsClient;

    private Grid<Message> grid;

    public MessagesList(MessagesPresenter presenter) {
        setClassName("messages-view");

        user = CookiesHandler.getUsernameFromCookies(getRequest());
        this.presenter = presenter;
        this.presenter.attachView(this);
        messages = this.presenter.initMessages(user);

        UI ui = UI.getCurrent();
        try {
            wsClient = new WSClient(ui, user);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        wsClient.setOnMessageListener(message -> {
            String[] m = message.split(":");

            if (m[0].equals(user)) {
                ui.access(() -> Notification.show("New message: " + message));
                reloadMessages();
            }
        });

        // Create header
        H1 header = new H1("Your Messages");
        header.getStyle().set("text-align", "center");

        // Create grid to display messages
        grid = new Grid<>(Message.class);
        grid.setItems(messages);
        grid.removeColumnByKey("read");
        grid.removeColumnByKey("id");
        grid.removeColumnByKey("message");
        grid.removeColumnByKey("content");

        Grid.Column<Message> messageColumn = grid.addColumn(Message::getContent).setHeader("Message").setAutoWidth(true);
        Grid.Column<Message> statusColumn = grid.addColumn(message -> message.isRead() ? "Read" : "Unread").setHeader("Status").setAutoWidth(true);
        Grid.Column<Message> actionsColumn = grid.addComponentColumn(message -> {
            if (message instanceof nominateOwnerMessage || message instanceof nominateManagerMessage) {
                return createRequestButtons(message);
            } else {
                return createRemoveButton(message);
            }
        }).setHeader("Actions").setAutoWidth(true);

        // Add item click listener to mark as read
        grid.addItemClickListener((ComponentEventListener<ItemClickEvent<Message>>) event -> {
            Message message = event.getItem();
            message.setRead(true);
            grid.getDataProvider().refreshItem(message);
            decrementNotificationCount();
        });

        // Add a class name generator to highlight unread messages
        grid.setClassNameGenerator(message -> message.isRead() ? "" : "unread-message");

        // Create a refresh button
        Button refreshButton = new Button("Refresh", e -> reloadMessages());
        refreshButton.getStyle().set("margin", "10px");

        // Create a layout for header and refresh button
        HorizontalLayout headerLayout = new HorizontalLayout(header, refreshButton);
        headerLayout.setAlignItems(Alignment.BASELINE);
        headerLayout.setWidthFull();
        header.getStyle().set("flex-grow", "1");

        // Add components to the layout
        add(headerLayout, grid);
        setSizeFull(); // Make the VerticalLayout take the full size of the view

        // Apply styles to the headers
        grid.getElement().executeJs("this.shadowRoot.querySelectorAll('vaadin-grid-cell-content')[0].classList.add('big-header');");
        grid.getElement().executeJs("this.shadowRoot.querySelectorAll('vaadin-grid-cell-content')[1].classList.add('small-header');");
        grid.getElement().executeJs("this.shadowRoot.querySelectorAll('vaadin-grid-cell-content')[2].classList.add('small-header');");
    }

    private void decrementNotificationCount() {
        // Find the MainLayoutView instance
        MainLayoutView mainLayout = (MainLayoutView) UI.getCurrent().getChildren()
                .filter(component -> component instanceof MainLayoutView)
                .findFirst()
                .orElse(null);

        if (mainLayout != null) {
            mainLayout.decrementNotificationCount();
        }
    }

    private void reloadMessages() {
        messages = this.presenter.initMessages(user);
        grid.setItems(messages);
    }

    private HorizontalLayout createRemoveButton(Message message) {
        Button removeButton = new Button("Remove", e -> {
            presenter.removeMessageById(message.getId());
            reloadMessages();
        });
        return new HorizontalLayout(removeButton);
    }

    private HorizontalLayout createRequestButtons(Message message) {
        Button acceptButton = new Button("Accept", e -> {
            presenter.handleRequest(message, true);
            reloadMessages();
        });
        Button rejectButton = new Button("Reject", e -> {
            presenter.handleRequest(message, false);
            reloadMessages();
        });
        return new HorizontalLayout(acceptButton, rejectButton);
    }

    public HttpServletRequest getRequest() {
        return ((VaadinServletRequest) VaadinRequest.getCurrent()).getHttpServletRequest();
    }

    public static void setMessages(List<Message> messages) {
        MessagesList.messages = messages;
    }

    public String getUser() {
        return user;
    }

    public static List<Message> getMessages() {
        return messages;
    }
}
