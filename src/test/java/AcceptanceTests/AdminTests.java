package AcceptanceTests;

import org.junit.BeforeClass;
import org.junit.Test;
import Service.AdminService;

public class AdminTests {
    static AdminService adminService;

    @BeforeClass
    public static void init(){
        adminService = new AdminService();

    }
    @Test
    public void closeStore(){
        adminService.closeStore();
    }

    @Test
    public void removeSubscriber(){
        adminService.removeSubscriber();
    }

    @Test
    public void recieveInfoWithResponse(){
        adminService.recieveInfoWithResponse();
    }

    @Test
    public void getPurchasesHistory(){
        adminService.getPurchaseHistory();
    }

    @Test
    public void recieveSystemInfo(){
        adminService.recieveSystemInfo();
    }



}
