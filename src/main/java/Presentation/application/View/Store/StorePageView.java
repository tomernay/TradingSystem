package Presentation.application.View.Store;

import Presentation.application.Presenter.Store.StorePagePresenter;
import Presentation.application.View.MainLayoutView;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.ArrayList;

@PageTitle("")
@Route(value = "store-page/:storeId", layout = MainLayoutView.class)
@StyleSheet("context://login-view-styles.css")
public class StorePageView extends VerticalLayout {

    private StorePagePresenter presenter;
    private VerticalLayout mainContent;

    public StorePageView(StorePagePresenter presenter) {
        addClassName("store-page-view");
        this.presenter = presenter;
        this.presenter.attachView(this);
        UI.getCurrent().getPage().executeJs(
                "document.body.addEventListener('click', function() {" +
                        "    $0.$server.handleUserAction();" +
                        "});",
                getElement()
        );
        mainContent = new VerticalLayout();


    }

    //change the search method to search in the store
    public void search(String searchTerm, int storeId) {
        presenter.search(searchTerm, storeId);
    }

    //display stores products
    public void getStoresProducts(int storeId) {
        //display products
        ArrayList<String> products = presenter.getStoresProducts(storeId);

    }




}



