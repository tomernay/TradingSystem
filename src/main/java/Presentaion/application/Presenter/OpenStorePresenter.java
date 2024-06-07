//package Presentaion.application.Presenter;
//
//import Presentaion.application.View.OpenStoreView;
//import Service.ServiceInitializer;
//import Service.StoreService;
//import Service.UserService;
//import Utilities.Response;
//import jakarta.servlet.http.HttpServletRequest;
//import org.springframework.stereotype.Component;
//
//@Component
//public class OpenStorePresenter {
//    private OpenStoreView view;
//    private final UserService userService; // Assuming you have a UserService
//    private final HttpServletRequest request;
//    private final StoreService storeService;
//
//    public OpenStorePresenter(HttpServletRequest request) {
//        this.userService =  ServiceInitializer.getInstance().getUserService();
//        this.request = request;
//        this.storeService = ServiceInitializer.getInstance().getStoreService();
//
//
//    }
//
//    public void attachView(OpenStoreView view) {
//        this.view = view;
//    }
//
//    public void openStore(){
//        Response<String> response = storeService.addStore();
//    }
//}
