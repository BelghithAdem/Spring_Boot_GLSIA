package com.BeeOranized.BeeOranized.Security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
            AccessDeniedException accessDeniedException) throws IOException, ServletException {
        response.setContentType("application/json");
        response.setStatus(HttpStatus.FORBIDDEN.value());

        // Create a custom error message
        String message = "Access denied: You don't have sufficient privileges to access this resource.";

        // Convert the error message to JSON format
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(message);

        // Write the JSON response to the client
        response.getWriter().write(json);
    }
}