package Presentaion.application.View;

import Presentaion.application.SubscriberDetails;
import Presentaion.application.View.Messages.MessagesList;
import Presentaion.application.View.Payment.PaymentPage;
import Presentaion.application.View.Store.RolesManagementViewImpl;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.html.Footer;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Header;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;


/**
 * The main view is a top-level placeholder for other views.
 */


public class MainLayout extends AppLayout implements BeforeEnterObserver {

    private static SubscriberDetails subscriberDetails;

    private H1 viewTitle;

    public MainLayout() {
        setPrimarySection(Section.DRAWER);
        addDrawerContent();
        addHeaderContent();
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

        nav.addItem(new SideNavItem("Payment", PaymentPage.class));
        nav.addItem(new SideNavItem("Messages", MessagesList.class));
        nav.addItem(new SideNavItem("Roles Management", RolesManagementViewImpl.class)); // New navigation item


        return nav;
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
        // Implement your logic to check if the user is logged in
        // For example, you can check if the subscriberDetails is set
        return subscriberDetails != null;
    }

    public static SubscriberDetails getSubscriberDetails() {
        return subscriberDetails;
    }

    public static void setSubscriberDetails(String username, String token) {
        subscriberDetails = new SubscriberDetails(username, token);
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