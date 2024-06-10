package Presentaion.application.View.Store;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route("store-management")
@PageTitle("Store Management")
public class StoreManagementView extends VerticalLayout {

    private Div content;

    public StoreManagementView() {
        content = new Div();
        content.setSizeFull();

        Button addProductButton = new Button("Add Product", e -> setContent(new AddProductView()));
        Button editProductDetailsButton = new Button("Edit Product Details", e -> setContent(new EditProductDetailsView()));
        // Add more buttons for other views as needed
        Button removeProductButton = new Button("remove Product Details", e -> setContent(new RemoveProductView()));
        Button addPaymentButton = new Button("add payment service", e -> setContent(new AddPaymentServiceView()));
        Button addSupplierButton = new Button("add supplier service", e -> setContent(new AddSupplierServiceView()));
        Button createDiscountButton = new Button("create Discount", e -> setContent(new CreateDiscountView()));

        VerticalLayout buttonLayout = new VerticalLayout(addProductButton, editProductDetailsButton,addPaymentButton,removeProductButton,addSupplierButton,createDiscountButton);
        buttonLayout.setSpacing(true);

        add(buttonLayout, content);
    }

    private void setContent(Component component) {
        content.removeAll();
        content.add(component);
    }
}
