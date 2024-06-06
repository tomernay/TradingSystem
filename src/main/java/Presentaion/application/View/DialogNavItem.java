package Presentaion.application.View;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.sidenav.SideNavItem;

public class DialogNavItem extends SideNavItem {

    public DialogNavItem(String label) {
        super(label);

        // Add click listener to open the dialog
//        addClickListener(e -> openDialog());
    }

    private void openDialog() {
        Dialog dialog = new Dialog();
        dialog.setWidth("400px");
        dialog.setHeight("300px");

        VerticalLayout dialogLayout = new VerticalLayout();
        dialogLayout.add(new Span("This is a dialog"));
        Button closeDialogButton = new Button("Close", e -> dialog.close());
        dialogLayout.add(closeDialogButton);

        dialog.add(dialogLayout);
        dialog.open();
    }
}
