package Presentation.application.Presenter.Store;

import Domain.Store.Conditions.ConditionDTO;
import Domain.Store.Discounts.DiscountDTO;
import Domain.Store.Discounts.TYPE;
import Presentation.application.CookiesHandler;
import Presentation.application.View.Store.DiscountBox;
import Presentation.application.View.Store.PolicyBox;
import Presentation.application.View.Store.StoreManagementView;
import Service.ServiceInitializer;
import Service.StoreService;
import Service.UserService;
import Utilities.Response;
import Domain.Store.Inventory.ProductDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
public class StoreManagementPresenter {
    private StoreManagementView view;
    private final UserService userService; // Assuming you have a UserService
    private final StoreService storeService; // Assuming you have a StoreService
    private final HttpServletRequest request;

    public StoreManagementPresenter(HttpServletRequest request) {
        this.userService = ServiceInitializer.getInstance().getUserService();
        this.storeService = ServiceInitializer.getInstance().getStoreService();
        this.request = request;
    }

    public void attachView(StoreManagementView view) {
        this.view = view;
    }

    public List<ProductDTO> getProducts(Integer storeId) {
        String username = CookiesHandler.getUsernameFromCookies(request);
        String token = CookiesHandler.getTokenFromCookies(request);
        Response<ArrayList<ProductDTO>> response = storeService.getAllProductsFromStore(storeId, username, token);
        if (response.isSuccess()) {
            return response.getData();
        } else {
            // Handle error (e.g., log it, show a message to the user)
            return new ArrayList<>();
        }
    }

    public List<DiscountDTO> loadDiscounts(Integer storeId) {
        String username = CookiesHandler.getUsernameFromCookies(request);
        String token = CookiesHandler.getTokenFromCookies(request);
        Response<List<DiscountDTO>> response = storeService.getDiscountsFromStore(storeId, username, token);
        return response.getData();
    }

    public List<ConditionDTO> loadPolicies(Integer storeId) {
        String username = CookiesHandler.getUsernameFromCookies(request);
        String token = CookiesHandler.getTokenFromCookies(request);
        Response<List<ConditionDTO>> response = storeService.getPoliciesFromStore(storeId, username, token);
        return response.getData();
    }

    public void saveDiscount(Integer storeId, String type, Double discountPercent, DiscountBox discount1, DiscountBox discount2, String discountType, PolicyBox condition, String typeDiscount ,String value) {
        String username = CookiesHandler.getUsernameFromCookies(request);
        String token = CookiesHandler.getTokenFromCookies(request);
        if (Objects.equals(type, "Simple")) {
            storeService.CreateSimpleDiscount(username, token, storeId,discountPercent, typeDiscount, value);
        }
        else if (Objects.equals(type, "Complex")) {
            storeService.makeComplexDiscount(username, token, storeId, discount1.getID(), discount2.getID(), discountType);
        }
        else if (Objects.equals(type, "Condition")) {
            storeService.makeConditionDiscount(username, token, storeId, discount1.getID(), condition.getID());
        }
    }

    public void removeDiscount(Integer storeId, DiscountDTO discountDTO) {
        String username = CookiesHandler.getUsernameFromCookies(request);
        String token = CookiesHandler.getTokenFromCookies(request);
        storeService.removeDiscount(storeId, username, token, discountDTO.getDiscountID());
    }

    public void savePolicy(Integer storeId, String type, String typePolicy, String value, String quantityType, Double quantity, Double minQuantity, Double maxQuantity, PolicyBox policy1, PolicyBox policy2, String policyConditionType) {
        String username = CookiesHandler.getUsernameFromCookies(request);
        String token = CookiesHandler.getTokenFromCookies(request);

        if ("Simple".equals(type)) {
            switch (quantityType) {
                case "At most" ->
                        storeService.addSimplePolicyToStore(username, token, storeId, null, null, quantity, typePolicy, value);
                case "At least" ->
                        storeService.addSimplePolicyToStore(username, token, storeId, null, quantity, null, typePolicy, value);
                case "Exactly" ->
                        storeService.addSimplePolicyToStore(username, token, storeId, quantity, null, null, typePolicy, value);
                case "Between" ->
                        storeService.addSimplePolicyToStore(username, token, storeId, null, minQuantity, maxQuantity, typePolicy, value);
            }
//            if ("Category".equals(policyType)) {
//                switch (quantityType) {
//                    case "At most" ->
//                            storeService.addSimplePolicyToStore(username, token, storeId, null, null, quantity,typePolicy , value);
//                    case "At least" ->
//                            storeService.addSimplePolicyToStore(username, token, storeId, null, quantity, null,typePolicy , value);
//                    case "Exactly" ->
//                            storeService.addSimplePolicyToStore(username, token, storeId, quantity, null, null,typePolicy , value);
//                    case "Between" ->
//                            storeService.addSimplePolicyToStore(username, token, storeId, null, minQuantity, maxQuantity,typePolicy , value);
//                }
//            } else if ("Product".equals(policyType)) {
//                switch (quantityType) {
//                    case "At most" ->
//                            storeService.addSimplePolicyToStore(username, token, storeId, null, null, quantity, , null);
//                    case "At least" ->
//                            storeService.addSimplePolicyToStore(username, token, storeId, null, quantity, null, , null);
//                    case "Exactly" ->
//                            storeService.addSimplePolicyToStore(username, token, storeId, quantity, null, null, , null);
//                    case "Between" ->
//                            storeService.addSimplePolicyToStore(username, token, storeId, null, minQuantity, maxQuantity, , null);
//                }
//            } else if ("Price".equals(policyType)) {
//                switch (quantityType) {
//                    case "At most" ->
//                            storeService.addSimplePolicyToStore(username, token, storeId, null, null, price, , true);
//                    case "At least" ->
//                            storeService.addSimplePolicyToStore(username, token, storeId, null, price, null, , true);
//                    case "Exactly" ->
//                            storeService.addSimplePolicyToStore(username, token, storeId, price, null, null, , true);
//                    case "Between" ->
//                            storeService.addSimplePolicyToStore(username, token, storeId, null, minPrice, maxPrice, , true);
//                }
//            }
        } else if ("Complex".equals(type)) {
            storeService.makeComplexCondition(token, storeId, policy1.getID(), policy2.getID(), policyConditionType, username);
        } else if ("Condition".equals(type)) {
            storeService.makeConditionPolicy(username, token, storeId, policy1.getID(), policy2.getID());
        }
    }


    public void removePolicy(Integer storeId, ConditionDTO conditionDTO) {
        String username = CookiesHandler.getUsernameFromCookies(request);
        String token = CookiesHandler.getTokenFromCookies(request);
        storeService.removePolicy(storeId, username, token, conditionDTO.getConditionID());
    }

    public String getProductName(Integer storeId, Integer productId) {
        String username = CookiesHandler.getUsernameFromCookies(request);
        String token = CookiesHandler.getTokenFromCookies(request);
        return storeService.getProductName(productId, storeId, username, token).getData();
    }

    public boolean hasPermission(Integer storeID, String permission) {
        String username = CookiesHandler.getUsernameFromCookies(request);
        return storeService.hasPermission(storeID, username, permission);
    }

    public boolean isCreator(Integer storeId) {
        String username = CookiesHandler.getUsernameFromCookies(request);
        return storeService.isStoreCreator(storeId, username);
    }

    public void closeStore(Integer storeId) {
        String username = CookiesHandler.getUsernameFromCookies(request);
        String token = CookiesHandler.getTokenFromCookies(request);
        storeService.closeStore(storeId, username, token);
    }

    public void reopenStore(Integer storeId) {
        String username = CookiesHandler.getUsernameFromCookies(request);
        String token = CookiesHandler.getTokenFromCookies(request);
        storeService.reopenStore(storeId, username, token);
    }

    public boolean isActiveStore(Integer storeId) {
        String username = CookiesHandler.getUsernameFromCookies(request);
        String token = CookiesHandler.getTokenFromCookies(request);
        return storeService.isStoreActive(storeId, username, token);
    }

    public boolean isLoggedIn() {
        String username = CookiesHandler.getUsernameFromCookies(request);
        String token = CookiesHandler.getTokenFromCookies(request);
        return userService.isValidToken(token, username);
    }

    public String getStoreName(Integer storeId) {
        String username = CookiesHandler.getUsernameFromCookies(request);
        String token = CookiesHandler.getTokenFromCookies(request);
        return storeService.getStoreNameByID(storeId, username, token).getData();
    }

    public boolean isSuspended() {
        String username = CookiesHandler.getUsernameFromCookies(request);
        return userService.isSuspended(username);
    }
}
