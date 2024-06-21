package Presentation.application.Presenter;

import Domain.Store.Inventory.ProductDTO;
import Presentation.application.CookiesHandler;
import Presentation.application.View.PaymentView;
import Service.OrderService;
import Service.ServiceInitializer;
import Service.StoreService;
import Service.UserService;
import Utilities.Response;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class PaymentPresenter {
    private PaymentView paymentView;
    private final OrderService orderService;
    private final StoreService storeService;
    private final UserService userService;
    private HttpServletRequest request;

    public PaymentPresenter(HttpServletRequest request) {
        orderService = ServiceInitializer.getInstance().getOrderService();
        storeService = ServiceInitializer.getInstance().getStoreService();
        userService = ServiceInitializer.getInstance().getUserService();
        this.request = request;
    }

    public void attachView(PaymentView view) {
        this.paymentView = view;
    }

    public void pay(String user, Double totalPrice, List<ProductDTO> products, String creditCardNumber, String expirationDate, String cvv, String fullName, String address, String token, String ID) {
        Response<String> payRes = orderService.payAndSupply(products, totalPrice, user, token, address, creditCardNumber, expirationDate, cvv, fullName, ID);
        if (!paymentView.hasTimerEnded() || creditCardNumber == null) {
            paymentView.showNotification(payRes.getMessage());
        }
        else{
            paymentView.showNotification("Payment failed - time is up!");
        }
        if (!payRes.isSuccess()) {
            paymentView.navigateToShoppingCart();
        }
        else {
            paymentView.navigateToHome();
        }
    }

    public List<ProductDTO> getProducts() {
        String token = CookiesHandler.getTokenFromCookies(request);
        String username = CookiesHandler.getUsernameFromCookies(request);
        List<ProductDTO> productList = new ArrayList<>();
        Response<Map<Integer, Map<Integer, Integer>>> response = userService.getShoppingCartContents(username, token);
        if (response.isSuccess()) {
            Map<Integer, Map<Integer, Integer>> products = response.getData();
            products.forEach((storeID, storeProducts) -> {
                storeProducts.forEach((product, quantity) -> {
                    Response<ProductDTO> productResponse = storeService.viewProductFromStoreByID(product, storeID, username, token);
                    if (productResponse.isSuccess()) {
                        ProductDTO productDTO = productResponse.getData();
                        productDTO.setQuantity(quantity); // Set quantity in the DTO
                        productList.add(productDTO);
                    }
                });
            });
        }
        return productList;
    }

    public boolean isLoggedIn() {
        String token = CookiesHandler.getTokenFromCookies(request);
        String username = CookiesHandler.getUsernameFromCookies(request);
        return userService.isValidToken(token, username);
    }
}
