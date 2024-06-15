package Presentation.application.Presenter;

import Presentation.application.CookiesHandler;
import Presentation.application.View.MainLayoutView;
import Service.ServiceInitializer;
import Service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import Domain.Store.Inventory.ProductDTO;
import Service.StoreService;
import Utilities.Response;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;

import java.util.*;


@Component
public class MainLayoutPresenter {
    private MainLayoutView view;
    private final UserService userService; // Assuming you have a UserService
    private final StoreService storeService;
    private final HttpServletRequest request;


    public MainLayoutPresenter(HttpServletRequest request) {
        this.userService = ServiceInitializer.getInstance().getUserService();
        this.storeService = ServiceInitializer.getInstance().getStoreService();
        this.request = request;
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
        } else {
            userService.logoutAsSubscriber(username);
        }
        CookiesHandler.deleteCookies(request); // Delete cookies after logout
        view.navigateToLogin();
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

    //add a store
    public void addStore(String storeName, TextField field){
        String username = CookiesHandler.getUsernameFromCookies(request);
        String token = CookiesHandler.getTokenFromCookies(request);
        Response<String> response = storeService.addStore(storeName, username, token);
        if(response.isSuccess()){
            view.addStoreSuccess();
        }
        else{
            view.addStoreError(response.getMessage(), field);
        }
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
        Response<String> res = userService.isManager(username);
        if(!res.isSuccess()){
            return false;
        }
        return res.getData().equals("true");
    }

    public boolean isOwner(String username){
        Response<String> res = userService.isOwner(username);
        if(!res.isSuccess()){
            return false;
        }
        return res.getData().equals("true");
    }

    public boolean isCreator(String username){
        Response<String> res = userService.isCreator(username);
        if(!res.isSuccess()){
            return false;
        }
        return res.getData().equals("true");
    }

    public ArrayList<ProductDTO> searchProducts(String search){
        String username = CookiesHandler.getUsernameFromCookies(request);
        String token = CookiesHandler.getTokenFromCookies(request);
        ArrayList<ProductDTO> products = combineSearchResults(search, username, token);
        return products;
    }

    private ArrayList<ProductDTO> combineSearchResults(String searchTerm, String username, String token) {
        Response<ArrayList<ProductDTO>> resultsByName = searchProductsByName(searchTerm, username, token);
        Response<ArrayList<ProductDTO>> resultsByCategory = searchProductsByCategory(searchTerm, username, token);

        if (!resultsByName.isSuccess() && !resultsByCategory.isSuccess()) {
            return new ArrayList<>();
        }

        // Combine both lists without duplicates
        Set<ProductDTO> combinedResultsSet = new HashSet<>(resultsByName.getData());
        combinedResultsSet.addAll(resultsByCategory.getData());

        // Convert the set back to a list if needed
        ArrayList<ProductDTO> combinedResults = new ArrayList<>(combinedResultsSet);

        return combinedResults;
    }

    private Response<ArrayList<ProductDTO>> searchProductsByCategory(String searchTerm, String username, String token) {
        return storeService.getProductsFromAllStoresByCategory(searchTerm, username, token);
    }

    private Response<ArrayList<ProductDTO>> searchProductsByName(String searchTerm, String username, String token) {
        return storeService.viewProductFromAllStoresByName(searchTerm, username, token);
    }

    public String getStoreIdByName(String storeName) {
        String username = CookiesHandler.getUsernameFromCookies(request);
        String token = CookiesHandler.getTokenFromCookies(request);
        Response<String> response = storeService.getStoreIDbyName(storeName, username, token);
        if (response.isSuccess()) {
            return response.getData();
        }
        return null;
    }
}