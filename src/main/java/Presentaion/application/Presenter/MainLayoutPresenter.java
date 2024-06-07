package Presentaion.application.Presenter;

import Presentaion.application.CookiesHandler;
import Presentaion.application.View.MainLayoutView;
import Service.ServiceInitializer;
import Service.StoreService;
import Service.UserService;
import Utilities.Response;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class MainLayoutPresenter {
    private MainLayoutView view;
    private final UserService userService; // Assuming you have a UserService
    private final StoreService storeService;
    private final HttpServletRequest request;
//    private final Subscriber subscriber;


    public MainLayoutPresenter(HttpServletRequest request) {
        this.userService = ServiceInitializer.getInstance().getUserService();
        this.storeService = ServiceInitializer.getInstance().getStoreService();
        this.request = request;
//        this.subscriber = (Subscriber) CookiesHandler.getUserFromCookies(request);
    }

    public void attachView(MainLayoutView view) {
        this.view = view;
    }

    public void search(String search) {

//        view.navigateToSearch(search);
    }

    public void changeUsername(String username, String newUsername, TextField usernameField) {
        String token = CookiesHandler.getTokenFromCookies(request);
        Response<String> response = userService.changeUsername(username, newUsername, token);
        if (!response.isSuccess()) {
            view.showUNError(response.getMessage(), usernameField);
        }
        else {
            view.UnSuccess();
        }

    }

    public void changePassword(String password, String confirmPassword, PasswordField passwordField) {
        String username = CookiesHandler.getUsernameFromCookies(request);
        String token = CookiesHandler.getTokenFromCookies(request);
        if (password.equals(confirmPassword)) {
            Response<String> response = userService.changePassword(username, password, confirmPassword, token);
            if (!response.isSuccess()) {
                view.showPwdError(response.getMessage(), passwordField);
            }
            else {
                view.pwdSuccess();
            }
        }
        else {
            view.showPwdError("Passwords do not match", passwordField);
        }


//            if (!register.isSuccess()) {
//                view.showError(register.getMessage());
//                return;
//            }
//            view.navigateToLogin();
//        } else {
//            view.showError("Passwords do not match");
//        }
        }

    public void logout() {
        String username = CookiesHandler.getUsernameFromCookies(request);
        assert username != null;
        if (username.contains("Guest")) {
            userService.logoutAsGuest(username);
            view.navigateToLogin();
        }
        else {
            userService.logoutAsSubscriber(username);
            view.navigateToLogin();
        }
    }


    public String getUserName(){
        String username = CookiesHandler.getUsernameFromCookies(request);;
        if (username == null){
            username = "Guest";
        }
        else if(username.contains("Guest")){
            username = username.substring(0,5);
        }
        return username;
    }

    public List<String> getStoresIds(){
        String username = CookiesHandler.getUsernameFromCookies(request);
        Response<Map<String, String>> storesRole = userService.getStoresRole(username);
        if (storesRole.isSuccess()) {
            return new ArrayList<>(storesRole.getData().keySet());
        }
        return new ArrayList<>();
    }

    //get store name by id
    public List<String> getStores(List<String> storesIds){
        List<String> stores = new ArrayList<>();
        String username = CookiesHandler.getUsernameFromCookies(request);
        String token = CookiesHandler.getTokenFromCookies(request);
        for(String storeID : storesIds){
            Response<String> storeName = storeService.getStoreNameByID(storeID, username, token);
            if(storeName.isSuccess()){
                stores.add(storeName.getData());
            }

        }
        return stores;

    }

    public List<String> getUsersStores(){
        List<String> ids = getStoresIds();
        return getStores(ids);
    }

    public boolean isManager(String username){

        return userService.isManager(username).getData().equals("true");
    }

    public boolean isOwner(String username){
        return userService.isOwner(username).getData().equals("true");
    }

    public boolean isCreator(String username){
        return userService.isCreator(username).getData().equals("true");
    }
}