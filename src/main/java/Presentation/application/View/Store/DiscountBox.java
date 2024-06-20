package Presentation.application.View.Store;

import Domain.Store.Discounts.DiscountDTO;
import Presentation.application.Presenter.Store.StoreManagementPresenter;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class DiscountBox extends VerticalLayout {
    private String ID;
    private StoreManagementPresenter presenter;
    private CreateDiscountDialog parentDialog;
    private String storeId;
    private String type; // SIMPLE, COMPLEX, CONDITION
    private String productId;
    private String category;
    private double discountPercent;
    private DiscountBox discount1;
    private DiscountBox discount2;
    private String discountType; // MAX or PLUS
    private String productName;
    private PolicyBox condition;
    private boolean isExpanded = false;
    private Div detailsDiv = new Div();

    // Constructor for simple discount
    public DiscountBox(StoreManagementPresenter presenter, CreateDiscountDialog parentDialog, String ID, String storeId, String type, String productId, String category, double discountPercent, String productName) {
        this.presenter = presenter;
        this.parentDialog = parentDialog;
        this.ID = ID;
        this.storeId = storeId;
        this.type = type;
        this.productId = productId;
        this.category = category;
        this.discountPercent = discountPercent;
        this.productName = productName != null ? productName : category;
        setHeight("auto");
        setWidthFull();
        setStyle();
        displayDiscountInfo();
    }

    // Constructor for complex discount
    public DiscountBox(StoreManagementPresenter presenter, CreateDiscountDialog parentDialog, String ID, String storeId, String type, DiscountBox discount1, DiscountBox discount2, String discountType) {
        this.presenter = presenter;
        this.parentDialog = parentDialog;
        this.ID = ID;
        this.storeId = storeId;
        this.type = type;
        this.discount1 = discount1;
        this.discount2 = discount2;
        this.discountType = discountType;
        setHeight("auto");
        setWidthFull();
        setStyle();
        displayDiscountInfo();
    }

    // Constructor for condition discount
    public DiscountBox(StoreManagementPresenter presenter, CreateDiscountDialog parentDialog, String ID, String storeId, String type, DiscountBox discount, PolicyBox condition) {
        this.presenter = presenter;
        this.parentDialog = parentDialog;
        this.ID = ID;
        this.storeId = storeId;
        this.type = type;
        this.discount1 = discount;
        this.condition = condition;
        setHeight("auto");
        setWidthFull();
        setStyle();
        displayDiscountInfo();
    }

    public DiscountBox(StoreManagementPresenter presenter, CreateDiscountDialog parentDialog, String storeId, DiscountDTO discountDTO) {
        this.presenter = presenter;
        this.parentDialog = parentDialog;
        this.ID = String.valueOf(discountDTO.getDiscountID());
        this.storeId = storeId;
        this.type = discountDTO.getType();
        this.discountPercent = discountDTO.getPercent() != null ? Double.parseDouble(discountDTO.getPercent()) : 0.0;
        this.productId = discountDTO.getProductID();
        this.category = discountDTO.getCategory();
        this.discountType = discountDTO.getDiscountType() != null ? discountDTO.getDiscountType().toString() : null;
        this.productName = discountDTO.getProductName();

        if (discountDTO.getDiscountDTO1() != null) {
            this.discount1 = parentDialog.getDiscountBox(discountDTO.getDiscountDTO1().getDiscountID());
            if (this.discount1 == null) {
                this.discount1 = new DiscountBox(presenter, parentDialog, storeId, discountDTO.getDiscountDTO1());
            }
        }

        if (discountDTO.getDiscountDTO2() != null) {
            this.discount2 = parentDialog.getDiscountBox(discountDTO.getDiscountDTO2().getDiscountID());
            if (this.discount2 == null) {
                this.discount2 = new DiscountBox(presenter, parentDialog, storeId, discountDTO.getDiscountDTO2());
            }
        }

        if (discountDTO.getCondition() != null) {
            this.condition = parentDialog.getPolicyBox(discountDTO.getCondition().getConditionID());
            if (this.condition == null) {
                this.condition = new PolicyBox(presenter, parentDialog, storeId, discountDTO.getCondition());
            }
        }

        setHeight("auto");
        setWidthFull();
        setStyle();
        displayDiscountInfo();
    }

    private void displayDiscountInfo() {
        removeAll();
        Div headerDiv = new Div();
        if ("SIMPLE".equals(type)) {
            headerDiv.add(new Div("Discount Percent: " + discountPercent + "%"));
            if (productId != null) {
                headerDiv.add(new Div("Product: " + productName));
            } else if (category != null) {
                headerDiv.add(new Div("Category: " + category));
            }
        } else if ("COMPLEX".equals(type)) {
            headerDiv.add(new Div("Discount Type: " + discountType));
        } else if ("CONDITION".equals(type)) {
            headerDiv.add(new Div("Condition Policy: " ));
        }
        headerDiv.getStyle().set("cursor", "pointer");
        headerDiv.addClickListener(event -> toggleExpand());
        add(headerDiv);

        detailsDiv.removeAll();
        if ("COMPLEX".equals(type)) {
            detailsDiv.add(new Div("Discount 1 Details:"));
            detailsDiv.add(discount1);
            detailsDiv.add(new Div("Discount 2 Details:"));
            detailsDiv.add(discount2);
        } else if ("CONDITION".equals(type)) {
            detailsDiv.add(new Div("Discount Details:"));
            detailsDiv.add(discount1);
            detailsDiv.add(new Div("Condition Details:"));
            detailsDiv.add(condition);
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
        displayDiscountInfo();
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
        details.append("Type: ").append(type).append(", ");
        if ("SIMPLE".equals(type)) {
            details.append("Discount Percent: ").append(discountPercent).append("%, ");
            if (productId != null) {
                details.append("Product: ").append(productName).append(", ");
            } else if (category != null) {
                details.append("Category: ").append(category).append(", ");
            }
        } else if ("COMPLEX".equals(type)) {
            details.append("Discount Type: ").append(discountType).append(", ");
            details.append("Discount 1: ").append(discount1.getDetailedInfo()).append(", ");
            details.append("Discount 2: ").append(discount2.getDetailedInfo()).append(", ");
        } else if ("CONDITION".equals(type)) {
            details.append("Discount: ").append(discount1.getDetailedInfo()).append(", ");
            details.append("Condition Policy: ").append(condition.getDetailedInfo()).append(", ");
        }
        return details.toString();
    }

    // Method to convert to DTO (data transfer object)
    public DiscountDTO toDTO() {
        return new DiscountDTO(Integer.parseInt(ID), productId, productName, storeId, discountType, category, String.valueOf(discountPercent), discount1 != null ? discount1.toDTO() : null, discount2 != null ? discount2.toDTO() : null, condition != null ? condition.toDTO() : null);
    }

    @Override
    public String toString() {
        return getDetailedInfo(); // or any other meaningful representation
    }

    public String getID() {
        return ID;
    }
}
