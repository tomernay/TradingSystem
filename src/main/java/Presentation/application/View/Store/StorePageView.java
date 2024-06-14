package Presentation.application.View.Store;

import Presentation.application.Presenter.StorePagePresenter;
import Presentation.application.View.MainLayoutView;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route(layout = MainLayoutView.class, value = "store-page")
@PageTitle("")
@StyleSheet("context://login-view-styles.css")
public class StorePageView extends VerticalLayout {

    private StorePagePresenter presenter;

    public StorePageView(StorePagePresenter presenter) {
        addClassName("store-page-view");
        this.presenter = presenter;
        this.presenter.attachView(this);
    }

    //change the search method to search in the store
    public void search(String search) {
        presenter.search(search);
    }

    //display storees products
    public void displayProducts() {
        //display products
        presenter.displayProducts();

    }




}



