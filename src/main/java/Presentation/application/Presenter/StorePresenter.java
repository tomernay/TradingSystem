package Presentation.application.Presenter;

import Presentation.application.View.Store.*;
import Service.PaymentService;
import Service.ServiceInitializer;
import Service.StoreService;
import Utilities.Response;
import org.springframework.stereotype.Component;

@Component
public class StorePresenter {
    StoreService service;
    PaymentService paymentService;



    public StorePresenter() {
        this.service = ServiceInitializer.getInstance().getStoreService();
        paymentService=ServiceInitializer.getInstance().getPaymentService();
    }

    /**
     * This method adds a product to the store.
     * @param edit the view to show notifications
     * @param storeID the ID of the store
     * @param name the name of the product
     * @param desc the description of the product
     * @param price the price of the product
     * @param quantity the quantity of the product
     * @param user the username of the user
     * @param token the token of the user
     */
    public void addProduct(AddProductView edit, String storeID, String name, String desc, double price, int quantity, String user, String token) {
        Response<String> res = service.addProductToStore(storeID, name, desc, price, quantity, user, token);
        edit.showNotification(res.getMessage());
    }

    /**
     * This method removes a product from the store.
     * @param removeProductView the view to show notifications
     * @param productID the ID of the product
     * @param storeID the ID of the store
     * @param username the username of the user
     * @param token the token of the user
     */
    public void removeProduct(RemoveProductView removeProductView, int productID, String storeID, String username, String token) {
        Response<String> res = service.removeProductFromStore(productID, storeID, username, token);
        if (res.isSuccess()) {
            removeProductView.showSuccessMessage(res.getMessage());
        } else {
            removeProductView.showErrorMessage(res.getMessage());
        }
    }

    /**
     * add payment adapter
     * @param view
     * @param paymentAdapter
     * @param name
     * @param user
     * @param token
     */
    public void addPayment(AddPaymentServiceView view, String paymentAdapter, String name, String user, String token){
        Response<String> res = paymentService.addPaymentAdapter(paymentAdapter,name,token,user);
        if (res.isSuccess()) {
            view.showSuccessMessage(res.getMessage());
        } else {
            view.showErrorMessage(res.getMessage());
        }
    }

    /**
     * add a new supplier
     * @param view
     * @param supplierAdapter
     * @param value
     * @param user
     * @param token
     */
    public void addSupplier(AddSupplierServiceView view, String supplierAdapter, String value, String user, String token) {
        Response<String> res = paymentService.addSupplierAdapter(supplierAdapter,value,token,user);
        if (res.isSuccess()) {
            view.showSuccessMessage(res.getMessage());
        } else {
            view.showErrorMessage(res.getMessage());
        }
    }

    public void CreatDiscountSimple(CreateDiscountView view, String username, String token, String productID, String storeID, String category, String percent) {
        Response<String> res = service.CreatDiscountSimple(username,token,productID,storeID,category,percent);
        if (res.isSuccess()) {
            view.showSuccessMessage(res.getMessage());
        } else {
            view.showErrorMessage(res.getMessage());
        }
    }
}

