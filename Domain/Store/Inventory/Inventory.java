package Domain.Store.Inventory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import Utilities.Response;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class Inventory {
    private String storeID; //Inventory for specific store
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

    public ConcurrentHashMap<Integer, Product> getProductsList() {
        return productsList;
    }

    public ConcurrentHashMap<String, ArrayList<Integer>> getCategories() {
        return categories;
    }

    /**
     *  @param builder: Builder (to create different products based on the params)
     *  @param productID: unique in the system, (assumption)
     */
    public synchronized Response<Integer> addProduct(Product.Builder builder, Integer productID) {
        if (!isProductExist(productID)) {
            builder.productID(productID); // Set the product ID using the provided productID
            Product product = builder.build(); // Build the product using the provided builder
            productsList.put(product.getProductID(), product);
            addProductToCategory(product);
            return Response.success("The product created successfully", product.getProductID());
        } else {
            return Response.error("The product already exists in the system, the creation is cancelled", productID);
        }
    }

    /**
     * Adds a product's ID to the appropriate categories in the categories map.
     * If a category does not already exist in the map, it is created.
     *
     * @param product The product to add to the categories.
     */
    public void addProductToCategory(Product product) {
        for (String category : product.getCategories()) {
            categories.computeIfAbsent(category, k -> new ArrayList<>()).add(product.getProductID());
        }
    }

    /**
     * Removes a product from the inventory.
     * This method removes the product with the specified product ID from the products list
     * and also removes the product ID from all categories it belongs to.
     *
     * @param productID The ID of the product to be removed.
     * @return A Response object containing the result of the removal operation.
     *         - If successful, returns a success response with the product ID.
     *         - If the product does not exist, returns an error response with the product ID.
     * @throws IllegalArgumentException if the provided productID is not valid.
     */
    public synchronized Response<Integer> removeProduct(int productID) {
        if (isProductExist(productID)) {
            productsList.remove(productID); // Remove from products list
            removeProductFromAllCategories(productID); // Remove from categories
            return Response.success("The product removed successfully", productID);
        } else {
            return Response.error("The product does not exist in the system, the removal is cancelled", productID);
        }
    }

    /**
     * Sets the categories for the product with the specified product ID.
     *
     * @param productID The ID of the product to set categories for.
     * @param categories The list of categories to be set for the product.
     * @throws IllegalArgumentException if the provided productID is not valid.
     */
    public void setProductCategory(int productID, ArrayList<String> categories) {
        if (isProductExist(productID)) {
            Product product = productsList.get(productID);
            product.setCategories(categories);
        } else {
            throw new IllegalArgumentException("Product with ID " + productID + " does not exist.");
        }
    }


    /**
     * Removes the specified product ID from all categories in the categories map.
     * The method iterates through the categories map and removes the product ID from each list.
     * If a category becomes empty after the removal, the category is also removed from the map.
     *
     * @param productID The ID of the product to be removed from all categories.
     * @throws IllegalArgumentException if the provided productID is not valid.
     */
    public synchronized void removeProductFromAllCategories(int productID) {
        for (String category : categories.keySet()) {
            ArrayList<Integer> productList = categories.get(category);
            if (productList != null) { // Ensure the product list is not null
                productList.remove(Integer.valueOf(productID)); // Remove the product ID from the list
                // Remove the category if the product list becomes empty
                if (productList.isEmpty()) {
                    categories.remove(category);
                }
            }
        }
    }

    public Response<String> removeCategoryFromProduct(int productID ,String category) {
        return getProduct(productID).removeCategory(category);
    }


    /**
     * Retrieves product information in the form of a DTO (Data Transfer Object) given a product ID.
     *
     * @param productID The unique ID of the product to retrieve information for.
     * @return A DTO representing the product information.
     * @throws IllegalArgumentException If the provided product ID is null or if the product does not exist.
     */
    public ProductDTO getProductInfo(Integer productID) {
        if (productID == null) {
            throw new IllegalArgumentException("Product ID cannot be null.");
        }
        // Ensure that the product exists in the productsList
        if (!isProductExist(productID)) {
            throw new IllegalArgumentException("Product with ID " + productID + " does not exist.");
        }
        Product product = productsList.get(productID);
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.convertValue(product, ProductDTO.class);
    }

    public boolean isProductExist(Integer productID){
        return productsList.contains(productID);
    }

    /**
     * Retrieves the categories that contain the specified product ID.
     * The method iterates through the categories map and collects the names
     * of categories that include the given product ID.
     * The result is returned as a JSON string.
     *
     * @param productId The ID of the product to search for in the categories.
     * @return A JSON string representing the list of category names that include the given product ID.
     * @throws JsonProcessingException If there is an error during JSON processing.
     */
    public synchronized String getProductsByCategory(int productId) throws JsonProcessingException {
        ArrayList<String> relatedCategories = new ArrayList<>();
        for(String category : categories.keySet()){
            if(categories.get(category).contains(productId)){
                relatedCategories.add(category);
            }
        }
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(relatedCategories);
    }


    /**
     * Retrieves the product with the specified product ID and converts it to a ProductDTO.
     * The method fetches the product from the productsList map and uses ObjectMapper
     * to convert the Product object to a ProductDTO object.
     *
     * @param productID The ID of the product to retrieve.
     * @return A ProductDTO object representing the product with the specified product ID.
     */
    public synchronized ProductDTO getProductCategory(int productID) {
        Product product = productsList.get(productID);
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.convertValue(product, ProductDTO.class);
    }


    public void setProductID(Integer oldProductID, Integer newProductID) {
        Product product = productsList.get(oldProductID);
        productsList.remove(oldProductID);
        removeProductFromAllCategories(oldProductID);

        product.setProductID(newProductID);
        productsList.put(newProductID, product);
        addProductToCategory(product);
    }

    public Product getProduct(Integer productID) {
        return productsList.get(productID);
    }


    public void setProductName(Integer productID, String newName) {
        if(isProductExist(productID)){
            getProduct(productID).setName(newName);
        }
    }

    public void setProductDesc(Integer productID, String newDesc) {
        if(isProductExist(productID)){
            getProduct(productID).setDesc(newDesc);
        }
    }

    public void setPrice(Integer productID, int newPrice) {
        if(isProductExist(productID)){
            getProduct(productID).setPrice(newPrice);
        }
    }

    public void setQuantity(Integer productID, int newQuantity) {
        if(isProductExist(productID)){
            getProduct(productID).setQuantity(newQuantity);
        }
    }

    public void addQuantity(Integer productID, int valueToAdd) {
        if(isProductExist(productID)){
            getProduct(productID).addQuantity(valueToAdd);
        }
    }

    public int getQuantity(int productID) throws Exception{
        if(isProductExist(productID)){
            return getProduct(productID).getQuantity();
        }
        throw new Exception("The product doesn't exist");
    }

    public String getStoreID(int productID) throws Exception {
        if (isProductExist(productID)) {
            return getProduct(productID).getStoreID();
        }
        throw new Exception("The product doesn't exist.");
    }

    public String getStoreName(int productID) throws Exception {
        if (isProductExist(productID)) {
            return getProduct(productID).getStoreName();
        }
        throw new Exception("The product doesn't exist.");
    }

    public String getProductDescription(int productID) throws Exception {
        if (isProductExist(productID)) {
            return getProduct(productID).getDesc();
        }
        throw new Exception("The product doesn't exist.");
    }

    public int getProductPrice(int productID) throws Exception {
        if (isProductExist(productID)) {
            return getProduct(productID).getPrice();
        }
        throw new Exception("The product doesn't exist.");
    }

    public void setStoreIDToProduct(int productID, String storeID){
        getProduct(productID).setStoreID(storeID);
    }

    public void getProductName(int productID) {
        getProduct(productID).getName();
    }

    public void setProductName(int productID ,String storeName) {
        getProduct(productID).setName(storeName);
    }

    public void setStoreNameToProduct(int productID ,String storeName) {
        getProduct(productID).setStoreName(storeName);
    }

























    public void addProduct(Product product) {
    }






}





