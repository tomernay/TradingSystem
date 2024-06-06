package Presentaion.application.View;

import Presentaion.application.Presenter.OpenStorePresenter;

public class OpenStoreView {
    private final OpenStorePresenter presenter;

    public OpenStoreView(OpenStorePresenter presenter) {
//        addCl("open-store-view");
        this.presenter = presenter;
        this.presenter.attachView(this);
    }


}
