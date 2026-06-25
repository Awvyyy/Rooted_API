package com.example.demo.auth;

import com.example.demo.auth.dto.request.LoginRequest;
import com.example.demo.auth.dto.request.RegisterRequest;
import com.example.demo.auth.dto.response.LoginResponse;
import com.example.demo.auth.dto.response.RegisterResponse;
import com.example.demo.emailVerification.VerificationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final VerificationService verificationService;
    public AuthController(
            AuthService authService,
            VerificationService verificationService
    ){
        this.authService = authService;
        this.verificationService = verificationService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public RegisterResponse register (@Valid @RequestBody RegisterRequest request){
        return authService.userRegister(request);
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public LoginResponse login (@Valid @RequestBody LoginRequest request){
        return authService.userLogin(request);
    }

    @GetMapping("/verify")
    public ResponseEntity<String> verify(
            @RequestParam String token
    ) {
        verificationService.verifyEmail(token);

        return ResponseEntity.ok("Email verified");
    }
}
