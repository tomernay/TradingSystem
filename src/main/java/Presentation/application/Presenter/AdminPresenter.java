package Presentation.application.Presenter;

import Presentation.application.CookiesHandler;
import Presentation.application.View.AdminView;
import Service.AdminService;
import Service.ServiceInitializer;
import Service.StoreService;
import Service.UserService;
import Utilities.Response;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class AdminPresenter {
    AdminView view;
    private final UserService userService; // Assuming you have a UserService
    private final StoreService storeService;
    private final AdminService adminService;
    private final HttpServletRequest request;

    public AdminPresenter(HttpServletRequest request) {
        this.userService = ServiceInitializer.getInstance().getUserService();
        this.storeService = ServiceInitializer.getInstance().getStoreService();
        this.adminService = ServiceInitializer.getInstance().getAdminService();
        this.request = request;
    }

    public void attachView(AdminView view) {
        this.view = view;
    }

    public List<String> getOpenStores() {
        String token = CookiesHandler.getTokenFromCookies(request);
        String username = CookiesHandler.getUsernameFromCookies(request);
        return storeService.getAllStores(username, token).getData();
    }

    public void closeStore(String storeName) {
        String token = CookiesHandler.getTokenFromCookies(request);
        String username = CookiesHandler.getUsernameFromCookies(request);
        Integer storeID = storeService.getStoreIDbyName(storeName, username, token).getData();
        storeService.closeStore(storeID, username, token);
    }

    public Set<String> getAllSubscribers() {
        String token = CookiesHandler.getTokenFromCookies(request);
        String username = CookiesHandler.getUsernameFromCookies(request);
        return userService.getAllSubscribers(username, token).getData();
    }

    public void removeSubscriber(String subName) {
        String token = CookiesHandler.getTokenFromCookies(request);
        String username = CookiesHandler.getUsernameFromCookies(request);
//        userService.removeSubscriber(subName, username, token);


    }

    public boolean suspendSubscriber(String subName, Date date) {
        Response<String> res = adminService.suspendUser(subName, date);
        return res.isSuccess();
    }

    public boolean cancelSuspension(String subName) {
        Response<String> res = adminService.reactivateUser(subName);
        return res.isSuccess();
    }

    public Map<String, Date> getSuspensionList() {
        return adminService.getSuspensionList().getData();
    }
}
