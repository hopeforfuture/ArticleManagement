package com.src.userservice.controller;

import com.src.userservice.dto.*;
import com.src.userservice.model.User;
import com.src.userservice.service.JwtService;
import com.src.userservice.service.UserService;
import com.src.userservice.validation.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/v1")
public class AuthController {

    @Autowired
    private UserService service;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;

    @PostMapping("users/signup")
    public ResponseEntity<UserResponse> signup(
            @Valid @RequestBody UserRequest request) {
        User user = service.createUser(request);
        UserResponse response = new UserResponse(user.getId(), user.getName(), user.getEmail(), user.getRole());

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("users/signin")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest request) {

        try {

            Authentication authentication =
                    authenticationManager.authenticate(
                            new UsernamePasswordAuthenticationToken(
                                    request.getEmail(),
                                    request.getPassword()
                            )
                    );

            if (authentication.isAuthenticated()) {

                String token =
                        jwtService.generateToken(request.getEmail());

                return ResponseEntity.ok(
                        new AuthResponse(
                                true,
                                "Login successful",
                                token,
                                request.getEmail()
                        )
                );
            }

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new AuthResponse(
                            false,
                            "Authentication failed",
                            null,
                            null
                    ));

        } catch (BadCredentialsException e) {

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new AuthResponse(
                            false,
                            "Invalid username or password",
                            null,
                            null
                    ));
        }
    }

    @PostMapping("users/avatar")
    public ResponseEntity<?> updateAvatar(
            @RequestPart("avatar") MultipartFile file,
            Authentication authentication) {

        try {

            Set<String> allowedTypes = Set.of(
                    "image/jpeg",
                    "image/png",
                    "image/gif"
            );

            if (file == null || file.isEmpty()) {
                return ResponseEntity
                        .badRequest()
                        .body(new ApiResponse(
                                false,
                                "Please select an image file"
                        ));
            }

            if (!allowedTypes.contains(file.getContentType())) {
                return ResponseEntity
                        .badRequest()
                        .body(new ApiResponse(
                                false,
                                "Only JPG, JPEG, PNG and GIF images are allowed"
                        ));
            }

            String email = authentication.getName();

            User user = service.updateAvatar(email, file);
            return ResponseEntity.ok(
                    Map.of(
                            "success", true,
                            "message", "Avatar updated successfully"
                    )
            );

        } catch (IOException e) {

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to update avatar");
        }
    }

    @PutMapping("users/profile")
    public ResponseEntity<CommonResponse> updateProfile(
            @Valid @RequestBody UpdateProfileRequest request,
            Authentication authentication) {

        User updatedUser = service.updateProfile(
                authentication,
                request
        );

        return ResponseEntity.ok(
                new CommonResponse(
                        true,
                        "Profile updated successfully"
                )
        );
    }

    @PostMapping("users/forgot-password")
    public ResponseEntity<String> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request) {

        service.forgotPassword(request);

        return ResponseEntity.ok(
                "If the email is registered, a password reset link has been sent."
        );
    }

    @PostMapping("/users/reset-password")
    public ResponseEntity<CommonResponse> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request) {

        String str =  service.resetPassword(request);
        System.out.println(str);
        boolean flag = true;
        if(str.equals("New password and confirm password do not match")) {
            flag = false;
        }
        else if(str.equals("Reset token has already been used")) {
            flag = false;
        }
        else if(str.equals("Reset token has expired")) {
            flag = false;
        }
        else if(str.equals("Password reset successful.")) {
            flag = true;
        }

        if(!flag) {
            return new ResponseEntity<>(new CommonResponse(flag, str), HttpStatus.FORBIDDEN);
        }

        return ResponseEntity.ok(
                new CommonResponse(
                        flag,
                        str
                )
        );
    }

    @PutMapping("users/update-password")
    public ResponseEntity<CommonResponse> updatePassword(
            Authentication authentication,
            @Valid @RequestBody UpdatePasswordRequest request) {

        String str =  service.updatePassword(
                authentication,
                request
        );

        boolean flag = true;
        if(str.equals("New password and confirm password do not match")) {
            flag = false;
        }
        else if(str.equals("User not found")) {
            flag = false;
        }
        else if(str.equals("Current password is incorrect")) {
            flag = false;
        }
        else if(str.equals("Password updated successfully")) {
            flag = true;
        }
        if(!flag) {
            return new ResponseEntity<>(new CommonResponse(flag, str), HttpStatus.FORBIDDEN);
        }
        return ResponseEntity.ok(
                new CommonResponse(
                        flag,
                        str
                )
        );
    }

    @GetMapping("/admin/users")
    public ResponseEntity<List<AdminUserResponse>> getAllUsers() {

        List<AdminUserResponse> users =
                service.getAllUsers();

        return ResponseEntity.ok(users);
    }

    @PutMapping("/admin/users/{userId}/block")
    public ResponseEntity<CommonResponse> blockUser(
            @PathVariable Long userId) {

        String str = service.blockUser(userId);

        boolean flag = true;
        if(str.equals("User not found")) {
            flag = false;
        }
        else if(str.equals("Admin can not be blocked")) {
            flag = false;
        }
        else if(str.equals("User is already blocked")) {
            flag = false;
        }
        else if(str.equals("User blocked successfully")) {
            flag = true;
        }

        if(!flag) {
            return new ResponseEntity<>(new CommonResponse(flag, str), HttpStatus.FORBIDDEN);
        }
        return ResponseEntity.ok(
                new CommonResponse(
                        flag,
                        str
                )
        );
    }

    @PutMapping("/admin/users/{userId}/unblock")
    public ResponseEntity<CommonResponse> unblockUser(
            @PathVariable Long userId) {

        String str =  service.unblockUser(userId);
        boolean flag = true;
        if(str.equals("User not found")) {
            flag = false;
        }
        else if(str.equals("User is already active")) {
            flag = false;
        }
        if(!flag) {
            return new ResponseEntity<>(new CommonResponse(flag, str), HttpStatus.FORBIDDEN);
        }
        return ResponseEntity.ok(
                new CommonResponse(
                        flag,
                        str
                )
        );
    }

}