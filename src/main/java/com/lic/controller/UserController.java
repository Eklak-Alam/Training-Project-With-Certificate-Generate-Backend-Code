package com.lic.controller;


import com.lic.dto.UserRegistrationDto;
import com.lic.entities.User;
import com.lic.security.JwtTokenProvider;
import com.lic.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class UserController {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody UserRegistrationDto registrationDto) {
        User registeredUser = userService.registerUser(registrationDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(registeredUser);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            User user = userService.findByUsername(loginRequest.username());

            System.out.println("Input Password: " + loginRequest.password());
            System.out.println("Stored Hash: " + user.getPassword());
            System.out.println("Matches? " + passwordEncoder.matches(loginRequest.password(), user.getPassword()));

            if (!passwordEncoder.matches(loginRequest.password(), user.getPassword())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid password");
            }

            String token = jwtTokenProvider.generateToken(user.getUsername(), user.getRole());
            return ResponseEntity.ok(new JwtAuthenticationResponse(token, user.getRole()));

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    }

    // DTO records
    record LoginRequest(String username, String password) {}
    record JwtAuthenticationResponse(String token, String role) {}
}