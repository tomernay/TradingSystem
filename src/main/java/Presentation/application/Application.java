package Presentation.application;

import Domain.Store.Inventory.ProductDTO;
import Domain.Store.Store;
import Domain.Users.Subscriber.Subscriber;
import Service.OrderService;
import Service.ServiceInitializer;
import Service.StoreService;
import Service.UserService;
import Utilities.Response;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.theme.Theme;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The entry point of the Spring Boot application.
 *
 * Use the @PWA annotation make the application installable on phones, tablets
 * and some desktop browsers.
 *
 */
@Push
@SpringBootApplication(scanBasePackages = {"Service", "Presentation"})
@Theme(value = "my-app")
public class Application implements AppShellConfigurator {

    public static void main(String[] args) {
        init();
        SpringApplication.run(Application.class, args);
    }

    static ServiceInitializer serviceInitializer;
    static StoreService storeService;
    static UserService userService;
    static OrderService orderService;
    static Subscriber subscriber, owner, manager;
    static Store store, store2;
    public static void init(){
        ServiceInitializer.reset();
        serviceInitializer = ServiceInitializer.getInstance();
        userService = serviceInitializer.getUserService();
        userService.register("miaa","Password123!");
        Response<String> resLogin=userService.loginAsSubscriber("miaa","Password123!");
       String token=resLogin.getData();
        subscriber=userService.getUserFacade().getUserRepository().getUser("miaa");
        storeService = serviceInitializer.getStoreService();
        orderService = serviceInitializer.getOrderService();
        userService.register("manager","Password123!");
        userService.loginAsSubscriber("manager","Password123!");
        userService.register("owner","Password123!");
        userService.loginAsSubscriber("owner","Password123!");
        owner=userService.getUserFacade().getUserRepository().getUser("owner");
        manager = userService.getUserFacade().getUserRepository().getUser("manager");
        storeService.addStore("newStore", "miaa",subscriber.getToken());
        store = storeService.getStoreFacade().getStoreRepository().getStore("0");
        storeService.addStore("newStore2", "miaa",subscriber.getToken());
        store2 = storeService.getStoreFacade().getStoreRepository().getStore("0");
        storeService.addProductToStore("0","yair","d",20,30,"miaa",subscriber.getToken());
        Response<String> res = userService.SendManagerNominationRequest("0", "miaa", "manager", List.of("VIEW_PRODUCTS","MANAGE_PRODUCTS", "VIEW_DISCOUNTS_POLICIES"), token);
        userService.managerNominationResponse(res.getData(), "manager", true, manager.getToken());
        Response<String> res2 = userService.SendOwnerNominationRequest("0", "miaa", "owner", token);
        userService.ownerNominationResponse(res2.getData(),"owner", true, owner.getToken());
        storeService.addProductToStore("0","Bamba","Bamba",200.0,1, new ArrayList<>(List.of("test")),"miaa",subscriber.getToken());
        storeService.addProductToStore("0","Bisli","Bisli",100.0,1,"miaa",subscriber.getToken());
        storeService.addProductToStore("1","CHIPS","CHIPS",200.0,1,"miaa",subscriber.getToken());
        storeService.addProductToStore("1","DORITOS","DORITOS",100.0,1,"miaa",subscriber.getToken());
        userService.addProductToShoppingCart("0", "1", 1, "miaa", subscriber.getToken());
        userService.addProductToShoppingCart("0", "2", 1, "miaa", subscriber.getToken());
        userService.addProductToShoppingCart("1", "1", 1,"miaa", subscriber.getToken());
        orderService.CreateOrder("miaa", subscriber.getToken(), "test");
        userService.logoutAsSubscriber("miaa");
    }
}
