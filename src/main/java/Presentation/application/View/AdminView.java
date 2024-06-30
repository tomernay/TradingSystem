package Presentation.application.View;


import Presentation.application.Presenter.AdminPresenter;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.component.button.Button;
import Presentation.application.View.UtilitiesView.WSClient;
import Utilities.Messages.Message;
import Utilities.Messages.NormalMessage;
import com.vaadin.flow.component.*;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Footer;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Header;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.dom.Element;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.VaadinRequest;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.server.VaadinServletRequest;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;


import java.net.URISyntaxException;
import java.util.*;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import Domain.Store.Inventory.ProductDTO;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import java.awt.*;

@Route(value = "admin-page", layout = MainLayoutView.class)
@StyleSheet("context://styles.css")
public class AdminView extends AppLayout  {
    private AdminPresenter presenter;
    private VerticalLayout mainContent;
    private VerticalLayout actions;


    public AdminView(AdminPresenter presenter) {
        addClassName("admin-view");
        this.presenter = presenter;
        this.presenter.attachView(this);
//        UI.getCurrent().getPage().executeJs(
//                "document.body.addEventListener('click', function() {" +
//                        "    $0.$server.handleUserAction();" +
//                        "});",
//                getElement()
//        );
        mainContent = new VerticalLayout();
        actions = new VerticalLayout();
        setContent(mainContent);
        mainContent.add(actions);
        actions.setAlignItems(FlexComponent.Alignment.CENTER);
        closeStoreButton();
        cancelSubscriptionButton();
        purchaseHistoryButton();
        systemManagerButton();
    }

    public void suspendUserButton() {
        Button suspendUserButton = new Button("Suspend User", e -> openSuspendUserDialog());
        suspendUserButton.addClassName("button");
        //set the button size
        suspendUserButton.setWidth("200px");

        actions.add(suspendUserButton);
    }

    private void openSuspendUserDialog() {
        Dialog dialog = new Dialog();
        dialog.setCloseOnEsc(false);
        dialog.setCloseOnOutsideClick(false);
        dialog.getElement().executeJs("this.$.overlay.$.overlay.style.backgroundColor = '#E6DCD3';");
        VerticalLayout dialogLayout = new VerticalLayout();
        //retrieve all open stores
        Set<String> subscribers = presenter.getAllSubscribers();
        ComboBox<String> storeComboBox = new ComboBox<>();
        storeComboBox.setItems(subscribers);
        storeComboBox.setLabel("Select subscriber to suspend:");
        storeComboBox.getElement().getStyle().set("color", "#3F352C");
        dialogLayout.add(storeComboBox);
        Button closeButton = new Button("Suspend subscriber", e -> {
            String subName = storeComboBox.getValue();
            if (subName == null) {
                Notification.show("Please select a store to close");
            } else {
                //ask for confirmation
                openSuspendConfirmationDialog(subName);

                //close store
                //presenter.closeStore(storeName);
                dialog.close();
            }
        });
        closeButton.addClassName("button");
        //center the button
        dialogLayout.setHorizontalComponentAlignment(FlexComponent.Alignment.CENTER, closeButton);
        dialogLayout.add(closeButton);
        dialog.add(dialogLayout);
        dialog.open();
    }

    private void openSuspendConfirmationDialog(String subName) {
        Dialog confirmationDialog = new Dialog();
        confirmationDialog.setCloseOnEsc(false);
        confirmationDialog.setCloseOnOutsideClick(false);
        confirmationDialog.getElement().executeJs("this.$.overlay.$.overlay.style.backgroundColor = '#E6DCD3';");
        VerticalLayout dialogLayout = new VerticalLayout();
        dialogLayout.setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.CENTER);
        dialogLayout.add(new H3("Are you sure you want to suspend " + subName + "?"));
        HorizontalLayout buttonsLayout = new HorizontalLayout();
        Button confirmButton = new Button("Yes", e -> {
            presenter.suspendSubscriber(subName);
            confirmationDialog.close();
        });
        confirmButton.addClassName("yes_button");
        Button cancelButton = new Button("No", e -> {
            confirmationDialog.close();
        });
        cancelButton.addClassName("no_button");
        buttonsLayout.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        buttonsLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);

        buttonsLayout.add(confirmButton, cancelButton);
        dialogLayout.add(buttonsLayout);
        confirmationDialog.add(dialogLayout);
        confirmationDialog.open();
    }

    public void cancelSuspensionButton() {
        Button cancelSuspensionButton = new Button("Cancel Suspension", e -> openCancelSuspensionDialog());
        cancelSuspensionButton.addClassName("button");
        //set the button size
        cancelSuspensionButton.setWidth("200px");

        actions.add(cancelSuspensionButton);
    }

    private void openCancelSuspensionDialog() {
        Dialog dialog = new Dialog();
        dialog.setCloseOnEsc(false);
        dialog.setCloseOnOutsideClick(false);
        dialog.getElement().executeJs("this.$.overlay.$.overlay.style.backgroundColor = '#E6DCD3';");
        VerticalLayout dialogLayout = new VerticalLayout();
        //retrieve all open stores
        Set<String> subscribers = presenter.getAllSubscribers();
        ComboBox<String> storeComboBox = new ComboBox<>();
        storeComboBox.setItems(subscribers);
        storeComboBox.setLabel("Select subscriber to cancel suspension:");
        storeComboBox.getElement().getStyle().set("color", "#3F352C");
        dialogLayout.add(storeComboBox);
        Button closeButton = new Button("Cancel suspension", e -> {
            String subName = storeComboBox.getValue();
            if (subName == null) {
                Notification.show("Please select a store to close");
            } else {
                //ask for confirmation
                presenter.cancelSuspension(subName);

                //close store
                //presenter.closeStore(storeName);
                dialog.close();
            }
        });
        closeButton.addClassName("button");
        //center the button
        dialogLayout.setHorizontalComponentAlignment(FlexComponent.Alignment.CENTER, closeButton);
        dialogLayout.add(closeButton);
        dialog.add(dialogLayout);
        dialog.open();
    }

    public void purchaseHistoryButton() {
        Button purchaseHistoryButton = new Button("Purchase History");
        purchaseHistoryButton.addClassName("button");
        //set the button size
        purchaseHistoryButton.setWidth("200px");

        actions.add(purchaseHistoryButton);
    }

    public void systemManagerButton() {
        Button systemManagerButton = new Button("System Management");
        systemManagerButton.addClassName("button");
        //set the button size
        systemManagerButton.setWidth("200px");

        actions.add(systemManagerButton);
    }

    public void closeStoreButton() {
        Button closeStoreButton = new Button("Close Store", e -> openCloseStoreDialog());
        closeStoreButton.addClassName("button");
        //set the button size
        closeStoreButton.setWidth("200px");

        actions.add(closeStoreButton);
    }

    private void openCloseStoreDialog() {
        Dialog dialog = new Dialog();
        dialog.setCloseOnEsc(false);
        dialog.setCloseOnOutsideClick(false);
        dialog.getElement().executeJs("this.$.overlay.$.overlay.style.backgroundColor = '#E6DCD3';");
        VerticalLayout dialogLayout = new VerticalLayout();
        //retrieve all open stores
        List<String> openStores = presenter.getOpenStores();
        ComboBox<String> storeComboBox = new ComboBox<>();
        storeComboBox.setItems(openStores);
        storeComboBox.setLabel("Select Store to Close");
        storeComboBox.getElement().getStyle().set("color", "#3F352C");
        dialogLayout.add(storeComboBox);
        Button closeButton = new Button("Close Store", e -> {
            String storeName = storeComboBox.getValue();
            if (storeName == null) {
                Notification.show("Please select a store to close");
            } else {
                //ask for confirmation
                openClosingConfirmationDialog(storeName);

                //close store
                //presenter.closeStore(storeName);
                dialog.close();
            }
        });
        closeButton.addClassName("button");
        //center the button
        dialogLayout.setHorizontalComponentAlignment(FlexComponent.Alignment.CENTER, closeButton);
        dialogLayout.add(closeButton);
        dialog.add(dialogLayout);
        dialog.open();


    }

    private void openClosingConfirmationDialog(String storeName) {
        Dialog confirmationDialog = new Dialog();
        confirmationDialog.setCloseOnEsc(false);
        confirmationDialog.setCloseOnOutsideClick(false);
        confirmationDialog.getElement().executeJs("this.$.overlay.$.overlay.style.backgroundColor = '#E6DCD3';");
        VerticalLayout dialogLayout = new VerticalLayout();
        dialogLayout.setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.CENTER);
        dialogLayout.add(new H3("Are you sure you want to close " + storeName + "?"));
        HorizontalLayout buttonsLayout = new HorizontalLayout();
        Button confirmButton = new Button("Yes", e -> {
            presenter.closeStore(storeName);
            confirmationDialog.close();
        });
        confirmButton.addClassName("yes_button");
        Button cancelButton = new Button("No", e -> {
            confirmationDialog.close();
        });
        cancelButton.addClassName("no_button");
        buttonsLayout.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        buttonsLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);

        buttonsLayout.add(confirmButton, cancelButton);
        dialogLayout.add(buttonsLayout);
        confirmationDialog.add(dialogLayout);
        confirmationDialog.open();
    }

    public void cancelSubscriptionButton() {
        Button cancelSubscribtionButton = new Button("Cancel Subscribtion", e -> openCancelSubscriptionDialog());
        cancelSubscribtionButton.addClassName("button");
        //set the button size
        cancelSubscribtionButton.setWidth("200px");

        actions.add(cancelSubscribtionButton);
    }

    private void openCancelSubscriptionDialog() {
        Dialog dialog = new Dialog();
        dialog.setCloseOnEsc(false);
        dialog.setCloseOnOutsideClick(false);
        dialog.getElement().executeJs("this.$.overlay.$.overlay.style.backgroundColor = '#E6DCD3';");
        VerticalLayout dialogLayout = new VerticalLayout();
        //retrieve all open stores
        Set<String> subscribers = presenter.getAllSubscribers();
        ComboBox<String> storeComboBox = new ComboBox<>();
        storeComboBox.setItems(subscribers);
        storeComboBox.setLabel("Select subscriber to remove:");
        storeComboBox.getElement().getStyle().set("color", "#3F352C");
        dialogLayout.add(storeComboBox);
        Button closeButton = new Button("Remove subscriber", e -> {
            String subName = storeComboBox.getValue();
            if (subName == null) {
                Notification.show("Please select a store to close");
            } else {
                //ask for confirmation
                openRemovingConfirmationDialog(subName);

                //close store
                //presenter.closeStore(storeName);
                dialog.close();
            }
        });
        closeButton.addClassName("button");
        //center the button
        dialogLayout.setHorizontalComponentAlignment(FlexComponent.Alignment.CENTER, closeButton);
        dialogLayout.add(closeButton);
        dialog.add(dialogLayout);
        dialog.open();
    }

    private void openRemovingConfirmationDialog(String subName) {
        Dialog confirmationDialog = new Dialog();
        confirmationDialog.setCloseOnEsc(false);
        confirmationDialog.setCloseOnOutsideClick(false);
        confirmationDialog.getElement().executeJs("this.$.overlay.$.overlay.style.backgroundColor = '#E6DCD3';");
        VerticalLayout dialogLayout = new VerticalLayout();
        dialogLayout.setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.CENTER);
        dialogLayout.add(new H3("Are you sure you want to remove " + subName + "?"));
        HorizontalLayout buttonsLayout = new HorizontalLayout();
        Button confirmButton = new Button("Yes", e -> {
            presenter.removeSubscriber(subName);
            confirmationDialog.close();
        });
        confirmButton.addClassName("yes_button");
        Button cancelButton = new Button("No", e -> {
            confirmationDialog.close();
        });
        cancelButton.addClassName("no_button");
        buttonsLayout.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        buttonsLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);

        buttonsLayout.add(confirmButton, cancelButton);
        dialogLayout.add(buttonsLayout);
        confirmationDialog.add(dialogLayout);
        confirmationDialog.open();
    }


}
