package com.BeeOranized.BeeOranized.ControllerVue;

import com.BeeOranized.BeeOranized.Dtos.JwtResponseDto;
import com.BeeOranized.BeeOranized.Dtos.LoginRequestDto;
import com.BeeOranized.BeeOranized.Dtos.SignupRequestDto;
import com.BeeOranized.BeeOranized.Securit.service.UserDetailsImpl;
import com.BeeOranized.BeeOranized.Security.jwt.JwtUtils;
import com.BeeOranized.BeeOranized.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class AuthControllerVue {

    private final UserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    public AuthControllerVue(UserService userService) {
        this.userService = userService;
    }

    // Authentifier l'utilisateur et renvoyer une réponse JWT
    @PostMapping("/signinVue")
    public String authenticateUser(@ModelAttribute LoginRequestDto loginRequest, Model model) {
        try {
            System.out.println("Authenticating user: " + loginRequest.getUserEmail());
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUserEmail(), loginRequest.getUserPassword()));

            System.out.println("Authentication successful");

            // Enregistrement de l'authentification dans le contexte de sécurité
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtUtils.generateJwtToken(authentication);

            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            List<String> roles = userDetails.getAuthorities().stream()
                    .map(item -> item.getAuthority())
                    .collect(Collectors.toList());

            // Ajouter les données à la vue
            model.addAttribute("userEmail", loginRequest.getUserEmail());
            model.addAttribute("roles", roles);
            model.addAttribute("jwt", jwt);

            // Rediriger vers le template "login" après une authentification réussie
            return "login"; // Vous pouvez ici rediriger vers une page après authentification

        } catch (Exception e) {
            e.printStackTrace(); // Affiche l'exception complète dans les logs
            model.addAttribute("error", "Invalid email or password");

            // Retourner à la page de connexion avec un message d'erreur
            return "login"; // Afficher la vue de connexion avec l'erreur
        }
    }

    // Inscription de l'utilisateur via le service
    @PostMapping("/signupVue")
    public String registerUser(@ModelAttribute SignupRequestDto signUpRequest, Model model) {
        return userService.registerUser(signUpRequest, model);
    }
    @PostMapping("/logoutVue")
    public String logout(Model model) {
        // Supprimer l'authentification du contexte de sécurité
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            // Réinitialiser le contexte de sécurité (déconnexion)
            SecurityContextHolder.clearContext();
        }

        // Rediriger vers la page de connexion après déconnexion
        model.addAttribute("message", "Successfully logged out.");
        return "login"; // Retourne à la page de connexion
    }
    // Exemple pour tester l'interface utilisateur
    @GetMapping("/example")
    public String getExample() {
        return "register"; // Nom de la vue
    }
}
