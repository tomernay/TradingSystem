package Presentaion.application.View.Store;

import Presentaion.application.Presenter.StorePresenter.StorePresenter;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

@Route("store-management")
@PageTitle("Store Management")
public class StoreManagementView extends VerticalLayout {

    private Div content;

    public StoreManagementView() {
        Tabs tabs = new Tabs();
        tabs.add(createTab("Edit Product Details", EditProductDetailsView.class));
        tabs.add(createTab("Add Product", AddProductView.class));
    /*    tabs.add(createTab("Remove Product", RemoveProductView.class));
        tabs.add(createTab("Change Store Details", ChangeStoreDetailsView.class));
        tabs.add(createTab("Update External Services", UpdateExternalServicesView.class));
        tabs.add(createTab("Manage Discounts", ManageDiscountsView.class));
        tabs.add(createTab("Set General Purchasing Rules", SetGeneralPurchasingRulesView.class));
        */
        tabs.addSelectedChangeListener(event -> setContent(event.getSelectedTab()));
        
        content = new Div();
        content.setSizeFull();

        add(tabs, content);
        setContent(tabs.getSelectedTab());
    }

    private Tab createTab(String title, Class<? extends Component> contentClass) {
        Tab tab = new Tab(title);
        tab.setId(contentClass.getSimpleName());
        tab.getElement().addEventListener("click", e -> {
            content.removeAll();
            try {
                content.add(contentClass.getDeclaredConstructor().newInstance());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        return tab;
    }

    private void setContent(Tab selectedTab) {
        content.removeAll();
        String id = selectedTab.getId().orElse("");
        try {
            Component component=new AddProductView();

            switch (id) {
                case "AddProductView":
                     // Or obtain it from a DI framework
                    component = new AddProductView();
                    break;
                case "EditProductView":
                   // Use the appropriate presenter
                    component = new EditProductDetailsView();
                    break;
                case "RemoveProductView":
                    break;
                // Add cases for other views that require specific presenters
                default:
                    component = new AddProductView();
                    break;
            }

            content.add(component);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
