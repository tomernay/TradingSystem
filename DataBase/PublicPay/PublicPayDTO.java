package DataBase.PublicPay;

import DataBase.Files.FilesHandler;
import DataBase.FireBaseConstants;
import DataBase.ServiceAccountDetails.Init;
import Domain.Store.Inventory.Inventory;
import Domain.Store.PurchasePolicy.PaymentTypes.PublicPay;
import Domain.Store.Store;
import cn.hutool.json.JSONObject;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.firebase.database.*;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

public class PublicPayDTO {
   PublicPay pay;
    public PublicPayDTO(PublicPay pay){
        this.pay = pay;
    }
    public static void addPublicPayment(PublicPay payment) throws IOException, ExecutionException, InterruptedException {
     try {


         Init.initializeFirebaseApp();

     }catch (Exception e) {}
        JSONObject jsonObject=new JSONObject();
        jsonObject.put(payment.getStore().getName(), payment).toString();
      FirebaseDatabase.getInstance().getReference(FireBaseConstants.paymentsRoot).child(FireBaseConstants.publicPay).child(payment.getStore().getName()).setValueAsync(jsonObject).get();

    }


    public  void retrievePublicPayment(String storeName) throws IOException {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(FireBaseConstants.paymentsRoot).child(FireBaseConstants.publicPay).child(storeName).child(storeName);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    Object o=dataSnapshot.getValue();
                     JsonNode json=PublicPayDAO.convertToJsonObject(o);
                    FilesHandler.writeJSONObjectToFile(json,new File(FireBaseConstants.publicPay+storeName+".json"));



                }
                catch (Exception e){
                    e.printStackTrace();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.err.println("Error retrieving data: " + databaseError.getMessage());
            }
        });
    }




    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {
        HashMap<String,Integer> products=new HashMap<>();
        products.put("1",1);

      addPublicPayment(new PublicPay(1.0,1,new Store("y","y",new Inventory(),"y"),"123",products));

     Thread.currentThread().sleep(1000000);
    }

}
