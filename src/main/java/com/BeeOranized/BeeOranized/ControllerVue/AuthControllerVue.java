package com.BeeOranized.BeeOranized.ControllerVue;
import com.BeeOranized.BeeOranized.Dtos.LoginRequestDto;
import com.BeeOranized.BeeOranized.Dtos.SignupRequestDto;
import com.BeeOranized.BeeOranized.Entity.User;
import com.BeeOranized.BeeOranized.Repository.UserRepository;
import com.BeeOranized.BeeOranized.Securit.service.UserDetailsImpl;
import com.BeeOranized.BeeOranized.Security.jwt.JwtUtils;
import com.BeeOranized.BeeOranized.Strategy.AuthContext;
import com.BeeOranized.BeeOranized.Strategy.JwtAuthStrategy;
import com.BeeOranized.BeeOranized.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
public class AuthControllerVue {
    private final AuthContext authContext;
    private final JwtAuthStrategy jwtAuthStrategy;

    private final UserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    public AuthControllerVue(AuthContext authContext, JwtAuthStrategy jwtAuthStrategy, UserService userService) {
        this.authContext = authContext;
        this.jwtAuthStrategy = jwtAuthStrategy;
        this.userService = userService;
    }

    @RequestMapping("/signinVue")
    public String authenticateUser(@ModelAttribute LoginRequestDto loginRequest, Model model) {
        authContext.setAuthStrategy(jwtAuthStrategy);
        return authContext.executeAuthentication(loginRequest, model);
    }

    // Inscription de l'utilisateur via le service
    @RequestMapping("/signupVue")
    public String registerUser(@ModelAttribute SignupRequestDto signUpRequest, Model model) {
        return userService.registerUser(signUpRequest, model);
    }


    @RequestMapping("/logoutVue")
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

    // Delete Product
    @RequestMapping("/deleteUser/{id}")
    public String deleteUser(@PathVariable("id") Long id) {
        userRepository.deleteById(id);
        return "redirect:/listuser";
    }

    @RequestMapping("/listuser")
    public String listUsers(Model model) {
        List<User> listUsers = userRepository.findAll();
        model.addAttribute("listUsers", listUsers);
        return "admin/userlist";
    }

    @RequestMapping(value = "/updateUser/{id}", method = RequestMethod.POST)
    public String updateUser(@PathVariable("id") Long id, @ModelAttribute("user") User user) {
        // Assuming the updateUser method is available in the userService.
        userService.updateUser(id, user);
        return "redirect:/listuser";  // Redirect to user list after updating
    }

    // Edit User Form
    @RequestMapping("/edit/{id}")
    public String editUser(@PathVariable("id") Long id, Model model) {
        Optional<User> user = Optional.of(userRepository.getById(id));
        if (user.isPresent()) {
            model.addAttribute("user", user.get());
        } else {
            return "redirect:/admin/userlist";  // Redirect if user not found
        }
        return "admin/userupdate";  // Return the user update form view
    }




}

