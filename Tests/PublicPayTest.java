package Tests;

import DataBase.FireBaseConstants;
import DataBase.Files.FilesHandler;

import DataBase.PublicPay.PublicPayDAO;
import DataBase.PublicPay.PublicPayDTO;
import Domain.Externals.Security.Security;
import Domain.Store.PurchasePolicy.PaymentTypes.PublicPay;
import Domain.Store.Store;
import Domain.Users.Subscriber.Subscriber;
import Service.Service;
import cn.hutool.json.JSONObject;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class PublicPayTest {
    static Service service;
    static Subscriber subscriber;
    static Subscriber buyer;
    static Subscriber buyer2;
    static Store store;
    @BeforeClass
    public static void init() throws Exception {
        service = new Service();
        service.getUserService().register("yair", "by");
        subscriber = service.getUserService().getUser("yair");

        service.getUserService().register("yair2", "by2");
        buyer = service.getUserService().getUser("yair2");
        String token= Security.generateJWT("yair");
        System.out.println(Security.isValidJWT(token,"yair"));
        service.getStoreService().addStore("yairStore", subscriber.getUsername(),token);
        service.getUserService().getUser(buyer.getUsername()).setCredit("1111111111");
        store = service.getStoreService().getStore("0");
        service.getUserService().register("yair3", "by2");
        buyer2 = service.getUserService().getUser("yair3");
        buyer2.setCredit("23324343");

        service.getPaymentService().createPublicPay(subscriber.getUsername(),token,new HashMap<>(),store,63,13);
    }

    @Test
    public void testPay() throws Exception {
        PublicPay pay=new PublicPay();
        PublicPayDTO publicPayDTO=new PublicPayDTO(pay);
        publicPayDTO.retrievePublicPayment("yairStore");
        TimeUnit.SECONDS.sleep(3);
        File publicPayFile=new File(FireBaseConstants.publicPay+"yairStore.json");
        JSONObject json=FilesHandler.readJSONObjectFromFile(publicPayFile);
        System.out.println(json);
        PublicPay publicPay= PublicPayDAO.convertJsonToPublicPay(json.toString());
        System.out.println(publicPay.getStore().getName());
        publicPay.setCard("1213453132");
        publicPay.setMaxFee(publicPay.getMaxFee()+7);
        service.getPaymentService().counterPublicPay(buyer.getUsername(),buyer.getToken(),publicPay);

        TimeUnit.SECONDS.sleep(13);
        publicPay.setCard("1213453132");
        publicPay.setMaxFee(publicPay.getMaxFee()+9);
        service.getPaymentService().counterPublicPay(buyer.getUsername(),buyer.getToken(),publicPay);
        Thread.sleep(100000000);
    }
}
