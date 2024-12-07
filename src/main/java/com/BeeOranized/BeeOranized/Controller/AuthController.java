package com.BeeOranized.BeeOranized.Controller;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import com.BeeOranized.BeeOranized.Dtos.*;
import com.BeeOranized.BeeOranized.Entity.*;
import com.BeeOranized.BeeOranized.Repository.*;
import com.BeeOranized.BeeOranized.Securit.service.UserDetailsImpl;
import com.BeeOranized.BeeOranized.Security.jwt.JwtUtils;
import com.BeeOranized.BeeOranized.services.EmailService;
import com.BeeOranized.BeeOranized.services.UserService;
import com.sun.xml.messaging.saaj.packaging.mime.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

//@CrossOrigin("http://localhost:4200/")
@RestController
public class AuthController {
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;
    @Autowired
    UserService userService;
    @Autowired
    EmailService emailService;
    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    MembreRepository membreRepository;

    @Autowired
    ChefScrumRepository chefScrumRepository;

    @Autowired
    AdminRepository adminRepository;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequestDto loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUserEmail(), loginRequest.getUserPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        return ResponseEntity.ok(new JwtResponseDto(jwt,
                loginRequest.getUserEmail(),
                roles,
                userDetails.getId()));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody SignupRequestDto signUpRequest) {
        try {
            System.out.println("Received Signup Request: " + signUpRequest.toString());

            if (userRepository.existsByUserEmail(signUpRequest.getUserEmail())) {
                return ResponseEntity
                        .badRequest()
                        .body(new MessageResponseDto("Error: Username is already taken!"));
            }

            Set<Role> roles = new HashSet<>();

            Role userRole = null;
            switch (signUpRequest.getUserRole()) {
                case "Membre_ROLE":
                    userRole = roleRepository.findByName(ERole.Membre_ROLE)
                            .orElseGet(() -> roleRepository.save(new Role(ERole.Membre_ROLE)));
                    break;
                case "ChefScrum_ROLE":
                    userRole = roleRepository.findByName(ERole.ChefScrum_ROLE)
                            .orElseGet(() -> roleRepository.save(new Role(ERole.ChefScrum_ROLE)));
                    break;
                case "ADMIN_ROLE":
                    userRole = roleRepository.findByName(ERole.ADMIN_ROLE)
                            .orElseGet(() -> roleRepository.save(new Role(ERole.ADMIN_ROLE)));
                    break;
                default:
                    return ResponseEntity
                            .badRequest()
                            .body(new MessageResponseDto("Error: Invalid role!"));
            }

            roles.add(userRole);

            User newUser;
            if ("Membre_ROLE".equals(signUpRequest.getUserRole())) {
                Membre membre = new Membre(signUpRequest.getName(), signUpRequest.getUserEmail(),
                        encoder.encode(signUpRequest.getUserPassword()), signUpRequest.getUserCity(), roles);
                newUser = membreRepository.save(membre);
            } else if ("ChefScrum_ROLE".equals(signUpRequest.getUserRole())) {
                ChefScrum chefScrum = new ChefScrum(signUpRequest.getName(), signUpRequest.getUserEmail(),
                        encoder.encode(signUpRequest.getUserPassword()), signUpRequest.getUserCity(), roles);
                newUser = chefScrumRepository.save(chefScrum);
            } else if ("ADMIN_ROLE".equals(signUpRequest.getUserRole())) {
                Admin admin = new Admin(signUpRequest.getName(), signUpRequest.getUserEmail(),
                        encoder.encode(signUpRequest.getUserPassword()), signUpRequest.getUserCity(), roles);
                newUser = adminRepository.save(admin);
            } else {
                return ResponseEntity
                        .badRequest()
                        .body(new MessageResponseDto("Error: Invalid role!"));
            }

            // Send account creation email
            sendAccountEmail(newUser, signUpRequest.getUserPassword());

            return ResponseEntity.ok(new MessageResponseDto("User registered successfully!"));
        } catch (Exception e) {
            System.err.println("Forbidden access: " + e.getMessage());
            throw new AccessDeniedException("Forbidden access: " + e.getMessage());
        }
    }

    private void sendAccountEmail(User user, String password) {
        // Prepare email content
        String subject = "Account Created";
        String message = "Dear User,\n\nYour account has been created successfully.\n\n\n\n" +
                "Please login to your account using the following credentials:\n\n" +
                "<strong>Email:</strong> <u>" + user.getUserEmail() + "</u>\n\n" +
                "<strong>Password:</strong> <u>" + password + "</u>\n\n" +
                "Regards,\nYour Team";

        // Send email to the user
        emailService.sendEmail(user.getUserEmail(), subject, message);
    }

    @PostMapping("/generate/{userEmail}")
    public User generateResetPasswordToken(@PathVariable String userEmail) {
        Optional<User> optionalExistingUser = userRepository.findByUserEmail(userEmail);

        if (optionalExistingUser.isPresent()) {
            User existingUser = optionalExistingUser.get();

            String resetPasswordToken = UUID.randomUUID().toString();
            System.out.println("user reset password is " + resetPasswordToken);

            // Save the token to the user entity
            existingUser.setResetPasswordToken(resetPasswordToken);
            sendResetPasswordEmail(existingUser, resetPasswordToken);

            return userRepository.save(existingUser);
        } else {
            // Handle the case when the user with the specified email doesn't exist
            return null; // or throw an exception, or return an error message
        }
    }

    private void sendResetPasswordEmail(User user, String resetPasswordToken) {
        // Prepare email content
        String subject = "Reset Password Request";
        String message = "Dear User,\n\nYou have requested to reset your password.\n" +
                "Please copy  the reset token below to reset your password:\n\n" +
                "the reset token is : <strong><u>" + resetPasswordToken + "</u></strong>\n\n" +
                "If you did not request this, please ignore this email.\n\nRegards,\nYour Team";

        // Send email to the user
        emailService.sendEmail(user.getUserEmail(), subject, message);
    }

    @PostMapping("/reset-password")
    public boolean resetPassword(@RequestBody ResetPasswordRequest request) {
        Optional<User> optionalExistingUser = userRepository.findByUserEmail(request.getUserEmail());
        if (optionalExistingUser.isPresent()) {
            User existingUser = optionalExistingUser.get();

            if (!request.getResetPasswordToken().trim().equals(existingUser.getResetPasswordToken())) {
                return false;
            }

            existingUser.setUserPassword(encoder.encode(request.getNewPassword()));
            existingUser.setResetPasswordToken(null);
            userRepository.save(existingUser);

            return true;
        } else {
            // Handle the case when the user with the specified email doesn't exist
            return false; // or throw an exception, or return an error message
        }
    }

    @PutMapping("/users/update-user/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody SignupRequestDto updateRequest) {
        User existingUser = userRepository.findById(id).orElse(null);
        if (existingUser != null) {
            // Update basic user information
            existingUser.setName(updateRequest.getName());
            existingUser.setUserEmail(updateRequest.getUserEmail());
            existingUser.setUserCity(updateRequest.getUserCity());

            // Determine user roles based on the user role in the update request
            Set<Role> roles = new HashSet<>();
            Role userRole = null;
            switch (updateRequest.getUserRole()) {
                case "Membre_ROLE":
                    userRole = roleRepository.findByName(ERole.Membre_ROLE)
                            .orElseGet(() -> roleRepository.save(new Role(ERole.Membre_ROLE)));
                    break;
                case "ChefScrum_ROLE":
                    userRole = roleRepository.findByName(ERole.ChefScrum_ROLE)
                            .orElseGet(() -> roleRepository.save(new Role(ERole.ChefScrum_ROLE)));
                    break;
                case "ADMIN_ROLE":
                    userRole = roleRepository.findByName(ERole.ADMIN_ROLE)
                            .orElseGet(() -> roleRepository.save(new Role(ERole.ADMIN_ROLE)));
                    break;
                default:
                    return ResponseEntity
                            .badRequest()
                            .body(null);
            }

            roles.add(userRole);

            // Update user roles
            existingUser.setRoles(roles);

            // Save the updated user
            return new ResponseEntity<>(userRepository.save(existingUser), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        User user = userRepository.findById(id).orElse(null);
        if (user != null) {
            return new ResponseEntity<>(user, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<HttpStatus> deleteUser(@PathVariable Long id) {
        try {
            userRepository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}