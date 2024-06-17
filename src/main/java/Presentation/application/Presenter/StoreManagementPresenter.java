package Presentation.application.Presenter;

import Domain.Store.Conditions.ConditionDTO;
import Domain.Store.Discounts.DiscountDTO;
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
    private HttpServletRequest request;

    public StoreManagementPresenter(HttpServletRequest request) {
        this.userService = ServiceInitializer.getInstance().getUserService();
        this.storeService = ServiceInitializer.getInstance().getStoreService();
        this.request = request;
    }

    public void attachView(StoreManagementView view) {
        this.view = view;
    }

    public List<ProductDTO> getProducts(String storeId) {
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

    public List<DiscountDTO> loadDiscounts(String storeId) {
        String username = CookiesHandler.getUsernameFromCookies(request);
        String token = CookiesHandler.getTokenFromCookies(request);
        Response<List<DiscountDTO>> response = storeService.getDiscountsFromStore(storeId, username, token);
        return response.getData();
    }

    public List<ConditionDTO> loadPolicies(String storeId) {
        String username = CookiesHandler.getUsernameFromCookies(request);
        String token = CookiesHandler.getTokenFromCookies(request);
        Response<List<ConditionDTO>> response = storeService.getPoliciesFromStore(storeId, username, token);
        return response.getData();
    }

    public void saveDiscount(String storeId, String type, String productId, String category, Double discountPercent, String productName, DiscountBox discount1, DiscountBox discount2, String discountType, PolicyBox condition) {
        String username = CookiesHandler.getUsernameFromCookies(request);
        String token = CookiesHandler.getTokenFromCookies(request);
        if (Objects.equals(type, "Simple")) {
            Response<String> res = storeService.CreatDiscountSimple(username, token, productId, storeId, category, String.valueOf(discountPercent));
        }
        else if (Objects.equals(type, "Complex")) {
            Response<String> res =  storeService.makeComplexDiscount(username, token, storeId, Integer.parseInt(discount1.getID()), Integer.parseInt(discount2.getID()), discountType);
        }
        else if (Objects.equals(type, "Condition")) {
            Response<String> res =  storeService.makeConditionDiscount(username, token, storeId, Integer.parseInt(discount1.getID()), Integer.parseInt(condition.getID()));
        }
    }

    public void removeDiscount(String storeId, DiscountDTO discountDTO) {
        String username = CookiesHandler.getUsernameFromCookies(request);
        String token = CookiesHandler.getTokenFromCookies(request);
        storeService.removeDiscount(storeId, username, token, String.valueOf(discountDTO.getDiscountID()));
    }

    public void savePolicy(String storeId, String type, String policyType, String category, String productId, String quantityType, String quantity, String minQuantity, String maxQuantity, String price, String minPrice, String maxPrice, PolicyBox policy1, PolicyBox policy2, String policyConditionType) {
        String username = CookiesHandler.getUsernameFromCookies(request);
        String token = CookiesHandler.getTokenFromCookies(request);

        if ("Simple".equals(type)) {
            if ("Category".equals(policyType)) {
                if (quantityType.equals("At most")) {
                    Response<String> res = storeService.addSimplePolicyToStore(username, token, storeId, category, null, null, null, Double.parseDouble(quantity), null);
                }
                else if (quantityType.equals("At least")) {
                    Response<String> res = storeService.addSimplePolicyToStore(username, token, storeId, category, null, null, Double.parseDouble(quantity), null, null);
                }
                else if (quantityType.equals("Exactly")) {
                    Response<String> res = storeService.addSimplePolicyToStore(username, token, storeId, category, null, Double.parseDouble(quantity), null, null, null);
                }
                else if (quantityType.equals("Between")) {
                    Response<String> res = storeService.addSimplePolicyToStore(username, token, storeId, category, null, null, Double.parseDouble(minQuantity), Double.parseDouble(maxQuantity), null);
                }
            } else if ("Product".equals(policyType)) {
                if (quantityType.equals("At most")) {
                    Response<String> res = storeService.addSimplePolicyToStore(username, token, storeId, null, Integer.parseInt(productId), null, null, Double.parseDouble(quantity), null);
                }
                else if (quantityType.equals("At least")) {
                    Response<String> res = storeService.addSimplePolicyToStore(username, token, storeId, null, Integer.parseInt(productId), null, Double.parseDouble(quantity), null, null);
                }
                else if (quantityType.equals("Exactly")) {
                    Response<String> res = storeService.addSimplePolicyToStore(username, token, storeId, null, Integer.parseInt(productId), Double.parseDouble(quantity), null, null, null);
                }
                else if (quantityType.equals("Between")) {
                    Response<String> res = storeService.addSimplePolicyToStore(username, token, storeId, null, Integer.parseInt(productId), null, Double.parseDouble(minQuantity), Double.parseDouble(maxQuantity), null);
                }
            } else if ("Price".equals(policyType)) {
                if (quantityType.equals("At most")) {
                    Response<String> res = storeService.addSimplePolicyToStore(username, token, storeId, null, null, null, null, Double.parseDouble(price), 1.0);
                }
                else if (quantityType.equals("At least")) {
                    Response<String> res = storeService.addSimplePolicyToStore(username, token, storeId, null, null, null, Double.parseDouble(price), null, 1.0);
                }
                else if (quantityType.equals("Exactly")) {
                    Response<String> res = storeService.addSimplePolicyToStore(username, token, storeId, null, null, Double.parseDouble(price), null, null, 1.0);
                }
                else if (quantityType.equals("Between")) {
                    Response<String> res = storeService.addSimplePolicyToStore(username, token, storeId, null, null, null, Double.parseDouble(minPrice), Double.parseDouble(maxPrice), 1.0);
                }
            }
        } else if ("Complex".equals(type)) {
            Response<String> res = storeService.makeComplexCondition(username, token, storeId, Integer.parseInt(policy1.getID()), Integer.parseInt(policy2.getID()), policyConditionType);
        } else if ("Condition".equals(type)) {
            Response<String> res = storeService.makeConditionPolicy(username, token, storeId, Integer.parseInt(policy1.getID()), Integer.parseInt(policy2.getID()));
        }
    }


    public void removePolicy(String storeId, ConditionDTO conditionDTO) {
        String username = CookiesHandler.getUsernameFromCookies(request);
        String token = CookiesHandler.getTokenFromCookies(request);
        storeService.removePolicy(storeId, username, token, String.valueOf(conditionDTO.getConditionID()));
    }

    public String getProductName(String storeId, String productId) {
        String username = CookiesHandler.getUsernameFromCookies(request);
        String token = CookiesHandler.getTokenFromCookies(request);
        return storeService.getProductName(Integer.parseInt(productId), storeId, username, token).getData();
    }
}
