package Domain.Store.Inventory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import Utilities.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import Domain.Store.Inventory.ProductDTO;

public class Inventory {
    private static final AtomicInteger productIDGenerator = new AtomicInteger(1);
    private final String storeID; //Inventory for specific store
    public ConcurrentHashMap<Integer, Product> productsList; // <productID, Product>
    private ConcurrentHashMap<String, ArrayList<Integer>> categories; // <Category:String, <ArrayList<ProductID>>

    // Constructor
    public Inventory(String _storeID) {
        this.storeID = _storeID;
        this.productsList = new ConcurrentHashMap<>();
        this.categories = new ConcurrentHashMap<>();
    }

    public String getStoreID() {
        return storeID;
    }


    public boolean isProductExist(Integer productID) {
        return productsList.containsKey(productID);
    }


    private synchronized Product getProduct(Integer productID) {
        return productsList.get(productID);
    }

    public synchronized Response<String> setProductQuantity(int productID, int newQuantity) {
        if (productID <= 0) {
            return Response.error("Invalid product ID: " + productID, null);
        }
        if (!isProductExist(productID)) {
            return Response.error("Product with ID: " + productID + " does not exist.", null);
        }
        Product product = getProduct(productID);
        if (product == null) {
            return Response.error("Product with ID: " + productID + " not found.", null);
        }
        return product.setQuantity(newQuantity);
    }


    public synchronized Response<String> addProductQuantity(int productID, int valueToAdd) {
        if (productID <= 0) {
            return Response.error("Invalid product ID: " + productID, null);
        }
        if (!isProductExist(productID)) {
            return Response.error("Product with ID: " + productID + " does not exist.", null);
        }
        Product product = getProduct(productID);
        if (product == null) {
            return Response.error("Product with ID: " + productID + " not found.", null);
        }
        return product.addQuantity(valueToAdd);
    }


    public synchronized Response<String> getProductQuantity(int productID) {
        if (productID < 0) {
            return Response.error("Invalid product ID: " + productID, null);
        }
        if (!isProductExist(productID)) {
            return Response.error("Product with ID: " + productID + " does not exist.", null);
        }
        Product product = getProduct(productID);
        if (product == null) {
            return Response.error("Product with ID: " + productID + " not found.", null);
        }
        int quantity = product.getQuantity();
        return Response.success("Product with ID: " + productID + " quantity retrieved successfully", String.valueOf(quantity));

    }


    public synchronized Response<String> getStoreID(int productID) {
        try {
            if (isProductExist(productID)) {
                String storeID = getProduct(productID).getStoreID();
                return new Response<>(true, "Store ID retrieved successfully", storeID);
            } else {
                return new Response<>(false, "Product does not exist with ID: " + productID);
            }
        } catch (Exception e) {
            return new Response<>(false, "Failed to retrieve store ID: " + e.getMessage());
        }
    }


    public synchronized Response<String> getStoreName(int productID) {
        try {
            if (isProductExist(productID)) {
                String storeName = getProduct(productID).getStoreName();
                return new Response<>(true, "Store name retrieved successfully", storeName);
            } else {
                return new Response<>(false, "Product does not exist with ID: " + productID);
            }
        } catch (Exception e) {
            return new Response<>(false, "Failed to retrieve store name: " + e.getMessage());
        }
    }


    public synchronized Response<String> getProductDescription(int productID) {
        if (productID < 0) {
            return Response.error("Invalid product ID: " + productID, null);
        }
        if (!isProductExist(productID)) {
            return Response.error("Product with ID: " + productID + " does not exist.", null);
        }
        Product product = getProduct(productID);
        if (product == null) {
            return Response.error("Product with ID: " + productID + " not found.", null);
        }
        String description = product.getDescription();
        return Response.success("Product with ID: " + productID + " description retrieved successfully", description);
    }

    public Response<String> setProductDescription(int productID, String newDescription) {
        if (productID < 0) {
            return Response.error("Invalid product ID: " + productID, null);
        }
        if (!isProductExist(productID)) {
            return Response.error("Product with ID: " + productID + " does not exist.", null);
        }
        Product product = getProduct(productID);
        if (product == null) {
            return Response.error("Product with ID: " + productID + " not found.", null);
        }
        product.setDescription(newDescription);
        return Response.success("Product with ID: " + productID + " description updated successfully", newDescription);

    }


    public synchronized Response<Integer> setStoreIDToProduct(int productID, String storeID) {
        try {
            if (isProductExist(productID)) {
                getProduct(productID).setStoreID(storeID);
                return new Response<>(true, "Store ID set to product successfully");
            } else {
                return new Response<>(false, "Product does not exist with ID: " + productID);
            }
        } catch (Exception e) {
            return new Response<>(false, "Failed to set store ID to product: " + e.getMessage());
        }
    }

    public synchronized Response<String> getProductName(int productID) {
        if (productID < 0) {
            return Response.error("Invalid product ID: " + productID, null);
        }
        if (!isProductExist(productID)) {
            return Response.error("Product with ID: " + productID + " does not exist.", null);
        }
        Product product = getProduct(productID);
        if (product == null) {
            return Response.error("Product with ID: " + productID + " not found.", null);
        }
        String name = product.getName();
        return Response.success("Product with ID: " + productID + " name retrieved successfully", name);
    }


    public synchronized Response<String> setProductName(int productID, String productName) {
        if (productID < 0) {
            return Response.error("Invalid product ID: " + productID, null);
        }
        if (!isProductExist(productID)) {
            return Response.error("Product with ID: " + productID + " does not exist.", null);
        }
        Product product = getProduct(productID);
        if (product == null) {
            return Response.error("Product with ID: " + productID + " not found.", null);
        }
        product.setName(productName);
        return Response.success("Product with ID: " + productID + " name updated successfully", productName);
    }


    public synchronized Response<Integer> setStoreNameToProduct(int productID, String storeName) {
        try {
            if (isProductExist(productID)) {
                getProduct(productID).setStoreName(storeName);
                return new Response<>(true, "Store name set to product successfully");
            } else {
                return new Response<>(false, "Product does not exist with ID: " + productID);
            }
        } catch (Exception e) {
            return new Response<>(false, "Failed to set store name to product: " + e.getMessage());
        }
    }

    public Response<String> getProductPrice(int productID) {
        if (productID < 0) {
            return Response.error("Invalid product ID: " + productID, null);
        }
        if (!isProductExist(productID)) {
            return Response.error("Product with ID: " + productID + " does not exist.", null);
        }
        Product product = getProduct(productID);
        if (product == null) {
            return Response.error("Product with ID: " + productID + " not found.", null);
        }
        int price = product.getPrice();
        return Response.success("Product with ID: " + productID + " price retrieved successfully", String.valueOf(price));
    }

    public Response<String> setProductPrice(int productID, int newPrice) {
        if (productID < 0) {
            return Response.error("Invalid product ID: " + productID, null);
        }
        if (!isProductExist(productID)) {
            return Response.error("Product with ID: " + productID + " does not exist.", null);
        }
        Product product = getProduct(productID);
        if (product == null) {
            return Response.error("Product with ID: " + productID + " not found.", null);
        }
        boolean bool = product.setPrice(newPrice);
        if(!bool){
            return Response.error("Invalid price: Price cannot be negative", null);
        }
        return Response.success("Product with ID: " + productID + " price updated successfully", String.valueOf(newPrice));
    }

    public Response<ArrayList<ProductDTO>> retrieveProductsByCategory(String category) {
        if (category == null || category.isEmpty()) {
            return Response.error("Invalid category: " + category, null);
        }
        if (!categories.containsKey(category)) {
            return Response.error("Category does not exist: " + category, null);
        }
        ArrayList<Integer> productIDs = categories.get(category);
        if (productIDs == null || productIDs.isEmpty()) {
            return Response.error("No products found in category: " + category, null);
        }
        ArrayList<ProductDTO> products = new ArrayList<>();
        for (Integer productID : productIDs) {
            Product product = productsList.get(productID);
            if (product != null) {
                products.add(new ProductDTO(product));
            }
        }
        return Response.success("Products retrieved successfully for category: " + category, products);
    }


    public synchronized Response<String> retrieveProductCategories(int productID) {
        if (productID < 0) {
            return Response.error("Invalid product ID: " + productID, null);
        }
        if (!isProductExist(productID)) {
            return Response.error("Product with ID: " + productID + " does not exist.", null);
        }
        List<String> relatedCategories = new ArrayList<>();
        for (Map.Entry<String, ArrayList<Integer>> entry : categories.entrySet()) {
            if (entry.getValue().contains(productID)) {
                relatedCategories.add(entry.getKey());
            }
        }
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String jsonString = objectMapper.writeValueAsString(relatedCategories);
            return Response.success("Categories retrieved successfully for product ID: " + productID, jsonString);
        } catch (JsonProcessingException e) {
            return Response.error("Error processing JSON: " + e.getMessage(), null);
        }
    }

    public Response<String> assignProductToCategory(int productID, String category) {
        if (productID < 0) {
            return Response.error("Invalid product ID: " + productID, null);
        }
        if (!isProductExist(productID)) {
            return Response.error("Product with ID: " + productID + " does not exist.", null);
        }
        if (category == null || category.isEmpty()) {
            return Response.error("Invalid category: " + category, null);
        }
        if(!categories.containsKey(category)){
            categories.put(category, new ArrayList<>());
        }
        ArrayList<Integer> productIDs = categories.get(category);
        if (productIDs.contains(productID)) {
            return Response.error("Product already exists in category: " + category, null);
        }
        productIDs.add(productID);
        return Response.success("Product with ID: " + productID + " assigned to category: " + category, category);
    }


    public void addProduct(Product product) {
    }


    public Response<String> removeCategoryFromStore(String category) {
        if (category == null || category.isEmpty()) {
            return Response.error("Invalid category: " + category, null);
        }
        if (!categories.containsKey(category)) {
            return Response.error("Category does not exist: " + category, null);
        }
        categories.remove(category);
        return Response.success("Category removed successfully: " + category, category);
    }


    public Response<ProductDTO> getProductFromStore(int productID) {
        if (productID < 0) {
            return Response.error("Invalid product ID: " + productID, null);
        }
        if (!isProductExist(productID)) {
            return Response.error("Product with ID: " + productID + " does not exist.", null);
        }
        Product product = getProduct(productID);
        if (product == null) {
            return Response.error("Product with ID: " + productID + " not found.", null);
        }
        return Response.success("Product retrieved successfully", new ProductDTO(product));
    }

    public Response<ArrayList<ProductDTO>> getAllProductsFromStore() {
        ArrayList<ProductDTO> products = new ArrayList<>();
        for (Map.Entry<Integer, Product> entry : productsList.entrySet()) {
            products.add(new ProductDTO(entry.getValue()));
        }
        return Response.success("Products retrieved successfully", products);
    }

    //product with no category will be added to General category
    public Response<String> addProductToStore(String storeID, String storeName, String name, String desc, int price, int quantity) {
        if (storeID == null || storeID.isEmpty()) {
            return Response.error("Store ID cannot be null or empty", null);
        }
        if (storeName == null || storeName.isEmpty()) {
            return Response.error("Store name cannot be null or empty", null);
        }
        if (name == null || name.isEmpty()) {
            return Response.error("Product name cannot be null or empty", null);
        }
        if (desc == null || desc.isEmpty()) {
            return Response.error("Product description cannot be null or empty", null);
        }
        if (price <= 0) {
            return Response.error("Price must be greater than 0", null);
        }
        if (quantity <= 0) {
            return Response.error("Quantity must be greater than 0", null);
        }
        Product product = new Product.Builder(storeID, name,  productIDGenerator.getAndIncrement())
                .storeName(storeName)
                .desc(desc)
                .price(price)
                .quantity(quantity)
                .build();
        productsList.put(product.getProductID(), product);
        if(!this.categories.containsKey("General")){
            this.categories.put("General", new ArrayList<>());
        }
        this.categories.get("General").add(product.getProductID());

        return Response.success("Product with ID: " + product.getProductID() + " added successfully", name);

    }

    //product with categories will be added to the categories he is associated with
    public Response<String> addProductToStore(String storeID, String storeName, String name, String desc, int price, int quantity, ArrayList<String> categories) {
        if (storeID == null || storeID.isEmpty()) {
            return Response.error("Store ID cannot be null or empty", null);
        }
        if (storeName == null || storeName.isEmpty()) {
            return Response.error("Store name cannot be null or empty", null);
        }
        if (name == null || name.isEmpty()) {
            return Response.error("Product name cannot be null or empty", null);
        }
        if (desc == null || desc.isEmpty()) {
            return Response.error("Product description cannot be null or empty", null);
        }
        if (price <= 0) {
            return Response.error("Price must be greater than 0", null);
        }
        if (quantity <= 0) {
            return Response.error("Quantity must be greater than 0", null);
        }
        if (categories == null || categories.isEmpty()) {
            return Response.error("Product must be associated with at least one category", null);
        }
        Product product = new Product.Builder(storeID, name, productIDGenerator.getAndIncrement())
                .storeName(storeName)
                .desc(desc)
                .price(price)
                .quantity(quantity)
                .build();
        productsList.put(product.getProductID(), product);
        for (String category : categories) {
            if(!this.categories.containsKey(category)){
                this.categories.put(category, new ArrayList<>());
            }
            this.categories.get(category).add(product.getProductID());
        }
        return Response.success("Product with ID: " + product.getProductID() + " added successfully", name);
    }

    public Response<String> removeProductFromStore(int productID) {
        if (productID < 0) {
            return Response.error("Invalid product ID: " + productID, null);
        }
        if (!isProductExist(productID)) {
            return Response.error("Product with ID: " + productID + " does not exist.", null);
        }
        Product product = getProduct(productID);
        if (product == null) {
            return Response.error("Product with ID: " + productID + " not found.", null);
        }
        productsList.remove(productID);
        for (Map.Entry<String, ArrayList<Integer>> entry : categories.entrySet()) {
            entry.getValue().remove(productID);
        }
        return Response.success("Product with ID: " + productID + " removed successfully", product.getName());

    }

    public Response<ProductDTO> getProductByName(String productName) {
        if (productName == null || productName.isEmpty()) {
            return Response.error("Product name cannot be null or empty", null);
        }
        for (Map.Entry<Integer, Product> entry : productsList.entrySet()) {
            if (entry.getValue().getName().equals(productName)) {
                return Response.success("Product with name: " + productName + " retrieved successfully", new ProductDTO(entry.getValue()));
            }
        }
        return Response.error("Product with name: " + productName + " not found", null);
    }
}
























