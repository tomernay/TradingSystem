package Presentation.application;

import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.server.VaadinServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

public class CookiesHandler {

    public static void setCookie(String name, String value, int maxAge) {
        VaadinServletResponse response = (VaadinServletResponse) VaadinService.getCurrentResponse();
        if (response != null) {
            Cookie cookie = new Cookie(name, value);
            cookie.setPath("/");
            cookie.setMaxAge(maxAge);
            cookie.setSecure(true); // Only send over HTTPS
            cookie.setHttpOnly(true); // Prevent JavaScript access
            response.addCookie(cookie);
        } else {
            throw new IllegalStateException("Unable to get current VaadinResponse");
        }
    }

    public static String getTokenFromCookies(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("token".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    public static String getUsernameFromCookies(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("username".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    public static void deleteCookies(HttpServletRequest request) {
        VaadinServletResponse response = (VaadinServletResponse) VaadinService.getCurrentResponse();
        if (response != null) {
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    cookie.setMaxAge(0); // This will delete the cookie
                    cookie.setPath("/"); // Allow the entire application to access it
                    response.addCookie(cookie);
                }
            }
        } else {
            throw new IllegalStateException("Unable to get current VaadinResponse");
        }
    }
}
