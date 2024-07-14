package Presentation.application.View;

import Presentation.application.CookiesHandler;
import Presentation.application.Presenter.MessagesPresenter;
import Utilities.Messages.Message;
import Utilities.Messages.nominateManagerMessage;
import Utilities.Messages.nominateOwnerMessage;
import com.vaadin.flow.component.ClientCallable;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.ItemClickEvent;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinRequest;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.server.VaadinServletRequest;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.List;

@Route(value = "MessagesList", layout = MainLayoutView.class)
@StyleSheet("context://styles.css")
public class MessagesList extends VerticalLayout implements BeforeEnterObserver {

    static private List<Message> messages = new ArrayList<>();
    String user;
    MessagesPresenter presenter;


    private Grid<Message> grid;

    public MessagesList(MessagesPresenter presenter) {
        setClassName("page-view");

        user = CookiesHandler.getUsernameFromCookies(getRequest());
        this.presenter = presenter;
        this.presenter.attachView(this);
        messages = this.presenter.initMessages(user);

        UI ui = UI.getCurrent();


        // Create header
        H1 header = new H1("Your Messages");
        header.getStyle().set("text-align", "center");

        // Create grid to display messages
        grid = new Grid<>(Message.class);
        grid.addClassName("custom-grid");
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
        refreshButton.addClassName("button");
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

        UI.getCurrent().getPage().executeJs(
                "document.body.addEventListener('click', function() {" +
                        "    $0.$server.handleUserAction();" +
                        "});",
                getElement()
        );

    }

    @ClientCallable
    public void handleUserAction() {
        if (!presenter.isLoggedIn() || !isLoggedIn()) {
            Notification.show("Token has timed out! Navigating you to login page...");
            UI.getCurrent().navigate(LoginView.class);
        }
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if (!presenter.isLoggedIn() || !isLoggedIn()) {
            event.rerouteTo(LoginView.class);
        }
    }

    private boolean isLoggedIn() {
        // Retrieve the current HTTP request
        HttpServletRequest request = (HttpServletRequest) VaadinService.getCurrentRequest();

        if (request != null) {
            // Retrieve cookies from the request
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if ("token".equals(cookie.getName())) {
                        // Assuming a valid token indicates a logged-in user
                        return isValidToken(cookie.getValue());
                    }
                }
            }
        }

        // If no valid token is found, the user is not logged in
        return false;
    }

    private boolean isValidToken(String token) {
        return token != null && !token.isEmpty();
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
        Button removeButton = new Button("Remove", e -> navigateToMessageRemoving(message.getId()));
        removeButton.addClassName("button");
        return new HorizontalLayout(removeButton);
    }

    private void navigateToMessageRemoving(Integer messageID) {
        Dialog confirmationDialog = new Dialog();
        confirmationDialog.getElement().executeJs("this.$.overlay.$.overlay.style.backgroundColor = '#E6DCD3';");
        confirmationDialog.setWidth("400px");

        VerticalLayout dialogLayout = new VerticalLayout();
        dialogLayout.setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        dialogLayout.add("Are you sure you want to remove this message?");

        Button confirmButton = new Button("Yes", e -> {
            presenter.removeMessageById(messageID);
            reloadMessages();
            confirmationDialog.close();
            showSuccess("Message removed successfully");
        });
        confirmButton.addClassName("yes_button");

        Button cancelButton = new Button("No", e -> confirmationDialog.close());
        cancelButton.addClassName("no_button");

        HorizontalLayout buttonLayout = new HorizontalLayout(cancelButton, confirmButton);
        buttonLayout.setDefaultVerticalComponentAlignment(Alignment.CENTER);
        buttonLayout.setJustifyContentMode(JustifyContentMode.END); // Align buttons to the right

        dialogLayout.add(buttonLayout);
        confirmationDialog.add(dialogLayout);
        confirmationDialog.open();
    }

    public void showSuccess(String message) {
        Notification.show(message, 3000, Notification.Position.MIDDLE);
    }

    private HorizontalLayout createRequestButtons(Message message) {
        Button acceptButton = new Button("Accept", e -> {
            presenter.handleRequest(message, true);
            reloadMessages();
        });
        acceptButton.addClassName("yes_button");
        Button rejectButton = new Button("Reject", e -> {
            presenter.handleRequest(message, false);
            reloadMessages();
        });
        rejectButton.addClassName("no_button");
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
