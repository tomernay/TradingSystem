package Presentaion.application;

import Domain.Store.Store;
import Domain.Users.Subscriber.Subscriber;
import Service.ServiceInitializer;
import Service.StoreService;
import Service.UserService;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.theme.Theme;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * The entry point of the Spring Boot application.
 *
 * Use the @PWA annotation make the application installable on phones, tablets
 * and some desktop browsers.
 *
 */
@SpringBootApplication(scanBasePackages = {"Service", "Presentaion"})
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
    static Store store;
    public static void init(){
        ServiceInitializer.reset();
        serviceInitializer = ServiceInitializer.getInstance();
        userService = serviceInitializer.getUserService();
        userService.register("miaa","Password123!");
        userService.loginAsSubscriber("miaa","Password123!");
        subscriber=userService.getUserFacade().getUserRepository().getUser("miaa");
        storeService = serviceInitializer.getStoreService();
        userService.register("notOwner","Password123!");
        userService.loginAsSubscriber("notOwner","Password123!");
        notOwner=userService.getUserFacade().getUserRepository().getUser("notOwner");
        storeService.addStore("newStore", "miaa",subscriber.getToken());
        store = storeService.getStoreFacade().getStoreRepository().getStore("0");
    }

}
