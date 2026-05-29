package com.bezkoder.spring.jpa.h2.controller;

import com.bezkoder.spring.jpa.h2.model.User;
import com.bezkoder.spring.jpa.h2.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Authentication controller — public endpoints (no auth required).
 *
 * <p>Note: CORS is handled globally in {@code SecurityConfig}.
 * Do NOT add {@code @CrossOrigin} here as it conflicts with the
 * global {@code allowCredentials=true} CORS policy.</p>
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder encoder;

    /**
     * Login — validates credentials against the DB.
     *
     * <p>Returns {@code username} and {@code role} so the Angular client
     * can store them in localStorage for subsequent Basic Auth headers.</p>
     *
     * @param loginRequest map with {@code username} and {@code password} keys
     * @return 200 OK with user info, or 401 if credentials are invalid
     */
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody Map<String, String> loginRequest) {
        String username = loginRequest.get("username");
        String password = loginRequest.get("password");

        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Username and password are required"));
        }

        return userRepository.findByUsername(username)
                .filter(user -> encoder.matches(password, user.getPassword()))
                .map(user -> ResponseEntity.ok(Map.of(
                        "message",  "Login successful",
                        "username", user.getUsername(),
                        "role",     user.getRole()
                )))
                .orElse(ResponseEntity.status(401)
                        .body(Map.of("message", "Invalid username or password")));
    }

    /**
     * Register a new user account.
     *
     * @param registerRequest map with {@code username}, {@code password}, and optional {@code role}
     * @return 200 OK on success, 400 if username is already taken
     */
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody Map<String, String> registerRequest) {
        String username = registerRequest.get("username");
        String password = registerRequest.get("password");
        String role     = registerRequest.getOrDefault("role", "Member");

        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Username and password are required"));
        }

        if (userRepository.findByUsername(username).isPresent()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Username is already taken"));
        }

        User user = new User(username, encoder.encode(password), role);
        userRepository.save(user);

        return ResponseEntity.ok(Map.of("message", "User registered successfully"));
    }
}
