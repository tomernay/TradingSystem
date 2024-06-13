package Presentaion.application.View.Store;

import Presentaion.application.Presenter.StorePresenter.StorePagePresenter;
import Presentaion.application.View.MainLayoutView;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.component.HasStyle;

@Route(layout = MainLayoutView.class, value = "store")
@PageTitle("Store Page" )
@StyleSheet("context://login-view-styles.css")
public class StorePageView {

    private StorePagePresenter presenter;

    public StorePageView(StorePagePresenter presenter) {
//        addClassName("store-page-view");
        this.presenter = presenter;
        this.presenter.attachView(this);
    }



}



