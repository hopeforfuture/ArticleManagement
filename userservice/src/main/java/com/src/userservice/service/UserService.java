package com.src.userservice.service;

import com.src.userservice.dao.PasswordResetTokenRepository;
import com.src.userservice.dao.UserRepository;
import com.src.userservice.dto.AdminUserResponse;
import com.src.userservice.exception.UserAlreadyExistsException;
import com.src.userservice.model.PasswordResetToken;
import com.src.userservice.model.User;
import com.src.userservice.validation.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.domain.Sort;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final Environment environment;
    private final PasswordResetTokenRepository passwordResetTokenRepository;

    @Autowired
    public UserService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            Environment environment,
            PasswordResetTokenRepository passwordResetTokenRepository) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.environment = environment;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
    }

    public User createUser(UserRequest request) {

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new UserAlreadyExistsException(
                    "User with email " + request.getEmail() + " already exists"
            );
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        // Hash password before saving
        user.setPassword(
                passwordEncoder.encode(request.getPassword())
        );
        user.setRole(environment.getProperty("role.user"));
        user.setStatus(1);
        User userSaved = userRepository.save(user);
        return userSaved;
    }

    public User updateAvatar(String email, MultipartFile file) throws IOException {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        user.setAvatarName(file.getOriginalFilename());
        user.setAvatarType(file.getContentType());
        user.setAvatarData(file.getBytes());

        return userRepository.save(user);
    }

    public User updateProfile(Authentication authentication, @Valid UpdateProfileRequest request) {


            String email = authentication.getName();
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            user.setName(request.getName());
            return userRepository.save(user);

    }

    public void forgotPassword(ForgotPasswordRequest request) {

        userRepository.findByEmail(request.getEmail())
                .ifPresent(user -> {

                    String token = UUID.randomUUID().toString();

                    PasswordResetToken resetToken = new PasswordResetToken();

                    resetToken.setToken(token);
                    resetToken.setUser(user);
                    resetToken.setExpiryTime(
                            LocalDateTime.now().plusMinutes(15)
                    );
                    resetToken.setUsed(false);

                    passwordResetTokenRepository.save(resetToken);

                    // For now, print token for testing
                    System.out.println(
                            "Password reset token: " + token
                    );
                });
    }

    public String resetPassword(ResetPasswordRequest request) {

        String returnStr;
        PasswordResetToken resetToken =
                passwordResetTokenRepository
                        .findByToken(request.getToken())
                        .orElseThrow(() ->
                                new RuntimeException("Invalid reset token"));

        if (resetToken.isUsed()) {
            return "Reset token has already been used";
        }
        else if (resetToken.getExpiryTime()
                .isBefore(LocalDateTime.now())) {
            return "Reset token has expired";
        }
        else if (!request.getNewPassword()
                .equals(request.getConfirmPassword())) {
            return  "New password and confirm password do not match";
        }
        User user = resetToken.getUser();
        user.setPassword(
                passwordEncoder.encode(request.getNewPassword())
        );

        userRepository.save(user);
        resetToken.setUsed(true);
        passwordResetTokenRepository.save(resetToken);
        returnStr = "Password reset successful.";
        return returnStr;
    }

    public String updatePassword(
            Authentication authentication,
            UpdatePasswordRequest request) {

        // 1. Check new password and confirm password
        if (!request.getNewPassword()
                .equals(request.getConfirmPassword())) {

            return "New password and confirm password do not match";
        }

        // 2. Get authenticated user's email from JWT
        String email = authentication.getName();

        // 3. Find authenticated user
        User user = userRepository.findByEmail(email)
                .orElse(null);

        if (user == null) {
            return "User not found";
        }

        // 4. Verify current password
        if (!passwordEncoder.matches(
                request.getCurrentPassword(),
                user.getPassword())) {
            return  "Current password is incorrect";
        }

        // 5. Encode and save new password
        user.setPassword(
                passwordEncoder.encode(
                        request.getNewPassword()
                )
        );

        userRepository.save(user);
        return "Password updated successfully";
    }

    public List<AdminUserResponse> getAllUsers() {

        return userRepository.findAll(
                        Sort.by(Sort.Direction.DESC, "id")
                )
                .stream()
                .map(user -> new AdminUserResponse(
                        user.getId(),
                        user.getName(),
                        user.getEmail(),
                        user.getRole(),
                        user.getAvatarName(),
                        user.getAvatarType(),
                        user.getCreatedAt(),
                        user.getUpdatedAt(),
                        user.getStatus()
                ))
                .toList();
    }

    public String blockUser(Long userId) {

        User user = userRepository.findById(userId)
                        .orElse(null);

        if(user == null) {
            return "User not found";
        }
        else if(user.getRole().equals("ADMIN")) {
            return "Admin can not be blocked";
        }
        else if(user.getStatus() == 0) {
            return "User is already blocked";
        }

        user.setStatus(0);

        userRepository.save(user);
        return "User blocked successfully";
    }

    public String unblockUser(Long userId) {

        User user = userRepository.findById(userId)
                        .orElse(null);

        if(user == null) {
            return "User not found";
        }
        else if(user.getStatus() == 1) {
            return "User is already active";
        }
        user.setStatus(1);

        userRepository.save(user);
        return "User unblocked successfully";
    }

}
