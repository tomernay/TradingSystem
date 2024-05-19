package DataBase.PublicPay;

import Domain.Store.PurchasePolicy.PaymentTypes.PublicPay;
import Domain.Store.Store;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.HashMap;

public class PublicPayDAO {
    public static PublicPay convertJsonToPublicPay(String publicPay) {

        try {

            // Parse JSON string
            JSONObject jsonObject = new JSONObject(publicPay);

            // Extract date
            JSONObject dateObject = jsonObject.getJSONArray("table").getJSONObject(0).getJSONObject("date");
            Date date = new Date(dateObject.getLong("time"));


            // Extract maxFee
            double maxFee = jsonObject.getJSONArray("table").getJSONObject(2).getDouble("maxFee");

            // Extract card
            String card = jsonObject.getJSONArray("table").getJSONObject(3).getStr("card");
            HashMap<String, Integer> products = new HashMap<>();
            try {


                // Extract products
                JSONArray productsArray = jsonObject.getJSONArray("table").getJSONObject(4).getJSONArray("products");

                for (int i = 0; i < productsArray.size(); i++) {
                    products.put(String.valueOf(productsArray.getInt(i)), productsArray.getInt(i));
                }
            }catch (Exception e) {  }

            // Extract store
            JSONObject storeObject = jsonObject.getJSONArray("table").getJSONObject(1).getJSONObject("store");

            // Create Store object
            Store store = new Store();
            store.setName(storeObject.getStr("name")); // Example: replace with actual field name
            store.setId(storeObject.getStr("id"));

            // Create and return PublicPay object
            return new PublicPay(maxFee, date, store, card, products);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public  static JSONObject convertToJsonObject(Object obj) throws IllegalAccessException {
        JSONObject jsonObject = new JSONObject();

        // Get all fields of the class, including private fields
        Field[] fields = obj.getClass().getDeclaredFields();

        for (Field field : fields) {
            field.setAccessible(true); // Allow access to private fields
            Object value = field.get(obj);
            jsonObject.put(field.getName(), value);
        }

        return jsonObject;
    }
}
