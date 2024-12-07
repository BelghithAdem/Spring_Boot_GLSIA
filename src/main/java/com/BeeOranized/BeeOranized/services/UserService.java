package com.BeeOranized.BeeOranized.services;

import com.BeeOranized.BeeOranized.Dtos.ApiResponsee;
import com.BeeOranized.BeeOranized.Dtos.UserDataDTO;
import com.BeeOranized.BeeOranized.Entity.User;
import com.BeeOranized.BeeOranized.Repository.UserRepository;
import com.BeeOranized.BeeOranized.Security.jwt.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

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

    public List<User> getAllUser() {
        return userRepository.findAll();
    }

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

}
