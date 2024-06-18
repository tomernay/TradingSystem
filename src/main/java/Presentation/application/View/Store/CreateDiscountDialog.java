package Presentation.application.View.Store;

import Domain.Store.Conditions.ConditionDTO;
import Domain.Store.Discounts.DiscountDTO;
import Domain.Store.Inventory.ProductDTO;
import Presentation.application.Presenter.StoreManagementPresenter;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.dnd.DragSource;
import com.vaadin.flow.component.dnd.DropEffect;
import com.vaadin.flow.component.dnd.DropTarget;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.TextRenderer;
import java.util.ArrayList;
import java.util.List;

public class CreateDiscountDialog extends Dialog {
    private StoreManagementPresenter presenter;
    private String storeId;
    private List<DiscountBox> discountBoxes = new ArrayList<>();
    private List<PolicyBox> policyBoxes = new ArrayList<>();
    private VerticalLayout discountCanvas = new VerticalLayout();
    private VerticalLayout policyCanvas = new VerticalLayout();
    private List<ProductDTO> products;

    public CreateDiscountDialog(StoreManagementPresenter presenter, String storeId, List<ProductDTO> products) {
        this.presenter = presenter;
        this.storeId = storeId;
        this.products = products;
        setWidth("100%");
        setHeight("100%");

        VerticalLayout layout = new VerticalLayout();
        layout.setSpacing(true);
        if (presenter.hasPermission(storeId, "MANAGE_DISCOUNTS_POLICIES")) {
            // Create and style buttons
            Button simpleDiscountButton = new Button("Create Simple Discount", e -> openSimpleDiscountForm());
            Button simplePolicyButton = new Button("Create Simple Policy", e -> openSimplePolicyForm());
            Button removeDiscountButton = new Button("Remove Discount", e -> openRemoveDiscountDialog());
            Button removePolicyButton = new Button("Remove Policy", e -> openRemovePolicyDialog());

            VerticalLayout createButtonsLayout = new VerticalLayout(simpleDiscountButton, simplePolicyButton);
            VerticalLayout removeButtonsLayout = new VerticalLayout(removeDiscountButton, removePolicyButton);

            HorizontalLayout buttonLayout = new HorizontalLayout();
            buttonLayout.setWidthFull();
            buttonLayout.setSpacing(true);
            buttonLayout.add(createButtonsLayout, removeButtonsLayout);
            buttonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
            layout.add(buttonLayout);
        }

        // Titles
        H3 policiesTitle = new H3("Policies:");
        H3 discountsTitle = new H3("Discounts:");

        // Add layouts to main layout
        layout.add(policiesTitle, policyCanvas, discountsTitle, discountCanvas);
        discountCanvas.setSizeFull();
        discountCanvas.getStyle().set("border", "1px solid black");
        discountCanvas.getStyle().set("overflow", "auto"); // Make canvas scrollable

        policyCanvas.setSizeFull();
        policyCanvas.getStyle().set("border", "1px solid black");
        policyCanvas.getStyle().set("overflow", "auto"); // Make canvas scrollable

        add(layout);

        loadAllBoxes(); // Load all existing boxes when entering the page
    }

    private void openRemoveDiscountDialog() {
        Dialog removeDiscountDialog = new Dialog();
        removeDiscountDialog.setWidth("400px");

        FormLayout formLayout = new FormLayout();

        ComboBox<DiscountBox> discountComboBox = new ComboBox<>("Discount");
        discountComboBox.setItems(discountBoxes);
        discountComboBox.setRenderer(new TextRenderer<>(discountBox -> {
            Div div = new Div();
            div.setText(discountBox.getDetailedInfo());
            div.getStyle().set("white-space", "pre-wrap"); // Ensure text wraps properly
            return div.getElement().getText();
        }));

        Button removeButton = new Button("Remove", e -> {
            DiscountBox selectedDiscount = discountComboBox.getValue();
            if (selectedDiscount != null) {
                presenter.removeDiscount(storeId, selectedDiscount.toDTO());
                loadAllBoxes();
                removeDiscountDialog.close();
            }
        });

        Button cancelButton = new Button("Cancel", e -> removeDiscountDialog.close());

        formLayout.add(discountComboBox, removeButton, cancelButton);
        removeDiscountDialog.add(formLayout);
        removeDiscountDialog.open();
    }

    private void openRemovePolicyDialog() {
        Dialog removePolicyDialog = new Dialog();
        removePolicyDialog.setWidth("400px");

        FormLayout formLayout = new FormLayout();

        ComboBox<PolicyBox> policyComboBox = new ComboBox<>("Policy");
        policyComboBox.setItems(policyBoxes);
        policyComboBox.setRenderer(new TextRenderer<>(policyBox -> {
            Div div = new Div();
            div.setText(policyBox.getDetailedInfo());
            div.getStyle().set("white-space", "pre-wrap"); // Ensure text wraps properly
            return div.getElement().getText();
        }));

        Button removeButton = new Button("Remove", e -> {
            PolicyBox selectedPolicy = policyComboBox.getValue();
            if (selectedPolicy != null) {
                presenter.removePolicy(storeId, selectedPolicy.toDTO());
                loadAllBoxes();
                removePolicyDialog.close();
            }
        });

        Button cancelButton = new Button("Cancel", e -> removePolicyDialog.close());

        formLayout.add(policyComboBox, removeButton, cancelButton);
        removePolicyDialog.add(formLayout);
        removePolicyDialog.open();
    }

    public void loadAllBoxes() {
        discountCanvas.removeAll();
        policyCanvas.removeAll();
        policyBoxes.clear();
        discountBoxes.clear();
        // Assuming we have functions in the presenter to load all discounts and policies
        List<DiscountDTO> loadedDiscounts = presenter.loadDiscounts(storeId);
        for (DiscountDTO discount : loadedDiscounts) {
            DiscountBox discountBox = new DiscountBox(presenter, this, storeId, discount);
            addDiscountBoxToCanvas(discountBox);
        }
        List<ConditionDTO> loadedPolicies = presenter.loadPolicies(storeId);
        for (ConditionDTO policy : loadedPolicies) {
            PolicyBox policyBox = new PolicyBox(presenter, this, storeId, policy);
            addPolicyBoxToCanvas(policyBox);
        }
    }

    private void openSimpleDiscountForm() {
        Dialog simpleDiscountDialog = new Dialog();
        simpleDiscountDialog.setWidth("400px");

        FormLayout formLayout = new FormLayout();

        ComboBox<ProductDTO> productComboBox = new ComboBox<>("Product");
        productComboBox.setItems(products);
        productComboBox.setItemLabelGenerator(ProductDTO::getName);

        TextField categoryField = new TextField("Category");

        productComboBox.addValueChangeListener(event -> {
            if (event.getValue() != null) {
                categoryField.setEnabled(false);
            } else {
                categoryField.setEnabled(true);
            }
        });

        categoryField.addValueChangeListener(event -> {
            if (!event.getValue().isEmpty()) {
                productComboBox.setEnabled(false);
            } else {
                productComboBox.setEnabled(true);
            }
        });

        NumberField discountPercentField = new NumberField("Discount Percent");

        Button saveButton = new Button("Create", e -> {
            ProductDTO selectedProduct = productComboBox.getValue();
            String productId = selectedProduct != null ? Integer.toString(selectedProduct.getProductID()) : null;
            String category = categoryField.getValue();
            if (category.isEmpty()) {
                category = null;
            }
            double discountPercent = discountPercentField.getValue();
            presenter.saveDiscount(storeId, "Simple", productId, category, discountPercent, selectedProduct != null ? selectedProduct.getName() : null, null, null, null, null); // Save discount
            loadAllBoxes(); // Reload all boxes
            simpleDiscountDialog.close();
        });

        Button cancelButton = new Button("Cancel", e -> simpleDiscountDialog.close());

        formLayout.add(productComboBox, categoryField, discountPercentField, saveButton, cancelButton);
        simpleDiscountDialog.add(formLayout);
        simpleDiscountDialog.open();
    }

    private void openSimplePolicyForm() {
        Dialog simplePolicyDialog = new Dialog();
        simplePolicyDialog.setWidth("400px");

        FormLayout formLayout = new FormLayout();

        ComboBox<String> policyTypeComboBox = new ComboBox<>("Policy Type");
        policyTypeComboBox.setItems("Category", "Product", "Price");

        ComboBox<String> quantityTypeComboBox = new ComboBox<>("Quantity Type");
        quantityTypeComboBox.setItems("At least", "At most", "Exactly", "Between");

        TextField categoryField = new TextField("Category");
        ComboBox<ProductDTO> productComboBox = new ComboBox<>("Product");
        productComboBox.setItems(products);
        productComboBox.setItemLabelGenerator(ProductDTO::getName);

        NumberField quantityField = new NumberField("Quantity");
        NumberField minQuantityField = new NumberField("Min Quantity");
        NumberField maxQuantityField = new NumberField("Max Quantity");
        NumberField priceField = new NumberField("Price");
        NumberField minPriceField = new NumberField("Min Price");
        NumberField maxPriceField = new NumberField("Max Price");

        // Initially hide all fields
        categoryField.setVisible(false);
        productComboBox.setVisible(false);
        quantityField.setVisible(false);
        minQuantityField.setVisible(false);
        maxQuantityField.setVisible(false);
        priceField.setVisible(false);
        minPriceField.setVisible(false);
        maxPriceField.setVisible(false);

        policyTypeComboBox.addValueChangeListener(event -> {
            String selectedType = event.getValue();
            boolean isCategory = "Category".equals(selectedType);
            boolean isProduct = "Product".equals(selectedType);
            boolean isPrice = "Price".equals(selectedType);

            categoryField.setVisible(isCategory);
            productComboBox.setVisible(isProduct);
            quantityTypeComboBox.setVisible(isCategory || isProduct || isPrice);

            // Reset visibility based on policy type
            quantityField.setVisible(false);
            minQuantityField.setVisible(false);
            maxQuantityField.setVisible(false);
            priceField.setVisible(false);
            minPriceField.setVisible(false);
            maxPriceField.setVisible(false);

            if (isPrice) {
                handleQuantityTypeSelection(quantityTypeComboBox.getValue(), isPrice, quantityField, minQuantityField, maxQuantityField, priceField, minPriceField, maxPriceField);
            } else {
                handleQuantityTypeSelection(quantityTypeComboBox.getValue(), false, quantityField, minQuantityField, maxQuantityField, priceField, minPriceField, maxPriceField);
            }
        });

        quantityTypeComboBox.addValueChangeListener(event -> {
            String selectedType = event.getValue();
            handleQuantityTypeSelection(selectedType, "Price".equals(policyTypeComboBox.getValue()), quantityField, minQuantityField, maxQuantityField, priceField, minPriceField, maxPriceField);
        });

        Button saveButton = new Button("Create", e -> {
            String policyType = policyTypeComboBox.getValue();
            String quantityType = quantityTypeComboBox.getValue();
            String category = categoryField.getValue();
            ProductDTO selectedProduct = productComboBox.getValue();
            String productId = selectedProduct != null ? Integer.toString(selectedProduct.getProductID()) : null;
            String quantity = quantityField.getValue() != null ? quantityField.getValue().toString() : null;
            String minQuantity = minQuantityField.getValue() != null ? minQuantityField.getValue().toString() : null;
            String maxQuantity = maxQuantityField.getValue() != null ? maxQuantityField.getValue().toString() : null;
            String price = priceField.getValue() != null ? priceField.getValue().toString() : null;
            String minPrice = minPriceField.getValue() != null ? minPriceField.getValue().toString() : null;
            String maxPrice = maxPriceField.getValue() != null ? maxPriceField.getValue().toString() : null;
            presenter.savePolicy(storeId, "Simple", policyType, category, productId, quantityType, quantity, minQuantity, maxQuantity, price, minPrice, maxPrice, null, null, null); // Save policy
            loadAllBoxes();
            simplePolicyDialog.close();
        });

        Button cancelButton = new Button("Cancel", e -> simplePolicyDialog.close());

        formLayout.add(policyTypeComboBox, quantityTypeComboBox, categoryField, productComboBox, quantityField, minQuantityField, maxQuantityField, priceField, minPriceField, maxPriceField, saveButton, cancelButton);
        simplePolicyDialog.add(formLayout);
        simplePolicyDialog.open();
    }

    private void handleQuantityTypeSelection(String selectedType, boolean isPrice, NumberField quantityField, NumberField minQuantityField, NumberField maxQuantityField, NumberField priceField, NumberField minPriceField, NumberField maxPriceField) {
        boolean isBetween = "Between".equals(selectedType);

        if (isPrice) {
            priceField.setVisible(!isBetween);
            minPriceField.setVisible(isBetween);
            maxPriceField.setVisible(isBetween);
            quantityField.setVisible(false);
            minQuantityField.setVisible(false);
            maxQuantityField.setVisible(false);
        } else {
            quantityField.setVisible(!isBetween);
            minQuantityField.setVisible(isBetween);
            maxQuantityField.setVisible(isBetween);
            priceField.setVisible(false);
            minPriceField.setVisible(false);
            maxPriceField.setVisible(false);
        }
    }

    private void addDiscountBoxToCanvas(DiscountBox discountBox) {
        if (presenter.hasPermission(storeId, "MANAGE_DISCOUNTS_POLICIES")) {
            DragSource<DiscountBox> dragSource = DragSource.create(discountBox);
            dragSource.setDraggable(true);

            DropTarget<DiscountBox> dropTarget = DropTarget.create(discountBox);
            dropTarget.setDropEffect(DropEffect.MOVE);
            dropTarget.addDropListener(event -> {
                if (event.getDragSourceComponent().isPresent()) {
                    Component sourceComponent = event.getDragSourceComponent().get();
                    if (sourceComponent instanceof PolicyBox) {
                        PolicyBox source = (PolicyBox) sourceComponent;
                        showPolicyToDiscountConnectionTypeDialog(source, discountBox);
                    } else if (sourceComponent instanceof DiscountBox) {
                        DiscountBox source = (DiscountBox) sourceComponent;
                        showConnectionTypeDialog(source, discountBox);
                    }
                }
            });
        }

        discountBoxes.add(discountBox);
        discountCanvas.add(discountBox);
    }

    private void addPolicyBoxToCanvas(PolicyBox policyBox) {
        if (presenter.hasPermission(storeId, "MANAGE_DISCOUNTS_POLICIES")) {
            DragSource<PolicyBox> dragSource = DragSource.create(policyBox);
            dragSource.setDraggable(true);

            DropTarget<PolicyBox> dropTarget = DropTarget.create(policyBox);
            dropTarget.setDropEffect(DropEffect.MOVE);
            dropTarget.addDropListener(event -> {
                if (event.getDragSourceComponent().isPresent()) {
                    Component sourceComponent = event.getDragSourceComponent().get();
                    if (sourceComponent instanceof DiscountBox) {
                        DiscountBox source = (DiscountBox) sourceComponent;
                        showDiscountToPolicyConnectionTypeDialog(source, policyBox);
                    } else if (sourceComponent instanceof PolicyBox) {
                        PolicyBox source = (PolicyBox) sourceComponent;
                        showPolicyConnectionTypeDialog(source, policyBox);
                    }
                }
            });
        }

        policyBoxes.add(policyBox);
        policyCanvas.add(policyBox);
    }

    private void showDiscountToPolicyConnectionTypeDialog(DiscountBox source, PolicyBox target) {
        Dialog connectionTypeDialog = new Dialog();
        connectionTypeDialog.setWidth("300px");

        FormLayout formLayout = new FormLayout();

        ComboBox<String> policyTypeComboBox = new ComboBox<>("Connection Type");
        policyTypeComboBox.setItems("Condition");

        Button saveButton = new Button("Connect", e -> {
            presenter.saveDiscount(storeId, "Condition", null, null, null, null, source, null, null, target); // Save discount with policy condition
            loadAllBoxes();
            connectionTypeDialog.close();
        });

        Button cancelButton = new Button("Cancel", e -> connectionTypeDialog.close());

        formLayout.add(policyTypeComboBox, saveButton, cancelButton);
        connectionTypeDialog.add(formLayout);
        connectionTypeDialog.open();
    }

    private void showConnectionTypeDialog(DiscountBox source, DiscountBox target) {
        Dialog connectionTypeDialog = new Dialog();
        connectionTypeDialog.setWidth("300px");

        FormLayout formLayout = new FormLayout();

        ComboBox<String> discountTypeComboBox = new ComboBox<>("Connection Type");
        discountTypeComboBox.setItems("MAX", "PLUS");

        Button saveButton = new Button("Connect", e -> {
            String discountType = discountTypeComboBox.getValue();
            presenter.saveDiscount(storeId, "Complex", null, null, null, null, source, target, discountType, null); // Save discount
            loadAllBoxes();
            connectionTypeDialog.close();
        });

        Button cancelButton = new Button("Cancel", e -> connectionTypeDialog.close());

        formLayout.add(discountTypeComboBox, saveButton, cancelButton);
        connectionTypeDialog.add(formLayout);
        connectionTypeDialog.open();
    }

    private void showPolicyConnectionTypeDialog(PolicyBox source, PolicyBox target) {
        Dialog connectionTypeDialog = new Dialog();
        connectionTypeDialog.setWidth("400px");
        connectionTypeDialog.setHeight("400px"); // Increase height to show full details

        FormLayout formLayout = new FormLayout();

        ComboBox<String> policyTypeComboBox = new ComboBox<>("Connection Type");
        policyTypeComboBox.setItems("AND", "OR", "XOR", "Condition");

        policyTypeComboBox.setWidth("100%"); // Ensure the combo box is wide enough

        Button saveButton = new Button("Connect", e -> {
            String policyType = policyTypeComboBox.getValue();

            if ("Condition".equals(policyType)) {
                Dialog conditionDialog = new Dialog();
                conditionDialog.setWidth("400px");
                conditionDialog.setHeight("500px"); // Increase height to show full details

                FormLayout conditionForm = new FormLayout();

                ComboBox<PolicyBox> conditionComboBox = new ComboBox<>("Policy Condition");
                conditionComboBox.setItems(policyBoxes);
                conditionComboBox.setWidth("100%");
                conditionComboBox.setRenderer(new TextRenderer<>(policyBox -> {
                    Div div = new Div();
                    div.setText(policyBox.getDetailedInfo());
                    div.getStyle().set("white-space", "pre-wrap"); // Ensure text wraps properly
                    return div.getElement().getText();
                }));

                ComboBox<PolicyBox> basePolicyComboBox = new ComboBox<>("Base Policy");
                basePolicyComboBox.setItems(policyBoxes);
                basePolicyComboBox.setWidth("100%");
                basePolicyComboBox.setRenderer(new TextRenderer<>(policyBox -> {
                    Div div = new Div();
                    div.setText(policyBox.getDetailedInfo());
                    div.getStyle().set("white-space", "pre-wrap"); // Ensure text wraps properly
                    return div.getElement().getText();
                }));

                Button conditionSaveButton = new Button("Create Condition", ce -> {
                    PolicyBox condition = conditionComboBox.getValue();
                    PolicyBox basePolicy = basePolicyComboBox.getValue();

                    if (condition != null && basePolicy != null) {
                        presenter.savePolicy(storeId, "Condition", null, null, null, null, null, null, null, null, null, null, basePolicy, condition, null); // Save discount
                        loadAllBoxes();
                        conditionDialog.close();
                        connectionTypeDialog.close();
                    }
                });

                Button conditionCancelButton = new Button("Cancel", ce -> conditionDialog.close());

                conditionForm.add(basePolicyComboBox, conditionComboBox, conditionSaveButton, conditionCancelButton);
                conditionDialog.add(conditionForm);
                conditionDialog.open();
            } else {
                presenter.savePolicy(storeId, "Complex", null, null, null, null, null, null, null, null, null, null, source, target, policyType); // Save discount
                loadAllBoxes();
                connectionTypeDialog.close();
            }
        });

        Button cancelButton = new Button("Cancel", e -> connectionTypeDialog.close());

        formLayout.add(policyTypeComboBox, saveButton, cancelButton);
        connectionTypeDialog.add(formLayout);
        connectionTypeDialog.open();
    }

    private void showPolicyToDiscountConnectionTypeDialog(PolicyBox source, DiscountBox target) {
        Dialog connectionTypeDialog = new Dialog();
        connectionTypeDialog.setWidth("300px");

        FormLayout formLayout = new FormLayout();

        ComboBox<String> discountTypeComboBox = new ComboBox<>("Connection Type");
        discountTypeComboBox.setItems("Condition");

        Button saveButton = new Button("Connect", e -> {
            presenter.saveDiscount(storeId, "Condition", null, null, null, null, target, null, null, source); // Save discount with policy condition
            loadAllBoxes();
            connectionTypeDialog.close();
        });

        Button cancelButton = new Button("Cancel", e -> connectionTypeDialog.close());

        formLayout.add(discountTypeComboBox, saveButton, cancelButton);
        connectionTypeDialog.add(formLayout);
        connectionTypeDialog.open();
    }

    private void saveAll() {
        // Implement logic to save all discounts and policies to the store
    }

    public PolicyBox getPolicyBox(int conditionID) {
        for (PolicyBox policyBox : policyBoxes) {
            if (Integer.parseInt(policyBox.getID()) == conditionID) {
                return policyBox;
            }
        }
        return null;
    }

    public DiscountBox getDiscountBox(int discountID) {
        for (DiscountBox discountBox : discountBoxes) {
            if (Integer.parseInt(discountBox.getID()) == discountID) {
                return discountBox;
            }
        }
        return null;
    }
}
