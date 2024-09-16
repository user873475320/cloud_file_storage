package ru.storage.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException {
        if (request.getServletPath().equals("/auth/login") || request.getServletPath().equals("/") || request.getServletPath().equals("/auth/registration")) {
            response.sendRedirect("/home?logoutFirst");
        } else {
            response.sendRedirect("/error/403");
        }
    }
}
