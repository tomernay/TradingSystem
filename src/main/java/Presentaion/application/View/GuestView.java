package Presentaion.application.View;

import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route("guest")
@PageTitle("Guest | Vaadin CRM")
public class GuestView extends Span {

    public GuestView() {
        setText("Welcome, Guest!");
    }
}