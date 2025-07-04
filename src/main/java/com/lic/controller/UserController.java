package com.lic.controller;

import com.lic.dto.*;
import com.lic.entities.User;
import com.lic.security.JwtTokenProvider;
import com.lic.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:3000/", allowCredentials = "true")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody UserRegistrationDto registrationDto) {
        User registeredUser = userService.registerUser(registrationDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(registeredUser);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.username(),
                            loginRequest.password()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            User user = userService.findByUsername(loginRequest.username());
            String token = jwtTokenProvider.generateToken(user.getUsername(), user.getRole());

            return ResponseEntity.ok(new JwtAuthenticationResponse(token, user.getRole()));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    }


    @PostMapping("/verify-user-credentials")
    public ResponseEntity<?> verifyUserCredentials(@RequestBody VerifyUserRequest request) {
        try {
            User user = userService.findByUsernameAndEmail(request.getUsername(), request.getEmail());
            return ResponseEntity.ok(new VerificationResponse(true, "User verified successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new VerificationResponse(false, "Invalid username or email"));
        }
    }

    @PutMapping("/update-user-password")
    public ResponseEntity<?> updateUserPassword(@RequestBody UpdatePasswordRequest request) {
        try {
            userService.updatePassword(request);
            return ResponseEntity.ok(new SimpleResponse("Password updated successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new SimpleResponse(e.getMessage()));
        }
    }

    // DTO records
    record LoginRequest(String username, String password) {}
    record JwtAuthenticationResponse(String token, String role) {}
}