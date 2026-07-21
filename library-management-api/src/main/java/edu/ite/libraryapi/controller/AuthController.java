package edu.ite.libraryapi.controller;

import edu.ite.libraryapi.dto.ApiResponse;
import edu.ite.libraryapi.dto.LoginRequest;
import edu.ite.libraryapi.dto.LoginResponse;
import edu.ite.libraryapi.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) { this.authService = authService; }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Login successful", authService.login(request)));
    }
}
