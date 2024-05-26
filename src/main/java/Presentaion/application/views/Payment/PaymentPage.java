package Presentaion.application.views.Payment;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;

@Route("payment")
public class PaymentPage extends VerticalLayout {

    public PaymentPage() {
        setClassName("payment-view");

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

    private Div createFormContainer() {
        Div formContainer = new Div();
        formContainer.addClassName("form-container");

        // Name Field
        Div nameField = createFieldContainer("Name", "name", "text", 20, "Enter your name");
        formContainer.add(nameField);

        // Card Number Field
        Div cardNumberField = createFieldContainer("Card Number", "cardnumber", "text", 16, "Enter your card number");
        formContainer.add(cardNumberField);

        // Expiration Date Field
        Div expirationField = createFieldContainer("Expiration (mm/yy)", "expirationdate", "text", 5, "Enter expiration date");
        formContainer.add(expirationField);

        // Security Code Field
        Div securityCodeField = createFieldContainer("Security Code", "securitycode", "text", 3, "Enter security code");
        formContainer.add(securityCodeField);

        Button button=new Button("Submit");
        button.addClickListener(new ComponentEventListener<ClickEvent<Button>>() {
            @Override
            public void onComponentEvent(ClickEvent<Button> buttonClickEvent) {
                System.out.println("click");
            }
        });
        formContainer.add(button);
        return formContainer;
    }

    private Div createFieldContainer(String label, String id, String type, int maxLength, String placeholder) {
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

        return fieldContainer;
    }
}