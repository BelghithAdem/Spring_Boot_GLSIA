package com.BeeOranized.BeeOranized.ControllerVue;

import com.BeeOranized.BeeOranized.Dtos.JwtResponseDto;
import com.BeeOranized.BeeOranized.Dtos.LoginRequestDto;
import com.BeeOranized.BeeOranized.Dtos.SignupRequestDto;
import com.BeeOranized.BeeOranized.Entity.User;
import com.BeeOranized.BeeOranized.Repository.UserRepository;
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
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
public class AuthControllerVue {

    private final UserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    public AuthControllerVue(UserService userService) {
        this.userService = userService;
    }

    // Authentifier l'utilisateur et renvoyer une réponse JWT
    @RequestMapping("/signinVue")
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
            System.out.println("User roles: " + roles.contains("ROLE_ADMIN"));
            // Rediriger vers le template "login" après une authentification réussie
            if (roles.contains("ADMIN_ROLE")) {
                return "/admin/index";  // Rediriger vers la page Admin
            } else if (roles.contains("ChefScrum_ROLE")) {
                return "scrummaster/index";  // Rediriger vers la page Scrum Master
            } else if (roles.contains("Membre_ROLE")) {
                return "/member/index";  // Rediriger vers la page Member
            } else {
                // Par défaut, rediriger vers la page d'accueil ou une page d'erreur
                return "login";  // Par exemple, vers la page d'accueil
            }
        } catch (Exception e) {
            e.printStackTrace(); // Affiche l'exception complète dans les logs
            model.addAttribute("error", "Invalid email or password");

            // Retourner à la page de connexion avec un message d'erreur
            return "login"; // Afficher la vue de connexion avec l'erreur
        }
    }

    // Inscription de l'utilisateur via le service
    @RequestMapping("/signupVue")
    public String registerUser(@ModelAttribute SignupRequestDto signUpRequest, Model model) {
        return userService.registerUser(signUpRequest, model);
    }

    @RequestMapping("/addUser")
    public String addUser(@ModelAttribute SignupRequestDto signUpRequest, Model model) {
        return userService.addUser(signUpRequest, model);
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
        return "redirect:/admin/userlist";  // Redirect to user list after updating
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
