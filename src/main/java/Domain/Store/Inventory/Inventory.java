package Domain.Store.Inventory;

import Utilities.SystemLogger;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import Utilities.Response;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class Inventory {
    private final AtomicInteger productIDGenerator = new AtomicInteger(1);
    private final String storeID; //Inventory for specific store
    public ConcurrentHashMap<Integer, Product> productsList; // <productID, Product>
    public final ConcurrentHashMap<String, ArrayList<Integer>> categories; // <Category:String, <ArrayList<ProductID>>
    private final ConcurrentHashMap<Product, Integer> lockedProducts; // <Product, Quantity>

    // Constructor
    public Inventory(String _storeID) {
        this.storeID = _storeID;
        this.productsList = new ConcurrentHashMap<>();
        this.categories = new ConcurrentHashMap<>();
        this.lockedProducts = new ConcurrentHashMap<>();
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
            SystemLogger.error("[ERROR] Invalid product ID: " + productID);
            return Response.error("Invalid product ID: " + productID, null);
        }
        if (!isProductExist(productID)) {
            SystemLogger.error("[ERROR] Product with ID: " + productID + " does not exist.");
            return Response.error("Product with ID: " + productID + " does not exist.", null);
        }
        Product product = getProduct(productID);
        if (product == null) {
            SystemLogger.error("[ERROR] Product with ID: " + productID + " not found.");
            return Response.error("Product with ID: " + productID + " not found.", null);
        }
        return product.setQuantity(newQuantity);
    }


    public synchronized Response<String> addProductQuantity(int productID, int valueToAdd) {
        if (productID <= 0) {
            SystemLogger.error("[ERROR] Invalid product ID: " + productID);
            return Response.error("Invalid product ID: " + productID, null);
        }
        if (!isProductExist(productID)) {
            SystemLogger.error("[ERROR] Product with ID: " + productID + " does not exist.");
            return Response.error("Product with ID: " + productID + " does not exist.", null);
        }
        Product product = getProduct(productID);
        if (product == null) {
            SystemLogger.error("[ERROR] Product with ID: " + productID + " not found.");
            return Response.error("Product with ID: " + productID + " not found.", null);
        }
        return product.addQuantity(valueToAdd);
    }


    public synchronized Response<String> getProductQuantity(int productID) {
        if (productID < 0) {
            SystemLogger.error("[ERROR] Invalid product ID: " + productID);
            return Response.error("Invalid product ID: " + productID, null);
        }
        if (!isProductExist(productID)) {
            SystemLogger.error("[ERROR] Product with ID: " + productID + " does not exist.");
            return Response.error("Product with ID: " + productID + " does not exist.", null);
        }
        Product product = getProduct(productID);
        if (product == null) {
            SystemLogger.error("[ERROR] Product with ID: " + productID + " not found.");
            return Response.error("Product with ID: " + productID + " not found.", null);
        }
        int quantity = product.getQuantity();
        SystemLogger.info("[SUCCESS] Product with ID: " + productID + " quantity retrieved successfully");
        return Response.success("Product with ID: " + productID + " quantity retrieved successfully", String.valueOf(quantity));

    }


    public synchronized Response<String> getStoreID(int productID) {
        try {
            if (isProductExist(productID)) {
                String storeID = getProduct(productID).getStoreID();
                SystemLogger.info("[SUCCESS] Store ID retrieved successfully");
                return Response.success("Store ID retrieved successfully", storeID);
            } else {
                SystemLogger.error("[ERROR] Product does not exist with ID: " + productID);
                return Response.error("Product does not exist with ID: " + productID, null);
            }
        } catch (Exception e) {
            SystemLogger.error("[ERROR] Failed to retrieve store ID: " + e.getMessage());
            return Response.error("Failed to retrieve store ID: " + e.getMessage(), null);
        }
    }


    public synchronized Response<String> getStoreName(int productID) {
        try {
            if (isProductExist(productID)) {
                String storeName = getProduct(productID).getStoreName();
                return Response.success("Store name retrieved successfully", storeName);
            } else {
                SystemLogger.error("[ERROR] Product does not exist with ID: " + productID);
                return Response.error("Product does not exist with ID: " + productID, null);
            }
        } catch (Exception e) {
            SystemLogger.error("[ERROR] Failed to retrieve store name: " + e.getMessage());
            return Response.error("Failed to retrieve store name: " + e.getMessage(), null);
        }
    }


    public synchronized Response<String> getProductDescription(int productID) {
        if (productID < 0) {
            SystemLogger.error("[ERROR] Invalid product ID: " + productID);
            return Response.error("Invalid product ID: " + productID, null);
        }
        if (!isProductExist(productID)) {
            SystemLogger.error("[ERROR] Product with ID: " + productID + " does not exist.");
            return Response.error("Product with ID: " + productID + " does not exist.", null);
        }
        Product product = getProduct(productID);
        if (product == null) {
            SystemLogger.error("[ERROR] Product with ID: " + productID + " not found.");
            return Response.error("Product with ID: " + productID + " not found.", null);
        }
        String description = product.getDescription();
        SystemLogger.info("[SUCCESS] Product with ID: " + productID + " description retrieved successfully");
        return Response.success("Product with ID: " + productID + " description retrieved successfully", description);
    }

    public synchronized Response<String> setProductDescription(int productID, String newDescription) {
        if (productID < 0) {
            SystemLogger.error("[ERROR] Invalid product ID: " + productID);
            return Response.error("Invalid product ID: " + productID, null);
        }
        if (!isProductExist(productID)) {
            SystemLogger.error("[ERROR] Product with ID: " + productID + " does not exist.");
            return Response.error("Product with ID: " + productID + " does not exist.", null);
        }
        if(newDescription == null || newDescription.isEmpty()){
            SystemLogger.error("[ERROR] Product description cannot be null or empty");
            return Response.error("Product description cannot be null or empty", null);
        }
        Product product = getProduct(productID);
        if (product == null) {
            SystemLogger.error("[ERROR] Product with ID: " + productID + " not found.");
            return Response.error("Product with ID: " + productID + " not found.", null);
        }
        product.setDescription(newDescription);
        SystemLogger.info("[SUCCESS] Product with ID: " + productID + " description updated successfully to: " + newDescription);
        return Response.success("Product with ID: " + productID + " description updated successfully", newDescription);

    }


    public synchronized Response<Integer> setStoreIDToProduct(int productID, String storeID) {
        try {
            if (isProductExist(productID)) {
                getProduct(productID).setStoreID(storeID);
                SystemLogger.info("[SUCCESS] Store ID set to product successfully");
                return Response.success("Store ID set to product successfully", null);
            } else {
                SystemLogger.error("[ERROR] Product does not exist with ID: " + productID);
                return Response.error("Product does not exist with ID: " + productID, null);
            }
        } catch (Exception e) {
            SystemLogger.error("[ERROR] Failed to set store ID to product: " + e.getMessage());
            return Response.error("Failed to set store ID to product: " + e.getMessage(), null);
        }
    }

    public synchronized Response<String> getProductName(int productID) {
        if (productID < 0) {
            SystemLogger.error("[ERROR] Invalid product ID: " + productID);
            return Response.error("Invalid product ID: " + productID, null);
        }
        if (!isProductExist(productID)) {
            SystemLogger.error("[ERROR] Product with ID: " + productID + " does not exist.");
            return Response.error("Product with ID: " + productID + " does not exist.", null);
        }
        Product product = getProduct(productID);
        if (product == null) {
            SystemLogger.error("[ERROR] Product with ID: " + productID + " not found.");
            return Response.error("Product with ID: " + productID + " not found.", null);
        }
        String name = product.getName();
        SystemLogger.info("[SUCCESS] Product with ID: " + productID + " name retrieved successfully");
        return Response.success("Product with ID: " + productID + " name retrieved successfully", name);
    }


    public synchronized Response<String> setProductName(int productID, String productName) {
        if (productID < 0) {
            SystemLogger.error("[ERROR] Invalid product ID: " + productID);
            return Response.error("Invalid product ID: " + productID, null);
        }
        if (!isProductExist(productID)) {
            SystemLogger.error("[ERROR] Product with ID: " + productID + " does not exist.");
            return Response.error("Product with ID: " + productID + " does not exist.", null);
        }
        if(productName == null || productName.isEmpty()){
            SystemLogger.error("[ERROR] Product name cannot be null or empty");
            return Response.error("Product name cannot be null or empty", null);
        }
        Product product = getProduct(productID);
        if (product == null) {
            SystemLogger.error("[ERROR] Product with ID: " + productID + " not found.");
            return Response.error("Product with ID: " + productID + " not found.", null);
        }
        product.setName(productName);
        SystemLogger.info("[SUCCESS] Product with ID: " + productID + " name updated successfully to: " + productName);
        return Response.success("Product with ID: " + productID + " name updated successfully", productName);
    }


    public synchronized Response<Integer> setStoreNameToProduct(int productID, String storeName) {
        try {
            if (isProductExist(productID)) {
                getProduct(productID).setStoreName(storeName);
                SystemLogger.info("[SUCCESS] Store name set to product successfully");
                return Response.success("Store name set to product successfully", null);
            } else {
                SystemLogger.error("[ERROR] Product does not exist with ID: " + productID);
                return Response.error("Product does not exist with ID: " + productID, null);
            }
        } catch (Exception e) {
            SystemLogger.error("[ERROR] Failed to set store name to product: " + e.getMessage());
            return Response.error("Failed to set store name to product: " + e.getMessage(), null);
        }
    }

    public Response<String> getProductPrice(int productID) {
        if (productID < 0) {
            SystemLogger.error("[ERROR] Invalid product ID: " + productID);
            return Response.error("Invalid product ID: " + productID, null);
        }
        if (!isProductExist(productID)) {
            SystemLogger.error("[ERROR] Product with ID: " + productID + " does not exist.");
            return Response.error("Product with ID: " + productID + " does not exist.", null);
        }
        Product product = getProduct(productID);
        if (product == null) {
            SystemLogger.error("[ERROR] Product with ID: " + productID + " not found.");
            return Response.error("Product with ID: " + productID + " not found.", null);
        }
        double price = product.getPrice();
        SystemLogger.info("[SUCCESS] Product with ID: " + productID + " price retrieved successfully");
        return Response.success("Product with ID: " + productID + " price retrieved successfully", String.valueOf(price));
    }

    public synchronized Response<String> setProductPrice(int productID, double newPrice) {
        if (productID < 0) {
            SystemLogger.error("[ERROR] Invalid product ID: " + productID);
            return Response.error("Invalid product ID: " + productID, null);
        }
        if (!isProductExist(productID)) {
            SystemLogger.error("[ERROR] Product with ID: " + productID + " does not exist.");
            return Response.error("Product with ID: " + productID + " does not exist.", null);
        }
        Product product = getProduct(productID);
        if (product == null) {
            SystemLogger.error("[ERROR] Product with ID: " + productID + " not found.");
            return Response.error("Product with ID: " + productID + " not found.", null);
        }
        boolean bool = product.setPrice(newPrice);
        if(!bool){
            SystemLogger.error("[ERROR] Invalid price: Price cannot be negative");
            return Response.error("Invalid price: Price cannot be negative", null);
        }
        SystemLogger.info("[SUCCESS] Product with ID: " + productID + " price updated successfully to: " + newPrice);
        return Response.success("Product with ID: " + productID + " price updated successfully", String.valueOf(newPrice));
    }

    public Response<ArrayList<ProductDTO>> retrieveProductsByCategoryFrom_OneStore(String category) {
        if (category == null || category.isEmpty()) {
            SystemLogger.error("[ERROR] Invalid category: " + category);
            return Response.error("Invalid category: " + category, null);
        }
        if (!categories.containsKey(category)) {
            SystemLogger.error("[ERROR] Category does not exist: " + category);
            return Response.error("Category does not exist: " + category, null);
        }
        ArrayList<Integer> productIDs = categories.get(category);
        if (productIDs == null || productIDs.isEmpty()) {
            SystemLogger.error("[ERROR] No products found in category: " + category);
            return Response.error("No products found in category: " + category, null);
        }
        ArrayList<ProductDTO> products = new ArrayList<>();
        for (Integer productID : productIDs) {
            Product product = productsList.get(productID);
            if (product != null) {
                products.add(new ProductDTO(product));
            }
        }
        SystemLogger.info("[SUCCESS] Products retrieved successfully for category: " + category);
        return Response.success("Products retrieved successfully for category: " + category, products);
    }


    public synchronized Response<String> retrieveProductCategories(int productID) {
        if (productID < 0) {
            SystemLogger.error("[ERROR] Invalid product ID: " + productID);
            return Response.error("Invalid product ID: " + productID, null);
        }
        if (!isProductExist(productID)) {
            SystemLogger.error("[ERROR] Product with ID: " + productID + " does not exist.");
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
            SystemLogger.info("[SUCCESS] Categories retrieved successfully for product ID: " + productID);
            return Response.success("Categories retrieved successfully for product ID: " + productID, jsonString);
        } catch (JsonProcessingException e) {
            SystemLogger.error("[ERROR] Error processing JSON: " + e.getMessage());
            return Response.error("Error processing JSON: " + e.getMessage(), null);
        }
    }

    public List<String> getProductsCategoriesAsList(int productID) {
        if (productID < 0) {
            SystemLogger.error("[ERROR] Invalid product ID: " + productID);
            return null;
        }
        if (!isProductExist(productID)) {
            SystemLogger.error("[ERROR] Product with ID: " + productID + " does not exist.");
            return null;
        }
        List<String> relatedCategories = new ArrayList<>();
        for (Map.Entry<String, ArrayList<Integer>> entry : categories.entrySet()) {
            if (entry.getValue().contains(productID)) {
                relatedCategories.add(entry.getKey());
            }
        }
        return relatedCategories;
    }

    public Response<String> assignProductToCategory(int productID, String category) {
        if (productID < 0) {
            SystemLogger.error("[ERROR] Invalid product ID: " + productID);
            return Response.error("Invalid product ID: " + productID, null);
        }
        if (!isProductExist(productID)) {
            SystemLogger.error("[ERROR] Product with ID: " + productID + " does not exist.");
            return Response.error("Product with ID: " + productID + " does not exist.", null);
        }
        if (category == null || category.isEmpty()) {
            SystemLogger.error("[ERROR] Invalid category: " + category);
            return Response.error("Invalid category: " + category, null);
        }
        if(!categories.containsKey(category)){
            categories.put(category, new ArrayList<>());
        }
        ArrayList<Integer> productIDs = categories.get(category);
        if (productIDs.contains(productID)) {
            SystemLogger.error("[ERROR] Product already exists in category: " + category);
            return Response.error("Product already exists in category: " + category, null);
        }
        productIDs.add(productID);
        SystemLogger.info("[SUCCESS] Product with ID: " + productID + " assigned to category: " + category);
        return Response.success("Product with ID: " + productID + " assigned to category: " + category, category);
    }


    public void addProduct(Product product) {
    }


    public Response<String> removeCategoryFromStore(String category) {
        if (category == null || category.isEmpty()) {
            SystemLogger.error("[ERROR] Invalid category: " + category);
            return Response.error("Invalid category: " + category, null);
        }
        if (!categories.containsKey(category)) {
            SystemLogger.error("[ERROR] Category does not exist: " + category);
            return Response.error("Category does not exist: " + category, null);
        }
        categories.remove(category);
        SystemLogger.info("[SUCCESS] Category removed successfully: " + category);
        return Response.success("Category removed successfully: " + category, category);
    }


    public Response<ProductDTO> getProductFromStore(int productID) {
        if (productID < 0) {
            SystemLogger.error("[ERROR] Invalid product ID: " + productID);
            return Response.error("Invalid product ID: " + productID, null);
        }
        if (!isProductExist(productID)) {
            SystemLogger.error("[ERROR] Product with ID: " + productID + " does not exist.");
            return Response.error("Product with ID: " + productID + " does not exist.", null);
        }
        Product product = getProduct(productID);
        if (product == null) {
            SystemLogger.error("[ERROR] Product with ID: " + productID + " not found.");
            return Response.error("Product with ID: " + productID + " not found.", null);
        }
        SystemLogger.info("[SUCCESS] Product retrieved successfully");
        ArrayList<String> productCategories = new ArrayList<>();
        for(Map.Entry<String, ArrayList<Integer>> entry2 : categories.entrySet()){
            if(entry2.getValue().contains(productID)){
                productCategories.add(entry2.getKey());
            }
        }
        return Response.success("Product retrieved successfully", new ProductDTO(product, productCategories));
    }

    public Response<ArrayList<ProductDTO>> getAllProductsFromStore() {
        ArrayList<ProductDTO> products = new ArrayList<>();
        for (Map.Entry<Integer, Product> entry : productsList.entrySet()) {
            List<String> categories = getProductsCategoriesAsList(entry.getKey());
            if (categories.isEmpty()) {
                products.add(new ProductDTO(entry.getValue()));
            }
            else {
                products.add(new ProductDTO(entry.getValue(), new ArrayList<>(categories)));
            }
        }
        SystemLogger.info("[SUCCESS] Products retrieved successfully");
        return Response.success("Products retrieved successfully", products);
    }

    //product with no category will be added to General category
    public synchronized Response<String> addProductToStore(String storeID, String storeName, String name, String desc, double price, int quantity) {
        if (storeID == null || storeID.isEmpty()) {
            SystemLogger.error("[ERROR] Store ID cannot be null or empty");
            return Response.error("Store ID cannot be null or empty", null);
        }
        if (storeName == null || storeName.isEmpty()) {
            SystemLogger.error("[ERROR] Store name cannot be null or empty");
            return Response.error("Store name cannot be null or empty", null);
        }
        if (name == null || name.isEmpty()) {
            SystemLogger.error("[ERROR] Product name cannot be null or empty");
            return Response.error("Product name cannot be null or empty", null);
        }
        if (desc == null || desc.isEmpty()) {
            SystemLogger.error("[ERROR] Product description cannot be null or empty");
            return Response.error("Product description cannot be null or empty", null);
        }
        if (price <= 0) {
            SystemLogger.error("[ERROR] Price must be greater than 0");
            return Response.error("Price must be greater than 0", null);
        }
        if (quantity <= 0) {
            SystemLogger.error("[ERROR] Quantity must be greater than 0");
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
        SystemLogger.info("[SUCCESS] Product with ID: " + product.getProductID() + " added successfully");
        return Response.success("Product with ID: " + product.getProductID() + " added successfully", name);

    }

    //product with categories will be added to the categories he is associated with
    public synchronized Response<String> addProductToStore(String storeID, String storeName, String name, String desc, double price, int quantity, ArrayList<String> categories) {
        if (storeID == null || storeID.isEmpty()) {
            SystemLogger.error("[ERROR] Store ID cannot be null or empty");
            return Response.error("Store ID cannot be null or empty", null);
        }
        if (storeName == null || storeName.isEmpty()) {
            SystemLogger.error("[ERROR] Store name cannot be null or empty");
            return Response.error("Store name cannot be null or empty", null);
        }
        if (name == null || name.isEmpty()) {
            SystemLogger.error("[ERROR] Product name cannot be null or empty");
            return Response.error("Product name cannot be null or empty", null);
        }
        if (desc == null || desc.isEmpty()) {
            SystemLogger.error("[ERROR] Product description cannot be null or empty");
            return Response.error("Product description cannot be null or empty", null);
        }
        if (price <= 0) {
            SystemLogger.error("[ERROR] Price must be greater than 0");
            return Response.error("Price must be greater than 0", null);
        }
        if (quantity <= 0) {
            SystemLogger.error("[ERROR] Quantity must be greater than 0");
            return Response.error("Quantity must be greater than 0", null);
        }
        if (categories == null || categories.isEmpty()) {
            SystemLogger.error("[ERROR] Product must be associated with at least one category");
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
        SystemLogger.info("[SUCCESS] Product with ID: " + product.getProductID() + " added successfully");
        return Response.success("Product with ID: " + product.getProductID() + " added successfully", name);
    }

    public synchronized Response<String> removeProductFromStore(int productID) {
        if (productID < 0) {
            SystemLogger.error("[ERROR] Invalid product ID: " + productID);
            return Response.error("Invalid product ID: " + productID, null);
        }
        if (!isProductExist(productID)) {
            SystemLogger.error("[ERROR] Product with ID: " + productID + " does not exist.");
            return Response.error("Product with ID: " + productID + " does not exist.", null);
        }
        Product product = getProduct(productID);
        if (product == null) {
            SystemLogger.error("[ERROR] Product with ID: " + productID + " not found.");
            return Response.error("Product with ID: " + productID + " not found.", null);
        }
        productsList.remove(productID);
        for (Map.Entry<String, ArrayList<Integer>> entry : categories.entrySet()) {
            // Convert productID to an Integer object before removing
            entry.getValue().remove(Integer.valueOf(productID));
        }

        SystemLogger.info("[SUCCESS] Product with ID: " + productID + " removed successfully");
        return Response.success("Product with ID: " + productID + " removed successfully", product.getName());

    }

    public Response<ProductDTO> getProductByName(String productName) {
        if (productName == null || productName.isEmpty()) {
            SystemLogger.error("[ERROR] Product name cannot be null or empty");
            return Response.error("Product name cannot be null or empty", null);
        }
        for (Map.Entry<Integer, Product> entry : productsList.entrySet()) {
            if (entry.getValue().getName().equals(productName)) {
                ArrayList<String> productCategories = new ArrayList<>();
                for(Map.Entry<String, ArrayList<Integer>> entry2 : categories.entrySet()){
                    if(entry2.getValue().contains(entry.getKey())){
                        productCategories.add(entry2.getKey());
                    }
                }
                SystemLogger.info("[SUCCESS] Product with name: " + productName + " retrieved successfully");
                return Response.success("Product with name: " + productName + " retrieved successfully", new ProductDTO(entry.getValue(), productCategories));
            }
        }
        SystemLogger.error("[ERROR] Product with name: " + productName + " not found");
        return Response.error("Product with name: " + productName + " not found", null);
    }

    public Response<ArrayList<ProductDTO>> viewProductByCategory(String category) {
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

    public Response<ProductDTO> viewProductByName(String productName) {
        if (productName == null || productName.isEmpty()) {
            return Response.error("Product name cannot be null or empty", null);
        }
        for (Map.Entry<Integer, Product> entry : productsList.entrySet()) {
            if (entry.getValue().getName().toLowerCase(Locale.ROOT).equals(productName.toLowerCase(Locale.ROOT))) {
                return Response.success("Product with name: " + productName + " retrieved successfully", new ProductDTO(entry.getValue()));
            }
        }
        return Response.error("Product with name: " + productName + " not found", null);
    }

    public Response<String> isCategoryExist(String category) {
        if (category == null || category.isEmpty()) {
            return Response.error("Category cannot be null or empty", null);
        }
        if (categories.containsKey(category)) {
            return Response.success("Category: " + category + " exists", category);
        }
        return Response.error("Category: " + category + " does not exist", null);
    }







    public synchronized Response<Map<ProductDTO,Integer>> LockProducts(Map<String, Integer> MapShoppingCart) {
        ArrayList<Product> LockedProducts = new ArrayList<>();
        Map<ProductDTO,Integer> productDTOList = new HashMap<>();
        for (Map.Entry<String, Integer> entry : MapShoppingCart.entrySet()) {
            Integer productQuantity = entry.getValue();
            int productID = Integer.parseInt(entry.getKey());
            if (!isProductExist(productID)) {
                SystemLogger.error("[ERROR] Product with ID: " + productID + " does not exist.");
                return Response.error("Product with ID: " + productID + " does not exist.", null);
            }
            Product product = getProduct(productID);
            if (product == null) {
                SystemLogger.error("[ERROR] Product with ID: " + productID + " not found.");
                return Response.error("Product with ID: " + productID + " not found.", null);
            }
            if (product.getQuantity() - productQuantity < 0) { // check if product is out of stock
                SystemLogger.error("[ERROR] Product with ID: " + productID + " is out of stock.");
                for (Product LockedProduct : LockedProducts) { // unlock all locked products
                    int LockedQuantity = MapShoppingCart.get(String.valueOf(LockedProduct.getProductID()));
                    int oldLockedQuantity =  lockedProducts.get(LockedProduct);
                    lockedProducts.put(LockedProduct, oldLockedQuantity - LockedQuantity);
                    LockedProduct.addQuantity( LockedQuantity);

                }
                return Response.error("Product with ID: " + product.getName() + " in store: " + product.getStoreName() + " has only: " + product.getQuantity() + " units left in stock!", null);
            }
            product.addQuantity(-productQuantity);
            if(lockedProducts.containsKey(product)) {
                lockedProducts.put(product, lockedProducts.get(product) + productQuantity);
            } else {
                lockedProducts.put(product, productQuantity);
            }
            LockedProducts.add(product);
            productDTOList.put(new ProductDTO(product),productQuantity);
        }
        SystemLogger.info("[SUCCESS] Shopping cart locked successfully");
        return Response.success("Shopping cart locked successfully",productDTOList);
    }

    public synchronized Response<String> unlockProductsBackToStore(Map<String, Integer> stringIntegerMap) {
        for (Map.Entry<String, Integer> entry : stringIntegerMap.entrySet()) {
            Integer productID = Integer.parseInt(entry.getKey());
            int quantity = entry.getValue();
            if (!isProductExist(productID)) {
                SystemLogger.error("[ERROR] Product with ID: " + productID + " does not exist.");
                return Response.error("Product with ID: " + productID + " does not exist.", null);
            }
            Product product = getProduct(productID);
            if (product == null) {
                SystemLogger.error("[ERROR] Product with ID: " + productID + " not found.");
                return Response.error("Product with ID: " + productID + " not found.", null);
            }
            product.addQuantity(quantity);
            lockedProducts.put(product,lockedProducts.get(product) - quantity);
        }
        SystemLogger.info("[SUCCESS] Shopping cart unlocked successfully");
        return Response.success("Shopping cart unlocked successfully", null);
    }

    public synchronized Response<String> RemoveOrderFromStoreAfterSuccessfulPurchase(Map<String, Integer> productsInStore) {
        for (Map.Entry<String, Integer> entry : productsInStore.entrySet()) {
            Integer productID = Integer.parseInt(entry.getKey());
            int quantity = entry.getValue();
            if (!isProductExist(productID)) {
                SystemLogger.error("[ERROR] Product with ID: " + productID + " does not exist.");
                return Response.error("Product with ID: " + productID + " does not exist.", null);
            }
            Product product = getProduct(productID);
            if (product == null) {
                SystemLogger.error("[ERROR] Product with ID: " + productID + " not found.");
                return Response.error("Product with ID: " + productID + " not found.", null);
            }
            lockedProducts.put(product,lockedProducts.get(product) - quantity);
        }
        SystemLogger.info("[SUCCESS] Shopping cart unlocked successfully");
        return Response.success("Shopping cart unlocked successfully", null);
    }

    public Response<String> calculateShoppingCartPrice(Map<String, Integer> productsInStore) {
        double totalPrice = 0;
        for (Map.Entry<String, Integer> entry : productsInStore.entrySet()) {
            Integer productID = Integer.parseInt(entry.getKey());
            int quantity = entry.getValue();
            if (!isProductExist(productID)) {
                SystemLogger.error("[ERROR] Product with ID: " + productID + " does not exist.");
                return Response.error("Product with ID: " + productID + " does not exist.", null);
            }
            Product product = getProduct(productID);
            if (product == null) {
                SystemLogger.error("[ERROR] Product with ID: " + productID + " not found.");
                return Response.error("Product with ID: " + productID + " not found.", null);
            }
            totalPrice += product.getPrice() * quantity;
        }
        SystemLogger.info("[SUCCESS] Total price calculated successfully");
        return Response.success("Total price calculated successfully", String.valueOf(totalPrice));
    }

    public Response<String> removeProductFromCategory(int productId, String category) {
        if (productId < 0) {
            SystemLogger.error("[ERROR] Invalid product ID: " + productId);
            return Response.error("Invalid product ID: " + productId, null);
        }
        if (!isProductExist(productId)) {
            SystemLogger.error("[ERROR] Product with ID: " + productId + " does not exist.");
            return Response.error("Product with ID: " + productId + " does not exist.", null);
        }
        if (category == null || category.isEmpty()) {
            SystemLogger.error("[ERROR] Category cannot be null or empty");
            return Response.error("Category cannot be null or empty", null);
        }
        if (!categories.containsKey(category)) {
            SystemLogger.error("[ERROR] Category does not exist: " + category);
            return Response.error("Category does not exist: " + category, null);
        }
        ArrayList<Integer> productIDs = categories.get(category);
        if (!productIDs.contains(productId)) {
            SystemLogger.error("[ERROR] Product with ID: " + productId + " does not exist in category: " + category);
            return Response.error("Product with ID: " + productId + " does not exist in category: " + category, null);
        }
        productIDs.remove(Integer.valueOf(productId));
        SystemLogger.info("[SUCCESS] Product with ID: " + productId + " removed from category: " + category);
        return Response.success("Product with ID: " + productId + " removed from category: " + category, category);
    }
}
























