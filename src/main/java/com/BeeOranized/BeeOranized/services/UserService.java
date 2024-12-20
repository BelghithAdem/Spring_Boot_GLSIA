package com.BeeOranized.BeeOranized.services;

import com.BeeOranized.BeeOranized.Dtos.ApiResponsee;
import com.BeeOranized.BeeOranized.Dtos.SignupRequestDto;
import com.BeeOranized.BeeOranized.Dtos.UserDataDTO;
import com.BeeOranized.BeeOranized.Entity.*;
import com.BeeOranized.BeeOranized.Repository.*;
import com.BeeOranized.BeeOranized.Security.jwt.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
public class UserService {


    @Autowired
    PasswordEncoder encoder;
    @Autowired
    JwtUtils jwtUtils;
    @Autowired
    private EmailService emailService;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private  RoleRepository roleRepository;
    @Autowired
    private  MembreRepository membreRepository;
    @Autowired
    private  ChefScrumRepository chefScrumRepository;
    @Autowired
    private  AdminRepository adminRepository;



    public String generateResetPasswordToken(User user) {
        User existingUser = userRepository.findByUserEmail(user.getUserEmail()).orElse(null);
        if (existingUser == null) {
            return null;
        }

        String resetPasswordToken = UUID.randomUUID().toString();
        System.out.println("user reset password is " + resetPasswordToken);

        // Save the token to the user entity
        existingUser.setResetPasswordToken(resetPasswordToken);
        userRepository.save(existingUser);

        return resetPasswordToken;
    }

    private void sendResetPasswordEmail(User user, String resetPasswordToken) {
        // Prepare email content
        String subject = "Reset Password Request";
        String message = "Dear User,\n\nYou have requested to reset your password.\n" +
                "Please click the link below to reset your password:\n\n" +
                "http://localhost:4200/reset-password?token=" + resetPasswordToken + "\n\n" +
                "If you did not request this, please ignore this email.\n\nRegards,\nYour Team";

        // Send email to the user
        emailService.sendEmail(user.getUserEmail(), subject, message);
    }

    public boolean resetPassword(String userEmail, String resetPasswordToken, String newPassword) {
        Optional<User> optionalExistingUser = userRepository.findByUserEmail(userEmail);
        if (optionalExistingUser.isPresent()) {
            User existingUser = optionalExistingUser.get();

            if (!resetPasswordToken.equals(existingUser.getResetPasswordToken())) {
                return false;
            }

            // Save the new password to the user entity
            existingUser.setUserPassword(encoder.encode(newPassword));
            existingUser.setResetPasswordToken(null);
            userRepository.save(existingUser);

            return true;
        } else {
            // Handle the case when the user with the specified email doesn't exist
            return false; // or throw an exception, or return an error message
        }
    }




    public UserDataDTO getUserDataByEmail(String email) {
        Optional<User> userOptional = userRepository.findByUserEmail(email);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            return convertToUserDataDTO(user);
        } else {
            return null; // Or throw an exception if preferred
        }
    }

    private UserDataDTO convertToUserDataDTO(User user) {
        UserDataDTO userDataDTO = new UserDataDTO();
        userDataDTO.setUserId(user.getUserId());

        // Segment the name into firstName and lastName
        String[] nameParts = segmentName(user.getName());
        userDataDTO.setFirstName(nameParts[0]);
        userDataDTO.setLastName(nameParts[1]);

        userDataDTO.setEmail(user.getUserEmail());
        return userDataDTO;
    }

    private String[] segmentName(String fullName) {
        String[] nameParts = fullName.split(" ", 2);
        if (nameParts.length == 1) {
            // If there's no space, use the full name as both first and last name
            return new String[] { nameParts[0], nameParts[0] };
        } else {
            return nameParts;
        }
    }

    public User updateUser(Long id, User userDetails) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("Product not found"));
        user.setName(userDetails.getName());
        user.setUserEmail(userDetails.getUserEmail());
        user.setName(userDetails.getName());
        user.setUserPassword(userDetails.getUserPassword());
        user.setUserCity(userDetails.getUserCity());
        user.setRoles(userDetails.getRoles());
        return userRepository.save(user);
    }
    public List<User> getAllScrumMasters() {
        // Find the role for ChefScrum_ROLE
        Optional<Role> chefScrumRoleOpt = roleRepository.findByName(ERole.ChefScrum_ROLE);

        // If the role exists, fetch all users who have this role
        if (chefScrumRoleOpt.isPresent()) {
            Role chefScrumRole = chefScrumRoleOpt.get();
            return userRepository.findByRolesContaining(chefScrumRole); // Assuming the `UserRepository` has this method
        } else {
            return Collections.emptyList(); // Return an empty list if the role doesn't exist
        }
    }
    public String registerUser(SignupRequestDto signUpRequest, Model model) {
        try {
            if (userRepository.existsByUserEmail(signUpRequest.getUserEmail())) {
                model.addAttribute("message", "Erreur : L'email est déjà pris !");
                return "register";
            }

            Set<Role> roles = new HashSet<>();
            Role userRole = assignUserRole(signUpRequest.getUserRole(), model);
            if (userRole == null) {
                return "register";
            }

            roles.add(userRole);

            User newUser = createUser(signUpRequest, roles);

            model.addAttribute("message", "Utilisateur enregistré avec succès !");
            return "login";

        } catch (Exception e) {
            model.addAttribute("message", "Une erreur inattendue s'est produite. Veuillez réessayer.");
            return "register";
        }
    }

    public String addUser(SignupRequestDto signUpRequest, Model model) {
        try {
            if (userRepository.existsByUserEmail(signUpRequest.getUserEmail())) {
                model.addAttribute("message", "Erreur : L'email est déjà pris !");
                return "admin/register";
            }

            Set<Role> roles = new HashSet<>();
            Role userRole = assignUserRole(signUpRequest.getUserRole(), model);
            if (userRole == null) {
                return "admin/register";
            }

            roles.add(userRole);

            User newUser = createUser(signUpRequest, roles);

            model.addAttribute("message", "Utilisateur enregistré avec succès !");
            return "listuser";

        } catch (Exception e) {
            model.addAttribute("message", "Une erreur inattendue s'est produite. Veuillez réessayer.");
            return "admin/register";
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
                return null;
        }
        return roleOpt.orElseGet(() -> roleRepository.save(new Role(ERole.valueOf(userRoleStr))));
    }

    private User createUser(SignupRequestDto signUpRequest, Set<Role> roles) {
        String encodedPassword = encoder.encode(signUpRequest.getUserPassword());

        switch (signUpRequest.getUserRole()) {
            case "Membre_ROLE":
                Membre membre = new Membre(signUpRequest.getName(), signUpRequest.getUserEmail(),
                        encodedPassword, signUpRequest.getUserCity(), roles);
                return membreRepository.save(membre);
            case "ChefScrum_ROLE":
                ChefScrum chefScrum = new ChefScrum(signUpRequest.getName(), signUpRequest.getUserEmail(),
                        encodedPassword, signUpRequest.getUserCity(), roles);
                return chefScrumRepository.save(chefScrum);
            case "ADMIN_ROLE":
                Admin admin = new Admin(signUpRequest.getName(), signUpRequest.getUserEmail(),
                        encodedPassword, signUpRequest.getUserCity(), roles);
                return adminRepository.save(admin);
            default:
                throw new IllegalArgumentException("Invalid role provided.");
        }
    }



}
