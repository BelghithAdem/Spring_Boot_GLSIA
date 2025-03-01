package com.BeeOranized.BeeOranized.Strategy;


import com.BeeOranized.BeeOranized.Dtos.LoginRequestDto;
import org.springframework.ui.Model;

public interface AuthStrategy {
    String authenticate(LoginRequestDto loginRequest, Model model);
}
