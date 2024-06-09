package Presentaion.application.View.Payment;

import Presentaion.application.CookiesHandler;
import Presentaion.application.Presenter.PaymentPresenter;
import Utilities.Response;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;

import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinRequest;
import com.vaadin.flow.server.VaadinServletRequest;
import jakarta.servlet.http.HttpServletRequest;

@Route("payment")
public class PaymentPage extends VerticalLayout {
    double fee;
      PaymentPresenter presenter;
    public PaymentPage() {
        setClassName("payment-view");
       presenter=new PaymentPresenter();
       presenter.attachView(this);

        // Payment Information Title
        H1 title = new H1("Payment Information");
        title.addClassName("payment-title");

        // Credit Card Component
        Div creditCard = createCreditCardComponent();

        // Form Container
        Div formContainer = createFormContainer();

        // Adding components to the layout
        add(title, creditCard, formContainer);
    }

    private Div createCreditCardComponent() {
        Div creditCard = new Div();
        creditCard.addClassName("container");
        creditCard.addClassName("preload");

        // Implement credit card SVG here (code is omitted for brevity)

        return creditCard;
    }

    TextField cardNumberField;
    private Div createFormContainer() {
        Div formContainer = new Div();
        formContainer.addClassName("form-container");

        // Name Field
        TextField nameField = createFieldContainer("Name", "name", "text", 20, "Enter your name");
        formContainer.add(nameField);

        // Card Number Field
        cardNumberField = createFieldContainer("Card Number", "cardnumber", "text", 16, "Enter your card number");
        formContainer.add(cardNumberField);

        // Expiration Date Field
        TextField expirationField = createFieldContainer("Expiration (mm/yy)", "expirationdate", "text", 5, "Enter expiration date");
        formContainer.add(expirationField);

        // Security Code Field
        TextField securityCodeField = createFieldContainer("Security Code", "securitycode", "text", 3, "Enter security code");
        formContainer.add(securityCodeField);

        Button button=new Button("Submit");
        button.addClickListener(new ComponentEventListener<ClickEvent<Button>>() {
            @Override
            public void onComponentEvent(ClickEvent<Button> buttonClickEvent) {
              // String token= CookiesHandler.getTokenFromCookies(getRequest());
             //   String user= CookiesHandler.getUsernameFromCookies(getRequest());

                showNotification(cardNumberField.getValue());
               presenter.pay("user",20,cardNumberField.getValue(),"token");

            }
        });
        formContainer.add(button);
        return formContainer;
    }
    public HttpServletRequest getRequest() {
        return ((VaadinServletRequest) VaadinRequest.getCurrent()).getHttpServletRequest();
    }
    private TextField createFieldContainer(String label, String id, String type, int maxLength, String placeholder) {
        Div fieldContainer = new Div();
        fieldContainer.addClassName("field-container");

        // Label
      //  fieldContainer.add(new Label(label));

        // Input
        TextField inputField = new TextField();
        inputField.setId(id);
         inputField.setTitle(label);
        inputField.setMaxLength(maxLength);
        inputField.setPlaceholder(placeholder);
        inputField.setRequired(true);

        // Add input field to field container
        fieldContainer.add(inputField);

        return inputField;
    }


    public void showNotification(String message) {
        Notification.show(message, 3000, Notification.Position.MIDDLE);
    }

    public void setFee(double fee) {
        this.fee = fee;
    }

}
