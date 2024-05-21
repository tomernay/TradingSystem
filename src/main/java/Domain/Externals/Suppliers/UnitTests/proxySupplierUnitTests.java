package src.main.java.Domain.Externals.Suppliers.UnitTests;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import src.main.java.DataBase.FireBaseConstants;
import src.main.java.Domain.Externals.Payment.CreditCard;
import src.main.java.Domain.Externals.Payment.DefaultPay;
import src.main.java.Domain.Externals.Suppliers.DefaultSupplier;
import src.main.java.Domain.Externals.Suppliers.SupplierAdapter;
import src.main.java.Domain.Store.Inventory.Product;
import src.main.java.Domain.Store.Inventory.checkSupplyLegal;
import src.main.java.Utilities.Response;

public class proxySupplierUnitTests {

    static SupplierAdapter supplierAdapter;
    static Product p;
    static checkSupplyLegal checkSupplyLegal;
    @BeforeClass
    public static void init(){
         supplierAdapter=new DefaultSupplier("yair");
        Product.Builder builder=new Product.Builder("1",1);
        p=builder.build();
         checkSupplyLegal= new checkSupplyLegal() {
            @Override
            public boolean isSupplyLegal(Product product, String storeID) {
                return true;
            }

            @Override
            public Response<String> supply(Product product, int quantity, String storeID) {
                return new Response<>(true,"",null);
            }
        };
    }
    @Test
    public void proxySupplierTest(){

      Response<String> response= supplierAdapter.supply(p,"1",new CreditCard(FireBaseConstants.visa),new CreditCard(FireBaseConstants.secoundVisa),40,new DefaultPay("yair"),3,checkSupplyLegal);
        Assert.assertTrue(response.isSuccess());
    }

    @Test
    public void nullProduct(){

        Response<String> response= supplierAdapter.supply(null,"1",new CreditCard(FireBaseConstants.visa),new CreditCard(FireBaseConstants.secoundVisa),40,new DefaultPay("yair"),3,checkSupplyLegal);
        Assert.assertFalse(response.isSuccess());
    }

    @Test
    public void illegalPayment(){

        Response<String> response= supplierAdapter.supply(p,"1",null,new CreditCard(FireBaseConstants.secoundVisa),40,new DefaultPay("yair"),3,checkSupplyLegal);
        Assert.assertFalse(response.isSuccess());
    }

    @Test
    public void supplyIllegal(){
       checkSupplyLegal=new checkSupplyLegal() {
           @Override
           public boolean isSupplyLegal(Product product, String storeID) {
               return false;
           }

           @Override
           public Response<String> supply(Product product, int quantity, String storeID) {
               return null;
           }
       };
        Response<String> response= supplierAdapter.supply(p,"1",null,new CreditCard(FireBaseConstants.secoundVisa),40,new DefaultPay("yair"),3,checkSupplyLegal);
        Assert.assertFalse(response.isSuccess());

    }




}
