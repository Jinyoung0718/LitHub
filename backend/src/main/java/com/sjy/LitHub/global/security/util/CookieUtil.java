package com.sjy.LitHub.global.security.util;

import com.sjy.LitHub.global.config.AppConfig;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

public class CookieUtil {

    public static Cookie createCookie(String key, String value, int maxAge) {
        Cookie cookie = new Cookie(key, value);
        cookie.setDomain(AppConfig.getSiteDomain());
        cookie.setMaxAge(maxAge);
        cookie.setPath("/");
        cookie.setHttpOnly(AppConfig.isCookieHttpOnly());
        cookie.setSecure(AppConfig.isCookieSecure());
        cookie.setAttribute("SameSite", AppConfig.getCookieSameSite());
        return cookie;
    }

    public static Cookie deleteCookie(String key) {
        Cookie cookie = new Cookie(key, "");
        cookie.setMaxAge(0);
        cookie.setPath("/");
        cookie.setHttpOnly(AppConfig.isCookieHttpOnly());
        cookie.setSecure(AppConfig.isCookieSecure());
        cookie.setAttribute("SameSite", AppConfig.getCookieSameSite());
        return cookie;
    }

    public static String getCookieValue(HttpServletRequest request, String cookieName) {
        if (request == null || request.getCookies() == null) {
            return null;
        }

        for (Cookie cookie : request.getCookies()) {
            if (cookieName.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }

        return null;
    }
}