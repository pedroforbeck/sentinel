package com.sentinel.api.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class ApiKeyInterceptor implements HandlerInterceptor {

    private static final String API_KEY_HEADER = "X-API-KEY";

    @Value("${sentinel.api.key}")
    private String expectedApiKey;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String clientApiKey = request.getHeader(API_KEY_HEADER);

        if (clientApiKey == null || !clientApiKey.equals(expectedApiKey)) {
            System.err.println("[SECURITY-ALERT] Blocked unauthorized access attempt from IP: " + request.getRemoteAddr());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("401 Unauthorized: Invalid or missing API Key");
            return false; // Blocks the request
        }

        return true; // Allows the request to proceed to the Controller
    }
}