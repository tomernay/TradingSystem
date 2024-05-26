package Presentaion;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.Lumo;

@Route("")
public class MainView extends VerticalLayout {

    public MainView() {
        TextField textField = new TextField("Your name");
        Button button = new Button("Say hello",
                e -> Notification.show("Hello, " + textField.getValue() + "!"));
        button.addClassName("my-button");
        add(textField, button);
    }
}
