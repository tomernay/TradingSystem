package src.main.java.Domain.Store.Inventory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import src.main.java.Utilities.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import Domain.Store.Inventory.ProductDTO;

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
     * @return A response indicating the success or failure of the operation.
     */
    public synchronized Response<Boolean> addProductToCategory(Product product) {
        try {
            if (product.getCategories() != null && !product.getCategories().isEmpty()) {
                for (String category : product.getCategories()) {
                    categories.computeIfAbsent(category, k -> new ArrayList<>()).add(product.getProductID());
                }
                return new Response<>(true, "Product added to categories successfully", true);
            } else {
                return new Response<>(false, "Product categories are null or empty", false);
            }
        } catch (Exception e) {
            return new Response<>(false, "Failed to add product to categories: " + e.getMessage(), false);
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
     */
    public synchronized Response<Integer> removeProduct(int productID) {
        if (isProductExist(productID)) {
            productsList.remove(productID); // Remove from products list
            removeProductFromAllCategories(productID); // Remove from categories
            return Response.success("The product has been removed successfully", productID);
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
    public Response<Integer> setProductCategory(int productID, ArrayList<String> categories) {
        if (isProductExist(productID)) {
            if (categories != null) {
                Product product = productsList.get(productID);
                product.setCategories(categories);
                return new Response<>(true, "Product category set successfully", productID);
            } else {
                return new Response<>(false, "Product's category can't be null", productID);
            }
        } else {
            return new Response<>(false, "The product does not exist in the system", productID);
        }
    }



    /**
     * Removes the specified product ID from all categories in the categories map.
     * The method iterates through the categories map and removes the product ID from each list.
     * If a category becomes empty after the removal, the category is also removed from the map.
     *
     * @param productID The ID of the product to be removed from all categories.
     * @return Response object containing success status, message, and the product ID.
     */
    public synchronized Response<Integer> removeProductFromAllCategories(int productID) {
        boolean productRemoved = false;
        boolean productExists = false;

        // Create a temporary list to avoid ConcurrentModificationException
        List<String> categoriesToRemove = new ArrayList<>();

        for (Map.Entry<String, ArrayList<Integer>> entry : categories.entrySet()) {
            String category = entry.getKey();
            ArrayList<Integer> productList = entry.getValue();

            if (productList != null) { // Ensure the product list is not null
                if (productList.contains(productID)) {
                    productExists = true;
                }

                boolean removed = productList.remove(Integer.valueOf(productID)); // Remove the product ID from the list
                if (removed) {
                    productRemoved = true;
                }
                // Schedule the category for removal if the product list becomes empty
                if (productList.isEmpty()) {
                    categoriesToRemove.add(category);
                }
            }
        }

        // Remove empty categories after iteration to avoid ConcurrentModificationException
        for (String category : categoriesToRemove) {
            categories.remove(category);
        }

        if (productRemoved) {
            return new Response<>(true, "Product removed from all categories successfully", productID);
        } else if (!productExists) {
            return new Response<>(false, "Product does not exist in any category", productID);
        } else {
            return new Response<>(false, "Failed to remove product from categories", productID);
        }
    }



    public Response<String> removeCategoryFromProduct(int productID ,String category) {
        return getProduct(productID).getData().removeCategory(category);
    }


    /**
     * Retrieves product information in the form of a DTO (Data Transfer Object) given a product ID.
     *
     * @param productID The unique ID of the product to retrieve information for.
     * @return A DTO representing the product information.
     * @throws IllegalArgumentException If the provided product ID is null or if the product does not exist.
     */
    public Domain.Store.Inventory.ProductDTO getProductInfo(Integer productID) {
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

    /**
     * Checks if the product with the specified ID exists in the products list.
     *
     * @param productID The ID of the product to check.
     * @return true if the product exists, false otherwise.
     */
    public boolean isProductExist(Integer productID) {
        return productsList.containsKey(productID);
    }


    /**
     * Retrieves the categories that contain the specified product ID.
     * The method iterates through the categories map and collects the names
     * of categories that include the given product ID.
     * The result is returned as a JSON string.
     *
     * @param productId The ID of the product to search for in the categories.
     * @return A Response object containing the JSON string representing the list of category names that include the given product ID.
     */
    public synchronized Response<String> getProductsByCategory(int productId) {
        List<String> relatedCategories = new ArrayList<>();
        for (String category : categories.keySet()) {
            List<Integer> productList = categories.get(category);
            if (productList != null && productList.contains(productId)) {
                relatedCategories.add(category);
            }
        }
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString;
        try {
            jsonString = objectMapper.writeValueAsString(relatedCategories);
            return new Response<>(true, "Successfully retrieved categories for product ID " + productId, jsonString);
        } catch (JsonProcessingException e) {
            return new Response<>(false, "Error processing JSON: " + e.getMessage());
        }
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


    public synchronized Response<Integer> setProductID(Integer oldProductID, Integer newProductID) {
        // Check if the old product ID exists
        if (!isProductExist(oldProductID)) {
            return new Response<>(false, "Old product ID does not exist: " + oldProductID);
        }
        // Check if the new product ID is valid
        if (newProductID == null || newProductID <= 0) {
            return new Response<>(false, "Invalid new product ID: " + newProductID);
        }
        // Check if the new product ID already exists
        if (isProductExist(newProductID)) {
            return new Response<>(false, "New product ID already exists: " + newProductID);
        }
        // Retrieve the product with the old ID
        Product product = productsList.get(oldProductID);
        if (product == null) {
            return new Response<>(false, "Product not found with old product ID: " + oldProductID);
        }

        // Remove the old product ID from all categories
        Response<Integer> removeResponse = removeProductFromAllCategories(oldProductID);
        if (!removeResponse.isSuccess()) {
            return new Response<>(false, "Failed to remove product from categories: " + removeResponse.getMessage());
        }

        // Set the new product ID
        product.setProductID(newProductID);

        // Add the product to the products list with the new ID
        productsList.remove(oldProductID);
        productsList.put(newProductID, product);

        // Add the product to the categories
        Response<Boolean> addResponse = addProductToCategory(product);
        if (!addResponse.isSuccess()) {
            return new Response<>(false, "Failed to add product to categories: " + addResponse.getMessage());
        }

        return new Response<>(true, "Product ID updated successfully from " + oldProductID + " to " + newProductID);
    }


    public synchronized Response<Product> getProduct(Integer productID) {
        if (productID == null || productID <= 0) {
            return new Response<>(false, "Invalid product ID: " + productID);
        }
        Product product = productsList.get(productID);
        if (product != null) {
            return new Response<>(true, "Product retrieved successfully", product);
        } else {
            return new Response<>(false, "Product not found with ID: " + productID);
        }
    }



    public synchronized Response<Integer> setProductName(Integer productID, String newName) {
        if (productID == null || productID <= 0) {
            return new Response<>(false, "Invalid product ID: " + productID);
        }

        if (newName == null || newName.isEmpty()) {
            return new Response<>(false, "New product name is null or empty");
        }

        if (!isProductExist(productID)) {
            return new Response<>(false, "Product does not exist with ID: " + productID);
        }

        Product product = getProduct(productID).getData();
        if (product != null) {
            product.setName(newName);
            return new Response<>(true, "Product name updated successfully");
        } else {
            return new Response<>(false, "Failed to update product name");
        }
    }


    public synchronized Response<Integer> setProductDesc(Integer productID, String newDesc) {
        if (productID == null || productID <= 0) {
            return new Response<>(false, "Invalid product ID: " + productID);
        }

        if (newDesc == null) {
            return new Response<>(false, "New product description is null");
        }

        if (!isProductExist(productID)) {
            return new Response<>(false, "Product does not exist with ID: " + productID);
        }

        Product product = getProduct(productID).getData();
        if (product != null) {
            product.setDesc(newDesc);
            return new Response<>(true, "Product description updated successfully");
        } else {
            return new Response<>(false, "Failed to update product description");
        }
    }


    public synchronized Response<Integer> setPrice(Integer productID, int newPrice) {
        if (productID == null || productID <= 0) {
            return new Response<>(false, "Invalid product ID: " + productID);
        }

        if (newPrice <= 0) {
            return new Response<>(false, "Invalid new price: " + newPrice);
        }

        if (!isProductExist(productID)) {
            return new Response<>(false, "Product does not exist with ID: " + productID);
        }

        Product product = getProduct(productID).getData();
        if (product != null) {
            product.setPrice(newPrice);
            return new Response<>(true, "Product price updated successfully");
        } else {
            return new Response<>(false, "Failed to update product price");
        }
    }


    public synchronized Response<Integer> setQuantity(Integer productID, int newQuantity) {
        if (productID == null || productID <= 0) {
            return new Response<>(false, "Invalid product ID: " + productID);
        }

        if (newQuantity < 0) {
            return new Response<>(false, "Invalid new quantity: " + newQuantity);
        }

        if (!isProductExist(productID)) {
            return new Response<>(false, "Product does not exist with ID: " + productID);
        }

        Product product = getProduct(productID).getData();
        if (product != null) {
            product.setQuantity(newQuantity);
            return new Response<>(true, "Product quantity updated successfully");
        } else {
            return new Response<>(false, "Failed to update product quantity");
        }
    }


    public synchronized Response<Integer> addQuantity(Integer productID, int valueToAdd) {
        if (productID == null || productID <= 0) {
            return new Response<>(false, "Invalid product ID: " + productID);
        }

        if (valueToAdd <= 0) {
            return new Response<>(false, "Invalid quantity value to add: " + valueToAdd);
        }

        if (!isProductExist(productID)) {
            return new Response<>(false, "Product does not exist with ID: " + productID);
        }

        Product product = getProduct(productID).getData();
        if (product != null) {
            product.addQuantity(valueToAdd);
            return new Response<>(true, "Quantity added to product successfully");
        } else {
            return new Response<>(false, "Failed to add quantity to product");
        }
    }


    public synchronized Response<Integer> getQuantity(int productID) {
        try {
            if (isProductExist(productID)) {
                int quantity = getProduct(productID).getData().getQuantity();
                return new Response<>(true, "Quantity retrieved successfully", quantity);
            } else {
                return new Response<>(false, "Product does not exist with ID: " + productID);
            }
        } catch (Exception e) {
            return new Response<>(false, "Failed to retrieve quantity: " + e.getMessage());
        }
    }


    public synchronized Response<String> getStoreID(int productID) {
        try {
            if (isProductExist(productID)) {
                String storeID = getProduct(productID).getData().getStoreID();
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
                String storeName = getProduct(productID).getData().getStoreName();
                return new Response<>(true, "Store name retrieved successfully", storeName);
            } else {
                return new Response<>(false, "Product does not exist with ID: " + productID);
            }
        } catch (Exception e) {
            return new Response<>(false, "Failed to retrieve store name: " + e.getMessage());
        }
    }


    public synchronized Response<String> getProductDescription(int productID) {
        try {
            if (isProductExist(productID)) {
                String description = getProduct(productID).getData().getDesc();
                return new Response<>(true, "Product description retrieved successfully", description);
            } else {
                return new Response<>(false, "Product does not exist with ID: " + productID);
            }
        } catch (Exception e) {
            return new Response<>(false, "Failed to retrieve product description: " + e.getMessage());
        }
    }


    public synchronized Response<Integer> getProductPrice(int productID) {
        try {
            if (isProductExist(productID)) {
                int price = getProduct(productID).getData().getPrice();
                return new Response<>(true, "Product price retrieved successfully", price);
            } else {
                return new Response<>(false, "Product does not exist with ID: " + productID);
            }
        } catch (Exception e) {
            return new Response<>(false, "Failed to retrieve product price: " + e.getMessage());
        }
    }


    public synchronized Response<Integer> setStoreIDToProduct(int productID, String storeID) {
        try {
            if (isProductExist(productID)) {
                getProduct(productID).getData().setStoreID(storeID);
                return new Response<>(true, "Store ID set to product successfully");
            } else {
                return new Response<>(false, "Product does not exist with ID: " + productID);
            }
        } catch (Exception e) {
            return new Response<>(false, "Failed to set store ID to product: " + e.getMessage());
        }
    }

    public synchronized Response<String> getProductName(int productID) {
        try {
            if (isProductExist(productID)) {
                String productName = getProduct(productID).getData().getName();
                return new Response<>(true, "Product name retrieved successfully", productName);
            } else {
                return new Response<>(false, "Product does not exist with ID: " + productID);
            }
        } catch (Exception e) {
            return new Response<>(false, "Failed to retrieve product name: " + e.getMessage());
        }
    }

    public synchronized Response<Integer> setProductName(int productID, String productName) {
        try {
            if (isProductExist(productID)) {
                getProduct(productID).getData().setName(productName);
                return new Response<>(true, "Product name set successfully");
            } else {
                return new Response<>(false, "Product does not exist with ID: " + productID);
            }
        } catch (Exception e) {
            return new Response<>(false, "Failed to set product name: " + e.getMessage());
        }
    }

    public synchronized Response<Integer> setStoreNameToProduct(int productID, String storeName) {
        try {
            if (isProductExist(productID)) {
                getProduct(productID).getData().setStoreName(storeName);
                return new Response<>(true, "Store name set to product successfully");
            } else {
                return new Response<>(false, "Product does not exist with ID: " + productID);
            }
        } catch (Exception e) {
            return new Response<>(false, "Failed to set store name to product: " + e.getMessage());
        }
    }


























    public void addProduct(Product product) {
    }






}





