package com.example.demo.auth;

import com.example.demo.auth.dto.request.LoginRequest;
import com.example.demo.auth.dto.request.RegisterRequest;
import com.example.demo.auth.dto.response.LoginResponse;
import com.example.demo.auth.dto.response.RegisterResponse;
import com.example.demo.emailVerification.VerificationService;
import com.example.demo.token.JwtService;
import com.example.demo.user.User;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.demo.user.UserRepository;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final VerificationService verificationService;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            VerificationService verificationService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.verificationService = verificationService;
    }

    @Transactional
    public RegisterResponse userRegister (RegisterRequest request){
        if (userRepository.existsByUsername(request.username())){
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Username is taken"
            );
        }

        if (userRepository.existsByEmail(request.email())){
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "email is already registered"
            );
        }

        User user = new User(
                request.username(),
                passwordEncoder.encode(request.password()),
                request.email(),
                request.countryCode()
        );

        userRepository.save(user);
        verificationService.sendVerificationEmail(user);

        return new RegisterResponse(request.username(), request.email());

    }

    @Transactional(readOnly = true)
    public LoginResponse userLogin (LoginRequest request){
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Wrong email or password"
                ));

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "wrong email or password"
            );

        }

        String accessToken = jwtService.generateToken(user);
        return new LoginResponse(accessToken, "Bearer");

    }
}
