package com.BeeOranized.BeeOranized.Strategy;


import com.BeeOranized.BeeOranized.Dtos.LoginRequestDto;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

@Service
public class AuthContext {
    private AuthStrategy authStrategy;

    public void setAuthStrategy(AuthStrategy authStrategy) {
        this.authStrategy = authStrategy;
    }

    public String executeAuthentication(LoginRequestDto loginRequest, Model model) {
        if (authStrategy == null) {
            throw new IllegalStateException("Authentication strategy not set.");
        }
        return authStrategy.authenticate(loginRequest, model);
    }
}
