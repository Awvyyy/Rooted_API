package com.example.demo.service;

import com.example.demo.dto.request.AuthRequest;
import com.example.demo.dto.request.LoginRequest;
import com.example.demo.dto.request.RegisterRequest;
import com.example.demo.dto.response.AuthResponse;
import com.example.demo.entity.User;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.demo.repository.UserRepository;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, UserService userService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
    }

    @Transactional
    public AuthResponse userRegister (RegisterRequest request){
        if (userRepository.existsByUsername(request.username())){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username is taken");
        }

        if (userRepository.existsByEmail(request.email())){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "email is already registered");
        }

        User user = new User(request.username(), passwordEncoder.encode(request.password()), request.email(), request.countryCode());

        userRepository.save(user);

        return toResponse(user);

    }

    @Transactional(readOnly = true)
    public AuthResponse userLogin (AuthRequest request){
        User user = userService.validateUser(request);

        return toResponse(user);

    }

    public AuthResponse toResponse(User user){
        return new AuthResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.isContainsProfilePicture(),
                user.getProfilePictureUrl(),
                user.getCountryCode());
    }
}
