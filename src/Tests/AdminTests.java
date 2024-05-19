package src.Tests;

import org.junit.BeforeClass;
import org.junit.Test;
import src.main.java.Service.Service;

public class AdminTests {
    static Service service;

    @BeforeClass
    public static void init(){
        service = new Service();

    }
    @Test
    public void closeStore(){
        service.getAdminService().closeStore();
    }

    @Test
    public void removeSubscriber(){
        service.getAdminService().removeSubscriber();
    }

    @Test
    public void recieveInfoWithResponse(){
        service.getAdminService().recieveInfoWithResponse();
    }

    @Test
    public void getPurchasesHistory(){
        service.getAdminService().getPurchaseHistory();
    }

    @Test
    public void recieveSystemInfo(){
        service.getAdminService().recieveSystemInfo();
    }



}
