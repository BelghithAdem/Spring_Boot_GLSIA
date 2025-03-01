package com.BeeOranized.BeeOranized.Strategy;


import com.BeeOranized.BeeOranized.Dtos.LoginRequestDto;
import com.BeeOranized.BeeOranized.Security.jwt.JwtUtils;
import com.BeeOranized.BeeOranized.Securit.service.UserDetailsImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class JwtAuthStrategy implements AuthStrategy {

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    public JwtAuthStrategy(AuthenticationManager authenticationManager, JwtUtils jwtUtils) {
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
    }

    @Override
    public String authenticate(LoginRequestDto loginRequest, Model model) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUserEmail(), loginRequest.getUserPassword())
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtUtils.generateJwtToken(authentication);

            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            List<String> roles = userDetails.getAuthorities().stream()
                    .map(item -> item.getAuthority())
                    .collect(Collectors.toList());

            model.addAttribute("userEmail", loginRequest.getUserEmail());
            model.addAttribute("roles", roles);
            model.addAttribute("jwt", jwt);

            if (roles.contains("ADMIN_ROLE")) {
                return "redirect:/listuser";
            } else if (roles.contains("ChefScrum_ROLE")) {
                return "scrummaster/index";
            } else if (roles.contains("Membre_ROLE")) {
                return "/member/index";
            } else {
                return "login";
            }
        } catch (Exception e) {
            model.addAttribute("error", "Invalid email or password");
            return "login";
        }
    }
}
