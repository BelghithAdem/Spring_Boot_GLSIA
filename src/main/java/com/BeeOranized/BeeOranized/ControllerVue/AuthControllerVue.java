package com.BeeOranized.BeeOranized.ControllerVue;

import java.util.*;

import com.BeeOranized.BeeOranized.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.BeeOranized.BeeOranized.Dtos.SignupRequestDto;
import com.BeeOranized.BeeOranized.Entity.*;
import com.BeeOranized.BeeOranized.Repository.*;
import com.BeeOranized.BeeOranized.services.EmailService;

@Controller
@RequestMapping("/auth")
public class AuthControllerVue {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private EmailService emailService;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private MembreRepository membreRepository;
    @Autowired
    private ChefScrumRepository chefScrumRepository;
    @Autowired
    private AdminRepository adminRepository;


    @GetMapping("/example")
    public String getExample() {
        return "register";
    }
    @GetMapping("/signupVue")
    public String registerUser(@ModelAttribute SignupRequestDto signUpRequest, Model model) {
        try {
            if (userRepository.existsByUserEmail(signUpRequest.getUserEmail())) {
                model.addAttribute("message", "Erreur : L'email est déjà pris !");
                return "register"; // Redirect to the registration page with error message
            }

            Set<Role> roles = new HashSet<>();
            Role userRole = assignUserRole(signUpRequest.getUserRole(), model);
            if (userRole == null) {
                return "register"; // Return if role is invalid
            }

            roles.add(userRole);

            User newUser = createUser(signUpRequest, roles);

            sendAccountEmail(newUser, signUpRequest.getUserPassword());

            model.addAttribute("message", "Utilisateur enregistré avec succès !");
            return "signupSuccess"; // Redirect to the success page

        } catch (Exception e) {
            return "register"; // Redirect to the registration page with error message
        }
    }

    private Role assignUserRole(String userRoleStr, Model model) {
        Optional<Role> roleOpt;
        switch (userRoleStr) {
            case "Membre_ROLE":
                roleOpt = roleRepository.findByName(ERole.Membre_ROLE);
                break;
            case "ChefScrum_ROLE":
                roleOpt = roleRepository.findByName(ERole.ChefScrum_ROLE);
                break;
            case "ADMIN_ROLE":
                roleOpt = roleRepository.findByName(ERole.ADMIN_ROLE);
                break;
            default:
                model.addAttribute("message", "Erreur : Rôle invalide !");
                return null; // Return null if role is invalid
        }
        return roleOpt.orElseGet(() -> roleRepository.save(new Role(ERole.valueOf(userRoleStr))));
    }

    private User createUser(SignupRequestDto signUpRequest, Set<Role> roles) {
        User newUser;
        String encodedPassword = encoder.encode(signUpRequest.getUserPassword());

        switch (signUpRequest.getUserRole()) {
            case "Membre_ROLE":
                Membre membre = new Membre(signUpRequest.getName(), signUpRequest.getUserEmail(),
                        encodedPassword, signUpRequest.getUserCity(), roles);
                newUser = membreRepository.save(membre);
                break;
            case "ChefScrum_ROLE":
                ChefScrum chefScrum = new ChefScrum(signUpRequest.getName(), signUpRequest.getUserEmail(),
                        encodedPassword, signUpRequest.getUserCity(), roles);
                newUser = chefScrumRepository.save(chefScrum);
                break;
            case "ADMIN_ROLE":
                Admin admin = new Admin(signUpRequest.getName(), signUpRequest.getUserEmail(),
                        encodedPassword, signUpRequest.getUserCity(), roles);
                newUser = adminRepository.save(admin);
                break;
            default:
                throw new IllegalArgumentException("Invalid role provided.");
        }
        return newUser;
    }

    private void sendAccountEmail(User user, String password) {
        String subject = "Account Created";
        String message = "Dear User,\n\nYour account has been created successfully.\n\n\n\n" +
                "Please login to your account using the following credentials:\n\n" +
                "<strong>Email:</strong> <u>" + user.getUserEmail() + "</u>\n\n" +
                "<strong>Password:</strong> <u>" + password + "</u>\n\n" +
                "Regards,\nYour Team";
        emailService.sendEmail(user.getUserEmail(), subject, message);
    }
}
