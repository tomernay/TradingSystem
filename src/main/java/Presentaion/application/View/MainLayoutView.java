package Presentaion.application.View;

import Domain.Users.Subscriber.Subscriber;
import Presentaion.application.CookiesHandler;
import Presentaion.application.Presenter.MainLayoutPresenter;
import Presentaion.application.View.Messages.MessagesList;
import Presentaion.application.View.Payment.PaymentPage;
import Service.ServiceInitializer;
import Service.UserService;
import Utilities.Messages.Message;
import Utilities.Messages.NormalMessage;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Footer;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Header;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinRequest;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.server.VaadinServletRequest;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.CookieHandler;
import java.util.ArrayList;
import java.util.Queue;


/**
 * The main view is a top-level placeholder for other views.
 */
@Route("")
@PageTitle("Main")
@StyleSheet("context://login-view-styles.css")
public class MainLayoutView extends AppLayout implements BeforeEnterObserver {

    private final MainLayoutPresenter presenter;

    private H1 viewTitle;


    public MainLayoutView(MainLayoutPresenter presenter) {
        addClassName("main-view");
        this.presenter = presenter;
        this.presenter.attachView(this);
        setPrimarySection(Section.DRAWER);
        addDrawerContent();
        addHeaderContent();
        addLogoutButton();
    }

    private void addHeaderContent() {
        DrawerToggle toggle = new DrawerToggle();
        toggle.setAriaLabel("Menu toggle");

        viewTitle = new H1();
        //  viewTitle.addClassNames(Lumo.FontStyle.LARGE, Lumo.Margin.NONE);

        addToNavbar(toggle, viewTitle);
    }

    private void addDrawerContent() {
        Span appName = new Span("My App");
        //  appName.addClassNames(Lumo.FontStyle.BOLD, Lumo.FontStyle.LARGE);
        Header header = new Header(appName);

        Scroller scroller = new Scroller(createNavigation());

        addToDrawer(header, scroller, createFooter());
    }

    private SideNav createNavigation() {
        SideNav nav = new SideNav();
        SideNavItem paymentItem = new SideNavItem("Payment");
        paymentItem.addAttachListener(new ComponentEventListener<AttachEvent>() {
            @Override
            public void onComponentEvent(AttachEvent event) {

            }
        });

       nav.addItem(new SideNavItem("Payment", PaymentPage.class));
        String user= CookiesHandler.getUsernameFromCookies(getRequest());
        Queue<Message> sub= ServiceInitializer.getInstance().getUserService().getUserFacade().getUserRepository().getUser(user).getMessages();
       sub.add(new NormalMessage("yayaya"));
        MessagesList.setMessages(sub);
        nav.addItem(new SideNavItem("Messages", MessagesList.class));
        nav.addItem(new SideNavItem("Roles Management", RolesManagementView.class)); // New navigation item


        return nav;
    }

    public HttpServletRequest getRequest() {
        return ((VaadinServletRequest) VaadinRequest.getCurrent()).getHttpServletRequest();
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        // Check if the user is already logged in
        if (!isLoggedIn()) {
            // If not logged in, reroute to the login page
            event.rerouteTo(LoginView.class);
        }
    }

    private boolean isLoggedIn() {
        // Retrieve the current HTTP request
        HttpServletRequest request = (HttpServletRequest) VaadinService.getCurrentRequest();

        if (request != null) {
            // Retrieve cookies from the request
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if ("token".equals(cookie.getName())) {
                        // Assuming a valid token indicates a logged-in user
                        return isValidToken(cookie.getValue());
                    }
                }
            }
        }

        // If no valid token is found, the user is not logged in
        return false;
    }

    private boolean isValidToken(String token) {
        // Implement your token validation logic here
        // This could involve checking the token against a database or decoding a JWT token
        return token != null && !token.isEmpty();
    }

    private void addLogoutButton() {
        Button logoutButton = new Button("Logout", e -> logout());
        logoutButton.getElement().getStyle().set("margin-top", "auto"); // This will push the button to the bottom
        addToDrawer(logoutButton);
    }

    public void navigateToLogin() {
        getUI().ifPresent(ui -> ui.navigate("login"));
    }

    private void logout() {
        presenter.logout();
    }

    private Footer createFooter() {
        Footer footer = new Footer();
        return footer;
    }

    @Override
    protected void afterNavigation() {
        super.afterNavigation();
        viewTitle.setText(getCurrentPageTitle());
    }

    private String getCurrentPageTitle() {
        PageTitle title = getContent().getClass().getAnnotation(PageTitle.class);
        return title == null ? "" : title.value();
    }
}