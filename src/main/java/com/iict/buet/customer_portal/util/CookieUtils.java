package com.iict.buet.customer_portal.util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CookieUtils {
    public static void setCookie(HttpServletResponse response,
                                 String name, String value, int maxAgeInMinutes, boolean httpOnly, boolean isSecure) {
        Cookie cookie = new Cookie(name, value);
        cookie.setMaxAge(maxAgeInMinutes * 60);
        cookie.setPath("/");
        cookie.setHttpOnly(httpOnly);
        cookie.setSecure(isSecure);
        response.addCookie(cookie);
    }

    public static String getCookie(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(name)) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    public static void deleteCookie(HttpServletResponse response, String name, boolean httpOnly, boolean isSecure) {
        setCookie(response, name, "", 0, httpOnly, isSecure);
    }
}
