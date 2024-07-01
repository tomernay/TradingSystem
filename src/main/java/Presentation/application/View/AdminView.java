package Presentation.application.View;


import Presentation.application.Presenter.AdminPresenter;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.grid.Grid;
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
import java.time.LocalDate;
import java.time.ZoneId;
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
    private List<Button> actions;


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
        actions = new ArrayList<>();
        setContent(mainContent);
        //mainContent.add(actions);
        //actions.setAlignItems(FlexComponent.Alignment.CENTER);
//        closeStoreButton();
//        cancelSubscriptionButton();
        purchaseHistoryButton();
//        systemManagerButton();
        suspendUserButton();
        cancelSuspensionButton();
        watchSuspensionsButton();

        organizeActions();

    }

    private void organizeActions() {
        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setSpacing(true); // Optional: Set spacing between components
        verticalLayout.setAlignItems(FlexComponent.Alignment.CENTER); // Center align the rows within the vertical layout


        for (int i = 0; i < actions.size(); i += 4) {
            HorizontalLayout row = new HorizontalLayout();
            row.setWidthFull(); // Ensure row takes full width
            row.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER); // Center align the buttons within the row


            // Add up to 4 buttons per row
            for (int j = 0; j < 4 && (i + j) < actions.size(); j++) {
                Button actionButton = actions.get(i + j);
                actionButton.addClassName("button");

                // Set the width and height of the button
                actionButton.setWidth("25%");
                actionButton.setHeight("100px");

                row.add(actionButton);
            }

            // Add the row to the vertical layout
            verticalLayout.add(row);
        }

        // Add the vertical layout to the main content area (assuming mainContent is a VerticalLayout or similar)
        mainContent.add(verticalLayout);
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
        dialog.setCloseOnEsc(true);
        dialog.setCloseOnOutsideClick(true);
        dialog.getElement().executeJs("this.$.overlay.$.overlay.style.backgroundColor = '#E6DCD3';");
        VerticalLayout dialogLayout = new VerticalLayout();

        Set<String> subscribers = presenter.getAllSubscribers();
        ComboBox<String> subscriberComboBox = new ComboBox<>();
        subscriberComboBox.addClassName("custom-context-menu");
        subscriberComboBox.setItems(subscribers);
        subscriberComboBox.setLabel("Select subscriber to suspend:");
        subscriberComboBox.getElement().getStyle().set("color", "#3F352C");
        dialogLayout.add(subscriberComboBox);

        DatePicker endDatePicker = new DatePicker();
        endDatePicker.setLabel("Select end date:");
        endDatePicker.getElement().getStyle().set("color", "#3F352C");
        dialogLayout.add(endDatePicker);

        Button suspendButton = new Button("Suspend subscriber", e -> {
            String subName = subscriberComboBox.getValue();
            LocalDate localEndDate = endDatePicker.getValue();
            if (subName == null) {
                Notification.show("Please select a subscriber to suspend");
            } else if (localEndDate == null) {
                Notification.show("Please select an end date for the suspension");
            } else {
                Date endDate = Date.from(localEndDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
                openSuspendConfirmationDialog(subName, endDate);
                dialog.close();
            }
        });
        suspendButton.addClassName("button");
        dialogLayout.setHorizontalComponentAlignment(FlexComponent.Alignment.CENTER, suspendButton);
        dialogLayout.add(suspendButton);
        dialog.add(dialogLayout);
        dialog.open();
    }

    private void openSuspendConfirmationDialog(String subName, Date endDate) {
        Dialog confirmationDialog = new Dialog();
        confirmationDialog.setCloseOnEsc(true);
        confirmationDialog.setCloseOnOutsideClick(true);
        confirmationDialog.getElement().executeJs("this.$.overlay.$.overlay.style.backgroundColor = '#E6DCD3';");
        VerticalLayout dialogLayout = new VerticalLayout();
        dialogLayout.setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.CENTER);
        dialogLayout.add(new H3("Are you sure you want to suspend " + subName + " until " + endDate + "?"));
        HorizontalLayout buttonsLayout = new HorizontalLayout();
        Button confirmButton = new Button("Yes", e -> {
            boolean success = presenter.suspendSubscriber(subName, endDate);
            if (success) {
                Notification.show("User suspended successfully");
            } else {
                Notification.show("Failed to suspend user");
            }
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
        dialog.setCloseOnEsc(true);
        dialog.setCloseOnOutsideClick(true);
        dialog.setWidth("600px");
        dialog.setHeight("400px");
        dialog.getElement().executeJs("this.$.overlay.$.overlay.style.backgroundColor = '#E6DCD3';");
        VerticalLayout dialogLayout = new VerticalLayout();

        // Retrieve the suspension list
        Map<String, Date> suspensionList = presenter.getSuspensionList();

        Grid<Map.Entry<String, Date>> grid = new Grid<>();
        grid.addClassName("custom-grid");
        grid.setItems(suspensionList.entrySet());
        grid.addColumn(entry -> entry.getKey()).setHeader("Subscriber ID").setFlexGrow(1).setWidth("50%");
        grid.addColumn(entry -> entry.getValue().toString()).setHeader("Suspension End Date").setFlexGrow(1).setWidth("50%");

        dialogLayout.add(grid);

        Button cancelButton = new Button("Cancel suspension", e -> {
            Map.Entry<String, Date> selectedEntry = grid.asSingleSelect().getValue();
            if (selectedEntry == null) {
                Notification.show("Please select a suspended subscriber to cancel suspension");
            } else {
                boolean success = presenter.cancelSuspension(selectedEntry.getKey());
                if (success) {
                    Notification.show("Suspension canceled successfully");
                } else {
                    Notification.show("Failed to cancel suspension");
                }
                dialog.close();
            }
        });
        cancelButton.addClassName("button");
        dialogLayout.setHorizontalComponentAlignment(FlexComponent.Alignment.CENTER, cancelButton);
        dialogLayout.add(cancelButton);
        dialog.add(dialogLayout);
        dialog.open();
    }

    public void watchSuspensionsButton() {
        Button watchSuspensionsButton = new Button("Watch Suspended Users", e -> openWatchSuspensionsDialog());
        watchSuspensionsButton.addClassName("button");
        watchSuspensionsButton.setWidth("200px");

        actions.add(watchSuspensionsButton);
    }

    private void openWatchSuspensionsDialog() {
        Dialog dialog = new Dialog();
        dialog.setCloseOnEsc(true);
        dialog.setCloseOnOutsideClick(true);
        dialog.setWidth("600px");
        dialog.setHeight("400px");
        dialog.getElement().executeJs("this.$.overlay.$.overlay.style.backgroundColor = '#E6DCD3';");
        VerticalLayout dialogLayout = new VerticalLayout();

        // Retrieve the suspension list
        Map<String, Date> suspensionList = presenter.getSuspensionList();

        Grid<Map.Entry<String, Date>> grid = new Grid<>();
        grid.addClassName("custom-grid");
        grid.setItems(suspensionList.entrySet());
        grid.addColumn(entry -> entry.getKey()).setHeader("Subscriber ID").setFlexGrow(1).setWidth("50%");
        grid.addColumn(entry -> entry.getValue().toString()).setHeader("Suspension End Date").setFlexGrow(1).setWidth("50%");

        dialogLayout.add(grid);

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
        dialog.setCloseOnEsc(true);
        dialog.setCloseOnOutsideClick(true);
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
        confirmationDialog.setCloseOnEsc(true);
        confirmationDialog.setCloseOnOutsideClick(true);
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
        dialog.setCloseOnEsc(true);
        dialog.setCloseOnOutsideClick(true);
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
        confirmationDialog.setCloseOnEsc(true);
        confirmationDialog.setCloseOnOutsideClick(true);
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
