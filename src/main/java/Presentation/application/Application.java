package Presentation.application;

import Domain.Store.Store;
import Domain.Users.Subscriber.Subscriber;
import Service.ServiceInitializer;
import Service.StoreService;
import Service.UserService;
import Utilities.Response;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.theme.Theme;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.HashMap;

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
    static Subscriber subscriber, notOwner;
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
        userService.register("notOwner","Password123!");
        userService.loginAsSubscriber("notOwner","Password123!");
        notOwner=userService.getUserFacade().getUserRepository().getUser("notOwner");
        storeService.addStore("newStore", "miaa",subscriber.getToken());
        store = storeService.getStoreFacade().getStoreRepository().getStore("0");
        storeService.addStore("newStore2", "miaa",subscriber.getToken());
        store2 = storeService.getStoreFacade().getStoreRepository().getStore("0");
        storeService.addProductToStore("0","yair","d",20,30,"miaa",token);
        System.out.println(store.getInventory().productsList);
        ServiceInitializer.getInstance().getOrderService().getOrderFacade().getOrderRepository().addOrder("0","miaa",new HashMap<>());
        System.out.println( ServiceInitializer.getInstance().getOrderService().getOrderFacade().getOrdersHistory("0").getData());
        storeService.addProductToStore("0","Bamba","Bamba",200.0,1,"miaa",subscriber.getToken());
        storeService.addProductToStore("0","Bisli","Bisli",100.0,1,"miaa",subscriber.getToken());
        storeService.addProductToStore("1","CHIPS","CHIPS",200.0,1,"miaa",subscriber.getToken());
        storeService.addProductToStore("1","DORITOS","DORITOS",100.0,1,"miaa",subscriber.getToken());
        userService.addProductToShoppingCart("0","1","miaa",subscriber.getToken(), 1);
        userService.addProductToShoppingCart("0","2","miaa",subscriber.getToken(), 1);
        userService.addProductToShoppingCart("1","1","miaa",subscriber.getToken(), 1);
    }

}
