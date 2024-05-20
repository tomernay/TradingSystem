package DataBase.PublicPay;

import Domain.Store.PurchasePolicy.PaymentTypes.PublicPay;
import Domain.Store.Store;
import cn.hutool.json.JSONObject;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.util.Date;
import java.util.HashMap;

public class PublicPayDAO {

    /**
     * Convert a PublicPay object to a JSONObject.
     * @param publicPayJson
     * @return
     */
    public static PublicPay convertJsonToPublicPay(JsonNode publicPayJson) {
        try {
            // Convert JsonNode to JSONObject using Jackson
            ObjectMapper mapper = new ObjectMapper();
            String jsonString = mapper.writeValueAsString(publicPayJson);
            JSONObject jsonObject = new JSONObject(jsonString);

            // Extract date
            JSONObject dateObject = jsonObject.getJSONObject("date");
            Date date = new Date(dateObject.getLong("time"));

            // Extract maxFee
            double maxFee = jsonObject.getDouble("maxFee");

            // Extract card
            String card = jsonObject.getStr("card");

            // Extract store
            JSONObject storeObject = jsonObject.getJSONObject("store");
            Store store = new Store();
            store.setName(storeObject.getStr("name"));
            store.setId(storeObject.getStr("id"));

            // Create and return PublicPay object (assuming products field is not used in the JSON)
            return new PublicPay(maxFee, date, store, card, new HashMap<>());

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Convert a Object to a JsonNode.
     * @param obj
     * @return
     * @throws IllegalAccessException
     */
    public  static JsonNode convertToJsonObject(Object obj) throws IllegalAccessException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        JsonNode node= mapper.valueToTree(obj);

        return node;
    }
}
