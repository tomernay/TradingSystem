package Presentaion.views;

import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import org.springframework.stereotype.Component;

@Route("")
@Component
public class RootView extends VerticalLayout {

    public RootView() {
        // Constructor can be used for initializing components
        init();
    }

    private void init() {
        // Example initialization code
        add(new H1("Welcome to My Vaadin Application"));
        add("This is the root view.");
    }
}
