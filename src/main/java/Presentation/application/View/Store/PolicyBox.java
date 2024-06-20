package Presentation.application.View.Store;

import Domain.Store.Conditions.ConditionDTO;
import Presentation.application.Presenter.Store.StoreManagementPresenter;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import java.util.Objects;

public class PolicyBox extends VerticalLayout {
    private String ID;
    private StoreManagementPresenter presenter;
    private CreateDiscountDialog parentDialog;
    private String storeId;
    private String type; // SIMPLE / COMPLEX / CONDITION
    private String policyType; // Category / Product / Price
    private String category;
    private String productId;
    private String quantityType;
    private String quantity;
    private String minQuantity;
    private String maxQuantity;
    private String price;
    private String minPrice;
    private String maxPrice;
    private PolicyBox policy1;
    private PolicyBox policy2;
    private String policyConditionType;
    private String productName;
    private boolean isExpanded = false;
    private Div detailsDiv = new Div();

    // Constructor for simple policy
    public PolicyBox(StoreManagementPresenter presenter, CreateDiscountDialog parentDialog, String ID, String storeId, String type, String policyType, String category, String productId, String quantityType, String quantity, String minQuantity, String maxQuantity, String price, String minPrice, String maxPrice, String productName) {
        this.presenter = presenter;
        this.parentDialog = parentDialog;
        this.ID = ID;
        this.storeId = storeId;
        this.type = type;
        this.policyType = policyType;
        this.category = category;
        this.productId = productId;
        this.quantityType = quantityType;
        this.quantity = quantity;
        this.minQuantity = minQuantity;
        this.maxQuantity = maxQuantity;
        this.price = price;
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
        this.productName = productName;
        setHeight("auto");
        setWidthFull();
        setStyle();
        displayPolicyInfo();
    }

    // Constructor for complex policy
    public PolicyBox(StoreManagementPresenter presenter, CreateDiscountDialog parentDialog, String ID, String storeId, String type, PolicyBox policy1, PolicyBox policy2, String policyConditionType) {
        this.presenter = presenter;
        this.parentDialog = parentDialog;
        this.ID = ID;
        this.storeId = storeId;
        this.type = type;
        this.policy1 = policy1;
        this.policy2 = policy2;
        this.policyConditionType = policyConditionType;
        setHeight("auto");
        setWidthFull();
        setStyle();
        displayPolicyInfo();
    }

    public PolicyBox(StoreManagementPresenter presenter, CreateDiscountDialog parentDialog, String storeId, ConditionDTO conditionDTO) {
        this.presenter = presenter;
        this.parentDialog = parentDialog;
        this.ID = String.valueOf(conditionDTO.getConditionID());
        this.storeId = storeId;
        this.type = conditionDTO.getDiscountType();
        this.policyType = determinePolicyType(conditionDTO);
        this.category = conditionDTO.getCategory();
        this.productId = conditionDTO.getProductID();
        this.quantityType = determineQuantityType(conditionDTO);
        this.policyConditionType = conditionDTO.getRelationType();
        if (category != null || productId != null) {
            this.quantity = conditionDTO.getAmount();
            this.minQuantity = conditionDTO.getMinAmount();
            this.maxQuantity = conditionDTO.getMaxAmount();
            this.price = null;
            this.minPrice = null;
            this.maxPrice = null;
        }
        else {
            this.quantity = null;
            this.minQuantity = null;
            this.maxQuantity = null;
            this.price = conditionDTO.getAmount();
            this.minPrice = conditionDTO.getMinAmount();
            this.maxPrice = conditionDTO.getMaxAmount();
        }
        this.productName = conditionDTO.getProductName();

        if (conditionDTO.getConditionDTO1() != null) {
            this.policy1 = parentDialog.getPolicyBox(conditionDTO.getConditionDTO1().getConditionID());
            if (this.policy1 == null) {
                this.policy1 = new PolicyBox(presenter, parentDialog, storeId, conditionDTO.getConditionDTO1());
            }
        }

        if (conditionDTO.getConditionDTO2() != null) {
            this.policy2 = parentDialog.getPolicyBox(conditionDTO.getConditionDTO2().getConditionID());
            if (this.policy2 == null) {
                this.policy2 = new PolicyBox(presenter, parentDialog, storeId, conditionDTO.getConditionDTO2());
            }
        }

        if (conditionDTO.getPolicyConditionDTO() != null) {
            this.policyConditionType = "Condition";
            this.policy2 = parentDialog.getPolicyBox(conditionDTO.getPolicyConditionDTO().getConditionID());
            if (this.policy2 == null) {
                this.policy2 = new PolicyBox(presenter, parentDialog, storeId, conditionDTO.getPolicyConditionDTO());
            }
        }

        setHeight("auto");
        setWidthFull();
        setStyle();
        displayPolicyInfo();
    }

    private String determineQuantityType(ConditionDTO conditionDTO) {
        if (conditionDTO.getMinAmount() != null && conditionDTO.getMaxAmount() != null) {
            return "Between";
        } else if (conditionDTO.getAmount() != null) {
            return "Exactly";
        } else if (conditionDTO.getMinAmount() != null) {
            return "At least";
        } else if (conditionDTO.getMaxAmount() != null) {
            return "At most";
        }
        return "";
    }

    private String determinePolicyType(ConditionDTO conditionDTO) {
        if (conditionDTO.getCategory() != null) {
            return "Category";
        } else if (conditionDTO.getProductID() != null) {
            return "Product";
        } else if (conditionDTO.getPrice() != null) {
            return "Price";
        }
        return "";
    }

    private void displayPolicyInfo() {
        removeAll();
        Div headerDiv = new Div();
        if ("Simple".equals(type)) {
            headerDiv.add(new Div("Policy Type: " + policyType));
            if (policyType.equals("Category")) {
                headerDiv.add(new Div("Category: " + category));
            } else if (policyType.equals("Product")) {
                headerDiv.add(new Div("Product: " + productName));
            }
            headerDiv.add(new Div("Quantity Type: " + quantityType));
            if ("Between".equals(quantityType)) {
                headerDiv.add(new Div("Min Quantity: " + minQuantity));
                headerDiv.add(new Div("Max Quantity: " + maxQuantity));
            }
            else if (quantityType.equals("Exactly")) {
                headerDiv.add(new Div("Quantity: " + quantity));
            }
            else if (quantityType.equals("At least")) {
                headerDiv.add(new Div("Min Quantity: " + minQuantity));
            }
            else if (quantityType.equals("At most")) {
                headerDiv.add(new Div("Max Quantity: " + maxQuantity));
            }
            if (policyType.equals("Price")) {
                headerDiv.add(new Div("Price Type: " + quantityType));
                if ("Between".equals(quantityType)) {
                    headerDiv.add(new Div("Min Price: " + minPrice));
                    headerDiv.add(new Div("Max Price: " + maxPrice));
                } else {
                    headerDiv.add(new Div("Price: " + price));
                }
            }
        } else if ("Complex".equals(type)) {
            headerDiv.add(new Div("Policy Complex Type: " + policyConditionType));
        } else if ("Condition".equals(type)) {
            headerDiv.add(new Div("Conditioned Policy: "));
        }
        headerDiv.getStyle().set("cursor", "pointer");
        headerDiv.addClickListener(event -> toggleExpand());
        add(headerDiv);

        detailsDiv.removeAll();
        if ("Complex".equals(type)) {
            detailsDiv.add(new Div("Policy 1 Details:"));
            detailsDiv.add(policy1);
            detailsDiv.add(new Div("Policy 2 Details:"));
            detailsDiv.add(policy2);
        } else if ("Condition".equals(type)) {
            detailsDiv.add(new Div("Base Policy Details:"));
            detailsDiv.add(policy1);
            detailsDiv.add(new Div("Condition Policy Details:"));
            detailsDiv.add(policy2);
        }

        if (isExpanded) {
            add(detailsDiv);
        }
    }

    private void toggleExpand() {
        isExpanded = !isExpanded;
        if (isExpanded) {
            add(detailsDiv);
        } else {
            remove(detailsDiv);
        }
        displayPolicyInfo();
    }

    private void setStyle() {
        getStyle().set("border", "1px solid black");
        getStyle().set("padding", "10px");
        getStyle().set("margin", "10px");
        getStyle().set("background-color", "#f9f9f9");
        getStyle().set("cursor", "pointer");
    }

    public String getDetailedInfo() {
        StringBuilder details = new StringBuilder();
        details.append("Type: ").append(type).append("\n");
        details.append("Policy Type: ").append(policyType).append("\n");
        if (category != null) {
            details.append("Category: ").append(category).append("\n");
        }
        if (productId != null) {
            details.append("Product: ").append(productName).append("\n");
        }
        if (quantityType != null) {
            details.append("Quantity Type: ").append(quantityType).append("\n");
        }
        if (quantity != null) {
            details.append("Quantity: ").append(quantity).append("\n");
        }
        if (minQuantity == null && maxQuantity != null) {
            details.append("Max Quantity: ").append(maxQuantity).append("\n");
        }
        if (minQuantity != null && maxQuantity == null) {
            details.append("Min Quantity: ").append(minQuantity).append("\n");
        }
        if (minQuantity != null && maxQuantity != null) {
            details.append("Min Quantity: ").append(minQuantity).append("\n");
            details.append("Max Quantity: ").append(maxQuantity).append("\n");
        }
        if (price != null) {
            details.append("Price: ").append(price).append("\n");
        }
        if (minPrice == null && maxPrice != null) {
            details.append("Max Price: ").append(maxPrice).append("\n");
        }
        if (minPrice != null && maxPrice == null) {
            details.append("Min Price: ").append(minPrice).append("\n");
        }
        if (minPrice != null && maxPrice != null) {
            details.append("Min Price: ").append(minPrice).append("\n");
            details.append("Max Price: ").append(maxPrice).append("\n");
        }
        return details.toString();
    }


    // Method to convert to DTO (data transfer object)
    public ConditionDTO toDTO() {
        if (Objects.equals(policyType, "Category") || Objects.equals(policyType, "Product")) {
            return new ConditionDTO(Integer.parseInt(ID), productId, productName, category, type, quantity, minQuantity, maxQuantity, null, null, null, null, null);
        } else if (Objects.equals(policyType, "Price")) {
            return new ConditionDTO(Integer.parseInt(ID), productId, productName, category, type, price, minPrice, maxPrice, "1.0", null, null, null, null);
        } else if (Objects.equals(type, "Complex")) {
            return new ConditionDTO(Integer.parseInt(ID), null, null, null, type, null, null, null, null, policy1.toDTO(), policy2.toDTO(), null, policyConditionType);
        } else {
            return new ConditionDTO(Integer.parseInt(ID), null, null, null, type, null, null, null, null, policy1.toDTO(), null, policy2.toDTO(), null);
        }
    }

    @Override
    public String toString() {
        return getDetailedInfo(); // or any other meaningful representation
    }

    public String getID() {
        return ID;
    }
}
