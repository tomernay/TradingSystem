package Presentaion.application.Presenter.StorePresenter;

import Presentaion.application.CookiesHandler;
import Presentaion.application.View.Payment.PaymentPage;
import Presentaion.application.View.Store.AddProductView;
import Presentaion.application.View.Store.EditProductDetailsView;
import Service.ServiceInitializer;
import Service.StoreService;
import Utilities.Response;
import com.vaadin.flow.server.VaadinRequest;
import com.vaadin.flow.server.VaadinServletRequest;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

@Component
public class StorePresenter {
    StoreService service;

      public StorePresenter(){
          service= ServiceInitializer.getInstance().getStoreService();
      }




    /**
     * This method adds a product to the store
     * @param storeID the ID of the store
     * @param name the name of the product
     * @param desc the description of the product
     * @param price the price of the product
     * @param quantity the quantity of the product
     * @return If successful, returns a success message. <br> If not, returns an error message.
     * category = "General", by default
     */
    public void addProduct(AddProductView edit, String storeID, String name, String desc, double price, int quantity,String user,String token){

       Response<String> res=  service.addProductToStore(storeID, name, desc, price, quantity, user,token);
        edit.showNotification(res.getMessage());
    }


}
