package Presentaion.views;

import Presentaion.AppContext;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import org.apache.catalina.core.ApplicationContext;
import org.springframework.beans.factory.annotation.Autowired;

@Route("")
public class MainView extends VerticalLayout {

    private AppContext applicationContext;

    @Autowired
    public MainView(AppContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public void someMethod() {
        // Example of using the application context
    //    MainView mainView = applicationContext.(MainView.class);
        // Now you can use mainView or any other beans obtained from the context
    }
}